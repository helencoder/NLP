package com.helencoder.util;

import java.util.*;

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

    /**
     * 数组转换为字符串
     * @param list
     * @param delimiter 分隔符
     * @return 字符串
     */
    public static String mkString(List<String> list, String delimiter) {
        StringBuffer sb = new StringBuffer();
        for (String data : list) {
            sb.append(data);
            sb.append(delimiter);
        }
        String str = sb.toString();

        if (str.isEmpty()) {
            return "";
        } else {
            return str.substring(0, str.lastIndexOf(delimiter));
        }
    }

    /**
     * Map排序(按键值排序)
     * @param map (Map)
     * @param flag true降序 false升序
     * @return sortedMap
     */
    public static <K, V extends Comparable<? super V>> Map<K, V> sortMapByValue(Map<K, V> map, boolean flag) {
        List<Map.Entry<K, V>> list = new LinkedList<Map.Entry<K, V>>(map.entrySet());
        Collections.sort(list, new Comparator<Map.Entry<K, V>>() {
            public int compare(Map.Entry<K, V> o1, Map.Entry<K, V> o2) {
                if (flag) { // 降序
                    return o2.getValue().compareTo(o1.getValue());
                } else {    // 升序
                    return o1.getValue().compareTo(o2.getValue());
                }
            }
        });

        Map<K, V> result = new LinkedHashMap<K, V>();
        for (Map.Entry<K, V> entry : list) {
            result.put(entry.getKey(), entry.getValue());
        }

        return result;
    }


}
