package com.helencoder.similarity.text;

import java.util.List;

/**
 * 编辑距离 文本相似性
 *  指两个字串之间，由一个转成另一个所需的最少编辑操作次数
 *  允许的编辑操作包括将一个字符替换成另一个字符，增加一个字符，删除一个字符
 *
 * Created by helencoder on 2017/7/31.
 */
public class EditDistanceSimilarity {
    public static void main(String[] args) {
        // 测试用例


    }

    /**
     * 编辑距离 文本相似性
     * @param wordsList1 文本1的分词结果
     * @param wordsList2 文本2的分词结果
     * @return float 文本相似性结果
     */
    public static float run(List<String> wordsList1, List<String> wordsList2) {
        //文本1
        StringBuilder text1 = new StringBuilder();
        wordsList1.forEach(word -> text1.append(word));
        //文本2
        StringBuilder text2 = new StringBuilder();
        wordsList2.forEach(word -> text2.append(word));
        int maxTextLength = Math.max(text1.length(), text2.length());
        if (maxTextLength == 0) {
            //两个空字符串
            return 1.0f;
        }
        //计算文本1和文本2的编辑距离
        int editDistance = editDistance(text1.toString(), text2.toString());
        float sim = (1 - editDistance / (float) maxTextLength);
        return sim;
    }

    /**
     * 编辑距离计算
     * @param text1
     * @param text2
     * @return 相似性结果
     */
    private static int editDistance(String text1, String text2) {
        int[] costs = new int[text2.length() + 1];
        for (int i = 0; i <= text1.length(); i++) {
            int previousValue = i;
            for (int j = 0; j <= text2.length(); j++) {
                if (i == 0) {
                    costs[j] = j;
                } else if (j > 0) {
                    int useValue = costs[j - 1];
                    if (text1.charAt(i - 1) != text2.charAt(j - 1)) {
                        useValue = Math.min(Math.min(useValue, previousValue), costs[j]) + 1;
                    }
                    costs[j - 1] = previousValue;
                    previousValue = useValue;

                }
            }
            if (i > 0) {
                costs[text2.length()] = previousValue;
            }
        }
        return costs[text2.length()];
    }

}
