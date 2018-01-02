package com.helencoder.knowledgeGraph;

import com.helencoder.util.FileIO;
import com.helencoder.util.Request;
import com.helencoder.util.json.JSONArray;
import com.helencoder.util.json.JSONObject;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.MultiGraph;

import java.util.*;

import static com.helencoder.util.LtpUtil.ltpResponseParse;

/**
 * 基于资讯的信息抽取(命名实体识别+语义角色标注)
 *  tag: who when where what
 *
 * Created by helencoder on 2017/12/29.
 */
public class WordMap {
    // GS样式设计
    private static String stylesheet = "graph { fill-color: white;} " +
            "node { fill-color: blue; size-mode: dyn-size; }" +
            "node.marked { fill-color: red; }" +
            "node.head { fill-color: green; }" +
            "node:clicked { fill-color: red; }" +
            "edge { fill-color: grey;}";
    // 汉语句子停用词
    private static String[] sentenceDelimiters = {"?", "!", ";", "？", "！", "。", "；", "……", "…", "\n"};

    public static void main(String[] args) throws Exception {
        // 词图展示
        //System.setProperty("org.graphstream.ui.renderer", "org.graphstream.ui.j2dviewer.J2DGraphRenderer");

        // 获取文本内容
        String content = FileIO.getFileData("public/map/6.txt");

        String[] arr = new String[] {"you", "wu"};
        List list = Arrays.asList(arr);
        System.out.println(arr.length);

//        String ltpUrl = "http://99.6.184.75:12345/ltp";
//        String response = Request.post(ltpUrl, "s=" + content + "&x=n&t=all");
//        List<JSONObject> jsonList = ltpResponseParse(response);
//        for (JSONObject json : jsonList) {
//            System.out.println(json.getString("id") + "\t" + json.getString("cont"));
//        }

        //System.out.println(response);

        System.exit(0);

        // 构造图
        Graph graph = new MultiGraph("mg");
        graph.setAutoCreate(true);
        graph.setStrict(false);

        graph.addAttribute("ui.stylesheet", stylesheet);
        graph.addAttribute("ui.quality");
        graph.addAttribute("ui.antialias");

        graph.display();

        LtpExtraction(graph, content);
    }

    /**
     * LTP信息抽取及构图
     */
    private static Graph LtpExtraction(Graph graph, String content) throws Exception {
        List<String> sentenceDelimitersList = Arrays.asList(sentenceDelimiters);
        // 文本句子拆分
        List<String> sentenceList = new ArrayList<String>();
        int index = 0;
        for (int i = 0; i < content.length(); i++) {
            if (sentenceDelimitersList.contains(String.valueOf(content.charAt(i)))) {
                sentenceList.add(content.substring(index, i));
                index = i + 1;
            }
        }

        List<List<JSONObject>> sentenceJsonList = new ArrayList<>();
        // LTP分词
        String ltpUrl = "http://99.6.184.75:12345/ltp";
        for (String sentence : sentenceList) {
            String response = Request.post(ltpUrl, "s=" + sentence + "&x=n&t=all");
            List<JSONObject> jsonList = ltpResponseParse(response);
            sentenceJsonList.add(jsonList);
        }

        // 遍历构图
        for (List<JSONObject> jsonList : sentenceJsonList) {
            Map<String, JSONObject> wordDetailsMap = detailsMap(jsonList);
            for (JSONObject json : jsonList) {
                // 命名实体分析
                // 暂时仅对单独成词的命名实体进行研究
                String ner = json.getString("ne");
                if (!ner.equals("O") && String.valueOf(ner.charAt(0)).equals("S")) {
                    graph.addNode(json.getString("cont"));
                    System.out.println(json.toString());
                }

                // 语义角色标注
                System.out.println(json.toString());
//                String srl = json.getString("arg");
//                JSONArray jsonArray = new JSONArray(srl);
//                for (int i = 0; i < jsonArray.length(); i++) {
//                    JSONObject tmpJson = jsonArray.getJSONObject(i);
//                    System.out.println(tmpJson);
//                }
            }
        }

        for (Node node : graph) {
            node.addAttribute("ui.label", node.getId());
        }

        return graph;
    }

    /**
     * 命名实体信息抽取
     */
    private static List<String> nerExtraction(List<JSONObject> jsonList) throws Exception {
        List<String> nerList = new ArrayList<String>();

        StringBuilder sb = new StringBuilder();
        for (JSONObject json : jsonList) {
            // 命名实体分析
            String ner = json.getString("ne");
            if (!ner.equals("O")) {
                switch (String.valueOf(ner.charAt(0))) {
                    case "B":
                        sb = new StringBuilder();
                        sb.append(json.getString("cont"));
                        break;
                    case "I":
                        sb.append(json.getString("cont"));
                        break;
                    case "E":
                        sb.append(json.getString("cont"));
                        nerList.add(sb.toString());
                        break;
                    case "S":
                        sb = new StringBuilder();
                        nerList.add(json.getString("cont"));
                        break;
                    default:
                        break;
                }
            }
        }

        for (String ner : nerList) {
            System.out.println(ner);
        }

        return nerList;
    }

    /**
     * 数据暂存
     */
    private static Map<String, JSONObject> detailsMap(List<JSONObject> jsonList) throws Exception {
        Map<String, JSONObject> map = new HashMap<String, JSONObject>();

        for (JSONObject json : jsonList) {
            map.put(json.getString("id"), json);
        }

        return map;
    }

}
