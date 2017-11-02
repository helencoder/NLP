package com.thirdparty.word2vec.domain;

/**
 * 隐藏神经元
 *
 * Created by helencoder on 2017/11/2.
 */
public class HiddenNeuron extends Neuron {
    public double[] syn1;// 隐藏层 -> 输出层

    public HiddenNeuron(int layerSize) {
        syn1 = new double[layerSize];
    }
}
