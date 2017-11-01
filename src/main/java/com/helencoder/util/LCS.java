package com.helencoder.util;

/**
 * 最长公共子序列匹配
 *
 * Created by helencoder on 2017/11/1.
 */
public class LCS {

    public static void main(String[] args) {
        // TODO Auto-generated method stub
        String str1 = "fjssharpsword";
        String str2 = "helloworld";

        String lcsStr = lcs(str1, str2);
        System.out.println(lcsStr);
    }

    public static String lcs(String str1, String str2) {
        if (str1.isEmpty() || str2.isEmpty()) {
            return "";
        }

        // 生成lcs递归矩阵
        int[][] matrix = lcsMatrix(str1, str2);

        // 输出lcs最长子序列
        return lcsRec(matrix, str1, str2, str1.length(), str2.length(), new StringBuffer()).toString();
    }

    /**
     * 最长子序列匹配--生成LCS矩阵
     *
     * 矩阵填充公式
     * 1、c[i,j] = 0 (i=0 or j=0)
     * 2、c[i,j] = c[i-1, j-1] + 1 (i,j>0 and i=j)
     * 3、c[i,j] = max[c[i,j-1], c[i-1,j]] (i,j>0 and i!=j)
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


