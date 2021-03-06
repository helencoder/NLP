package com.helencoder.ltp;

import com.helencoder.util.json.JSONException;
import com.helencoder.util.json.JSONObject;

import java.util.*;

import static com.helencoder.util.LtpUtil.ltpRequest;
import static com.helencoder.util.LtpUtil.ltpResponseParse;

/**
 * 利用LTP组件进行分词优化及关键词提取
 *
 * Created by helencoder on 2017/11/6.
 */
public class LtpOptimise {
    
    // 过滤词性设置
    private static String[] filterPos = {"wp", "u", "c", "p", "nd", "o", "e", "g",
            "h", "k", "q", "d", "a", "b", "i", "m", "x", "r", "v", "nt", "z"};
    // 优化分词过滤词性设置
    private static String[] optimisePos = {"r", "m", "d", "q", "nt", "nd", "c"};
    // 优化分词关系设置
    private static String[] optimiseTag = {"ATT", "VOB", "SBV"};

    /**
     * 分词过滤、优化分词
     * 规则：
     *  1、连续或间隔(ATT、VOB、SBV)合并,包括前后两种情况
     *  2、双动词合并剔除
     *  3、优化分词和过滤分词比对
     */
    public static Map<String, List<String>> run(String sentence) {
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

        // 优化分词信息(应对连续优化问题)
        String lastWord = "";
        String lastIndex = "-1";

        // 过滤词性设置
        List<String> filterPosList = Arrays.asList(filterPos);
        List<String> optimisePosList = Arrays.asList(optimisePos);
        List<String> optimiseTagList = Arrays.asList(optimiseTag);

        // 以分词结果的id为键,相应的json数据为键值
        Map<String, JSONObject> detailMap = new HashMap<String, JSONObject>();
        Map<String, Word> wordDetailMap = new HashMap<String, Word>();
        // 信息记录
        for (JSONObject json : jsonObjectList) {
            try {
                if (json.has("id")) {
                    detailMap.put(json.getString("id"), json);
                    wordDetailMap.put(json.getString("cont"), new Word(json));
                }
                // 获取命名实体
                if (json.has("ne") && !json.getString("ne").equals("O")) {
                    namedEntityList.add(json.getString("cont"));
                }
                // 获取过滤分词
                if (!filterPosList.contains(json.getString("pos")) && json.getString("cont").length() > 1) {
                    filterWordsList.add(json.getString("cont"));
                }
            } catch (JSONException ex) {
                ex.printStackTrace();
            }
        }

        // 获取优化分词(仅针对位置上连续的单词合并优化)
        StringBuffer sb = new StringBuffer();
        // 已处理的单词index
        Set<String> optimiseSet = new HashSet<>();
        for (JSONObject json : jsonObjectList) {
            try {

                if ((json.has("relate") && optimiseTagList.contains(json.getString("relate"))) &&
                        (json.has("pos") && !optimisePosList.contains(json.getString("pos")))) {

                    if (json.has("id") && !optimiseSet.contains(json.getString("id"))) {

                        // 获取对应的parent的id值
                        int parentId = json.getInt("parent");
                        int localId = json.getInt("id");

                        int max = Math.max(parentId, localId);
                        int min = Math.min(parentId, localId);
                        int interval = max - min;
                        if (interval > 1) {    // 有间隔
                            // 遍历优化
                            for (int i = min; i <= max; i++) {
                                String id = String.valueOf(i);
                                JSONObject tmpData = detailMap.get(id);
                                if (optimiseTagList.contains(tmpData.getString("relate")) &&
                                        !optimisePosList.contains(tmpData.getString("pos"))) {
                                    sb.append(tmpData.getString("cont"));
                                } else {
                                    sb = new StringBuffer();
                                    break;
                                }
                            }
                            if (!sb.toString().isEmpty()) {
                                optimisedWordsList.add(sb.toString());
                                for (int i = min; i <= max; i++) {
                                    optimiseSet.add(String.valueOf(i));
                                }

                                sb = new StringBuffer();
                            }
                        } else if (interval == 1){    // 无间隔
                            if ((detailMap.get(String.valueOf(min)).getString("pos").equals("v") &&
                                    detailMap.get(String.valueOf(max)).getString("pos").equals("v")) ||
                                    (optimisePosList.contains(detailMap.get(String.valueOf(min)).getString("pos")) || optimisePosList.contains(detailMap.get(String.valueOf(max)).getString("pos")))) {   // 连续双动词过滤&连接词不符合优化词性限制

                            } else {
                                sb.append(detailMap.get(String.valueOf(min)).getString("cont"));
                                sb.append(detailMap.get(String.valueOf(max)).getString("cont"));
                                while (detailMap.get(String.valueOf(max)).getInt("parent") - max == 1) {
                                    sb.append(detailMap.get(String.valueOf(max + 1)).getString("cont"));
                                    max++;
                                }
                            }


                            if (!sb.toString().isEmpty()) {
                                optimisedWordsList.add(sb.toString());
                                for (int i = 0; i < sb.length() ; i++) {
                                    optimiseSet.add(String.valueOf(min + i));
                                }

                                sb = new StringBuffer();
                            }


                        }
                    }
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

        System.out.println();

        // 优化分词
        System.out.println("优化分词: ");
        for (String list : optimisedWordsList) {
            System.out.println(list);
        }

        System.out.println();

        // 命名实体
        System.out.println("命名实体: ");
        for (String list : namedEntityList) {
            System.out.println(list);
        }

        System.out.println();

        // 对于过滤分词和优化分词进行统一的整合过滤

        // 过滤分词添加(假如其在优化分词中不存在,则保留)
        List<String> selectList = new ArrayList<String>();
        for (int i = 0; i < filterWordsList.size(); i++) {
            String word = filterWordsList.get(i);
            boolean flag = false;
            for (int j = 0; j < optimisedWordsList.size(); j++) {
                String optimiseWord = optimisedWordsList.get(j);
                if (optimiseWord.indexOf(word) != -1) {
                    flag = true;
                }
            }
            if (!flag) {
                selectList.add(word);
            }
        }

        // 优化分词过滤
        for (int i = 0; i < optimisedWordsList.size(); i++) {
            String optimiseWord = optimisedWordsList.get(i);
            System.out.println(optimiseWord);

            selectList.add(optimiseWord);

//            boolean flag = false;
//            for (int j = 0; j < filterWordsList.size(); j++) {
//                String word = filterWordsList.get(j);
//                if (!optimiseWord.isEmpty()) {
//                    optimiseWord = optimiseWord.replace(word, "");
//                }
//            }
//            if (optimiseWord.isEmpty()) {
//                selectList.add(optimisePosList.get(i));
//            } else {
//                if (wordDetailMap.containsKey(optimiseWord)) {
//                    Word wordDetail = wordDetailMap.get(optimiseWord);
//                    if (wordDetail.getPos() == "n") {
//                        selectList.add(optimisePosList.get(i));
//                    }
//                } else {
//                    selectList.add(optimisePosList.get(i));
//                }
//            }
        }

        // 合并分词
        System.out.println("合并分词: ");
        for (String list : selectList) {
            System.out.println(list);
        }

        System.out.println();

        // 数据记录
        Map<String, List<String>> detailsMap = new HashMap<String, List<String>>();
        detailsMap.put("filterWordsList", filterWordsList);
        detailsMap.put("optimisedWordsList", optimisedWordsList);
        detailsMap.put("namedEntityList", namedEntityList);
        detailsMap.put("selectList", selectList);

        return detailsMap;
    }

}
