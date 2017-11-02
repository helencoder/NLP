package com.helencoder.similarity.sentence;

import com.helencoder.similarity.word.WordSimilarity;

import java.util.List;
import java.util.Map;

/**
 * 句子相似度计算
 *
 * Created by helencoder on 2017/9/18.
 */
public class SentenceSimilarity {
    public static void main(String[] args) {
        // 测试用例

        String sentence1 = "事发后，伤员被及时送往就近医院救治。";
        String sentence2 = "晚上7时左右，所有伤员被送到了医院。";

        System.out.println(calSentenceSimilrity(sentence1, sentence2));

        String sentence3 = "有哪些好的技术类博客？";
        String sentence4 = "有哪些值得关注的技术博客";

        System.out.println(calSentenceSimilrity(sentence3, sentence4));
    }

    /**
     * 计算句子相似度(有效匹配对)
     *
     * @param sentence1 句子1
     * @param sentence2 句子2
     *
     */
    public static double calSentenceSimilrity(String sentence1, String sentence2) {
        Map<String, List<String>> sentence1DetailsMap = Preprocess.getDetails(sentence1);
        Map<String, List<String>> sentence2DetailsMap = Preprocess.getDetails(sentence2);

        // 有效匹配对、命名实体、优化分词、过滤分词占比依次为0.4、0.2、0.2、0.2
        double[] weights = {0.4, 0.2, 0.2, 0.2};

        // 有效匹配对相似度计算
        double sim1 = effectivePairsListSimilarity(sentence1DetailsMap.get("effectivePairsList"),
                sentence2DetailsMap.get("effectivePairsList"), "");
        // 命名实体相似度计算
        double sim2 = namedEntityListSimilarity(sentence1DetailsMap.get("namedEntityList"),
                sentence2DetailsMap.get("namedEntityList"));
        // 优化分词相似度计算
        double sim3 = optimisedWordsListSimilarity(sentence1DetailsMap.get("optimisedWordsList"),
                sentence2DetailsMap.get("optimisedWordsList"));
        // 过滤分词相似度计算
        double sim4 = filterWordsListSimilarity(sentence1DetailsMap.get("filterWordsList"),
                sentence2DetailsMap.get("filterWordsList"));

        System.out.println("有效匹配对相似度: " + sim1);
        System.out.println("命名实体相似度: " + sim2);
        System.out.println("优化分词相似度: " + sim3);
        System.out.println("过滤分词相似度: " + sim4);

        return (weights[0] * sim1 + weights[1] * sim2 + weights[2] * sim3 + weights[3] * sim4);
    }

    /**
     * 有效匹配对列表相似度计算
     *
     * @param pairs1 有效匹配对1
     * @param pairs2 有效匹配对2
     * @param flag "hownet" 《知网》计算, 否则,《同义词词林》计算
     *
     * @return double 有效匹配对列表相似度
     */
    private static double effectivePairsListSimilarity(List<String> pairs1, List<String> pairs2, String flag) {
        if (pairs1.isEmpty() || pairs2.isEmpty()) {
            return 0;
        }
        double partUp = 0;
        for (String outPair : pairs1) {
            for (String innerPair : pairs2) {
                if (flag.equals("hownet")) {
                    partUp += calPairsWeightWithHownet(outPair, innerPair);
                } else {
                    partUp += calPairsWeightWithCilin(outPair, innerPair);
                }
            }
        }
        return partUp / (pairs1.size() * pairs2.size());
    }

    /**
     * 命名实体相似度计算
     */
    private static double namedEntityListSimilarity(List<String> list1, List<String> list2) {
        if (list1.isEmpty() || list2.isEmpty()) {
            return 0;
        }
        double partUp = 0;
        for (String outerWord : list1) {
            for (String innerWord : list2) {
                if (outerWord.equals(innerWord)) {
                    partUp += 1;
                }
            }
        }
        return partUp / (list1.size() * list2.size());
    }

    /**
     * 优化分词相似度计算
     */
    private static double optimisedWordsListSimilarity(List<String> list1, List<String> list2) {
        if (list1.isEmpty() || list2.isEmpty()) {
            return 0;
        }
        double partUp = 0;
        for (String outerWord : list1) {
            for (String innerWord : list2) {
                if (outerWord.equals(innerWord)) {
                    partUp += 1;
                }
            }
        }
        return partUp / (list1.size() * list2.size());
    }

    /**
     * 过滤分词相似度计算
     */
    private static double filterWordsListSimilarity(List<String> list1, List<String> list2) {
        if (list1.isEmpty() || list2.isEmpty()) {
            return 0;
        }
        double partUp = 0;
        for (String outerWord : list1) {
            for (String innerWord : list2) {
                if (outerWord.equals(innerWord)) {
                    partUp += 1;
                }
            }
        }
        return partUp / (list1.size() * list2.size());
    }

    /**
     * 有效匹配对权重计算(知网)
     *
     * @param pairs1 有效匹配对1
     * @param pairs2 有效匹配对2
     */
    private static double calPairsWeightWithHownet(String pairs1, String pairs2) {
        if (pairs1.equals(pairs2)) {
            return 1;
        }
        String pairs1CoreWord = pairs1.split("_")[0];
        String pairs1ConcatWord = pairs1.split("_")[1];
        String pairs2CoreWord = pairs2.split("_")[0];
        String pairs2ConcatWord = pairs2.split("_")[1];

        // 利用《知网》进行语义计算
        double sim1 = WordSimilarity.howNet(pairs1CoreWord, pairs2CoreWord);
        double sim2 = WordSimilarity.howNet(pairs1ConcatWord, pairs2ConcatWord);

        return 0.5 * (sim1 + sim2);
    }

    /**
     * 有效匹配对权重计算(同义词词林)
     *
     * @param pairs1 有效匹配对1
     * @param pairs2 有效匹配对2
     */
    private static double calPairsWeightWithCilin(String pairs1, String pairs2) {
        if (pairs1.equals(pairs2)) {
            return 1;
        }
        String pairs1CoreWord = pairs1.split("_")[0];
        String pairs1ConcatWord = pairs1.split("_")[1];
        String pairs2CoreWord = pairs2.split("_")[0];
        String pairs2ConcatWord = pairs2.split("_")[1];

        // 利用《同义词词林》进行语义计算
        double sim1 = WordSimilarity.cilin(pairs1CoreWord, pairs2CoreWord);
        double sim2 = WordSimilarity.cilin(pairs1ConcatWord, pairs2ConcatWord);

        return 0.5 * (sim1 + sim2);
    }

}
