package com.helencoder.util;

import java.io.File;
import java.util.Scanner;

/**
 * KMP算法(字符串查找)
 *
 * Created by zhenghailun on 2017/10/31.
 */
public class KMP {

    public static void main(String[] args) throws Exception {
        //从文件读入数据
        KMP kmp = new KMP();
        String str= "AAAAAAAADRFWASD";
        int ans = kmp.search(str,"FW");
        //输出答案
        if (ans != -1) {
            System.out.println(ans);
        }
    }

    /**
     * KMP算法查找字符串出现位置,不存在返回-1
     */
    public int search(String str, String pattern) {
        char[] strs = str.toCharArray();
        char[] patterns = pattern.toCharArray();
        int L = strs.length, N = patterns.length, i = 0, j = 0; // i: str pointer, j: pattern pointer
        if (N < 1) return 0;
        if (L < N) return -1;
        int[] lps = lps(pattern); // get the array that stores the longest subarray whose prefix is also its suffix
        while (i < L) {
            if (strs[i] == patterns[j]) { // same value found, move both str and pattern pointers to their right
                ++i;
                ++j;
                if (j == N) return i - N; // whole match found
            } else if (j > 0) j = lps[j - 1]; // move pattern pointer to a previous safe location
            else ++i; // restart searching at next str pointer
        }
        return -1;
    }

    /**
     * KMP算法移动策略
     */
    private int[] lps(String pattern) {
        int j = 0, i = 1, L = pattern.length();
        int[] res = new int[L];
        char[] chars = pattern.toCharArray();
        while (i < L) {
            if (chars[i] == chars[j]) res[i++] = ++j;
            else {
                int temp = i - 1;
                while (temp > 0) {
                    int prevLPS = res[temp];
                    if (chars[i] == chars[prevLPS]) {
                        res[i++] = prevLPS + 1;
                        j = prevLPS;
                        break;
                    } else temp = prevLPS - 1;
                }
                if (temp <= 0) {
                    res[i++] = 0;
                    j = 0;
                }
            }
        }
        return res;
    }


}