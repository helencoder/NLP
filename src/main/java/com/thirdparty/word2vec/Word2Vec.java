package com.thirdparty.word2vec;

import com.thirdparty.word2vec.domain.WordEntry;
import com.thirdparty.word2vec.util.Learn;

import java.io.*;
import java.util.*;

/**
 * Word2Vec归集版
 *
 * Created by helencoder on 2017/10/12.
 */
public class Word2Vec {
    private int words;
    private int size;
    private boolean loadModel;
    private HashMap<String, float[]> wordMap = new HashMap<String, float[]>();

    public Word2Vec() {
        loadModel = false;
    }

    /**
     * Word2Vec模型训练
     *
     * @param inputFile 预先分词文件路径(自定义分词组件)
     * @param modelFile 生成模型文件路径
     */
    public void trainModel(String inputFile, String modelFile) throws IOException {
        // 模型训练
        File inputfile = new File(inputFile);
        File outFile = new File(modelFile);
        Learn learn = new Learn();
        learn.learnFile(inputfile);

        learn.saveModel(outFile);
    }

    /**
     * Word2Vec模型加载(Java训练模型)
     *
     * @param modelFile 模型文件路径
     */
    public void loadModel(String modelFile)  throws IOException {
        DataInputStream dis = new DataInputStream(new BufferedInputStream(new FileInputStream(modelFile)));
        words = dis.readInt();
        size = dis.readInt();

        float vector = 0;

        String key = null;
        float[] value = null;
        for (int i = 0; i < words; i++) {
            double len = 0;
            key = dis.readUTF();
            value = new float[size];
            for (int j = 0; j < size; j++) {
                vector = dis.readFloat();
                len += vector * vector;
                value[j] = vector;
            }

            len = Math.sqrt(len);

            for (int j = 0; j < size; j++) {
                value[j] /= len;
            }
            wordMap.put(key, value);
        }

        loadModel = true;
    }

    /**
     * 获取词向量
     *
     * @param word
     * @return float[] 词向量
     */
    public float[] getWordVector(String word) {
        if (!loadModel) {
            return null;
        }
        return wordMap.get(word);
    }

    /**
     * 获取相似词语
     *
     * @param word 单词
     * @param num 相似单词数量
     * @return 相似词语集合
     */
    public Set<WordEntry> getSimilarWords(String word, int num) {
        if (loadModel == false)
            return null;
        float[] center = getWordVector(word);
        if (center == null) {
            return Collections.emptySet();
        }
        int resultSize = getWords() < num ? getWords() : num;
        TreeSet<WordEntry> result = new TreeSet<WordEntry>();
        double min = Double.MIN_VALUE;
        for (Map.Entry<String, float[]> entry : getWordMap().entrySet()) {
            float[] vector = entry.getValue();
            float dist = calDist(center, vector);
            if (result.size() <= resultSize) {
                result.add(new WordEntry(entry.getKey(), dist));
                min = result.last().score;
            } else {
                if (dist > min) {
                    result.add(new WordEntry(entry.getKey(), dist));
                    result.pollLast();
                    min = result.last().score;
                }
            }
        }
        result.pollFirst();

        return result;
    }

    /**
     * 计算词语相似度
     *
     * @param word1 单词
     * @param word2 单词
     * @return float 词语相似度
     */
    public float wordSimilarity(String word1, String word2) {
        if (loadModel == false) {
            return 0;
        }
        float[] word1Vec = getWordVector(word1);
        float[] word2Vec = getWordVector(word2);
        if(word1Vec == null || word2Vec == null) {
            return 0;
        }
        return calDist(word1Vec, word2Vec);
    }

    /**
     * 计算向量内积
     *
     * @param vec1 词向量
     * @param vec2 词向量
     * @return float 向量内积
     */
    private float calDist(float[] vec1, float[] vec2) {
        float dist = 0;
        for (int i = 0; i < vec1.length; i++) {
            dist += vec1[i] * vec2[i];
        }
        return dist;
    }


    public HashMap<String, float[]> getWordMap() {
        return wordMap;
    }

    public int getWords() {
        return words;
    }

    /**
     * Word2Vec模型加载(Java训练模型)
     *
     * @param modelFile 模型文件路径
     */
    public void loadGoogleModel(String modelFile) throws IOException {
        DataInputStream dis = null;
        BufferedInputStream bis = null;
        double len = 0;
        float vector = 0;
        try {
            bis = new BufferedInputStream(new FileInputStream(modelFile));
            dis = new DataInputStream(bis);
            //读取词数
            words = Integer.parseInt(readString(dis));
            // //大小
            size = Integer.parseInt(readString(dis));
            String word;
            float[] vectors = null;
            for (int i = 0; i < words; i++) {
                word = readString(dis);
                vectors = new float[size];
                len = 0;
                for (int j = 0; j < size; j++) {
                    vector = readFloat(dis);
                    len += vector * vector;
                    vectors[j] = (float) vector;
                }
                len = Math.sqrt(len);

                for (int j = 0; j < size; j++) {
                    vectors[j] /= len;
                }

                wordMap.put(word, vectors);
                dis.read();
            }
        } finally {
            bis.close();
            dis.close();
        }
    }

    public static float readFloat(InputStream is) throws IOException {
        byte[] bytes = new byte[4];
        is.read(bytes);
        return getFloat(bytes);
    }

    /**
     * 读取一个float
     *
     * @param b
     * @return
     */
    public static float getFloat(byte[] b) {
        int accum = 0;
        accum = accum | (b[0] & 0xff) << 0;
        accum = accum | (b[1] & 0xff) << 8;
        accum = accum | (b[2] & 0xff) << 16;
        accum = accum | (b[3] & 0xff) << 24;
        return Float.intBitsToFloat(accum);
    }

    private static final int MAX_SIZE = 50;
    /**
     * 读取一个字符串
     *
     * @param dis
     * @return
     * @throws IOException
     */
    private static String readString(DataInputStream dis) throws IOException {
        byte[] bytes = new byte[MAX_SIZE];
        byte b = dis.readByte();
        int i = -1;
        StringBuilder sb = new StringBuilder();
        while (b != 32 && b != 10) {
            i++;
            bytes[i] = b;
            b = dis.readByte();
            if (i == 49) {
                sb.append(new String(bytes));
                i = -1;
                bytes = new byte[MAX_SIZE];
            }
        }
        sb.append(new String(bytes, 0, i + 1));
        return sb.toString();
    }

}
