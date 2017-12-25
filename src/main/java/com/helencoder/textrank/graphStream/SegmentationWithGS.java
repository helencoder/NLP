package com.helencoder.textrank.graphStream;

import com.hankcs.hanlp.seg.common.Term;
import com.helencoder.segmentation.Segmentation;
import com.helencoder.util.FileIO;
import org.graphstream.algorithm.PageRank;
import org.graphstream.algorithm.generator.DorogovtsevMendesGenerator;
import org.graphstream.graph.Edge;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.MultiGraph;
import org.graphstream.graph.implementations.SingleGraph;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 分词后基于GraphStream构图
 *
 * Created by helencoder on 2017/12/25.
 */
public class SegmentationWithGS {
    public static void main(String[] args) {
        //System.setProperty("gs.ui.renderer", "org.graphstream.ui.j2dviewer.J2DGraphRenderer");

        Segmentation segmentUtil = new Segmentation();
        String content = FileIO.getFileData("file.txt");
        List<Term> termList = segmentUtil.segToList(content, true);

        // 构造图
        Graph graph = new MultiGraph("mg");

        graph.setAutoCreate(true);
        graph.setStrict(false);

        graph.addAttribute("ui.quality");
        graph.addAttribute("ui.antialias");

        // 设定样式
//        String styleSheet =
//                "node {" +
//                        "	size: 3px;fill-color: #777;text-mode: hidden;z-index: 0;" +
//                        "}" +
//                        "node.marked {" +
//                        "	fill-color: red;" +
//                        "}" +
//                        "edge {" +
//                        "   shape: line;fill-color: #222;arrow-size: 3px, 2px;" +
//                "}";
//        graph.addAttribute("ui.stylesheet", styleSheet);

        graph.display();

        Map<Integer, String> indexMap = new HashMap<Integer, String>();
        Map<String, Integer> wordMap = new HashMap<String, Integer>();
        int index = 0;
        for (Term term : termList) {
            indexMap.put(index, term.word);
            wordMap.put(term.word, index);
            index++;
        }
        System.out.println(wordMap.entrySet().size());

        for (Term term : termList) {
            String outerWord = term.word;
            int outerIndex = wordMap.get(outerWord);
            for (Term innerTerm : termList) {
                String innerWord = innerTerm.word;
                int innerIndex = wordMap.get(innerWord);
                if (!outerWord.equals(innerWord) &&
                        !(graph.hasAttribute(outerWord + "--" + innerWord) || graph.hasAttribute(innerWord + "--" + outerWord))) {
                    if (Math.abs(innerIndex - outerIndex) <= 10) {
                        Edge edge = graph.addEdge(outerWord + "--" + innerWord, outerWord, innerWord);

                        //edge.addAttribute("ui.class", "bridge");
                    }
                }

            }
            System.out.println(term.word);
        }

        for (Node node : graph) {
            node.addAttribute("ui.label", node.getId());
        }

//        for(Edge edge: graph.getEachEdge()) {
//            if(edge.hasAttribute("isTollway")) {
//                edge.addAttribute("ui.class", "tollway");
//            } else if(edge.hasAttribute("isTunnel")) {
//                edge.addAttribute("ui.class", "tunnel");
//            } else if(edge.hasAttribute("isBridge")) {
//                edge.addAttribute("ui.class", "bridge");
//            }
//
//            // Add this :
//            double speedMax = edge.getNumber("speedMax") / 130.0;
//            edge.setAttribute("ui.color", speedMax);
//        }

    }

    public static void PageRank() throws Exception {
        Graph graph = new SingleGraph("test");
        graph.addAttribute("ui.antialias", true);
        graph.addAttribute("ui.stylesheet", "node {fill-color: red; size-mode: dyn-size;} edge {fill-color:grey;}");
        graph.display();

        DorogovtsevMendesGenerator generator = new DorogovtsevMendesGenerator();
        generator.setDirectedEdges(true, true);
        generator.addSink(graph);

        PageRank pageRank = new PageRank();
        pageRank.setVerbose(true);
        pageRank.init(graph);

        generator.begin();
        while (graph.getNodeCount() < 100) {
            generator.nextEvents();
            for (Node node : graph) {
                double rank = pageRank.getRank(node);
                node.addAttribute("ui.size", 5 + Math.sqrt(graph.getNodeCount() * rank * 20));
                node.addAttribute("ui.label", String.format("%.2f%%", rank * 100));
            }
            Thread.sleep(1000);
        }
    }

}


