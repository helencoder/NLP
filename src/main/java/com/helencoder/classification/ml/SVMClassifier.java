package com.helencoder.classification.ml;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.PairFunction;
import org.apache.spark.mllib.classification.SVMModel;
import org.apache.spark.mllib.classification.SVMWithSGD;
import org.apache.spark.mllib.evaluation.BinaryClassificationMetrics;
import org.apache.spark.mllib.regression.LabeledPoint;
import scala.Tuple2;

/**
 * SVM
 *
 * Created by helencoder on 2018/3/6.
 */
public class SVMClassifier {

    /**
     * 创建SparkContext
     */
    private static String master = "local[*]";
    private static SparkConf conf = new SparkConf()
            .setAppName(SVMClassifier.class.getName())
            .setMaster(master);
    private static JavaSparkContext sc = new JavaSparkContext(conf);

    public static void main(String[] args) {
        JavaRDD<LabeledPoint> trainData = DataSet.loadTrainSet(sc);

        // Split initial RDD into two... [60% training data, 40% testing data].
        JavaRDD<LabeledPoint> training = trainData.sample(false, 0.8, 11L);
        training.cache();
        JavaRDD<LabeledPoint> testData = trainData.subtract(training);

        // Run training algorithm to build the model.
        int numIterations = 100;
        SVMModel model = SVMWithSGD.train(training.rdd(), numIterations);

        // Clear the default threshold.
        model.clearThreshold();

        // Compute raw scores on the test set.
        JavaRDD<Tuple2<Object, Object>> scoreAndLabels = testData.map(p ->
                new Tuple2<>(model.predict(p.features()), p.label()));

        // Evaluate model on test instances and compute test error
//        JavaPairRDD<Double, Double> predictionAndLabel = testData.mapToPair(p -> new Tuple2<>(model.predict(p.features()), p.label()));

        JavaPairRDD<Double, Double> predictionAndLabel = testData.mapToPair(
                new PairFunction<LabeledPoint, Double, Double>() {
                    @Override
                    public Tuple2<Double, Double> call(LabeledPoint p) throws Exception {
                        double pLabel = model.predict(p.features()) > 0.5 ? 1.0 : 0.0;
                        return new Tuple2<>(pLabel, p.label());
                    }
                }
        );

        double testErr =
                predictionAndLabel.filter(pl -> !pl._1().equals(pl._2())).count() / (double) testData.count();
        System.out.println("Test Error: " + testErr);

        // Save and load model
//        model.save(sc, "target/tmp/javaSVMWithSGDModel");
//        SVMModel sameModel = SVMModel.load(sc, "target/tmp/javaSVMWithSGDModel");
        // $example off$

        sc.stop();
    }
}
