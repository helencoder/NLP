package com.helencoder.classification.ml;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.mllib.classification.NaiveBayes;
import org.apache.spark.mllib.classification.NaiveBayesModel;
import org.apache.spark.mllib.regression.LabeledPoint;
import scala.Tuple2;

/**
 * Naive Bayes
 *
 * Created by helencoder on 2018/3/6.
 */
public class NaiveBayesClassifier {
    /**
     * 创建SparkContext
     */
    private static String master = "local[*]";
    private static SparkConf conf = new SparkConf()
            .setAppName(NaiveBayesClassifier.class.getName())
            .setMaster(master);
    private static JavaSparkContext sc = new JavaSparkContext(conf);

    public static void main(String[] args) {
        JavaRDD<LabeledPoint> trainData = DataSet.loadTrainSet(sc);

        JavaRDD<LabeledPoint>[] tmp = trainData.randomSplit(new double[]{0.8, 0.2});
        JavaRDD<LabeledPoint> training = tmp[0]; // training set
        JavaRDD<LabeledPoint> test = tmp[1]; // test set
        NaiveBayesModel model = NaiveBayes.train(training.rdd(), 1.0);
        JavaPairRDD<Double, Double> predictionAndLabel =
                test.mapToPair(p -> new Tuple2<>(model.predict(p.features()), p.label()));
        double accuracy =
                predictionAndLabel.filter(pl -> pl._1().equals(pl._2())).count() / (double) test.count();
        System.out.println("accuracy: " + accuracy);

        // Save and load model
//        model.save(sc.sc(), "target/tmp/myNaiveBayesModel");
//        NaiveBayesModel sameModel = NaiveBayesModel.load(sc.sc(), "target/tmp/myNaiveBayesModel");
        // $example off$

        sc.stop();
    }

}
