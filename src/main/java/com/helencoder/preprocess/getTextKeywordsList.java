package com.helencoder.preprocess;

import com.helencoder.textrank.TextRank;
import com.helencoder.textrank.Word;

import java.util.List;

/**
 * 获取文本关键词列表
 *
 * Created by helencoder on 2017/7/31.
 */
public class getTextKeywordsList {

    /**
     * TextRank获取文本关键词(包含词性、权重)
     *
     * @param content 文本内容
     * @param size 获取关键词个数
     * @return 关键词list
     */
    public static List<Word> run(String content, int size) {
        TextRank textRank = new TextRank();
        List<Word> keywordsList = textRank.getKeywordsList(content, size, 2);
        return keywordsList;
    }

}
