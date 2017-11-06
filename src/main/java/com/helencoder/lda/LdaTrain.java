package com.helencoder.lda;

/**
 * LDA模型训练
 *
 * Created by helencoder on 2017/10/24.
 */
public class LdaTrain {
    public static void main(String[] args) {
        LDACmdOption ldaOption = new LDACmdOption();
        // 是否开始训练模型
        ldaOption.est = true;
        // 是否是基于先前已有的模型基础上继续用新数据训练模型
        ldaOption.estc = false;
        // 是否使用先前已经训练好的模型进行推断
        //ldaOption.inf = true;
        // 数据结果（模型数据）保存位置
        ldaOption.dir = "models/chinese";
        // 训练数据或原始数据文件名
        ldaOption.dfile = "zixun.txt";
        // 选择使用哪一个迭代的模型结果来进行推断
        ldaOption.modelName = "model-final";
        // 平滑系数
        ldaOption.alpha = 0.5;
        ldaOption.beta = 0.1;
        // 类簇数目，谨慎设置
        ldaOption.K = 6;
        // 迭代数目，谨慎设置
        ldaOption.niters = 1000;
        // 指定把迭代结果模型保存到硬盘上的迭代跨度，即每迭代100次保存一次
        ldaOption.savestep = 100;
        // 对每一个类别（话题）选前多少个最大概率词项
        ldaOption.twords = 100;
        //ldaOption.withrawdata = false;
        // 生成的副产品的文件名
        //ldaOption.wordMapFileName = "";
        //topicNum = ldaOption.K;
        Estimator estimator = new Estimator();
        estimator.init(ldaOption);
        estimator.estimate();

    }
}
