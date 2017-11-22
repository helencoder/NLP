package com.helencoder.util;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 公共方法类
 * <p>
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
        for (int i = 0; i < length; i++) {
            verifyCode.append(dict.charAt(random.nextInt(dict.length())));
        }
        return verifyCode.toString();
    }

    /**
     * 字符串查找
     *
     * @param pattern 待查找的pattern
     * @param str     查找的字符串
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
     *
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
     *
     * @param map  (Map)
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

    /**
     * 判断字符串中是否包含数字
     *
     * @param str 字符串
     * @return boolean
     */
    public static Boolean isNumbersInclude(String str) {
        Pattern p = Pattern.compile(".*\\d+.*");
        Matcher m = p.matcher(str);
        return m.matches();
    }

    /**
     * 判断字符串中是否包含字母
     *
     * @param str 字符串
     * @return boolean
     */
    public static Boolean isCharacterInclude(String str) {
        String regex = ".*[a-zA-Z]+.*";
        Matcher m = Pattern.compile(regex).matcher(str);
        return m.matches();
    }

    /**
     * 半角转全角
     *
     * @param input String.
     * @return 全角字符串.
     */
    public static String ToSBC(String input) {
        char c[] = input.toCharArray();
        for (int i = 0; i < c.length; i++) {
            if (c[i] == ' ') {
                c[i] = '\u3000';
            } else if (c[i] < '\177') {
                c[i] = (char) (c[i] + 65248);

            }
        }
        return new String(c);
    }

    /**
     * 全角转半角
     *
     * @param input String.
     * @return 半角字符串
     */
    public static String ToDBC(String input) {


        char c[] = input.toCharArray();
        for (int i = 0; i < c.length; i++) {
            if (c[i] == '\u3000') {
                c[i] = ' ';
            } else if (c[i] > '\uFF00' && c[i] < '\uFF5F') {
                c[i] = (char) (c[i] - 65248);

            }
        }
        String returnString = new String(c);

        return returnString;
    }

}
