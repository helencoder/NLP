package com.helencoder.lda;

/**
 * LDA模型训练类
 *
 * Created by helencoder on 2017/12/6.
 */
public class Estimator {

    private Model trnModel;
    private LDAOption option;

    public boolean init(LDAOption option) {
        this.option = option;
        this.trnModel = new Model();

        if (option.est) {

        } else if (option.estc) {

        }

        return true;
    }

    public void estimate() {

    }

}
