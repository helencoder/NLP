package com.helencoder.similarity.text;

import com.thirdparty.word2vec.Word2Vec;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by helencoder on 2017/7/17.
 */
public class Word2VecSimilarity {
    public static void main(String[] args) {
        // 文本相似度计算(Word2Vec)

        //run();

    }


    /**
     * Word2Vec 文本相似性
     *
     * @param wordsList1 单词list
     * @param wordsList2 单词list
     * @return 文本相似性
     */
    public static float run(List<String> wordsList1, List<String> wordsList2) {
        if (wordsList1.isEmpty() && wordsList2.isEmpty()) {
            return 1.0f;
        }

        // 获取文本的词向量
        List<float[]> wordsVectorList1 = new ArrayList<float[]>();
        List<float[]> wordsVectorList2 = new ArrayList<float[]>();
        for (String word : wordsList1) {
            float[] wordVector = getWordVector(word);
            wordsVectorList1.add(wordVector);
        }
        for (String word : wordsList2) {
            float[] wordVector = getWordVector(word);
            wordsVectorList2.add(wordVector);
        }

        // 获取文本相似度
        float sim = getSimilarity(wordsVectorList1, wordsVectorList2);
        return sim;
    }

    /**
     * Word2Vec 文本相似性
     */
    public static float getSim(List<float[]> wordsVectorList1, List<float[]> wordsVectorList2) {
        // 获取文本相似度
        float sim = getSimilarity(wordsVectorList1, wordsVectorList2);
        return sim;
    }

    /**
     * 加载Word2Vec训练模型
     */
    private static Word2Vec vec = init();

