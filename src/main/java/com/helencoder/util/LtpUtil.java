package com.helencoder.util;

import com.helencoder.util.json.JSONException;
import com.helencoder.util.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * LTP组件调用类
 *
 * Created by helencoder on 2017/9/18.
 */
public class LtpUtil {
    private static String api_key = "31u359Z1F6ixrIEn7cKhPPhirv8P5fQvoAfZIJwd";

    public static void main(String[] args) {
        // 测试用例


    }

    /**
     * LTP组件分词外部调用方法
     *
     * @param data 带分词的句子或文本
     * @param optFlag true 分词优化 false 未优化
     * @param posFlag true 附带词性 false 不附带词性
     * @param filterFlag true 词性过滤 false 未过滤
     * @return List<String> (单词list 或者 单词\t词性)
     *
     */
//    public static List<String> ws(String data, Boolean optFlag, Boolean posFlag, Boolean filterFlag) {
//        String s = data;
//        String x = "n";
//        String t = "dp";
//
//        String response = ltpRequest(s, x, t);
//        List<JSONObject> originalList = ltpResponseParse(response);
//
//    }

    /**
     * LTP组件分词优化方法(默认依存句法分析)
     *
     * @param data 待分词句子或文本
     * @return List<String> 分词优化后的单词list
     */
    public static List<String> run(String data) {
        String s = data;
        String x = "n";
        String t = "dp";

        String response = ltpRequest(s, x, t);
        List<JSONObject> originalList = ltpResponseParse(response);

        // 分词优化
        List<String> optimizeList = segOptimization(originalList);
        return optimizeList;
    }

    /**
     * LTP词性过滤
     *
     * @param wordData 分词后结果(单词\t词性),某些不包含词性的单词为已处理过,默认保留
     * @return Boolean true 保留 false 过滤
     */
    public static Boolean isWordAllow(String wordData) {
        if (wordData.indexOf("\t") == -1) {
            return true;
        }
        String[] filterPos = {"wp", "u", "c", "p", "nd", "o", "e", "g", "h", "k", "q"};
        List<String> filterPosList = Arrays.asList(filterPos);

        if (filterPosList.contains(wordData.split("\t")[1])) {
            return false;
        }
        return true;
    }

    /**
     * LTP组件请求方法
     *
     * 请求参数和数据 形式"s=xxx&x=xxx&t=xxx"
     * @param s 输入字符串,在xml选项x为n的时候,代表输入句子,为y时代表输入xml
     * @param x 用以志明是否使用xml
     * @param t 用以指明分析目标,t可以为分词(ws),词性标注(pos),命名实体识别(ner),依存句法分析(dp),语义角色标注(srl)或者全部任务(all)
     *
     * @return json数据(自定义方法解析)
     */
    public static String ltpRequest(String s, String x, String t) {
        // 离线版本
        String url = "http://99.6.184.75:12345/ltp";
        String param = "s=" + s + "&x=" + x + "&t=" + t;
        String response = Request.post(url, param);

        // 在线版本
//        String api_url = "http://api.ltp-cloud.com/analysis/";
//        String pattern = "all";
//        String format = "json";
//
//        String param = "api_key=" + api_key + "&text=" + s + "&pattern=" + pattern + "&format=" + format;
//        String response = Request.post(api_url, param);

        return response;
    }

