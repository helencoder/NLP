package com.helencoder.util;

import java.util.Random;

/**
 * 公共方法类
 *
 * Created by zhenghailun on 2017/10/31.
 */
public class BasicUtil {

    public static String verifyCode(int length) {
        String dict = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
        StringBuilder verifyCode = new StringBuilder(length);
        Random random = new Random();
        for(int i = 0; i < length; i++){
            verifyCode.append(dict.charAt(random.nextInt(dict.length())));
        }
        return verifyCode.toString();
    }

}
