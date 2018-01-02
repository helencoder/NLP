package com.helencoder.textrank.graphStream;

import com.hankcs.hanlp.HanLP;
import com.hankcs.hanlp.seg.Segment;
import com.hankcs.hanlp.seg.common.Term;
import com.helencoder.util.BasicUtil;
import com.helencoder.util.FileIO;
import com.helencoder.util.Request;
import com.helencoder.util.json.JSONObject;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.*;

import java.util.*;

import static com.helencoder.util.LtpUtil.ltpResponseParse;
import static jodd.util.ThreadUtil.sleep;

/**
 * WordMap
 *
 * Created by helencoder on 2017/12/26.
 */
public class WordMap {



    public static void main(String[] args) throws Exception {
        // 词图展示
        //System.setProperty("org.graphstream.ui.renderer", "org.graphstream.ui.j2dviewer.J2DGraphRenderer");

        // 获取文本内容
        String content = FileIO.getFileData("public/map/6.txt");

        // 构造图
        Graph graph = new MultiGraph("mg");
        graph.setAutoCreate(true);
        graph.setStrict(false);

        graph.addAttribute("ui.stylesheet", stylesheet);
        graph.addAttribute("ui.quality");
        graph.addAttribute("ui.antialias");

        graph.display();

        // 共现关系
        Graph textGraph = CooccurrenceMap(graph, content);

        // LTP依存句法关系
//        Graph textGraph = LtpDependencyParserMap(graph, content);

        // 展示
        optVisualisation(textGraph);

        TextRankGS(textGraph);
    }


    // 保留词性设置(hanlp)
    private static String[] hanlpRetainPos = {"a", "ad", "an", "i", "j", "l", "v", "vg", "vd", "vn"};

    /**
     * 基于共现关系的词图Map
     *
     * @param graph 图
     * @param content 文本
     */
    public static Graph CooccurrenceMap(Graph graph, String content) {
        // hanlp分词
        Segment segment = HanLP.newSegment()
                .enableNameRecognize(true)
                .enableOrganizationRecognize(true)
                .enableNumberQuantifierRecognize(true)
                .enablePlaceRecognize(true)
                .enableTranslatedNameRecognize(true);
        List<Term> termList = segment.seg(content);

        // hanlp保留词性设置
        List<String> retainPosList = Arrays.asList(hanlpRetainPos);

        Map<String, Term> wordDetailsMap = new HashMap<String, Term>();
        // 词索引构造
        Map<Integer, String> indexMap = new HashMap<Integer, String>();
        for (int i = 0; i < termList.size(); i++) {
            Term term = termList.get(i);
            String word = term.word;
            wordDetailsMap.put(word, term);
            // 词长限制
            if (word.length() < 2) {
                continue;
            }
            // 词性限制
            String pos = term.nature.toString();
            if (String.valueOf(pos.charAt(0)).equals("n") || retainPosList.contains(pos)) {
                System.out.println(term.word + "\t" + term.nature.toString());
                indexMap.put(i, term.word);
            }
        }

        // 共现词列表构造
        Map<String, List<String>> wordMap = new HashMap<String, List<String>>();
        int window = 5;
        for (int i = 0; i < termList.size(); i++) {
            String word = termList.get(i).word;
            // 选取当前相邻的数字进行添加
            int startIndex = i - window >= 0 ? i - window : 0;
            int endIndex = i + window < termList.size() ? i + window : termList.size() - 1;
            List<String> tmpList = new ArrayList<String>();
            for (int j = startIndex; j <= endIndex ; j++) {
                if (j != i && indexMap.get(j) != null) {
                    tmpList.add(indexMap.get(j));
                }
            }
            wordMap.put(word, tmpList);
        }

        // 获取hanlp词性权重设置Map
        Map<String, Double> hanlpWordWeightMap = wordWeight(true);

        // 构图
        for (Map.Entry<String, List<String>> entry : wordMap.entrySet()) {
            String word = entry.getKey();
            // 对应顶点根据词性设定权重
            double weight = 0;
            String pos = wordDetailsMap.get(word).nature.toString();
            if (String.valueOf(pos.charAt(0)).equals("n")) {
                weight = 0.8;
            } else if (retainPosList.contains(pos)) {
                weight = hanlpWordWeightMap.get(pos);
            }
            Node tmpNode = graph.addNode(word);
            tmpNode.setAttribute("weight", weight);
            tmpNode.setAttribute("pos", pos);

            List<String> tmpList = entry.getValue();
            for (String coWord : tmpList) {
                graph.addEdge(word + "--" + coWord, word, coWord, true);
            }
        }

        for (Node node : graph) {
            node.addAttribute("ui.label", node.getId());
        }

        return graph;
    }