    /**
     * 加载Word2Vec训练模型
     */
    private static Word2Vec init() {
        Word2Vec vec = new Word2Vec();
        try {
            vec.loadGoogleModel("data/wiki_chinese_word2vec(Google).model");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return vec;
    }

    /**
     * Word2Vec获取词向量
     * @param word 单词
     * @return 词向量
     */
    public static float[] getWordVector(String word) {
        return vec.getWordVector(word);
    }

    /**
     * Word2vec获取文章间语义相似度
     */
    private static float getSimilarity(List<float[]> vector1, List<float[]> vector2) {
        float[] indexVector = new float[vector1.size()];
        float[] searchVector = new float[vector2.size()];
        for (int i = 0; i < indexVector.length; i++) {
            indexVector[i] = calMaxSimilarity(vector1.get(i), vector2);
        }
        for (int i = 0; i < searchVector.length; i++) {
            searchVector[i] = calMaxSimilarity(vector2.get(i), vector1);
        }
        float sum1 = 0;
        for (int i = 0; i < indexVector.length; i++) {
            sum1 += indexVector[i];
        }
        float sum2 = 0;
        for (int i = 0; i < searchVector.length; i++) {
            sum2 += searchVector[i];
        }
        return (sum1 + sum2) / (vector1.size() + vector2.size());
    }

    /**
     * 计算指定词向量与词向量数组中所有词向量的最大相似度
     * (最小返回0)
     * @param indexVector 词语向量
     * @param vectorList 词语向量列表
     * @return
     */
    private static float calMaxSimilarity(float[] indexVector, List<float[]> vectorList) {
        float max = -1;
        if (vectorList.contains(indexVector)) {
            return 1;
        } else {
            for (float[] word : vectorList) {
                float temp = wordSimilarity(indexVector, word);
                if (temp == 0) continue;
                if (temp > max) {
                    max = temp;
                }
            }
        }
        if (max == -1) return 0;
        return max;
    }

    /**
     * 计算词相似度(词向量)
     * @param word1Vec
     * @param word2Vec
     * @return
     */
    private static float wordSimilarity(float[] word1Vec, float[] word2Vec) {
        if(word1Vec == null || word2Vec == null) {
            return 0;
        }
        return calDist(word1Vec, word2Vec);
    }

    /**
     * 计算向量内积
     * @param vec1
     * @param vec2
     * @return
     */
    private static float calDist(float[] vec1, float[] vec2) {
        float dist = 0;
        for (int i = 0; i < vec1.length; i++) {
            dist += vec1[i] * vec2[i];
        }
        return dist;
    }

//    public static void test() {
//        // 获取文本内容
//        String filePath1 = "data/zixun_file/0a2cfd3c-7166-4c47-998d-2aaba3573fe5.txt";
//        String filePath2 = "data/zixun_file/0a5fd8ac-705d-4796-91a1-cac6495b87c4.txt";
//        String cotent1 = FileIO.getFileData(filePath1);
//        String cotent2 = FileIO.getFileData(filePath2);
//
//        long startTime=System.currentTimeMillis();   //获取开始时间
//
//        // 获取文本关键词
//        List<String> keywordsList1 = featureExtraction(cotent1, 100);
//        List<String> keywordsList2 = featureExtraction(cotent2, 100);
//
//        // 获取文本的词向量
//        List<float[]> keywordsVectorList1 = new ArrayList<float[]>();
//        List<float[]> keywordsVectorList2 = new ArrayList<float[]>();
//
//        for (String word : keywordsList1) {
//            float[] wordVector = getWordVector(word);
//            keywordsVectorList1.add(wordVector);
//        }
//
//        for (String word : keywordsList2) {
//            float[] wordVector = getWordVector(word);
//            keywordsVectorList2.add(wordVector);
//        }
//
//        // 获取文本相似度
//        float sim = getTextSimilarity(keywordsList1, keywordsList2);
//        System.out.println(sim);
//
//        float sim1 = getSimilarity(keywordsVectorList1, keywordsVectorList2);
//        System.out.println(sim1);
//
//        long endTime=System.currentTimeMillis(); //获取结束时间
//        System.out.println("程序运行时间： "+(endTime-startTime)+"ms");
//    }
//
//    /**
//     * 创建SparkContext
//     */
//    private static String master = "local[*]";
//    private static SparkConf conf = new SparkConf()
//            .setAppName(WordCountTask.class.getName())
//            .setMaster(master);
//    private static JavaSparkContext sc = new JavaSparkContext(conf);
//
//
//
//    public static void run() {
//        List<String> keywordsList = new ArrayList<String>();
//
//        // 读取文本
//        JavaRDD<String> fileDataRDD = sc.textFile("public/record.txt");
//        fileDataRDD.cache();
//
//        // 转换为键值对RDD,newsid作为键,文本内容为键值
//        JavaPairRDD<String, String> fileContentRDD = fileDataRDD.mapToPair(
//                new PairFunction<String, String, String>() {
//                    @Override
//                    public Tuple2<String, String> call(String content) throws Exception {
//                        String[] details = content.split("\t\t\t");
//                        return new Tuple2<>(details[0], details[1]);
//                    }
//                }
//        );
//        fileContentRDD.cache();
//        // 转换为键值对RDD,newsid作为键,关键词list为键值
//        JavaPairRDD<String, List<String>> fileKeywordsRDD = fileDataRDD.mapToPair(
//                new PairFunction<String, String, List<String>>() {
//                    @Override
//                    public Tuple2<String, List<String>> call(String content) throws Exception {
//                        String[] details = content.split("\t\t\t");
//                        String newsID = details[0];
//                        String article = details[1];
//                        List<String> keywordsList = featureExtraction(article, 100);
//                        return new Tuple2<>(newsID, keywordsList);
//                    }
//                }
//        );
//        fileKeywordsRDD.cache();
//
//        // 转换为键值RDD,newsid作为键,关键词list对应的词向量数组作为键值
//        JavaPairRDD<String, List<float[]>> fileWordVectorsRDD = fileKeywordsRDD.mapToPair(
//                new PairFunction<Tuple2<String, List<String>>, String, List<float[]>>() {
//                    @Override
//                    public Tuple2<String, List<float[]>> call(Tuple2<String, List<String>> stringListTuple2) throws Exception {
//                        Tuple2<String, List<String>> keywordsData = stringListTuple2;
//                        String newsid = keywordsData._1;
//                        List<String> keywordslist = keywordsData._2;
//                        List<float[]> wordVectorList = new ArrayList<float[]>();
//                        for (String keyword : keywordslist) {
//                            float[] wordVector = getWordVector(keyword);
//                            wordVectorList.add(wordVector);
//                        }
//                        return new Tuple2<>(newsid, wordVectorList);
//                    }
//                }
//        );
//        fileWordVectorsRDD.cache();
//
//        // 在这里直接计算两个文章的相似度(此种方式不可行,会报异常,对于RDD不能嵌套)
////        fileWordVectorsRDD.foreach(r -> {
////            String indexNewsID = r._1();
////            List<float[]> indexWordsVectorList = r._2();
////
////            System.out.println("当前查询文本的newsid为：" + indexNewsID);
////            // 时间记录
////            long startTime=System.currentTimeMillis();   //获取开始时间
////
////            fileWordVectorsRDD.foreach(r1 -> {
////                String searchNewsID = r1._1();
////                if (!indexNewsID.equals(searchNewsID)) {
////                    List<float[]> searchWordsVectorList = r1._2();
////
////                    // 获取文本相似度
////                    float sim = getTextSimilarityByVector(indexWordsVectorList, searchWordsVectorList);
////
////                    // 进行文本文件记录
////                    String simStr = sim + "";
////                    String fileStr = indexNewsID + "\t" + searchNewsID + "\t\t\t" + simStr;
////                    FileIO.appendFile("public/sim_record_word2vec_lambda.txt", fileStr);
////                }
////
////            });
////
////            long endTime=System.currentTimeMillis(); //获取结束时间
////            System.out.println("程序运行时间： "+(endTime-startTime)+"ms");
////
////        });
//
//
//        // RDD转换
//        List<String> fileDetailsList = fileDataRDD.collect();
////        List<String> fileDetailList = new ArrayList<String>();
////        fileDetailList.addAll(fileDetailsList);
//        Map<String, List<float[]>> fileKeywordsVectorMap = fileWordVectorsRDD.collectAsMap();
//
//        // 在这里直接计算两个文章的相似度(利用转换好的map进行处理)
//        fileWordVectorsRDD.foreach(r -> {
//            String indexNewsID = r._1();
//            List<float[]> indexWordsVectorList = r._2();
//
//            System.out.println("当前查询文本的newsid为：" + indexNewsID);
//            // 时间记录
//            long startTime=System.currentTimeMillis();   //获取开始时间
//
//            for (String searchDetail : fileDetailsList) {
//                String[] arr1 = searchDetail.split("\t\t\t");
//                String searchNewsID = arr1[0];
//                if (!indexNewsID.equals(searchNewsID)) {
//                    // 获取对比文本的关键词向量list
//                    List<float[]> searchWordsVectorList = fileKeywordsVectorMap.get(searchNewsID);
//
//                    // 获取文本相似度
//                    float sim = getSimilarity(indexWordsVectorList, searchWordsVectorList);
//
//                    // 进行文本文件记录
//                    String simStr = sim + "";
//                    String fileStr = indexNewsID + "\t" + searchNewsID + "\t\t\t" + simStr;
//                    FileIO.appendFile("public/sim_record_word2vec_lambda.txt", fileStr);
//                }
//            }
//
//            long endTime=System.currentTimeMillis(); //获取结束时间
//            System.out.println("程序运行时间： "+(endTime-startTime)+"ms");
//
//        });
//
//        // RDD转换为List
////        List<String> fileDetailsList = fileDataRDD.collect();
////        // RDD转换为Map
////        Map<String, String> fileContentMap = fileContentRDD.collectAsMap();
////        //Map<String, List<String>> fileKeywordsMap = fileKeywordsRDD.collectAsMap();
////        Map<String, List<float[]>> fileKeywordsVectorMap = fileWordVectorsRDD.collectAsMap();
////
////        // 文本相似度计算
////
////        // 文本相似度记录和计算
////        // 文本相似文本记录
////        Map<String, List<String>> fileSimFileMap = new HashMap<String, List<String>>();
////
////        // 相似度数据记录Map
////        Map<String, Float> simDetailsTotal = new HashMap<String, Float>();
////
////        int count = 1;
////
////        for (String detail : fileDetailsList) {
////            String[] arr = detail.split("\t\t\t");
////            String indexNewsID = arr[0];
////            System.out.println("当前查询文本的newsid为：" + indexNewsID);
////
////            // 时间记录
////            long startTime=System.currentTimeMillis();   //获取开始时间
////
////            // 获取当前文本的关键词list
////            //List<String> indexWordsList = fileKeywordsMap.get(indexNewsID);
////            // 获取当前文本的关键词向量list
////            List<float[]> indexWordsVectorList = fileKeywordsVectorMap.get(indexNewsID);
////
////            // 中间记录文本
////            Map<String, Float> simDetails = new HashMap<String, Float>();
////
////            for (String searchDetail : fileDetailsList) {
////                String[] arr1 = searchDetail.split("\t\t\t");
////                String searchNewsID = arr1[0];
////                if (!indexNewsID.equals(searchNewsID)) {
////
////                    // 进行相似度数据记录Map检测
////                    String flag = indexNewsID + "\t" + searchNewsID;
////                    String flag1 = searchNewsID + "\t" + indexNewsID;
////                    if (simDetailsTotal.containsKey(flag)) {
////                        float sim = simDetailsTotal.get(flag);
////                        // 中间过渡记录
////                        simDetails.put(searchNewsID, sim);
////                    } else if (simDetailsTotal.containsKey(flag1)) {
////                        float sim = simDetailsTotal.get(flag1);
////                        // 中间过渡记录
////                        simDetails.put(searchNewsID, sim);
////                    } else {
////                        // 获取对比文本关键词list
////                        //List<String> searchWordsList = fileKeywordsMap.get(searchNewsID);
////
////                        // 获取对比文本的关键词向量list
////                        List<float[]> searchWordsVectorList = fileKeywordsVectorMap.get(searchNewsID);
////
////                        // 计算语义相似度
////
////                        // 常规做法
////                        //float sim = getTextSimilarity(indexWordsList, searchWordsList);
////
////                        // 利用预先存储的词向量进行计算
////                        float sim = getTextSimilarityByVector(indexWordsVectorList, searchWordsVectorList);
////
////                        //System.out.println("与对比文章" + searchNewsID + " 文本相似度为: " + sim);
////
////                        // 中间过渡记录
////                        simDetails.put(searchNewsID, sim);
////
////                        // 相似度数据记录Map
////                        String flag2 = indexNewsID + "\t" + searchNewsID;
////                        simDetailsTotal.put(flag2, sim);
////
////                        // 进行文本文件记录
////                        String simStr = sim + "";
////                        String fileStr = flag2 + "\t\t\t" + simStr;
////                        FileIO.appendFile("public/sim_record_word2vec.txt", fileStr);
////
////                    }
////
////                }
////            }
////
////            long endTime=System.currentTimeMillis(); //获取结束时间
////            System.out.println("程序运行时间： "+(endTime-startTime)+"ms");
////
////            System.out.println("当前计算完毕文章数量为： " + count);
////            count += 1;
////            // 当前查询文本相似度排序
////
////
////        }
//
//
//
//    }
//
//    /**
//     * 文本特征提取(关键词提取)
//     */
//    public static List<String> featureExtraction(String content, int size) {
//        List<String> keywordsList = TextRank.getKeywordList(content, size, 2);
//        return keywordsList;
//    }
//
//
//
//    /**
//     * Word2Vec获取词语相似度
//     */
//    public static float getWordSimilarity(String word1, String word2) {
//        return vec.wordSimilarity(word1, word2);
//    }
//
//    /**
//     * Word2Vec获取文章间语义相似度(借助文章关键词)
//     */
//    public static float getTextSimilarity(List<String> keywordsList1, List<String> keywordsList2) {
//        return vec.sentenceSimilarity(keywordsList1, keywordsList2);
//    }



}
