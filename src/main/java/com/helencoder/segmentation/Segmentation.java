package com.helencoder.segmentation;

import com.hankcs.hanlp.HanLP;
import com.hankcs.hanlp.dictionary.stopword.CoreStopWordDictionary;
import com.hankcs.hanlp.seg.Segment;
import com.hankcs.hanlp.seg.common.Term;

import java.util.ArrayList;
import java.util.List;

/**
 * 分词组件
 *
 * Created by helencoder on 2017/10/25.
 */
public class Segmentation {
    private static Segment segment;
    private static String[] allowSpeechTags = {"d", "f", "g", "h", "i", "j", "k", "l", "n", "s", "t", "v", "x"};

    /**
     * 构造函数
     *
     * 初始化HanLp分词组件
     */
    public Segmentation() {
        if (segment == null) {
            segment = HanLP.newSegment()
                    .enableNameRecognize(true)
                    .enableOrganizationRecognize(true)
                    .enableNumberQuantifierRecognize(true)
                    .enablePlaceRecognize(true)
                    .enableTranslatedNameRecognize(true);
        }
    }

    /**
     * 分词、List形式输出结果
     *
     * @param content 文本内容
     * @param flag 是否进行词性过滤
     * @return List<Term>
     */
    public List<Term> segToList(String content, boolean flag) {
        List<Term> termList = segment.seg(content);
        List<Term> wordList = new ArrayList<Term>();
        for (Term term : termList) {
            if (flag) {
                if (this.isWordAllow(term)) {
                    wordList.add(term);
                }
            } else {
                wordList.add(term);
            }
        }

        return wordList;
    }

    /**
     * 分词、字符串形式输出结果
     *
     * @param content 文本内容
     * @param flag 是否进行词性过滤
     * @return String
     */
    public String segToStr(String content, String delimiter, boolean flag) {
        List<Term> termList = segment.seg(content);
        StringBuffer sb = new StringBuffer();
        for (Term term : termList) {
            if (flag) {
                if (this.isWordAllow(term)) {
                    sb.append(term.word);
                    sb.append(delimiter);
                }
            } else {
                sb.append(term.word);
                sb.append(delimiter);
            }
        }

        return sb.toString().substring(0, sb.toString().lastIndexOf(delimiter));
    }

    /**
     * 词性过滤
     *
     * @param term 单词
     * @return boolean
     */
    public boolean isWordAllow(Term term) {
        if (term.nature == null) {
            return false;
        } else {
            String nature = term.nature.toString();
            char firstChar = nature.charAt(0);
            boolean flag = false;
            // 词性过滤
            for (String tag: allowSpeechTags) {
                if (tag.charAt(0) == firstChar) {
                    flag = true;
                }
            }
            return flag && term.word.trim().length() > 1 && !CoreStopWordDictionary.contains(term.word);
        }
    }
}
