package com.helencoder.preprocess;

import com.hankcs.hanlp.seg.common.Term;
import com.helencoder.segmentation.Segmentation;
import com.helencoder.util.FileIO;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * 获取文本分词列表
 *
 * Created by helencoder on 2017/7/31.
 */
public class getTextSegWordsList {

    /**
     * 获取文章分词结果list
     * @param filepath 带分词的文本路径
     * @return wordsList 分词结果list
     */
    public static List<String> run(String filepath) throws FileNotFoundException {
        File file = new File(filepath);
        if (!file.exists()) {
            throw new FileNotFoundException(filepath + "\t文件不存在");
        }

        Segmentation segmention = new Segmentation();

        List<String> wordsList = new ArrayList<String>();
        String content = FileIO.getFileData(filepath);
        // 分词处理
        List<Term> termList = segmention.segToList(content, false);
        // 词语过滤

        Iterator iterator1 = termList.iterator();
        while (iterator1.hasNext()) {
            Term t = (Term)iterator1.next();
            // 词语过滤(词性,停用词,词长)
            if (segmention.isWordAllow(t)) {
                wordsList.add(t.word.toString());
            }
        }

        return wordsList;
    }

}
