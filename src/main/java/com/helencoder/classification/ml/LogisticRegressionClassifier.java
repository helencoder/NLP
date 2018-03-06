package com.helencoder.classification.ml;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.mllib.classification.LogisticRegressionWithLBFGS;
import org.apache.spark.mllib.evaluation.MulticlassMetrics;
import org.apache.spark.mllib.feature.HashingTF;
import org.apache.spark.mllib.linalg.Matrix;
import org.apache.spark.mllib.regression.LabeledPoint;
import scala.Tuple2;

import java.util.Arrays;

/**
 * Logistic Regression
 *
 * Created by helencoder on 2018/3/5.
 */
public class LogisticRegressionClassifier {

    /**
     * 创建SparkContext
     */
    private static String master = "local[*]";
    private static SparkConf conf = new SparkConf()
            .setAppName(LogisticRegressionClassifier.class.getName())
            .setMaster(master);
    private static JavaSparkContext sc = new JavaSparkContext(conf);

    public static void main(String[] args) {
        JavaRDD<LabeledPoint> trainData = DataSet.loadTrainSet(sc);

        // Split initial RDD into two... [60% training data, 40% testing data].
        JavaRDD<LabeledPoint>[] splits = trainData.randomSplit(new double[]{0.8, 0.2}, 11L);
        JavaRDD<LabeledPoint> training = splits[0].cache();
        JavaRDD<LabeledPoint> test = splits[1];

        // Run training algorithm to build the model.
        org.apache.spark.mllib.classification.LogisticRegressionModel model = new LogisticRegressionWithLBFGS()
                .setNumClasses(2)
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
