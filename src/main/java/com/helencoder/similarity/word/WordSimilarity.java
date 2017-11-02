package com.helencoder.similarity.word;

import com.helencoder.similarity.Cilin;
import com.thirdparty.word2vec.Word2Vec;

import java.io.IOException;

/**
 * 单词语义相似度计算
 *  1、《知网》语义计算
 *  2、《同义词词林》语义相似度计算
 *  3、Word2Vec语义相似度计算
 *
 * Created by helencoder on 2017/9/18.
 */
public class WordSimilarity {
    public static void main(String[] args) {
        // 测试用例

        String word1 = "美丽";
        String word2 = "漂亮";

        System.out.println(howNet(word1, word2));
        System.out.println(cilin(word1, word2));
        //System.out.println(word2vec(word1, word2));

    }

    /**
     * 《知网》词语语义相似度计算
     *
     * @param word1 单词1
     * @param word2 单词2
     *
     * @return 语义相似度
     */
    public static double howNet(String word1, String word2) {
        com.thirdparty.hownet.WordSimilarity.loadGlossary();
        return com.thirdparty.hownet.WordSimilarity.simWord(word2, word1);
    }

    /**
     * 《同义词词林》词语语义相似度计算
     *
     * @param word1 单词1
     * @param word2 单词2
     *
     * @return 语义相似度
     */
    public static double cilin(String word1, String word2) {
        Cilin cilin = new Cilin();
        return cilin.wordSimilarity(word1, word2);
    }

    /**
     * Word2Vec词语相关性计算
     *
     * @param word1 单词1
     * @param word2 单词2
     *
     * @return 词语相关性
     */
    public static double word2vec(String word1, String word2) {
        Word2Vec vec = new Word2Vec();
        try {
            vec.loadGoogleModel("data/dict/wiki_chinese_word2vec(Google).model");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return vec.wordSimilarity("计算机", "电脑");
    }

}

