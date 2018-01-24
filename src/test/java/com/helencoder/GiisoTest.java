package com.helencoder;

import com.helencoder.textrank.TextrankOptimization;
import com.helencoder.util.BasicUtil;
import com.helencoder.util.FileIO;
import com.helencoder.util.json.JSONArray;
import com.helencoder.util.json.JSONObject;
import giiso.APIRequest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Giiso测试
 *
 * Created by zhenghailun on 2018/1/24.
 */
public class GiisoTest {
    private static String secretId = "200840cab96842db991388d6ccadc887";
    private static String secretKey = "c515181b104c4133afa0840b95e1b05c";

    public static void main(String[] args) throws Exception{

        APIRequest apiRequest = new APIRequest(secretId, secretKey);
        String requestUrl = "http://robotapi.giiso.com/openapi/text/getKeywords";

        TextrankOptimization textrankOptimization = new TextrankOptimization();

        List<String> fileList = FileIO.getFileList("data/zixun");
        for (String filename : fileList) {
            String newsid = filename.replace(".txt", "");
            String filepath = "data/zixun/" + filename;
            String content = FileIO.getFileData(filepath);

            if (content.length() >= 5000) {
                continue;
            }

            // TextRank提取关键词
            textrankOptimization.analyze(content, 5);
            List<String> keywordsList = textrankOptimization.getKeywordsList(10);
            String textrankStr = BasicUtil.mkString(keywordsList, ",");

            // Giiso提取关键词
            Map<String, String> contentMap = new HashMap<>();
            contentMap.put("content", content);

            String response = apiRequest.post(requestUrl, contentMap);
            System.out.println(response);
            JSONObject jsonObject = new JSONObject(response);
            JSONObject dataJson = new JSONObject(jsonObject.getString("data"));
            JSONArray jsonArray = new JSONArray(dataJson.getString("keywordList"));
            List<String> giisoList = new ArrayList<>();
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject innerJson = (JSONObject) jsonArray.get(i);
                System.out.println(innerJson.getString("key") + "\t" + innerJson.getString("value"));
                giisoList.add(innerJson.getString("key"));
            }
            String giisoStr = BasicUtil.mkString(giisoList, ",");

            FileIO.appendFile("keywords.txt", newsid + "\t\t" + giisoStr + "\t\t" + textrankStr);

        }


//        String content = FileIO.getFileData("record.txt");
//        Map<String, String> contentMap = new HashMap<>();
//        contentMap.put("content", content);
//
//        String response = apiRequest.post(requestUrl, contentMap);
//        System.out.println(response);
//        JSONObject jsonObject = new JSONObject(response);
//        JSONObject dataJson = new JSONObject(jsonObject.getString("data"));
//        JSONArray jsonArray = new JSONArray(dataJson.getString("keywordList"));
//        for (int i = 0; i < jsonArray.length(); i++) {
//            JSONObject innerJson = (JSONObject) jsonArray.get(i);
//            System.out.println(innerJson.getString("key") + "\t" + innerJson.getString("value"));
//        }

    }
}
