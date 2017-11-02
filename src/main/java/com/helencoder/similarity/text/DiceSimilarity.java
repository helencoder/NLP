package com.helencoder.similarity.text;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Dice系数 文本相似性
 * Sorensen–Dice系数（Sorensen–Dice coefficient），通过计算两个集合交集的大小的2倍除以两个集合的大小之和来评估他们的相似度
 *
 * Created by helencoder on 2017/7/31.
 */
public class DiceSimilarity {
    public static void main(String[] args) {
        // 测试用例


    }


    /**
     * 计算基于Dice系数的文本相似性
     * @param wordsList1 文本1的分词结果
     * @param wordsList2 文本2的分词结果
     * @return float 文本相似性结果
     */
    public static float run(List<String> wordsList1, List<String> wordsList2) {
        if (wordsList1.isEmpty() && wordsList2.isEmpty()) {
            return 1.0f;
        }
        //转变为不重复的集合
        Set<String> wordsSet1 = new HashSet<String>(wordsList1);
        Set<String> wordsSet2 = new HashSet<String>(wordsList2);
        // 两个集合的大小
        int setSize1 = wordsSet1.size();
        int setSize2 = wordsSet2.size();

        // 求交集（去重），计算交集的不重复词的个数
        wordsSet1.retainAll(wordsSet2);
        int intersectionSize = wordsSet1.size();

        //相似度分值
        float sim = 2 * intersectionSize / (float) (setSize1 + setSize2);
        return sim;
    }

}
