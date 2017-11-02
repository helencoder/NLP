package com.helencoder.similarity.sentence;

import com.helencoder.util.json.JSONException;
import com.helencoder.util.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static com.helencoder.util.LtpUtil.ltpRequest;
import static com.helencoder.util.LtpUtil.ltpResponseParse;

/**
 * 利用LTP组件的依存句法获取有效匹配对
 *  获取句子核心词(HED)、然后获取相应的名词、动词、形容词
 *
 * Created by helencoder on 2017/9/18.
 */
public class ObtainPairs {

    /**
     * 获取有效匹配对(全句核心词--核心词动词、名词、形容词)
     *
     * @param sentence 句子
     *
     * @return paris 有效匹配对
     */
    public static List<String> run(String sentence) {
        String s = sentence;
        String x = "n";
        String t = "all";   // 主要利用词性标注、命名实体识别、依存句法分析

        String response = ltpRequest(s, x, t);
        List<JSONObject> originalList = ltpResponseParse(response);

        String coreWord = "";
        String coreIndex = "0";
        // 获取句子核心词
        for (JSONObject json : originalList) {
            try {
                if (json.has("relate") && json.getString("relate").equals("HED")) {
                    coreWord = json.getString("cont");
                    coreIndex = json.getString("id");
                }
            } catch (JSONException ex) {
                ex.printStackTrace();
            }
        }

        // 获取有效匹配对
        List<String> pairs = new ArrayList<String>();
        for (JSONObject json : originalList) {
            try {
                if (json.has("parent") && json.getString("parent").equals(coreIndex) && json.has("pos")
                        && (json.getString("pos").equals("a") || json.getString("pos").equals("n") ||
                        json.getString("pos").equals("v"))) {
                    String word = json.getString("cont");
                    pairs.add(coreWord + "_" + word);
                }
            } catch (JSONException ex) {
                ex.printStackTrace();
            }
        }

        return pairs;
    }

}
