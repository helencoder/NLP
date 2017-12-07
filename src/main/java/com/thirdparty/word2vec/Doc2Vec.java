package com.thirdparty.word2vec;

import com.helencoder.util.FileIO;
import com.thirdparty.word2vec.domain.Neuron;
import com.thirdparty.word2vec.util.Learn;
import com.thirdparty.word2vec.util.LearnDoc2Vec;

import java.io.File;
import java.util.Map;

/**
 * Doc2Vec
 *
 * Created by helencoder on 2017/12/7.
 */
public class Doc2Vec {
    public static void main(String[] args) throws Exception {
        File result = new File("data/doc2vec/clinicalcases.txt");

        Learn learn = new Learn();

        // 训练词向量
        learn.learnFile(result);

        // 得到训练完的词向量，训练文本向量

        Map<String, Neuron> word2vec_model = learn.getWordMap();

        LearnDoc2Vec learn_doc = new LearnDoc2Vec(word2vec_model);

        learn_doc.learnFile(result);

        // 文本向量写文件

        Map<Integer, float[]> docVec = learn_doc.getDocVector();

        StringBuilder sb = new StringBuilder();

        for (int doc_no : docVec.keySet()) {

            StringBuilder doc = new StringBuilder("sent_" + doc_no + " ");

            float[] vector = docVec.get(doc_no);

            for (float e : vector) {

                doc.append(e + " ");
            }
            sb.append(doc.toString().trim() + "\n");

        }
        FileIO.writeFile("data/doc2vec/clinical_doc_200_java.vec", sb.toString());

    }
}
