package com.helencoder.classification;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.api.java.function.PairFunction;
import org.apache.spark.mllib.classification.LogisticRegressionModel;
import org.apache.spark.mllib.classification.LogisticRegressionWithLBFGS;
import org.apache.spark.mllib.classification.LogisticRegressionWithSGD;
import org.apache.spark.mllib.feature.HashingTF;
import org.apache.spark.mllib.linalg.Vector;
import org.apache.spark.mllib.regression.LabeledPoint;
import scala.Tuple2;

import java.util.Arrays;

/**
 * 二分类
 *
 * Created by helencoder on 2017/11/3.
 */
public class BinaryclassClassification {
    // 测试函数
    public static void main(String[] args) {

        String labelFilePath = "label.txt";
        String noiseFilePath = "noise.txt";
        String testFilePath = "test.txt";
        new BinaryclassClassification().classification(labelFilePath, noiseFilePath, testFilePath);
    }

    /**
     * 创建SparkContext
     *
     */
    private static String master = "local[*]";
    private static SparkConf conf = new SparkConf()
            .setAppName(BinaryclassClassification.class.getName())
            .setMaster(master);
    private static JavaSparkContext sc = new JavaSparkContext(conf);

    /**
     * 分类器应用示例
     *
     */
    public void classification(String labelFilePath, String noiseFilePath, String testFilePath) {
        JavaRDD<String> label = sc.textFile(labelFilePath);
        JavaRDD<String> noise = sc.textFile(noiseFilePath);

        // 创建一个HashingTF实例来把邮件文本映射为包含10000个特征的向量
        final HashingTF tf = new HashingTF(10000);

        // 创建LabelPoint数据集分别存放阳性和阴性的例子
        JavaRDD<LabeledPoint> posExamples = label.map(
                new Function<String, LabeledPoint>() {
                    @Override
                    public LabeledPoint call(String file) throws Exception {
                        return new LabeledPoint(1, tf.transform(Arrays.asList(file.split(" "))));
                    }
                }
        );
        JavaRDD<LabeledPoint> negExamples = noise.map(
                new Function<String, LabeledPoint>() {
                    @Override
                    public LabeledPoint call(String file) throws Exception {
                        return new LabeledPoint(0, tf.transform(Arrays.asList(file.split(" "))));
                    }
                }
        );
        JavaRDD<LabeledPoint> trainData = posExamples.union(negExamples);
        trainData.cache();  // 缓存训练数据RDD

        // 使用SGD算法运行逻辑回归
        // LogisticRegressionModel model = new LogisticRegressionWithSGD().run(trainData.rdd());
        // 使用LBFGS算法运行逻辑回归
        LogisticRegressionModel model = new LogisticRegressionWithLBFGS().run(trainData.rdd());

        // 以阳性和阴性的例子分别进行测试
        JavaRDD<String> testRDD = sc.textFile(testFilePath);
        testRDD.cache();
        // 过滤操作,只保留预测positive的样本
        JavaRDD<String> testFilterRDD = testRDD.filter(
                new Function<String, Boolean>() {
                    @Override
                    public Boolean call(String file) throws Exception {
                        return model.predict(tf.transform(Arrays.asList(file.split(" ")))) == 1.0 ? true: false;
                    }
                }
        );
        // 转化为键值对,文本为键，预测结果为键值
        JavaPairRDD<String, Boolean> testMapRDD = testRDD.mapToPair(
                new PairFunction<String, String, Boolean>() {
                    @Override
                    public Tuple2<String, Boolean> call(String file) throws Exception {
                        return new Tuple2(file, model.predict(tf.transform(Arrays.asList(file.split(" ")))) == 1.0 ? true: false);
                    }
                }
        );

        // 控制台输出结果
        testMapRDD.foreach(result -> System.out.println("当前文本: " + result._1 + "\n其预测结果为: " + result._2));

    }


    /**
     * 垃圾邮件分类
     */
    public void emailClassification() {
        JavaRDD<String> spam = sc.textFile("spam.txt");
        JavaRDD<String> normal = sc.textFile("normal.txt");

        // 创建一个HashingTF实例来把邮件文本映射为包含10000个特征的向量
        final HashingTF tf = new HashingTF(10000);

        // 创建LabelPoint数据集分别存放阳性(垃圾邮件)和阴性(正常邮件)的例子
        JavaRDD<LabeledPoint> posExamples = spam.map(
                new Function<String, LabeledPoint>() {
                    @Override
                    public LabeledPoint call(String email) throws Exception {
                        return new LabeledPoint(1, tf.transform(Arrays.asList(email.split(" "))));
                    }
                }
        );
        JavaRDD<LabeledPoint> negExamples = normal.map(
                new Function<String, LabeledPoint>() {
                    @Override
                    public LabeledPoint call(String email) throws Exception {
                        return new LabeledPoint(0, tf.transform(Arrays.asList(email.split(" "))));
                    }
                }
        );
        JavaRDD<LabeledPoint> trainData = posExamples.union(negExamples);
        trainData.cache();  // 缓存训练数据RDD

        // 使用SGD算法运行逻辑回归
        LogisticRegressionModel model = new LogisticRegressionWithSGD().run(trainData.rdd());

        // 以阳性(垃圾邮件)和阴性(正常邮件)的例子分别进行测试
        Vector posTest = tf.transform(Arrays.asList("O M G GET cheap stuff buy sending money to ...".split(" ")));
        Vector negTest = tf.transform(Arrays.asList("Hi Dad, I started studying Spark the other ...".split(" ")));
        Vector noiseTest = tf.transform(Arrays.asList("helen, You should work harder ...".split(" ")));
        System.out.println("Prediction for positive example: " + model.predict(posTest));
        System.out.println("Prediction for negative example: " + model.predict(negTest));
        System.out.println("Prediction for negative example: " + model.predict(noiseTest));

    }
}
