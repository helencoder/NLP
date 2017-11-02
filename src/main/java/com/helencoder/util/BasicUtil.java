package com.helencoder.util;

import java.util.List;
import java.util.Random;

/**
 * 公共方法类
 *
 * Created by zhenghailun on 2017/10/31.
 */
public class BasicUtil {

    /**
     * 生成验证码
     *
     * @param length 验证码长度
     * @return String 验证码
     */
    public static String verifyCode(int length) {
        String dict = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
        StringBuilder verifyCode = new StringBuilder(length);
        Random random = new Random();
        for(int i = 0; i < length; i++){
            verifyCode.append(dict.charAt(random.nextInt(dict.length())));
        }
        return verifyCode.toString();
    }

    /**
     * 字符串查找
     *
     * @param pattern 待查找的pattern
     * @param str 查找的字符串
     * @return boolean
     */
    public static boolean isPatternExists(String pattern, String str) {
        int index = KMP.search(str, pattern);
        return index != -1;
    }

    /**
     * 获取最长公共子序列
     *
     * @param str1 字符串1
     * @param str2 字符串2
     * @return String 最长公共子序列
     */
    public static String getCommonSequence(String str1, String str2) {
        return LCS.commonSequence(str1, str2);
    }

    /**
     * 获取最长公共子串
     *
     * @param str1 字符串1
     * @param str2 字符串2
     * @return String 最长公共子串
     */
    public static List<String> getCommonString(String str1, String str2) {
        return LCS.commonString(str1, str2);
    }

}
