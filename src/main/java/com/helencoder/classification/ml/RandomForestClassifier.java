package com.helencoder.classification.ml;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.mllib.regression.LabeledPoint;
import org.apache.spark.mllib.tree.RandomForest;
import org.apache.spark.mllib.tree.model.RandomForestModel;
import scala.Tuple2;

import java.util.HashMap;
import java.util.Map;

/**
 * Random Forest
 *
 * Created by helencoder on 2018/3/6.
 */
public class RandomForestClassifier {

    /**
     * 创建SparkContext
     */
    private static String master = "local[*]";
    private static SparkConf conf = new SparkConf()
            .setAppName(RandomForestClassifier.class.getName())
            .setMaster(master);
    private static JavaSparkContext sc = new JavaSparkContext(conf);

    public static void main(String[] args) {
        JavaRDD<LabeledPoint> trainData = DataSet.loadTrainSet(sc);

        // Split the data into training and test sets (30% held out for testing)
        JavaRDD<LabeledPoint>[] splits = trainData.randomSplit(new double[]{0.8, 0.2});
        JavaRDD<LabeledPoint> trainingData = splits[0];
        JavaRDD<LabeledPoint> testData = splits[1];

        // Train a RandomForest model.
        // Empty categoricalFeaturesInfo indicates all features are continuous.
        Integer numClasses = 2;
        Map<Integer, Integer> categoricalFeaturesInfo = new HashMap<>();
        Integer numTrees = 3; // Use more in practice.
        String featureSubsetStrategy = "auto"; // Let the algorithm choose.
        String impurity = "gini";
        Integer maxDepth = 5;
        Integer maxBins = 32;
        Integer seed = 12345;

        RandomForestModel model = RandomForest.trainClassifier(trainingData, numClasses,
                categoricalFeaturesInfo, numTrees, featureSubsetStrategy, impurity, maxDepth, maxBins,
                seed);

        // Evaluate model on test instances and compute test error
        JavaPairRDD<Double, Double> predictionAndLabel =
                testData.mapToPair(p -> new Tuple2<>(model.predict(p.features()), p.label()));
        predictionAndLabel.foreach(r -> {
            System.out.println(r._1() + "\t" + r._2());
        });
        double testErr =
                predictionAndLabel.filter(pl -> !pl._1().equals(pl._2())).count() / (double) testData.count();
        System.out.println("Test Error: " + testErr);
        System.out.println("Learned classification forest model:\n" + model.toDebugString());

        // Save and load model
//        model.save(sc.sc(), "target/tmp/myRandomForestClassificationModel");
//        RandomForestModel sameModel = RandomForestModel.load(sc.sc(), "target/tmp/myRandomForestClassificationModel");
        // $example off$

        sc.stop();

    }
}
