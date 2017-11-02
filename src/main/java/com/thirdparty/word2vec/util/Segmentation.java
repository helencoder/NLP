package com.thirdparty.word2vec.util;

import com.hankcs.hanlp.HanLP;
import com.hankcs.hanlp.seg.Segment;
import com.thirdparty.word2vec.tokenizer.Tokenizer;

/**
 * 分词组件
 *
 * Created by helencoder on 2017/11/2.
 */
public class Segmentation {
    public void split(String inputFile, String splitFile) {
        // 分词组件
        Segment segment = HanLP.newSegment().enableAllNamedEntityRecognize(true)
                .enableCustomDictionary(true).enableMultithreading(4);
        HanLP.Config.ShowTermNature = false;// 关闭词性标注

        // 文本分词
        Tokenizer.fileSegment(segment, inputFile, splitFile);
    }
}
