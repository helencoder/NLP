package com.helencoder.classification.ml;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.mllib.feature.HashingTF;
import org.apache.spark.mllib.regression.LabeledPoint;

import java.util.Arrays;

/**
 * spark分类数据
 *
 * Created by helencoder on 2018/3/5.
 */
public class DataSet {

    /**
     * 训练数据加载
     */
    public static JavaRDD<LabeledPoint> loadTrainSet(JavaSparkContext sc) {
        // 加载训练数据
        String posFilePath = "data/mllib/classification/pos.txt";
        String negFilePath = "data/mllib/classification/neg.txt";
        JavaRDD<String> posData = sc.textFile(posFilePath);
        JavaRDD<String> negData = sc.textFile(negFilePath);

        // 创建一个HashingTF实例来把邮件文本映射为包含10000个特征的向量
        final HashingTF tf = new HashingTF(10000);

        // 训练数据向量化
        JavaRDD<LabeledPoint> posRDD = posData.map(
                new Function<String, LabeledPoint>() {
                    @Override
                    public LabeledPoint call(String file) throws Exception {
                        return new LabeledPoint(0, tf.transform(Arrays.asList(file.split(" "))));
                    }
                }
        );
        JavaRDD<LabeledPoint> negRDD = negData.map(
                new Function<String, LabeledPoint>() {
                    @Override
                    public LabeledPoint call(String file) throws Exception {
                        return new LabeledPoint(1, tf.transform(Arrays.asList(file.split(" "))));
                    }
                }
        );

        JavaRDD<LabeledPoint> trainData = posRDD.union(negRDD);
        trainData.cache();  // 缓存训练数据RDD

        return trainData;
    }

    /**
     * 测试数据加载
     */
    public static void loadTestSet(JavaSparkContext sc) {
        String testFilePath = "data/mllib/classification/test.txt";

    }


}