    // 汉语句子停用词
    private static String[] sentenceDelimiters = {"?", "!", ";", "？", "！", "。", "；", "……", "…", "\n"};
    // 保留词性设置(LTP)
    private static String[] ltpRetainPos = {"a", "b", "i", "j", "n", "nh", "ni", "nl", "ns", "nz", "v", "ws"};
    // 依存句法关系类型设置
    private static String[] ltpRetainDependencyParser = {"SBV", "ATT", "ADV", "HED"};

    /**
     * 基于LTP依存句法的词图Map
     *
     * @param graph 图
     * @param content 文本
     */
    public static Graph LtpDependencyParserMap(Graph graph, String content) throws Exception {
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
        List<String> retainPosList = Arrays.asList(ltpRetainPos);
        List<String> retainDependencyParserList = Arrays.asList(ltpRetainDependencyParser);

        // 获取ltp词性权重设置Map
        Map<String, Double> ltpWordWeightMap = wordWeight(false);

        // 遍历并构建图
        for (List<JSONObject> jsonList : sentenceJsonList) {
            Map<Integer, JSONObject> indexJsonMap = new HashMap<Integer, JSONObject>();
            for (JSONObject json : jsonList) {
                indexJsonMap.put(json.getInt("id"), json);
            }

            for (Map.Entry<Integer, JSONObject> entry : indexJsonMap.entrySet()) {
                String word = entry.getValue().getString("cont");
                String pos = entry.getValue().getString("pos");
                String relate = entry.getValue().getString("relate");
                int parentId = entry.getValue().getInt("parent");

                // 词长限制
                if (word.length() < 2) {
                    continue;
                }

                // 词连接过滤设置
                if (parentId != -1  && retainDependencyParserList.contains(relate) && retainPosList.contains(pos)) {
                    String parentWord = indexJsonMap.get(parentId).getString("cont");
                    String parentPos = indexJsonMap.get(parentId).getString("pos");
                    if (parentWord.length() > 1 && retainPosList.contains(parentPos)) {
                        Node tmpNode = graph.addNode(word);
                        tmpNode.setAttribute("pos", pos);
                        // 对应顶点根据词性设定权重
                        if (ltpWordWeightMap.get(pos) != null) {
                            double weight = ltpWordWeightMap.get(pos);
                            tmpNode.setAttribute("weight", weight);
                        } else {
                            tmpNode.setAttribute("weight", 0);
                        }

                        graph.addEdge(parentWord + "--" + word, parentWord, word, true);
                    }
                }

            }
        }

        for (Node node : graph) {
//            System.out.println(node.getId() + "\t" + node.getDegree() + "\t" + node.getInDegree() + "\t" + node.getOutDegree());
//            if (node.getDegree() == 0) {
//                graph.removeNode(node);
//            }
            node.addAttribute("ui.label", node.getId());
        }

        return graph;
    }

