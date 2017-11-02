package com.helencoder.similarity.text;

import com.helencoder.preprocess.getTextWordsFrequency;
import com.helencoder.textrank.Word;

import java.util.*;

/**
 * 余弦相似度计算
 * 判定方式：余弦相似度，通过计算两个向量的夹角余弦值来评估他们的相似度
 * 余弦夹角原理：
 *  向量a=(x1,y1),向量b=(x2,y2)
 *  similarity=a.b/|a|*|b|
 *  a.b=x1x2+y1y2
 *  |a|=根号[(x1)^2+(y1)^2],|b|=根号[(x2)^2+(y2)^2]
 *
 * 步骤:
 *  1、找出两篇文章的关键词
 *  2、合并关键词，组成一个集合，计算每篇文章对于这个集合中的词的词频
 *  3、生成两篇文章各自的词频向量
 *  4、计算两个向量的余弦相似度
 *
 * @author helen
 */
public class CosineSimilarity {
    public static void main(String[] args) {
        // 计算文本相似度
        //run();

    }

    /**
     * 余弦相似度计算
     * @param wordsList1 单词列表
     * @param wordsList2 单词列表
     * @param content1 文本内容
     * @param content2 文本内容
     * @return 文本相似性
     */
    public static float run(List<String> wordsList1, List<String> wordsList2, String content1, String content2) {
        if (wordsList1.isEmpty() && wordsList2.isEmpty()) {
            return 1.0f;
        }

        List<String> wordsList = wordsList1;
        wordsList.removeAll(wordsList2);
        wordsList.addAll(wordsList2);
        //转变为不重复的集合
        Set<String> wordsSet = new HashSet<String>(wordsList);
        // 获取相应的词频向量
        List<Integer> wordsFrquency1 = getTextWordsFrequency.run(wordsSet, content1);
        List<Integer> wordsFrquency2 = getTextWordsFrequency.run(wordsSet, content2);

        // 计算余弦相似度距离
        Iterator<Integer> iterator1 = wordsFrquency1.iterator();
        Iterator<Integer> iterator2 = wordsFrquency2.iterator();

        double partUp = 0;
        double aSq = 0;
        double bSq = 0;
        while (iterator1.hasNext() && iterator2.hasNext()) {
            Integer integer1 = iterator1.next();
            Integer integer2 = iterator2.next();
            partUp += (integer1 * integer2);
            aSq += Math.pow(integer1, 2);
            bSq += Math.pow(integer2, 2);
        }

        float sim = (float)(partUp / (Math.sqrt(aSq) * Math.sqrt(bSq)));
        return sim;
    }

