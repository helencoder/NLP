package com.helencoder.util;

import org.nlpcn.commons.lang.finger.FingerprintService;
import org.nlpcn.commons.lang.finger.SimHashService;
import org.nlpcn.commons.lang.jianfan.JianFan;
import org.nlpcn.commons.lang.pinyin.Pinyin;

import java.util.ArrayList;
import java.util.List;

/**
 * NLP相关组件(nlp-lang)
 *
 * Created by helencoder on 2017/11/8.
 */
public class NlpUtil {

    /**
     * 汉字转拼音
     *  注:1、未转换成功的汉字返回null  2、有些汉字可能无法返回声调
     *
     * @param str 待转换的汉字字符串
     * @param flag 是否包含声调, true包括;false不包括
     * @return List
     */
    public static List<String> wordToPinyin(String str, boolean flag) {
        List<String> result = new ArrayList<String>();
        if (flag) {
            result.addAll(Pinyin.tonePinyin(str));
        } else {
            result.addAll(Pinyin.pinyin(str));
        }

        return result;
    }

    /**
     * 简繁互换
     *
     * @param str 待转换中文字符串
     * @param flag true简转繁;flase繁转简
     * @return String
     */
    public static String simplifiedTraditional(String str, boolean flag) {
        if (flag) {
            return JianFan.j2f(str);
        } else {
            return JianFan.f2j(str);
        }
    }

    /**
     * 获取文本的SimHash值
     *
     * @param content 文本
     * @return long
     */
    public static long simHashFingerprint(String content) {
        SimHashService simHashService = new SimHashService();
        return simHashService.fingerprint(content);
    }

    /**
     * 获取文本指纹
     *
     * @param content 文本
     * @return String
     */
    public static String fingerprint(String content) {
        FingerprintService fingerprintService = new FingerprintService();
        return fingerprintService.fingerprint(content);
    }

}
