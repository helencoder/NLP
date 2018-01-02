package com.helencoder.classification;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.broadcast.Broadcast;
import org.apache.spark.mllib.evaluation.MultilabelMetrics;
import org.apache.spark.mllib.feature.HashingTF;
import scala.Tuple2;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

/**
 * 多标签分类
 *
 * Created by helencoder on 2017/11/3.
 */
public class MultiLableClassification {
    public static void main(String[] args) {
        // 多标签分类

        //run();
        example();
    }

    /**
     * 创建SparkContext
     *
     */
    private static String master = "local[*]";
    private static SparkConf conf = new SparkConf()
            .setAppName(MultiLableClassification.class.getName())
            .setMaster(master);
    private static JavaSparkContext sc = new JavaSparkContext(conf);

    /**
     * 多标签分类模型评估
     */
    public static void example() {
        // $example on$
        List<Tuple2<double[], double[]>> data = Arrays.asList(
                new Tuple2<>(new double[]{0.0, 1.0}, new double[]{0.0, 2.0}),
                new Tuple2<>(new double[]{0.0, 2.0}, new double[]{0.0, 1.0}),
                new Tuple2<>(new double[]{}, new double[]{0.0}),
                new Tuple2<>(new double[]{2.0}, new double[]{2.0}),
                new Tuple2<>(new double[]{2.0, 0.0}, new double[]{2.0, 0.0}),
                new Tuple2<>(new double[]{0.0, 1.0, 2.0}, new double[]{0.0, 1.0}),
                new Tuple2<>(new double[]{1.0}, new double[]{1.0, 2.0})
        );
        JavaRDD<Tuple2<double[], double[]>> scoreAndLabels = sc.parallelize(data);

        // Instantiate metrics object
        MultilabelMetrics metrics = new MultilabelMetrics(scoreAndLabels.rdd());

        // Summary stats
        System.out.format("Recall = %f\n", metrics.recall());
        System.out.format("Precision = %f\n", metrics.precision());
        System.out.format("F1 measure = %f\n", metrics.f1Measure());
        System.out.format("Accuracy = %f\n", metrics.accuracy());

        // Stats by labels
        for (int i = 0; i < metrics.labels().length - 1; i++) {
            System.out.format("Class %1.1f precision = %f\n", metrics.labels()[i], metrics.precision(
                    metrics.labels()[i]));
            System.out.format("Class %1.1f recall = %f\n", metrics.labels()[i], metrics.recall(
                    metrics.labels()[i]));
            System.out.format("Class %1.1f F1 score = %f\n", metrics.labels()[i], metrics.f1Measure(
                    metrics.labels()[i]));
        }

        // Micro stats
        System.out.format("Micro recall = %f\n", metrics.microRecall());
        System.out.format("Micro precision = %f\n", metrics.microPrecision());
        System.out.format("Micro F1 measure = %f\n", metrics.microF1Measure());

        // Hamming loss
        System.out.format("Hamming loss = %f\n", metrics.hammingLoss());

        // Subset accuracy
        System.out.format("Subset accuracy = %f\n", metrics.subsetAccuracy());
        // $example off$

        sc.stop();
    }

