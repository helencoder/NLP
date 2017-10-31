package com.helencoder;

import com.hankcs.hanlp.seg.common.Term;
import com.helencoder.segmentation.Segmentation;
import com.helencoder.textrank.Word;

import java.util.List;

/**
 * Created by helencoder on 2017/10/31.
 */
public class SegmentationTest {
    public static void main(String[] args) {
        String str = "传统的 hash 算法只负责将原始内容尽量均匀随机地映射为一个签名值，"
                + "原理上相当于伪随机数产生算法。产生的两个签名，如果相等，说明原始内容在一定概 率 下是相等的；"
                + "如果不相等，除了说明原始内容不相等外，不再提供任何信息，因为即使原始内容只相差一个字节，"
                + "所产生的签名也很可能差别极大。从这个意义 上来 说，要设计一个 hash 算法，"
                + "对相似的内容产生的签名也相近，是更为艰难的任务，因为它的签名值除了提供原始内容是否相等的信息外，"
                + "还能额外提供不相等的 原始内容的差异程度的信息。 鹏华环球发现，信诚岁岁添金,宏观经济,房产税,基本面,投保妙招";
        Segmentation segmentUtil = new Segmentation();
        List<Term> wordList = segmentUtil.segToList(str, true);
        float wordCount = 0;
        for (Term term : wordList) {
            //System.out.println(term.word + "\t" + term.nature + "\t" + term.getFrequency());
            wordCount += term.getFrequency();
        }

        System.out.println(wordCount);

        for (Term term : wordList) {
            System.out.println(term.word + "\t" + term.nature + "\t" + term.getFrequency());
        }
    }
}
