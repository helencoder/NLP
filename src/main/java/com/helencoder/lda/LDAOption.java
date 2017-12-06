package com.helencoder.lda;

import org.kohsuke.args4j.Option;

/**
 * LDA options
 *
 * 参数说明
 * 	#-est 此参数用于初次训练LDA模型;
 * 	#-estc:此参数用于从已有的模型当中训练LDA模型;
 * 	#-inf: 此参数用于对新的文档进行测试;
 * 	#-model <string>: 已有的模型的名称;
 * 	#-alpha <double>: α超参数，默认是50/K，K为主题数量;
 * 	#-beta <double>: β超参数，默认为0.1;
 * 	#-ntopics <int>: 主题数量，默认100;
 * 	#-niters <int>: Gibbs抽样迭代次数，默认为2000;
 * 	#-savestep <int>: 保持模型的迭代次数，即每迭代多少次将保持一次结果模型。默认为200;
 * 	#-twords <int>: 每个主题最可能的单词数量。默认为0，如果设定大于0，比如20，JGibbLDA将会根据此参数在每次保持模型时为每个主题打印出最可能的20个词;
 * 	#-dir <string>: 输入词集文件夹目录;此目录也是模型结果输出目录
 * 	#-dfile <string>: 输入词集文件名称
 *
 * Created by helencoder on 2017/12/6.
 */
public class LDAOption {

    @Option(name="-est", usage="Specify whether we want to estimate model from scratch")
    public boolean est = false;

    @Option(name="-estc", usage="Specify whether we want to continue the last estimation")
    public boolean estc = false;

    @Option(name="-inf", usage="Specify whether we want to do inference")
    public boolean inf = true;

    @Option(name="-dir", usage="Specify directory")
    public String dir = "data/models/";

    @Option(name="-dfile", usage="Specify data file")
    public String dfile = "newdocs.dat";

    @Option(name="-model", usage="Specify the model name")
    public String modelName = "model-01000";

    @Option(name="-alpha", usage="Specify alpha")
    public double alpha = 0.2;

    @Option(name="-beta", usage="Specify beta")
    public double beta = 0.1;

    @Option(name="-ntopics", usage="Specify the number of topics")
    public int K = 100;

    @Option(name="-niters", usage="Specify the number of iterations")
    public int niters = 2000;

    @Option(name="-savestep", usage="Specify the number of steps to save the model since the last save")
    public int savestep = 200;

    @Option(name="-twords", usage="Specify the number of most likely words to be printed for each topic")
    public int twords = 100;

    @Option(name="-withrawdata", usage="Specify whether we include raw data in the input")
    public boolean withrawdata = false;

    @Option(name="-wordmap", usage="Specify the wordmap file")
    public String wordMapFileName = "wordmap.txt";
}
