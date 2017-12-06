package com.helencoder.lda;

import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;

/**
 * 基于JGibbLDA重构,实现动态数据加载与输出
 *
 * Created by helencoder on 2017/12/6.
 */
public class LDA {
    public static void main(String[] args) {
        // 设置LDA参数
        LDAOption ldaOption = new LDAOption();
        ldaOption.est = true;
        ldaOption.alpha = 0.2;
        ldaOption.beta = 0.1;
        ldaOption.K = 10;
        ldaOption.niters = 2000;
        ldaOption.savestep = 200;
        ldaOption.dir = "models/chinese";
        ldaOption.dfile = "predict.txt";
        ldaOption.modelName = "model-final";

        try {
            if (args.length != 0) { // 命令行参数配置解析更新
                CmdLineParser parser = new CmdLineParser(ldaOption);
                parser.parseArgument(args);
            }

            if (ldaOption.est || ldaOption.estc) {    // LDA模型训练
                Estimator estimator = new Estimator();
                estimator.init(ldaOption);
                estimator.estimate();
            } else if (ldaOption.inf) { // LDA模型推断

            }

        } catch (CmdLineException ex) {
            ex.printStackTrace();
        }

    }
}
