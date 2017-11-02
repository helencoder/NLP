package com.helencoder.preprocess;

import com.hankcs.hanlp.seg.common.Term;
import com.helencoder.segmentation.Segmentation;
import com.helencoder.util.FileIO;

import java.io.FileNotFoundException;
import java.util.Iterator;
import java.util.List;

/**
 * 文本预处理
 *
 * Created by helencoder on 2017/7/31.
 */
public class TextPreprocess {

    /**
     * 文本预处理
     *  文件逐行存储 (文件名 \t 分词结果)
     * @param path 原始文本文件存储路径
     * @param recordFilePath 预处理记录文件路径
     */
    public static void preprocessBySeg(String path, String recordFilePath) {
        try {
            Segmentation segmention = new Segmentation();
            List<String> fileList = FileIO.getFileList(path);
            for (String filename : fileList) {
                // 获取文本内容
                String filepath = path + "/" + filename;
                String content = FileIO.getFileData(filepath);
                // 分词处理
                List<Term> termList = segmention.segToList(content, false);

                // 词语记录
                String str = filename.replace(".txt", "") + "\t";

                // 词语过滤

                Iterator iterator = termList.iterator();
                while (iterator.hasNext()) {
                    Term t = (Term)iterator.next();
                    // 词语过滤(词性,停用词,词长)
                    if (segmention.isWordAllow(t)) {
                        String wordstr = t.word.toString() + " ";
                        str += wordstr ;
                    }
                }

                // 写入文件
                if (str.length() > 100) {
                    FileIO.appendFile(recordFilePath, str.trim());
                }

                System.out.println(filename);
            }
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * 文本预处理
     *  文件逐行存储 (文件名 \t 文本内容)
     * @param path 原始文本文件存储路径
     * @param recordFilePath 预处理记录文件路径
     */
    public static void preprocess(String path, String recordFilePath) {
        try {
            List<String> fileList = FileIO.getFileList(path);
            for (String filename : fileList) {
                // 获取文本内容
                String filepath = path + "/" + filename;
                String content = FileIO.getFileData(filepath);

                // 词语记录
                String str = filename.replace(".txt", "") + "\t" + content.replace("\n", "");

                // 写入文件
                if (str.length() > 100) {
                    FileIO.appendFile(recordFilePath, str.trim());
                }

                System.out.println(filename);
            }
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        }
    }

}
