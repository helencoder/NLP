package com.helencoder.util;

import java.util.ArrayList;
import java.util.List;

/**
 * 最长公共子序列匹配
 * <p>
 * Created by helencoder on 2017/11/1.
 */
public class LCS {

    /**
     * 最长公共子序列--LCS算法
     * 矩阵填充公式
     *  1、c[i,j] = 0 (i=0 or j=0)
     *  2、c[i,j] = c[i-1, j-1] + 1 (i,j>0 and i=j)
     *  3、c[i,j] = max[c[i,j-1], c[i-1,j]] (i,j>0 and i!=j)
     *
     * @param str1 字符串1
     * @param str2 字符串2
     * @return String 最长公共子序列
     */
    public static String commonSequence(String str1, String str2) {
        // 参数检查
        if (str1 == null || str2 == null) {
            return "";
        }
        if (str1.equals("") || str2.equals("")) {
            return "";
        }

        int matrix[][] = new int[str1.length() + 1][str2.length() + 1];
        // 矩阵填充
        for (int i = 1; i <= str1.length(); i++) {
            for (int j = 1; j <= str2.length(); j++) {
                if (str1.charAt(i - 1) == str2.charAt(j - 1)) {
                    matrix[i][j] = matrix[i - 1][j - 1] + 1;
                } else {
                    matrix[i][j] = matrix[i][j - 1] > matrix[i - 1][j] ? matrix[i][j - 1] : matrix[i - 1][j];
                }
            }
        }

        int i = str1.length();
        int j = str2.length();
        StringBuffer sb = new StringBuffer();
        while ((i != 0) && (j != 0)) { // 利用上面得到的矩阵计算子序列，从最右下角往左上走
            if (str1.charAt(i - 1) == str2.charAt(j - 1)) {
                sb.append(str1.charAt(i - 1)); // 相同时即为相同的子串
                i--;
                j--;
            } else {
                if (matrix[i][j - 1] > matrix[i - 1][j]) {
                    j--;
                } else {
                    i--;
                }
            }
        }

        return sb.reverse().toString();
    }


    /**
     * 最长公共子串--LCS算法
     *
     * @param str1 字符串1
     * @param str2 字符串2
     * @return List<String> 最长公共子串list
     */
    public static List<String> commonString(String str1, String str2) {
        // 参数检查
        if (str1 == null || str1 == null) {
            return new ArrayList<String>();
        }
        if (str1.equals("") || str1.equals("")) {
            return new ArrayList<String>();
        }

        int maxLen = str1.length() > str2.length() ? str1.length() : str2.length();
        int[] max = new int[maxLen];
        int[] maxIndex = new int[maxLen];
        int[] c = new int[maxLen];
        List<String> list = new ArrayList<>();

        for (int i = 0; i < str2.length(); i++) {
            for (int j = str1.length() - 1; j >= 0; j--) {
                if (str2.charAt(i) == str1.charAt(j)) {
                    if ((i == 0) || (j == 0))
                        c[j] = 1;
                    else
                        c[j] = c[j - 1] + 1;
                } else {
                    c[j] = 0;
                }

                if (c[j] > max[0]) {   //如果是大于那暂时只有一个是最长的,而且要把后面的清0;
                    max[0] = c[j];
                    maxIndex[0] = j;

                    for (int k = 1; k < maxLen; k++) {
                        max[k] = 0;
                        maxIndex[k] = 0;
                    }
                } else if (c[j] == max[0]) {   //有多个是相同长度的子串
                    for (int k = 1; k < maxLen; k++) {
                        if (max[k] == 0) {
                            max[k] = c[j];
                            maxIndex[k] = j;
                            break;  //在后面加一个就要退出循环了
                        }

                    }
                }
            }
        }

        for (int j = 0; j < maxLen; j++) {
            if (max[j] > 0) {

                StringBuffer sb = new StringBuffer();
                for (int i = maxIndex[j] - max[j] + 1; i <= maxIndex[j]; i++) {
                    sb.append(str1.charAt(i));
                }
                String lcs = sb.toString();
                list.add(lcs);
            }
        }

        return list;
    }


    /**
     * 最长公共子序列--LCS算法(详尽版)
     */
    private static String LCSsequence2(String str1, String str2) {
        // 参数检查
        if (str1 == null || str2 == null) {
            return "";
        }
        if (str1.equals("") || str2.equals("")) {
            return "";
        }

        // 生成lcs递归矩阵
        int[][] matrix = lcsMatrix(str1, str2);

        // 输出lcs最长子序列
        return lcsRec(matrix, str1, str2, str1.length(), str2.length(), new StringBuffer()).toString();
    }

    /**
     * 最长子序列匹配--生成LCS矩阵
     * 矩阵填充公式
     *  1、c[i,j] = 0 (i=0 or j=0)
     *  2、c[i,j] = c[i-1, j-1] + 1 (i,j>0 and i=j)
     *  3、c[i,j] = max[c[i,j-1], c[i-1,j]] (i,j>0 and i!=j)
     *
     */
    private static int[][] lcsMatrix(String str1, String str2) {
        // 建立二维矩阵
        int[][] matrix = new int[str1.length() + 1][str2.length() + 1];

        // 初始化边界条件
        for (int i = 0; i <= str1.length(); i++) {
            matrix[i][0] = 0;//每行第一列置零
        }
        for (int j = 0; j <= str2.length(); j++) {
            matrix[0][j] = 0;//每列第一行置零
        }

        // 填充矩阵
        for (int i = 1; i <= str1.length(); i++) {
            for (int j = 1; j <= str2.length(); j++) {
                if (str1.charAt(i - 1) == str2.charAt(j - 1)) {
                    matrix[i][j] = matrix[i - 1][j - 1] + 1;
                } else {
                    matrix[i][j] = Math.max(matrix[i - 1][j], matrix[i][j - 1]);
                }
            }
        }

        return matrix;
    }

    /**
     * 最长子序列匹配--输出最长子序列
     * 矩阵递归遍历输出
     */
    private static StringBuffer lcsRec(int[][] matrix, String s1, String s2, int row, int col, StringBuffer sb) {
        if (row == 0 || col == 0) {
            return sb;
        }
        if (s1.charAt(row - 1) == s2.charAt(col - 1)) {
            lcsRec(matrix, s1, s2, row - 1, col - 1, sb);
            sb.append(s1.charAt(row - 1));
            return sb;
        } else if (matrix[row - 1][col] >= matrix[row][col]) {
            lcsRec(matrix, s1, s2, row - 1, col, sb);
        } else {
            lcsRec(matrix, s1, s2, row, col - 1, sb);
        }
        return sb;
    }

}



