package com.helencoder.textrank.graphStream;

import com.hankcs.hanlp.seg.common.Term;
import com.helencoder.segmentation.Segmentation;
import com.helencoder.util.FileIO;
import com.helencoder.util.LtpUtil;
import com.helencoder.util.Request;
import com.helencoder.util.json.JSONObject;
import org.graphstream.graph.Edge;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.MultiGraph;

import java.util.*;

import static com.helencoder.util.LtpUtil.ltpResponseParse;

/**
 * WordMap
 *
 * Created by helencoder on 2017/12/26.
 */
public class WordMap {
    public static void main(String[] args) throws Exception {
        // 词图展示

        // 获取文本内容
        String content = FileIO.getFileData("file.txt");

        // 构造图
        Graph graph = new MultiGraph("mg");
        graph.setAutoCreate(true);
        graph.setStrict(false);

        graph.addAttribute("ui.quality");
        graph.addAttribute("ui.antialias");

        graph.display();

        // 共现关系
        //CooccurrenceMap(graph, content);

        // LTP依存句法关系
        LtpDependencyParserMap(graph, content);

    }

    /**
     * 基于共现关系的词图Map
     *
     * @param graph 图
     * @param content 文本
     */
    public static void CooccurrenceMap(Graph graph, String content) {
        // hanlp分词
        Segmentation segmentUtil = new Segmentation();
        List<Term> termList = segmentUtil.segToList(content, true);

        // 词索引构造
        Map<Integer, String> indexMap = new HashMap<Integer, String>();
        int index = 0;
        for (Term term : termList) {
            indexMap.put(index, term.word);
            index++;
        }

        Map<String, List<String>> wordMap = new HashMap<String, List<String>>();
        int flag = 0;
        int window = 5;
        for (Term term : termList) {
            // 选取当前相邻的数字进行添加
            int startIndex = flag - window >= 0 ? flag - window : 0;
            int endIndex = flag + window < termList.size() ? flag + window : termList.size() - 1;
            List<String> tmpSet = new ArrayList<String>();
            for (int i = startIndex; i <= endIndex; i++) {
                if (i != flag) {
                    tmpSet.add(indexMap.get(i));
                }
            }
            wordMap.put(term.word, tmpSet);

            flag++;
        }

        for (Map.Entry<String, List<String>> entry : wordMap.entrySet()) {
            String word = entry.getKey();
            List<String> tmpList = entry.getValue();
            for (String coWord : tmpList) {
                Edge edge = graph.addEdge(word + "--" + coWord, word, coWord);
            }
        }

        for (Node node : graph) {
            node.addAttribute("ui.label", node.getId());
        }

    }


    // 汉语句子停用词
    private static String[] sentenceDelimiters = {"?", "!", ";", "？", "！", "。", "；", "……", "…", "\n"};
    // 过滤词性设置
    private static String[] filterPos = {"wp", "u", "c", "p", "nd", "o", "e", "g", "h", "k", "q", "d",
            "a", "b", "i", "m", "x", "r", "v", "nt", "z"};
    // 依存句法关系类型设置
    private static String[] filterDependencyParser = {"SBV", "ATT", "ADV", "HED"};

    /**
     * 基于LTP依存句法的词图Map
     *
     * @param graph 图
     * @param content 文本
     */
    public static void LtpDependencyParserMap(Graph graph, String content) throws Exception {
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
            String response = Request.post(ltpUrl, "s=" + sentence + "&x=n&t=dp");
            List<JSONObject> jsonList = ltpResponseParse(response);
            sentenceJsonList.add(jsonList);
        }

        // 过滤词性设置
        List<String> filterPosList = Arrays.asList(filterPos);
        List<String> filterDependencyParserList = Arrays.asList(filterDependencyParser);

        // 遍历并构建图
        for (List<JSONObject> jsonList : sentenceJsonList) {
            Map<Integer, JSONObject> indexJsonMap = new HashMap<>();
            for (JSONObject json : jsonList) {
                indexJsonMap.put(json.getInt("id"), json);
            }

            for (Map.Entry<Integer, JSONObject> entry : indexJsonMap.entrySet()) {
                String word = entry.getValue().getString("cont");
                String relate = entry.getValue().getString("relate");
                int parentId = entry.getValue().getInt("parent");
                if (parentId != -1 && word.length() > 1 && filterDependencyParserList.contains(relate)) {
                    String parentWord = indexJsonMap.get(parentId).getString("cont");
                    if (parentWord.length() > 1) {
                        Edge edge = graph.addEdge(parentWord + "--" + word, parentWord, word);
                    }
                }

                // 各句子核心词查询
//                if (parentId == -1 && word.length() > 1) {
//                    Edge edge = graph.addEdge( "HEAD--" + word, "HEAD", word);
//                }
            }
        }

        for (Node node : graph) {
            node.addAttribute("ui.label", node.getId());
        }

    }

}
