package com.helencoder.simhash;

import com.hankcs.hanlp.seg.common.Term;
import com.helencoder.segmentation.Segmentation;
import com.helencoder.textrank.TextRank;
import com.helencoder.textrank.Word;
import org.wltea.analyzer.IKSegmentation;
import org.wltea.analyzer.Lexeme;

import java.io.IOException;
import java.io.StringReader;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * SimHash实现步骤:
 *  1、利用HanLP对文章进行分词，得到N维特征向量（默认为64维）
 *  2、为分词设置权重(HanLP自带的frequency)
 *  3、为特征向量计算哈希
 *  4、对所有特征向量加权，累加（目前仅进行非加权累加）
 *  5、对累加结果，大于零置一，小于零置零
 *  6、得到文本指纹（fingerprint）
 *
 * Created by helencoder on 2017/10/31.
 */
public class SimHash {
    public String tokens;
    public BigInteger intSimHash;
    public String strSimHash;
    public int hashbits = 64;

    public SimHash(String tokens) throws IOException {
        this.tokens = tokens;
        this.intSimHash = this.simHash();
    }

    public SimHash(String tokens, int hashbits) throws IOException {
        this.tokens = tokens;
        this.hashbits = hashbits;
        this.intSimHash = this.simHash();
    }

    HashMap<String, Integer> wordMap = new HashMap<String, Integer>();

    public BigInteger simHash() throws IOException {
        // 定义特征向量/数组
        float[] v = new float[this.hashbits];
        // 1、文本关键词提取
        TextRank textRank = new TextRank();
        List<Word> wordList = textRank.getKeywordsList(this.tokens, 100, 5);
        for (Word word : wordList) {
            // 2、将每一个分词hash为一组固定长度的数列.比如 64bit 的一个整数.
            BigInteger t = this.hash(word.getWord());
            for (int i = 0; i < this.hashbits; i++) {
                BigInteger bitmask = new BigInteger("1").shiftLeft(i);
                // 3、建立一个长度为64的整数数组(假设要生成64位的数字指纹,也可以是其它数字),
                // 对每一个分词hash后的数列进行判断,如果是1000...1,那么数组的第一位和末尾一位加1,
                // 中间的62位减一,也就是说,逢1加1,逢0减1.一直到把所有的分词hash数列全部判断完毕.
                if (t.and(bitmask).signum() != 0) {
                    // 这里是计算整个文档的所有特征的向量和(后为权重值)
                    v[i] += word.getWeight();
//                    v[i] += 1;
                } else {
                    v[i] -= word.getWeight();
//                    v[i] -= 1;
                }
            }
        }

//        Segmentation segmentation = new Segmentation();
//        List<Term> wordList = segmentation.segToList(this.tokens, false);
//
//        for (Term term : wordList) {
//            // 2、将每一个分词hash为一组固定长度的数列.比如 64bit 的一个整数.
//            BigInteger t = this.hash(term.word);
//            for (int i = 0; i < this.hashbits; i++) {
//                BigInteger bitmask = new BigInteger("1").shiftLeft(i);
//                // 3、建立一个长度为64的整数数组(假设要生成64位的数字指纹,也可以是其它数字),
//                // 对每一个分词hash后的数列进行判断,如果是1000...1,那么数组的第一位和末尾一位加1,
//                // 中间的62位减一,也就是说,逢1加1,逢0减1.一直到把所有的分词hash数列全部判断完毕.
//                if (t.and(bitmask).signum() != 0) {
//                    // 这里是计算整个文档的所有特征的向量和(后为权重值)
//                    v[i] += term.getFrequency();
////                    v[i] += 1;
//                } else {
//                    v[i] -= term.getFrequency();
////                    v[i] -= 1;
//                }
//            }
//        }

        BigInteger fingerprint = new BigInteger("0");
        StringBuffer simHashBuffer = new StringBuffer();
        for (int i = 0; i < this.hashbits; i++) {
            // 4、最后对数组进行判断,大于0的记为1,小于等于0的记为0,得到一个 64bit 的数字指纹/签名.
            if (v[i] >= 0) {
                fingerprint = fingerprint.add(new BigInteger("1").shiftLeft(i));
                simHashBuffer.append("1");
            } else {
                simHashBuffer.append("0");
            }
        }
        this.strSimHash = simHashBuffer.toString();
        //System.out.println(this.strSimHash + " length " + this.strSimHash.length());
        return fingerprint;
    }

    private BigInteger hash(String source) {
        if (source == null || source.length() == 0) {
            return new BigInteger("0");
        } else {
            char[] sourceArray = source.toCharArray();
            BigInteger x = BigInteger.valueOf(((long) sourceArray[0]) << 7);
            BigInteger m = new BigInteger("1000003");
            BigInteger mask = new BigInteger("2").pow(this.hashbits).subtract(new BigInteger("1"));
            for (char item : sourceArray) {
                BigInteger temp = BigInteger.valueOf((long) item);
                x = x.multiply(m).xor(temp).and(mask);
            }
            x = x.xor(new BigInteger(String.valueOf(source.length())));
            if (x.equals(new BigInteger("-1"))) {
                x = new BigInteger("-2");
            }
            return x;
        }
    }

    public int hammingDistance(SimHash other) {

        BigInteger x = this.intSimHash.xor(other.intSimHash);
        int tot = 0;

        // 统计x中二进制位数为1的个数
        // 我们想想，一个二进制数减去1，那么，从最后那个1（包括那个1）后面的数字全都反了，
        // 对吧，然后，n&(n-1)就相当于把后面的数字清0，
        // 我们看n能做多少次这样的操作就OK了。

        while (x.signum() != 0) {
            tot += 1;
            x = x.and(x.subtract(new BigInteger("1")));
        }
        return tot;
    }

    public int getDistance(String str1, String str2) {
        int distance;
        if (str1.length() != str2.length()) {
            distance = -1;
        } else {
            distance = 0;
            for (int i = 0; i < str1.length(); i++) {
                if (str1.charAt(i) != str2.charAt(i)) {
                    distance++;
                }
            }
        }
        return distance;
    }

    public List subByDistance(SimHash simHash, int distance) {
        // 分成几组来检查
        int numEach = this.hashbits / (distance + 1);
        List characters = new ArrayList();

        StringBuffer buffer = new StringBuffer();

        int k = 0;
        for (int i = 0; i < this.intSimHash.bitLength(); i++) {
            // 当且仅当设置了指定的位时，返回 true
            boolean sr = simHash.intSimHash.testBit(i);

            if (sr) {
                buffer.append("1");
            } else {
                buffer.append("0");
            }

            if ((i + 1) % numEach == 0) {
                // 将二进制转为BigInteger
                BigInteger eachValue = new BigInteger(buffer.toString(), 2);
                System.out.println("----" + eachValue);
                buffer.delete(0, buffer.length());
                characters.add(eachValue);
            }
        }

        return characters;
    }
}
