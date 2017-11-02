package com.helencoder.similarity.text;

import com.helencoder.preprocess.getTextWordsFrequency;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * 欧几里得距离 文本相似性
 *  欧几里得距离（Euclidean Distance），通过计算两点间的距离来评估他们的相似度
 *  欧几里得距离原理：
 *      设A(x1, y1)，B(x2, y2)是平面上任意两点
 *      两点间的距离dist(A,B)=sqrt((x1-x2)^2+(y1-y2)^2)
 *
 *  tips:用词频标注词的权重，作为单词对应的坐标值
 *
 * Created by helencoder on 2017/7/31.
 */
public class EuclideanDistance {
    public static void main(String[] args) {
        // 测试用例

    }

    /**
     * 欧几里得距离 文本相似性
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

        // 计算欧几里得距离
        Iterator<Integer> iterator1 = wordsFrquency1.iterator();
        Iterator<Integer> iterator2 = wordsFrquency2.iterator();
        float score = 0.0f;
        while (iterator1.hasNext() && iterator2.hasNext()) {
            int a = iterator1.next();
            int b = iterator2.next();
            float x = (float) Math.pow((a - b), 2);
            score += x;
        }
        float sim = (float)Math.sqrt(score);
        return sim;

    }

}
