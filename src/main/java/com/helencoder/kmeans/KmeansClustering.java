package com.helencoder.kmeans;

import com.thirdparty.word2vec.Word2Vec;

import java.io.IOException;
import java.util.*;

/**
 * KMeans聚类
 *
 * Created by helencoder on 2017/6/27.
 */
public class KmeansClustering {

    private List<String> data;    // 待聚类数据
    private int K;          // 簇个数
    private float[] arr;    // 数据维度数组
    private int flag = 10;  // 算法收敛条件
    private int dimension;  // 数据维度
    private Map<String, float[]> clusterData;   // 带聚类数据初始存储
    private List<String> classData; // 每个类的均值中心
    private Set<String> noises;    // 噪声数据
    private String[][] classifyData;    // 分类数据

    /**
     * 构造函数
     * @param data
     * @param K
     * @return
     */
    public KmeansClustering(List<String> data, int K) {
        this.data = data;
        this.K = K;
        // 聚类数据初始化存储
        this.clusterInitData();
        // 聚类初始化
        this.clusterInit(data, K);
    }

    /**
     * 聚类初始化
     * @param data
     * @param K
     * @return
     */
    private void clusterInit(List<String> data, int K) {
        // 中心点数据初始化
        this.centerDatasInit();
        // 初始化分类
        for (int i = 0; i < this.data.size(); i++) {
            float[] wordVec = this.clusterData.get(this.data.get(i));
            if (wordVec == null) {
                continue;
            }
            float dis = 1.0f;
            for (int j = 0; j < this.classData.size(); j++) {

            }
        }
    }

    /**
     * 初始化选取中心点
     *
     */
    private void centerDatasInit() {
        // 初始数据打乱
        Collections.shuffle(this.data);

    }

    /**
     * 聚类数据初始化存储
     *
     */
    private void clusterInitData() {
        Map<String, float[]> clusterData = new HashMap<String, float[]>(this.data.size());
        Set<String> noises = new HashSet<String>();
        // 引入word2vec类
        Word2Vec vec = new Word2Vec();
        try {
            vec.loadGoogleModel("data/model/wiki_chinese_word2vec(Google).model");
        } catch (IOException e) {
            e.printStackTrace();
        }

        Iterator iterator = this.data.iterator();
        while (iterator.hasNext()) {
            String word = (String)iterator.next();
            // 获取词向量，并记录
            float[] wordVec = vec.getWordVector(word);
            if (wordVec == null || wordVec.length == 0) {
                noises.add(word);
            } else {
                clusterData.put(word, wordVec);
            }

        }
        this.clusterData = clusterData;
        this.noises = noises;
    }

    // 两个向量间距离计算
    /**
     * 两个向量间距离计算
     * @param vec1
     * @param vec2
     * @return float
     */
    private float calDist(float[] vec1, float[] vec2) {
        float dist = 0;
        for (int i = 0; i < vec1.length; i++) {
            dist += vec1[i] * vec2[i];
        }
        return (1 - dist);
    }

}
