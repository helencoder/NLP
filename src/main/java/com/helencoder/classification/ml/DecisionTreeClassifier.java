package com.helencoder.classification.ml;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.mllib.feature.HashingTF;
import org.apache.spark.mllib.regression.LabeledPoint;
import org.apache.spark.mllib.tree.DecisionTree;
import org.apache.spark.mllib.tree.model.DecisionTreeModel;
import scala.Tuple2;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Decision Tree
 *
 * Created by helencoder on 2018/3/6.
 */
public class DecisionTreeClassifier {

    /**
     * 创建SparkContext
     */
    private static String master = "local[*]";
    private static SparkConf conf = new SparkConf()
            .setAppName(DecisionTreeClassifier.class.getName())
            .setMaster(master);
    private static JavaSparkContext sc = new JavaSparkContext(conf);

    public static void main(String[] args) {
        JavaRDD<LabeledPoint> trainData = DataSet.loadTrainSet(sc);

        // Split the data into training and test sets (30% held out for testing)
        JavaRDD<LabeledPoint>[] splits = trainData.randomSplit(new double[]{0.8, 0.2});
        JavaRDD<LabeledPoint> trainingData = splits[0];
        JavaRDD<LabeledPoint> testData = splits[1];

        // Set parameters.
        //  Empty categoricalFeaturesInfo indicates all features are continuous.
        int numClasses = 2;
        Map<Integer, Integer> categoricalFeaturesInfo = new HashMap<>();
        String impurity = "gini";
        int maxDepth = 5;
        int maxBins = 32;

        // Train a DecisionTree model for classification.
        DecisionTreeModel model = DecisionTree.trainClassifier(trainingData, numClasses,
                categoricalFeaturesInfo, impurity, maxDepth, maxBins);

        // Evaluate model on test instances and compute test error
        JavaPairRDD<Double, Double> predictionAndLabel =
                testData.mapToPair(p -> new Tuple2<>(model.predict(p.features()), p.label()));

        double testErr =
                predictionAndLabel.filter(pl -> !pl._1().equals(pl._2())).count() / (double) testData.count();

        System.out.println("Test Error: " + testErr);
        System.out.println("Learned classification tree model:\n" + model.toDebugString());

        // Save and load model
//        model.save(sc.sc(), "target/tmp/myDecisionTreeClassificationModel");
//        DecisionTreeModel sameModel = DecisionTreeModel
//                .load(sc.sc(), "target/tmp/myDecisionTreeClassificationModel");
        // $example off$

    }
}
