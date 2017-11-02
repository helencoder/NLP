package com.helencoder.similarity.text;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Jaccard相似性系数 文本相似性
 *  通过计算两个集合交集的大小除以并集的大小来评估他们的相似度
 *
 * Created by helencoder on 2017/7/31.
 */
public class JaccardSimilarity {
    public static void main(String[] args) {
        // 测试用例

    }

    /**
     *
     */
    public static float run(List<String> wordsList1, List<String> wordsList2) {
        if (wordsList1.isEmpty() && wordsList2.isEmpty()) {
            return 1.0f;
        }
        //转变为不重复的集合
        Set<String> wordsSet1 = new HashSet<String>(wordsList1);
        Set<String> wordsSet2 = new HashSet<String>(wordsList2);

        // 求交集（去重），计算交集的不重复词的个数
        wordsSet1.retainAll(wordsSet2);
        int intersectionSize = wordsSet1.size();

        //求并集
        Set<String> unionSet = new HashSet<>();
        wordsList1.forEach(word -> unionSet.add(word));
        wordsList2.forEach(word -> unionSet.add(word));
        //并集的大小
        int unionSize = unionSet.size();
        //相似度分值
        float sim = intersectionSize / (float) unionSize;
        return sim;
    }

}
