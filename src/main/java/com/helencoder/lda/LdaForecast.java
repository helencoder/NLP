package com.helencoder.lda;

/**
 * LDA模型预测
 *
 * Created by helencoder on 2017/10/24.
 */
public class LdaForecast {
    public static void main(String[] args) {
        LDACmdOption ldaOption = new LDACmdOption();
        ldaOption.inf = true;
        ldaOption.estc = false;
        ldaOption.dir = "models/chinese";
        ldaOption.modelName = "model-final";
        ldaOption.dfile = "predict.txt";
        Inferencer inferencer = new Inferencer();
        inferencer.init(ldaOption);
        Model newModel = inferencer.inference();
    }
}
