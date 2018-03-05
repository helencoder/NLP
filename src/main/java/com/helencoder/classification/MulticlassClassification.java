package com.helencoder.classification;

import org.apache.spark.SparkConf;
import org.apache.spark.SparkContext;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.mllib.classification.LogisticRegressionModel;
import org.apache.spark.mllib.classification.LogisticRegressionWithLBFGS;
import org.apache.spark.mllib.evaluation.MulticlassMetrics;
import org.apache.spark.mllib.feature.HashingTF;
import org.apache.spark.mllib.linalg.Matrix;
import org.apache.spark.mllib.regression.LabeledPoint;
import org.apache.spark.mllib.util.MLUtils;
import scala.Tuple2;

import java.util.Arrays;

/**
 * 多类分类
 *
 * Created by helencoder on 2017/11/3.
 */
public class MulticlassClassification {

    /**
     * 创建SparkContext
     */
    private static String master = "local[*]";
    private static SparkConf conf = new SparkConf()
            .setAppName(MulticlassClassification.class.getName())
            .setMaster(master);
    private static JavaSparkContext sc = new JavaSparkContext(conf);

    public static void main(String[] args) {
        // 加载训练数据
        String label0FilePath = "data/mllib/classification/labeledNews/0.txt";
        String label1FilePath = "data/mllib/classification/labeledNews/1.txt";
        String label2FilePath = "data/mllib/classification/labeledNews/2.txt";
        JavaRDD<String> label0Data = sc.textFile(label0FilePath);
        JavaRDD<String> label1Data = sc.textFile(label1FilePath);
        JavaRDD<String> label2Data = sc.textFile(label2FilePath);

        // 创建一个HashingTF实例来把邮件文本映射为包含10000个特征的向量
        final HashingTF tf = new HashingTF(10000);

        // 训练数据向量化
        JavaRDD<LabeledPoint> label0RDD = label0Data.map(
                new Function<String, LabeledPoint>() {
                    @Override
                    public LabeledPoint call(String file) throws Exception {
                        return new LabeledPoint(0, tf.transform(Arrays.asList(file.split(" "))));
                    }
                }
        );
        JavaRDD<LabeledPoint> label1RDD = label1Data.map(
                new Function<String, LabeledPoint>() {
                    @Override
                    public LabeledPoint call(String file) throws Exception {
                        return new LabeledPoint(1, tf.transform(Arrays.asList(file.split(" "))));
                    }
                }
        );
        JavaRDD<LabeledPoint> label2RDD = label2Data.map(
                new Function<String, LabeledPoint>() {
                    @Override
                    public LabeledPoint call(String file) throws Exception {
                        return new LabeledPoint(2, tf.transform(Arrays.asList(file.split(" "))));
                    }
                }
        );

        JavaRDD<LabeledPoint> trainData = label0RDD.union(label1RDD).union(label2RDD);
        trainData.cache();  // 缓存训练数据RDD

        // Split initial RDD into two... [60% training data, 40% testing data].
        JavaRDD<LabeledPoint>[] splits = trainData.randomSplit(new double[]{0.8, 0.2}, 11L);
        JavaRDD<LabeledPoint> training = splits[0].cache();
        JavaRDD<LabeledPoint> test = splits[1];

        // Run training algorithm to build the model.
        LogisticRegressionModel model = new LogisticRegressionWithLBFGS()
                .setNumClasses(3)
                .run(training.rdd());

        // Compute raw scores on the test set.
        JavaPairRDD<Object, Object> predictionAndLabels = test.mapToPair(p ->
                new Tuple2<>(model.predict(p.features()), p.label()));

        // Get evaluation metrics.
        MulticlassMetrics metrics = new MulticlassMetrics(predictionAndLabels.rdd());

        // Confusion matrix
        Matrix confusion = metrics.confusionMatrix();
        System.out.println("Confusion matrix: \n" + confusion);

        // Overall statistics
        //System.out.println("Accuracy = " + metrics.accuracy());

        // Stats by labels
        for (int i = 0; i < metrics.labels().length; i++) {
            System.out.format("Class %f precision = %f\n", metrics.labels()[i],metrics.precision(
                    metrics.labels()[i]));
            System.out.format("Class %f recall = %f\n", metrics.labels()[i], metrics.recall(
                    metrics.labels()[i]));
            System.out.format("Class %f F1 score = %f\n", metrics.labels()[i], metrics.fMeasure(
                    metrics.labels()[i]));
        }

        //Weighted stats
        System.out.format("Weighted precision = %f\n", metrics.weightedPrecision());
        System.out.format("Weighted recall = %f\n", metrics.weightedRecall());
        System.out.format("Weighted F1 score = %f\n", metrics.weightedFMeasure());
        System.out.format("Weighted false positive rate = %f\n", metrics.weightedFalsePositiveRate());

        // Save and load model
        //model.save(sc, "target/tmp/LogisticRegressionModel");
        //LogisticRegressionModel sameModel = LogisticRegressionModel.load(sc,"target/tmp/LogisticRegressionModel");
        // $example off$

        sc.stop();
    }
}
