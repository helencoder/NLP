package com.helencoder;

import com.helencoder.simhash.SimHash;

import java.io.IOException;

/**
 * SimHash可用性测试
 *
 * Created by helencoder on 2017/10/31.
 */
public class SimHashTest {
    public static void main(String[] args) {
        try {
            String s = "传统的 hash 算法只负责将原始内容尽量均匀随机地映射为一个签名值，"
                    + "原理上相当于伪随机数产生算法。产生的两个签名，如果相等，说明原始内容在一定概 率 下是相等的；"
                    + "如果不相等，除了说明原始内容不相等外，不再提供任何信息，因为即使原始内容只相差一个字节，"
                    + "所产生的签名也很可能差别极大。从这个意义 上来 说，要设计一个 hash 算法，"
                    + "对相似的内容产生的签名也相近，是更为艰难的任务，因为它的签名值除了提供原始内容是否相等的信息外，"
                    + "还能额外提供不相等的 原始内容的差异程度的信息。";
            SimHash hash1 = new SimHash(s, 64);

            // 删除首句话，并加入两个干扰串
            s = "原理上相当于伪随机数产生算法。产生的两个签名，如果相等，说明原始内容在一定概 率 下是相等的；"
                    + "如果不相等，除了说明原始内容不相等外，不再提供任何信息，因为即使原始内容只相差一个字节，"
                    + "所产生的签名也很可能差别极大。从这个意义 上来 说，要设计一个 hash 算法，"
                    + "对相似的内容产生的签名也相近，是更为艰难的任务，因为它的签名值除了提供原始内容是否相等的信息外，"
                    + "干扰1还能额外提供不相等的 原始内容的差异程度的信息。";
            SimHash hash2 = new SimHash(s, 64);

            // 首句前添加一句话，并加入四个干扰串
            s = "simhash算法的输入是一个向量，输出是一个 f 位的签名值。为了陈述方便，"
                    + "假设输入的是一个文档的特征集合，每个特征有一定的权重。"
                    + "传统干扰4的 hash 算法只负责将原始内容尽量均匀随机地映射为一个签名值，"
                    + "原理上这次差异有多大呢3相当于伪随机数产生算法。产生的两个签名，如果相等，"
                    + "说明原始内容在一定概 率 下是相等的；如果不相等，除了说明原始内容不相等外，不再提供任何信息，"
                    + "因为即使原始内容只相差一个字节，所产生的签名也很可能差别极大。从这个意义 上来 说，"
                    + "要设计一个 hash 算法，对相似的内容产生的签名也相近，是更为艰难的任务，因为它的签名值除了提供原始"
                    + "内容是否相等的信息外，干扰1还能额外提供不相等的 原始再来干扰2内容的差异程度的信息。";
            SimHash hash3 = new SimHash(s, 64);

            // 两种方式计算汉明距离
            System.out.println(hash1.hammingDistance(hash2));
            // 根据鸽巢原理（也成抽屉原理，见组合数学），如果两个签名的海明距离在 3 以内，它们必有一块签名subByDistance()完全相同。
            int dis = hash1.getDistance(hash1.strSimHash, hash3.strSimHash);
            System.out.println(dis);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
