package com.helencoder.simhash;

import java.math.BigInteger;
import java.util.StringTokenizer;
/**
 * SimHashForEnglish(英文分词)实现步骤:
 *  1、对文本分词，得到N维特征向量（默认为64维）
 *  2、为分词设置权重（tf-idf）
 *  3、为特征向量计算哈希
 *  4、对所有特征向量加权，累加（目前仅进行非加权累加）
 *  5、对累加结果，大于零置一，小于零置零
 *  6、得到文本指纹（fingerprint）
 *
 * Created by helencoder on 2017/10/30.
 */
public class SimHashForEnglish {
    private String tokens;
    private BigInteger strSimHash;
    private int hashbits = 64;

    public SimHashForEnglish(String tokens) {
        this.tokens = tokens;
        this.strSimHash = this.simHash();
    }

    public SimHashForEnglish(String tokens, int hashbits) {
        this.tokens = tokens;
        this.hashbits = hashbits;
        this.strSimHash = this.simHash();
    }

    public BigInteger simHash() {
        int[] v = new int[this.hashbits];
        StringTokenizer stringTokens = new StringTokenizer(this.tokens);
        while (stringTokens.hasMoreTokens()) {
            String temp = stringTokens.nextToken();
            BigInteger t = this.hash(temp);
            for (int i = 0; i < this.hashbits; i++) {
                BigInteger bitmask = new BigInteger("1").shiftLeft(i);
                if (t.and(bitmask).signum() != 0) {
                    v[i] += 1;
                } else {
                    v[i] -= 1;
                }
            }
        }
        BigInteger fingerprint = new BigInteger("0");
        for (int i = 0; i < this.hashbits; i++) {
            if (v[i] >= 0) {
                fingerprint = fingerprint.add(new BigInteger("1").shiftLeft(i));
            }
        }
        return fingerprint;
    }

    private BigInteger hash(String source) {
        if (source == null || source.length() == 0) {
            return new BigInteger("0");
        } else {
            char[] sourceArray = source.toCharArray();
            BigInteger x = BigInteger.valueOf(((long) sourceArray[0]) << 7);
            BigInteger m = new BigInteger("1000003");
            BigInteger mask = new BigInteger("2").pow(this.hashbits).subtract(
                    new BigInteger("1"));
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

    public int hammingDistance(SimHashForEnglish other) {
        BigInteger m = new BigInteger("1").shiftLeft(this.hashbits).subtract(
                new BigInteger("1"));
        BigInteger x = this.strSimHash.xor(other.strSimHash).and(m);
        int tot = 0;
        while (x.signum() != 0) {
            tot += 1;
            x = x.and(x.subtract(new BigInteger("1")));
        }
        return tot;
    }

    public static void main(String[] args) {
        String s = "This is a test string for testing";
        SimHashForEnglish hash1 = new SimHashForEnglish(s, 64);
        System.out.println(hash1.strSimHash + "  " + hash1.strSimHash.bitLength());
        s = "This is a test string for testing also";
        SimHashForEnglish hash2 = new SimHashForEnglish(s, 64);
        System.out.println(hash2.strSimHash+ "  " + hash2.strSimHash.bitLength());
        s = "This is a test string for testing als";
        SimHashForEnglish hash3 = new SimHashForEnglish(s, 64);
        System.out.println(hash3.strSimHash+ "  " + hash3.strSimHash.bitLength());
        System.out.println("============================");
        System.out.println(hash1.hammingDistance(hash2));
        System.out.println(hash1.hammingDistance(hash3));
    }
}

