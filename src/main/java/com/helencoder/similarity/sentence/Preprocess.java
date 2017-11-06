package com.helencoder.similarity.sentence;

import com.helencoder.util.json.JSONException;
import com.helencoder.util.json.JSONObject;

import java.util.*;

import static com.helencoder.util.LtpUtil.ltpRequest;
import static com.helencoder.util.LtpUtil.ltpResponseParse;

/**
 * 句子相似度数据预处理
 *  利用LTP组件获取有效搭配对、优化分词、命名实体
 *
 * Created by helencoder on 2017/9/18.
 */
public class Preprocess {
    // 过滤词性设置
    private static String[] filterPos = {"wp", "u", "c", "p", "nd", "o", "e", "g", "h", "k", "q", "d"};
    private static String[] pairsPos = {"a", "n", "v"};

    /**
     * 获取句子相似性计算必要信息(有效匹配对、命名实体、过滤分词、优化分词)
     *
     * @param sentence 待检测句子
     *
     * @return detailsMap (optimisedWordsList: 优化分词 namedEntityList: 命名实体
     *                      effectivePairsList: 有效匹配对 filterWordsList: 过滤分词)
     */
    public static Map<String, List<String>> getDetails(String sentence) {
        String s = sentence;
        String x = "n";
        String t = "all";   // 主要利用词性标注、命名实体识别、依存句法分析
        // 请求LTP组件
        String response = ltpRequest(s, x, t, false);
        List<JSONObject> jsonObjectList = ltpResponseParse(response);

        // 过滤分词
        List<String> filterWordsList = new ArrayList<String>();
        // 优化分词
        List<String> optimisedWordsList = new ArrayList<String>();
        // 命名实体
        List<String> namedEntityList = new ArrayList<String>();
        // 获取有效匹配对
        List<String> effectivePairsList = new ArrayList<String>();

        // 句子核心词信息
        String coreWord = "";
        String coreIndex = "0";

        // 优化分词信息(应对连续优化问题)
        String lastWord = "";
        String lastIndex = "0";

        // 过滤词性设置
        List<String> filterPosList = Arrays.asList(filterPos);

        for (JSONObject json : jsonObjectList) {
            try {
                // 获取句子核心词
                if (json.has("relate") && json.getString("relate").equals("HED")) {
                    coreWord = json.getString("cont");
                    coreIndex = json.getString("id");
                }
                // 获取命名实体
                if (json.has("ne") && !json.getString("ne").equals("O")) {
                    namedEntityList.add(json.getString("cont"));
                }
                // 获取优化分词
                if (json.has("relate") && json.getString("relate").equals("ATT") && json.getString("pos").equals("n") && (json.getInt("id") + 1) == json.getInt("parent")) {
                    // 获取其对应的parent的id号
                    String tmpWord = json.getString("cont");
                    int parentid = json.getInt("parent");

                    if (!lastIndex.equals(json.getString("id"))) {
                        // 获取对应的父类信息
                        JSONObject parentData = jsonObjectList.get(parentid);
                        String parentWord = parentData.getString("cont");
                        optimisedWordsList.add(tmpWord + parentWord);

                        // 记录已处理信息
                        lastIndex = parentid + "";
                    } else {
                        lastIndex = json.getString("id");
                    }
                }
                // 获取过滤分词
                if (!filterPosList.contains(json.getString("pos"))) {
                    filterWordsList.add(json.getString("cont"));
                }

            } catch (JSONException ex) {
                ex.printStackTrace();
            }
        }

        List<String> pairsPosList = Arrays.asList(pairsPos);
        // 获取有效匹配对
        for (JSONObject json : jsonObjectList) {
            try {
                if (json.has("parent") && json.getString("parent").equals(coreIndex)
                        && json.has("pos") && pairsPosList.contains(json.getString("pos"))) {
                    effectivePairsList.add(coreWord + "_" + json.getString("cont"));
                }
            } catch (JSONException ex) {
                ex.printStackTrace();
            }
        }

        // 过滤分词
        System.out.println("过滤分词: ");
        for (String list : filterWordsList) {
            System.out.println(list);
        }

        // 优化分词
        System.out.println("优化分词: ");
        for (String list : optimisedWordsList) {
            System.out.println(list);
        }

        // 命名实体
        System.out.println("命名实体: ");
        for (String list : namedEntityList) {
            System.out.println(list);
        }

        // 有效匹配对
        System.out.println("有效匹配对: ");
        for (String list : effectivePairsList) {
            System.out.println(list);
        }

        // 数据记录
        Map<String, List<String>> detailsMap = new HashMap<String, List<String>>();
        detailsMap.put("optimisedWordsList", optimisedWordsList);
        detailsMap.put("namedEntityList", namedEntityList);
        detailsMap.put("effectivePairsList", effectivePairsList);
        detailsMap.put("filterWordsList", filterWordsList);

        return detailsMap;
    }

}
