package com.helencoder.preprocess;

import com.helencoder.textrank.Word;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * 获取文本单词词频
 *
 * Created by helencoder on 2017/7/31.
 */
public class getTextWordsFrequency {

    /**
     * 获取文本单词词频
     * @param wordsList 单词list
     * @param content 文本内容
     * @return 单词list对应的词频list
     */
    public static List<Integer> run(Set<String> wordsList, String content) {
        List<Integer> vector = new ArrayList<Integer>();

        for (String word : wordsList) {
            String longStr = content;

            // 字符统计
            int count = 0;
            // 调用String类的indexOf(String str)方法，返回第一个相同字符串出现的下标
            while (longStr.contains(word)) {
                // 如果存在相同字符串则次数加1
                count++;
                // 调用String类的substring(int beginIndex)方法，获得第一个相同字符出现后的字符串
                longStr = longStr.substring(longStr.indexOf(word) + word.length());
            }
            vector.add(count);
        }
        return vector;
    }

    /**
     * 获取文本单词词频(附加权值)
     *
     * @param wordsList 单词list
     * @param content 文本内容
     * @return 单词list对应的词频list
     */
    public static List<Float> runWithWeight(Set<Word> wordsList, String content) {
        List<Float> vector = new ArrayList<Float>();

        for (Word word : wordsList) {
            String longStr = content;

            // 字符统计
            int count = 0;
            // 调用String类的indexOf(String str)方法，返回第一个相同字符串出现的下标
            while (longStr.contains(word.word)) {
                // 如果存在相同字符串则次数加1
                count++;
                // 调用String类的substring(int beginIndex)方法，获得第一个相同字符出现后的字符串
                longStr = longStr.substring(longStr.indexOf(word.word) + word.length());
            }
            vector.add(count * word.weight);
        }
        return vector;
    }

}