    /**
     * LTP server返回数据解析
     *  基本思路: 遍历查找,配对截取{}
     *
     * @param data ltp返回的json数据格式 (三层[]中包含多个{}，每个分词结果对应一个{})
     * @return List<JSONObeject> 分词信息list(包含词性,依存句法信息等的json对象)
     */
    public static List<JSONObject> ltpResponseParse(String data) {
        List<JSONObject> list = new ArrayList<JSONObject>();

        // '{'和'}'两个符号索引值和出现次数记录
        int leftIndex = 0;
        int leftCount = 0;
        int rightIndex = 0;
        int rightCount = 0;
        for (int i = 0; i < data.length(); i++) {
            if (leftCount == rightCount && leftCount != 0) {
                // 将原有统计数置0
                leftCount = 0;
                rightCount = 0;
                // 此时进行截取
                String str = data.substring(leftIndex, rightIndex + 1);
                // json数据解析
                try {
                    JSONObject json = new JSONObject(str);
                    list.add(json);
                } catch (JSONException ex) {
                    ex.printStackTrace();
                }
            }

            // 碰到左侧'{'的情况
            if (data.charAt(i) == '{') {
                if (leftCount == 0) {
                    leftIndex = i;
                }
                leftCount++;
            }

            // 碰到右侧'}'的情况
            if (data.charAt(i) == '}') {
                rightCount++;
                rightIndex = i;
            }
        }
        return list;
    }

    /**
     * 分词优化,遇到id为0重新进行截取计算
     *  基本思路:按照句子(id为0)进行截取,按照句子进行分词优化
     *
     * @param list 未经优化的ltp分词信息结果list
     * @param list<String> 优化后的分词结果list(单词\t词性)
     */
    private static List<String> segOptimization(List<JSONObject> list) {
        // 分词优化的单词记录
        List<String> words = new ArrayList<String>();
        // 进行遍历截取
        int startIndex = 0;
        List<JSONObject> listCopy = new ArrayList<JSONObject>(list);
        for (int i = 1; i < list.size(); i++) {
            try {
                JSONObject json = list.get(i);
                if (json.getInt("id") == 0) {
                    int endIndex = i;
                    // list截取

                    List<JSONObject> sentenceList = list.subList(startIndex, endIndex);
                    // 进行分词优化
                    List<String> segWords = segSentenceOptimization(sentenceList);
                    words.addAll(segWords);
                    startIndex = i;
                }
                // 当进行到最后时进行进一步截取
                if (i == list.size() - 1) {
                    List<JSONObject> sentenceList = list.subList(startIndex, list.size());
                    List<String> segWords = segSentenceOptimization(sentenceList);
                    words.addAll(segWords);
                }

            } catch (JSONException ex) {
                ex.printStackTrace();
            }
        }

        return words;
    }

    /**
     * LTP组件分词优化(单个句子)(依存句法分析)
     *  基本思路:利用依存句法分析,将定中关系(ATT)进行合成,仅对相邻的单词进行操作
     *
     * @param list 单个句子的ltp分词信息结果list
     * @return List<String> 优化分词结果list(单词\t词性)
     */
    private static List<String> segSentenceOptimization(List<JSONObject> list) {
        // 分词优化的单词记录
        List<String> words = new ArrayList<String>();
        // 已处理的id号记录
        List<Integer> handleFlag = new ArrayList<Integer>();
        for (int i = 0; i < list.size(); i++) {
            // 是否已处理的检测
            if (handleFlag.contains(i)) {
                continue;
            }
            handleFlag.add(i);

            JSONObject json = list.get(i);
            try {
                // 定中关系(ATT)的合并,仅对前后单词进行合并
                //if (json.has("relate") && (json.getString("relate").equals("ATT") || json.getString("relate").equals("ADV")) && (i + 1) == json.getInt("parent")) {
                if (json.has("relate") && json.getString("relate").equals("ATT") && json.getString("pos").equals("n") && (i + 1) == json.getInt("parent")) {
                    // 获取其对应的parent的id号
                    String tmpWord = json.getString("cont");
                    int parentid = json.getInt("parent");
                    handleFlag.add(parentid);
                    // 获取对应的父类信息
                    JSONObject parentData = list.get(parentid);
                    String parentWord = parentData.getString("cont");
                    String word = tmpWord + parentWord;
                    words.add(word);
                } else {
                    // 获取单词的词性
                    words.add(json.getString("cont") + "\t" + json.getString("pos"));
                }
            } catch (JSONException ex) {
                ex.printStackTrace();
            }
        }

        return words;
    }

}