    /**
     * 多标签分类实践
     *
     * 步骤：1、进行文本和标签的对应读入
     *      2、进行标签的归集和转换
     *      3、进行特征提取
     */
    public static void run() {
        // 将文章以及对应标签读入
        String articleFilepath = "data/tag_classification/finance.txt";
        String labelFilepath = "data/tag_classification/finance_tags.txt";
        Map<String, String> articleLabelMap = loadArticleLabelData(articleFilepath, labelFilepath);

        // 获取所有的标签值
        Set<String> labelSet = new HashSet<String>();
        for (Map.Entry<String, String> entry : articleLabelMap.entrySet()) {
            String value = entry.getValue();
            String[] labels = value.split(",");
            for (String label : labels) {
                labelSet.add(label);
            }
        }

        // 获取labelMap
        Map<String, Integer> labelMap = transferLabel(labelSet);

        // 进行算法转换
        // 读入文本
        JavaRDD<String> articleDataRDD = sc.textFile(articleFilepath);

        // 创建一个HashingTF实例来把文本映射为包含100个特征的向量
        final HashingTF tf = new HashingTF(10000);

        // 创建广播变量
        final Broadcast<Map<String, String>> broadcastArticleLabelMap = sc.broadcast(articleLabelMap);
        final Broadcast<Map<String, Integer>> broadcastLabelMap = sc.broadcast(labelMap);

        // RDD转换(前面为正文特征,后为多标签)
        JavaRDD<Tuple2<double[], double[]>> scoreAndLabels = articleDataRDD.filter(
                new Function<String, Boolean>() {
                    @Override
                    public Boolean call(String line) throws Exception {
                        // 使用广播变量
                        final Map<String, String> articleLabelMap = broadcastArticleLabelMap.getValue();
                        if (articleLabelMap.containsKey(line)) {
                            return true;
                        }
                        return false;
                    }
                }).map(
                new Function<String, Tuple2<double[], double[]>>() {
                    @Override
                    public Tuple2<double[], double[]> call(String line) throws Exception {
                        // 使用广播变量
                        final Map<String, String> articleLabelMap = broadcastArticleLabelMap.getValue();
                        final Map<String, Integer> labelMap = broadcastLabelMap.getValue();
                        // 标签转换
                        String labelData = articleLabelMap.get(line);
                        String[] labels = labelData.split(",");
                        double[] label = new double[labels.length];
                        for (int i = 0; i < labels.length; i++) {
                            label[i] = labelMap.get(labels[i]);
                        }
                        return new Tuple2<double[], double[]>(tf.transform(Arrays.asList(line.split(" "))).toArray(), label);
                    }
                }
        );

        // 多标签分类效果评估
        MultilabelMetrics metrics = new MultilabelMetrics(scoreAndLabels.rdd());

        // Summary stats
        System.out.format("Recall = %f\n", metrics.recall());
        System.out.format("Precision = %f\n", metrics.precision());
        System.out.format("F1 measure = %f\n", metrics.f1Measure());
        System.out.format("Accuracy = %f\n", metrics.accuracy());

        // Stats by labels
        for (int i = 0; i < metrics.labels().length - 1; i++) {
            System.out.format("Class %1.1f precision = %f\n", metrics.labels()[i], metrics.precision(
                    metrics.labels()[i]));
            System.out.format("Class %1.1f recall = %f\n", metrics.labels()[i], metrics.recall(
                    metrics.labels()[i]));
            System.out.format("Class %1.1f F1 score = %f\n", metrics.labels()[i], metrics.f1Measure(
                    metrics.labels()[i]));
        }

        // Micro stats
        System.out.format("Micro recall = %f\n", metrics.microRecall());
        System.out.format("Micro precision = %f\n", metrics.microPrecision());
        System.out.format("Micro F1 measure = %f\n", metrics.microF1Measure());

        // Hamming loss
        System.out.format("Hamming loss = %f\n", metrics.hammingLoss());

        // Subset accuracy
        System.out.format("Subset accuracy = %f\n", metrics.subsetAccuracy());
        // $example off$

        sc.stop();

    }

    /**
     * 获取文章、标签对应map
     */
    private static Map<String, String> loadArticleLabelData(String articleFilepath, String labelFilepath) {
        Map<String, String> articleLabelMap = new HashMap<String, String>();
        try {
            BufferedReader br1 = new BufferedReader(new InputStreamReader(new FileInputStream(articleFilepath)));
            BufferedReader br2 = new BufferedReader(new InputStreamReader(new FileInputStream(labelFilepath)));
            String content = br1.readLine();
            String label = br2.readLine();
            while (content != null && label != null) {
                articleLabelMap.put(content, label);
                content = br1.readLine();
                label = br2.readLine();
            }
            br1.close();
            br2.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return articleLabelMap;
    }

    /**
     * 标签归集转换
     */
    private static Map<String, Integer> transferLabel(Set<String> labelSet) {
        Map<String, Integer> labelMap = new HashMap<String, Integer>();

        List<String> labelList = new ArrayList<String>(labelSet);
        for (int i = 0; i < labelList.size(); i++) {
            labelMap.put(labelList.get(i), i);
        }

        return labelMap;
    }
}
