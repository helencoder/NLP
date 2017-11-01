package com.helencoder.simhash;

import java.io.UnsupportedEncodingException;

/**
 * 自然语言处理版本
 *
 * Created by helencoder on 2017/11/1.
 */
public class NLPSimHash {
    public static void main(String[] args) {
        // 测试用例
        String input = "郑海伦123";
        String input2 = "郑海伦棒";
        try {
            long simHash = getSimHash(input);
//            System.out.println(simHash);
            System.out.println(Long.toBinaryString(simHash));
            long simHash2 = getSimHash(input2);
//            System.out.println(simHash2);
            System.out.println(Long.toBinaryString(simHash2));
        } catch (UnsupportedEncodingException ex) {
            ex.printStackTrace();
        }


    }

    public static int byte2int(byte b) {
        return (b & 0xff);
    }

    private static int MAX_CN_CODE = 6768;
    private static int MAX_CODE = 6768 + 117;

    /**
     * 获取中文字符串的散列编码
     */
    public static int getHashCode(char c) throws UnsupportedEncodingException {
        String s = String.valueOf(c);
        int maxValue = 6768;
        byte[] b = s.getBytes("gb2312");
        if (b.length == 2) {
            int index = (byte2int(b[0]) - 176) * 94 + (byte2int(b[1]) - 161);
            return index;
        } else if (b.length == 1){
            int index = byte2int(b[0]) - 9 + MAX_CN_CODE;
            return index;
        }
        return c;
    }

    /**
     * 获取中文字符串的散列编码
     */
    public static long getSimHash(String input) throws UnsupportedEncodingException {
        if (input == null || "".equals(input)) {
            return  -1;
        }
        int b = 13;

        long simHash = getHashCode(input.charAt(0));
        int maxBit = b;
        for (int i = 1; i < input.length(); i++) {
            simHash *= MAX_CODE;
            simHash += getHashCode(input.charAt(i));
            maxBit += b;
        }

        long originalValue = simHash;

        for (int i = 0; i < (64 / maxBit); i++) {
            simHash = simHash << maxBit;
            simHash += originalValue;
        }

        return simHash;
    }

}
