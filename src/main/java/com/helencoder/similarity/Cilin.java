package com.helencoder.similarity;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 《同义词词林》语义相似度计算
 *
 * Created by helencoder on 2017/9/18.
 */
public class Cilin {
    private static Map<String, List<String>> cilinMap = new HashMap<String, List<String>>();
    private double[] weights = {1.0, 0.5, 0.25, 0.125, 0.06, 0.03};
    private double initDis = 10;
    private double a = 5;

    public Cilin() {
        // 加载字典文件
        cilinMap = loadDict();
    }

    /**
     * 词语语义相似度计算
     *
     * @param word1 单词1
     * @param word2 单词2
     *
     * @return double 词语语义相似度
     */
    public double wordSimilarity(String word1, String word2) {
        List<String> word1EncodingData = this.wordEncoding(word1);
        List<String> word2EncodingData = this.wordEncoding(word2);
        if (word1EncodingData.isEmpty() || word2EncodingData.isEmpty()) {
            // 此处应有未登录词限制
            System.out.println("存在未登录词");
            return 0;
        }

        double minDis = 10;
        for (int i = 0; i < word1EncodingData.size(); i++) {
            String code1 = word1EncodingData.get(i);
            for (int j = 0; j < word2EncodingData.size(); j++) {
                String code2 = word2EncodingData.get(j);
                double dis = codeDis(code1, code2);
                if (dis < minDis) {
                    minDis = dis;
                }
            }
        }

        return a / (minDis + a);
    }

    /**
     * 加载字典文件数据
     */
    private Map<String, List<String>> loadDict() {
        Map<String, List<String>> cilinMap = new HashMap<String, List<String>>();
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(
                    new FileInputStream("data/dict/cilin.txt")));

            for (String line = br.readLine(); line != null; line = br.readLine()) {
                String code = line.substring(0, 8);
                String words = line.substring(9);
                for (String word : words.split(" ")) {
                    List<String> tmpList = new ArrayList<String>();
                    tmpList.add(code);
                    if (cilinMap.containsKey(word)) {
                        tmpList.addAll(cilinMap.get(word));
                    }
                    cilinMap.put(word, tmpList);
                }
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        return cilinMap;
    }

    /**
     * 获取指定词语编码
     *
     * @param word
     */
    private List<String> wordEncoding(String word) {
        List<String> wordEncodingData = new ArrayList<String>();
        if (cilinMap.containsKey(word)) {
            wordEncodingData.addAll(cilinMap.get(word));
        }

        return wordEncodingData;
    }

    /**
     * 编码距离计算
     *
     * @param code1 编码1
     * @param code2 编码2
     *
     * @return double 编码距离
     */
    private double codeDis(String code1, String code2) {
        if (code1.equals(code2)) {
            if (code1.charAt(7) != '#') {
                return 0;
            } else {
                return weights[5] * initDis;
            }
        } else {
            int flag = 0;
            for (int i = 0; i < 8; i++) {
                if (code1.charAt(i) != code2.charAt(i)) {
                    flag = i;
                    break;
                }
                flag++;
            }

            // 进行层级判断(按照哈工大扩展版编码规则表实现)
            int hierarchy = 0;
            switch (flag) {
                case 0:
                case 1:
                    hierarchy = flag + 1;
                    break;
                case 2:
                case 3:
                    hierarchy = 3;
                    break;
                case 4:
                    hierarchy = flag;
                    break;
                case 5:
                case 6:
                    hierarchy = 5;
                    break;
                case 7:
                    hierarchy = 6;
                    break;
            }

            return weights[hierarchy - 1] * initDis;
        }
    }

}