    /**
     * 余弦相似度计算(附加权值)
     *
     * @param wordsList1 单词列表
     * @param wordsList2 单词列表
     * @param content1 文本内容
     * @param content2 文本内容
     * @return 文本相似性
     */
    public static float runWithWeight(List<Word> wordsList1, List<Word> wordsList2, String content1, String content2) {
        if (wordsList1.isEmpty() && wordsList2.isEmpty()) {
            return 1.0f;
        }

        // 文本关键词合并、去重(相同关键词取权重较大的)
        Map<String, Word> wordsMap = new HashMap<String, Word>();
        for (Word word : wordsList1) {
            if (wordsMap.containsKey(word.word)) {
                // 进行权值的比较
                Word oldWord = wordsMap.get(word.word);
                if (oldWord.weight < word.weight) {
                    wordsMap.put(word.word, word);
                }
            } else {
                wordsMap.put(word.word, word);
            }
        }

        //转变为不重复的集合
        Set<Word> wordsSet = new HashSet<Word>();
        for (Map.Entry<String, Word> entry : wordsMap.entrySet()) {
            wordsSet.add(entry.getValue());
        }

        // 获取相应的词频向量
        List<Float> wordsFrquency1 = getTextWordsFrequency.runWithWeight(wordsSet, content1);
        List<Float> wordsFrquency2 = getTextWordsFrequency.runWithWeight(wordsSet, content2);

        // 计算余弦相似度距离
        Iterator<Float> iterator1 = wordsFrquency1.iterator();
        Iterator<Float> iterator2 = wordsFrquency2.iterator();

        double partUp = 0;
        double aSq = 0;
        double bSq = 0;
        while (iterator1.hasNext() && iterator2.hasNext()) {
            Float integer1 = iterator1.next();
            Float integer2 = iterator2.next();
            partUp += (integer1 * integer2);
            aSq += Math.pow(integer1, 2);
            bSq += Math.pow(integer2, 2);
        }

        float sim = (float)(partUp / (Math.sqrt(aSq) * Math.sqrt(bSq)));
        return sim;
    }


//    /**
//     * 创建SparkContext
//     */
//    private static String master = "local[*]";
//    private static SparkConf conf = new SparkConf()
//            .setAppName(WordCountTask.class.getName())
//            .setMaster(master);
//    private static JavaSparkContext sc = new JavaSparkContext(conf);
//
//    /**
//     * 文本预处理
//     * 计算两片文本的文本相似度
//     */
//    public static void run() {
//        // 将文本读入
//        JavaRDD<String> fileDataRDD = sc.textFile("public/record.txt");
//
//        // 转换为键值对RDD,其中newsid作为键,文本内容为键值
//        JavaPairRDD<String, String> fileContentRDD = fileDataRDD.mapToPair(
//                new PairFunction<String, String, String>() {
//                    @Override
//                    public Tuple2<String, String> call(String line) throws Exception {
//                        String[] details = line.split("\t\t\t");
//                        return new Tuple2<>(details[0], details[1]);
//                    }
//                }
//        );
//        fileContentRDD.cache();
//
//        // 获取文本的关键词信息
//        // newsid作为键,关键词list作为键值
//        JavaPairRDD<String, List<String>> fileKeywordsRDD = fileContentRDD.mapToPair(
//                new PairFunction<Tuple2<String, String>, String, List<String>>() {
//                    @Override
//                    public Tuple2<String, List<String>> call(Tuple2<String, String> tuple2) throws Exception {
//                        String newsid = tuple2._1;
//                        String content = tuple2._2;
//                        // 获取文本关键词
//                        List<String> keywordsList = TextRank.getKeywordList(content, 100, 2);
//                        return new Tuple2<>(newsid, keywordsList);
//                    }
//                }
//        );
//
//        // RDD转换
//        Map<String, String> contentMap = fileContentRDD.collectAsMap();
//        Map<String, List<String>> keywordsMap = fileKeywordsRDD.collectAsMap();
//
//        Set<String> newsidList = contentMap.keySet();
//        // 进行文本相似度计算
//        int count = 0;
//        for (String indexNewsID : newsidList) {
//            List<String> indexKeywordsList = keywordsMap.get(indexNewsID);
//            String indexContent = contentMap.get(indexNewsID);
//            System.out.println("当前查询文章为：" + indexNewsID + " 已处理文章数为：" + count);
//            count += 1;
//
//            long startTime=System.currentTimeMillis();   //获取开始时间
//            //doSomeThing();  //测试的代码段
//
//            for (String cmpNewsID : newsidList) {
//                if (!cmpNewsID.equals(indexNewsID)) {
//                    List<String> cmpKeywordsList = keywordsMap.get(cmpNewsID);
//                    String cmpContent = contentMap.get(cmpNewsID);
//                    // 合并关键词
//                    indexKeywordsList.removeAll(cmpKeywordsList);
//                    indexKeywordsList.addAll(cmpKeywordsList);
//                    List<String> keywordsList = indexKeywordsList;
//                    // 计算词频向量
//                    List<Integer> indexVector = calWordFrequencyVector(keywordsList, indexContent);
//                    List<Integer> cmpVector = calWordFrequencyVector(keywordsList, cmpContent);
//                    // 计算余弦相似度
//                    double cosineSim = cosineSimilarity(indexVector, cmpVector);
//
//                    // 进行文本记录
//                    String str = indexNewsID + "\t" + cmpNewsID +"\t\t\t" + cosineSim;
//                    FileIO.appendFile("public/cosine_sim_record.txt", str);
//
//                }
//
//            }
//
//            long endTime=System.currentTimeMillis(); //获取结束时间
//            System.out.println("程序运行时间： "+(endTime-startTime)+"ms");
//
//
//        }
//
//
//
//    }
//
//
//    /**
//     * 计算词频向量
//     *
//     */
//    public static List<Integer> calWordFrequencyVector(List<String> wordsList, String content) {
//        List<Integer> vector = new ArrayList<Integer>();
//
//        for (String word : wordsList) {
//            String longStr = content;
//
//            // 字符统计
//            int count = 0;
//            // 调用String类的indexOf(String str)方法，返回第一个相同字符串出现的下标
//            while (longStr.indexOf(word) != -1) {
//                // 如果存在相同字符串则次数加1
//                count++;
//                // 调用String类的substring(int beginIndex)方法，获得第一个相同字符出现后的字符串
//                longStr = longStr.substring(longStr.indexOf(word) + word.length());
//            }
//            vector.add(count);
//        }
//
//        return vector;
//    }
//
//
//    /**
//     * 余弦相似度计算
//     */
//    public static double cosineSimilarity(List<Integer> vector1, List<Integer> vector2) {
//        if (vector1.size() != vector2.size()) {
//            throw new IllegalArgumentException("向量长度不一致！");
//        }
//
//        ListIterator<Integer> listIterator1 = vector1.listIterator();
//        ListIterator<Integer> listIterator2 = vector2.listIterator();
//
//        double partUp = 0;
//        double aSq = 0;
//        double bSq = 0;
//        while (listIterator1.hasNext() && listIterator2.hasNext()) {
//            Integer integer1 = listIterator1.next();
//            Integer integer2 = listIterator2.next();
//            partUp += (integer1 * integer2);
//            aSq += Math.pow(integer1, 2);
//            bSq += Math.pow(integer2, 2);
//        }
//        double cosSim = partUp / (Math.sqrt(aSq) * Math.sqrt(bSq));
//        return cosSim;
//    }

}