    /**
     * 词性权重设置
     *
     * @param flag true hanlp词性设置; false ltp词性设置
     */
    private static Map<String, Double> wordWeight(boolean flag) {
        Map<String, Double> wordWeightMap = new HashMap<String, Double>();

        if (flag) {
            // hanlp词性设置(hanlp中名词权重直接设为0.8)
            wordWeightMap.put("a", 0.5);    // 形容词
            wordWeightMap.put("ad", 0.3);   // 副形词
            wordWeightMap.put("an", 0.6);   // 名形词
            wordWeightMap.put("i", 0.6);    // 成语
            wordWeightMap.put("j", 0.7);    // 简称略语
            wordWeightMap.put("l", 0.6);    // 习用语
            wordWeightMap.put("v", 0.3);    // 动词
            wordWeightMap.put("vg", 0.2);   // 动语素
            wordWeightMap.put("vd", 0.4);   // 副动词
            wordWeightMap.put("vn", 0.6);   // 名动词
        } else {
            // ltp词性设置
            wordWeightMap.put("a", 0.5);    // 形容词(美丽)
            wordWeightMap.put("b", 0.6);    // 名形词(大型,西式)
            wordWeightMap.put("i", 0.6);    // 成语(百花齐放)
            wordWeightMap.put("j", 0.7);    // 简称略语(公检法)
            wordWeightMap.put("n", 0.8);    // 名词(苹果)
            wordWeightMap.put("nh", 0.7);   // 人名(杜甫,汤姆)
            wordWeightMap.put("ni", 0.9);   // 机构名称(保险公司)
            wordWeightMap.put("nl", 0.6);   // 位置名称(城郊)
            wordWeightMap.put("ns", 0.6);   // 地名(北京)
            wordWeightMap.put("nz", 0.8);   // 专有名词(诺贝尔奖)
            wordWeightMap.put("v", 0.3);    // 动词(跑,学习)
            wordWeightMap.put("ws", 0.5);   // 外语(CPU)
        }

        return wordWeightMap;
    }

    // GS样式设计
    private static String stylesheet = "graph { fill-color: white;} " +
            "node { fill-color: blue; size-mode: dyn-size; }" +
            "node.marked { fill-color: red; }" +
            "node.head { fill-color: green; }" +
            "node:clicked { fill-color: red; }" +
            "edge { fill-color: grey;}";

    /**
     * 展示优化
     */
    private static void optVisualisation(Graph graph) {
        // 图遍历展示
        for (Node node : graph) {
            node.setAttribute("ui.class", "head");
            System.out.println(node.getId() + "\t" + node.getDegree());
            sleep(1000);
            Iterator<? extends Node> k = node.getBreadthFirstIterator();
            while (k.hasNext()) {
                Node next = k.next();
                System.out.println(next.getId());
                next.setAttribute("ui.class", "marked");
                sleep(100);
            }
        }
    }

    /**
     * TextRank
     *
     */
    private static void TextRankGS(Graph graph) {
        //graph.setNullAttributesAreErrors(true);

        Map<String, Double> wordWeightMap = new HashMap<String, Double>();
        // 图遍历
        for (Node node : graph) {
            Iterator<? extends Node> iterator = node.getNeighborNodeIterator();    // 有链接关系的顶点集

            double weight = 0.0;
            while (iterator.hasNext()) {
                Node linkNode = iterator.next();
                // 仅利用传入节点进行权重计算
                if (linkNode.getEdgeBetween(node).getSourceNode().equals(linkNode)) {
                    if (linkNode.getAttribute("weight") != null) {
                        double nodeWeight = Double.valueOf(linkNode.getAttribute("weight").toString());
                        weight += nodeWeight * linkNode.getOutDegree() / linkNode.getDegree();
                    }
                }
            }
            wordWeightMap.put(node.getId(), 0.15 + 0.85 * weight);
        }

        // 降序输出
        Map<String, Double> sortedMap = BasicUtil.sortMapByValue(wordWeightMap, false);

        for (Map.Entry<String, Double> entry : sortedMap.entrySet()) {
            System.out.println(entry.getKey() + "\t" + entry.getValue());
        }


    }

}
