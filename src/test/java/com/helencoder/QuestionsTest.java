package com.helencoder;

import com.hankcs.hanlp.seg.Segment;
import com.hankcs.hanlp.seg.common.Term;
import com.helencoder.segmentation.Segmentation;
import com.helencoder.util.BasicUtil;
import com.helencoder.util.FileIO;
import scala.util.parsing.combinator.testing.Str;

import java.io.*;
import java.util.*;

/**
 * 问题聚类
 *
 * Created by zhenghailun on 2017/11/12.
 */
public class QuestionsTest {
    public static void main(String[] args) {

        // 按照问题进行归类
        List<String> topicsList = FileIO.getFileDataByLine("data/topics.txt");
        List<String> simKeywordsList = FileIO.getFileDataByLine("data/sim_keywords.txt");
        List<String> questionsList = FileIO.getFileDataByLine("data/questions.txt");

        Map<String, List<String>> topicSimMap = new HashMap<>();
        Map<String, List<String>> questionTopicsMap = new HashMap<>();

        for (String topic : topicsList) {
            List<String> tmpList = new ArrayList<>();
            tmpList.add(topic);
            for (String detail : simKeywordsList) {
                String delWord = detail.split("\t")[0];
                String changeWord = detail.split("\t")[1];
                if (topic.equals(changeWord)) {
                    tmpList.add(delWord);
                }
            }
            topicSimMap.put(topic, tmpList);
        }

        for (String question : questionsList) {
            List<String> tmpList = new ArrayList<>();
            for (String topic : topicsList) {
                List<String> simTopicList = topicSimMap.get(topic);
                for (String word : simTopicList) {
                    if (question.indexOf(word) != -1) {
                        tmpList.add(topic);
                        break;
                    }
                }
            }

            questionTopicsMap.put(question, tmpList);
        }

        // 记录
        for (Map.Entry<String, List<String>> entry : questionTopicsMap.entrySet()) {
            List<String> indexList = entry.getValue();
            System.out.println(entry.getKey());
            String indexStr = BasicUtil.mkString(indexList, " ");
            if (indexStr.length() == 0) {
                continue;
            }
            FileIO.appendFile("data/record.txt", indexStr);
            FileIO.appendFile("data/record.txt", "\t" + entry.getKey());
            for (Map.Entry<String, List<String>> entry1 : questionTopicsMap.entrySet()) {
                List<String> cmpList = entry1.getValue();
                if (indexList.size() != cmpList.size()) {
                    continue;
                }
                boolean flag = true;
                for (String word : indexList) {
                    if (!cmpList.contains(word)) {
                        flag = false;
                    }
                }

                if (flag) {
                    FileIO.appendFile("data/record.txt", "\t" + entry1.getKey());
                }
            }
        }



    }

    public static void record() {
        List<String> keywordsList = FileIO.getFileDataByLine("data/questions/new_model_300/new_topic_300.txt");
        Set<String> set = new HashSet<>();
        set.addAll(keywordsList);

        System.out.println(set.size());

        List<String> questionsList = FileIO.getFileDataByLine("data/questions/questions.txt");

        Set<String> questionsSet = new HashSet<>();
        for (String topic : set) {
            if (topic.toLowerCase().equals("app")) {
                continue;
            }
            FileIO.appendFile("data/questions/new_model_300/topic_questions.txt", topic);
            for (String question : questionsList) {
                if (question.indexOf(topic) != -1) {
                    FileIO.appendFile("data/questions/new_model_300/topic_questions.txt", "\t" + question);
                    questionsSet.add(question);
                }
            }
        }

        FileIO.appendFile("data/questions/new_model_300/topic_questions.txt", "APP");
        for (String question : questionsList) {
            if (question.toLowerCase().indexOf("app") != -1) {
                questionsSet.add(question);
                FileIO.appendFile("data/questions/new_model_300/topic_questions.txt", "\t" + question);
            }
        }

        System.out.println(questionsSet.size());
    }

    public static void fileReplace() {
        List<String> questionsList = FileIO.getFileDataByLine("data/questions/new_model_300/questions_with_userdict_1.txt");
        List<String> simKeywordsList = FileIO.getFileDataByLine("data/questions/sim_keywords.txt");

        Map<String, String> simWordMap = new HashMap<>();
        for (String word : simKeywordsList) {
            System.out.println(word.split("\t")[0] + "\t" + word.split("\t")[1]);
            simWordMap.put(word.split("\t")[0], word.split("\t")[1]);
        }

        for (String question : questionsList) {
            StringBuilder sb = new StringBuilder();
            for (String word : question.split(" ")) {
                if (simWordMap.containsKey(word)) {
                    sb.append(simWordMap.get(word));
                } else {
                    sb.append(word);
                }
                sb.append(" ");
            }
            if (sb.length() > 0) {
                FileIO.appendFile("data/questions/new_model_300/questions_with_userdict_2.txt", sb.toString());
            }
        }
    }

    public static void fileCombine() {
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(
                    new FileInputStream("data/questions/questionsSegWithHanlp.txt")));
            BufferedReader br1 = new BufferedReader(new InputStreamReader(
                    new FileInputStream("data/questions/questionsWithCoreWords.txt")));

            for (String line = br.readLine(), line1 = br1.readLine(); line != null; line = br.readLine(), line1 = br1.readLine()) {
                String str = line + " " + line1;
                String[] list = str.split(" ");
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < list.length; i++) {
                    if (list[i].length() >= 2) {
                        sb.append(list[i]);
                        sb.append(" ");
                    }
                }

                FileIO.appendFile("data/questions/questions_model.txt", sb.toString());
            }

        } catch (IOException ex) {

        }
    }

    public static void statistics() {

        List<String> topic100List = FileIO.getFileDataByLine("data/questions/model/topic_100.txt");
        List<String> topic200List = FileIO.getFileDataByLine("data/questions/model_200/topic_200.txt");
        List<String> topic300List = FileIO.getFileDataByLine("data/questions/model_300/topic_300.txt");

        Set<String> topic100Set = new HashSet<>();
        Set<String> topic200Set = new HashSet<>();
        Set<String> topic300Set = new HashSet<>();

        topic100Set.addAll(topic100List);
        topic200Set.addAll(topic200List);
        topic300Set.addAll(topic300List);

        System.out.println(topic300Set.size());

        // 获取两者中均存在的topic数量
        Set<String> commonSet = new HashSet<>();
        for (String topic : topic100Set) {
            if (topic200Set.contains(topic) && topic300Set.contains(topic)) {
                commonSet.add(topic);
            }
        }

        System.out.println(commonSet.size());

        // 获取所有的问题
        List<String> questionsList = FileIO.getFileDataByLine("data/questions/questions.txt");

        Set<String> questionsSet = new HashSet<>();
        for (String word : commonSet) {
            if (word.toLowerCase().equals("app")) {
                continue;
            }
            int count = 0;
            FileIO.appendFile("common_record.txt", word);
            for (String question : questionsList) {
                if (question.toLowerCase().indexOf(word) != -1) {
                    count++;
                    questionsSet.add(question);
                    FileIO.appendFile("common_record.txt", "\t" + question);
                }
            }
            System.out.println(word + "\t" + count);
        }

        int count = 0;
        FileIO.appendFile("common_record.txt", "APP");
        for (String question : questionsList) {
            if (question.toLowerCase().indexOf("app") != -1) {
                count++;
                questionsSet.add(question);
                FileIO.appendFile("common_record.txt", "\t" + question);
            }
        }
        System.out.println("APP\t" + count);

        System.out.println(questionsSet.size());

    }

}
