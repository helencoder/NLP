package com.helencoder.textrank;

import com.hankcs.hanlp.HanLP;
import com.hankcs.hanlp.dictionary.stopword.CoreStopWordDictionary;
import com.hankcs.hanlp.seg.Segment;
import com.hankcs.hanlp.seg.common.Term;

import java.util.*;

/**
 * TextRank优化版
 *  1、增加词性权重投票机制
 *  2、默认取名词性语素作为输出
 *
 * Created by zhenghailun on 2018/1/14.
 */
public class TextrankOptimization {
    // 算法参数
    protected final static double d = 0.85d;
    private final static int max_iter = 200;
    private final static double min_diff = 0.001d;

    private int nKeyword;
    private int window;
    private String text;
    private List<Term> words;
    private List<List<Term>> sentences;
    private Map<String, Double> natureWeightMap;
    private Map<String, String> wordNatureMap;
    private Map<String, Double> wordWeightMap;
    private Map<String, List<Term>> wordWindowMap;
    private Map<String, Double> wordRankMap;
    private Map<String, Double> keywordsMap;

    // 分词组件
    private static Segment segment;
    private static CoreStopWordDictionary coreStopWordDictionary;
    // 词性过滤、HanLP词性文件(名词性语素特殊处理)
    private String[] allow_speech_tags = {"a", "ad", "an", "i", "j", "l", "v", "vg", "vd", "vn"};
    // 句子停用词
    private String[] sentence_delimiters = {"?", "!", ";", "？", "！", "。", "；", "……", "…"};

    /**
     * 构造函数,加载默认配置
     *
     * 加载分词组件
     */
    public TextrankOptimization() {
        if (segment == null) {
            segment = HanLP.newSegment()
                    .enableNameRecognize(true)
                    .enableOrganizationRecognize(true)
                    .enableNumberQuantifierRecognize(true)
                    .enablePlaceRecognize(true)
                    .enableTranslatedNameRecognize(true);
        }
    }

    /**
     * 文本分析
     *
     * @param text 文本
     * @param window 窗口大小
     */
    public void analyze(String text, int window) {
        this.text = text.replaceAll(" ", "");
        this.window = window;
        this.words = segment.seg(this.text);
        this.sentences = seg2sentence();
        this.natureWeightMap = natureWeight();
        this.wordNatureMap = recordWordMap();
        this.wordWeightMap = recordWordWeightMap();
        this.wordWindowMap = buildWordWindowMap();
        this.wordRankMap = getWordRankMap();
        this.keywordsMap = sortMap(wordRankMap);
    }

    /**
     * 获取关键词
     *
     * @param count 关键词个数
     */
    public List<String> getKeywordsList(int count) {
        this.nKeyword = count;
        List<String> keywordsList = new ArrayList<>();
        if (text == null) {
            return keywordsList;
        }

        for (Map.Entry<String, Double> entry : keywordsMap.entrySet()) {
            if (keywordsList.size() >= nKeyword) {
                break;
            }
            if (filterByWord(entry.getKey())) {
                //Word word = new Word(entry.getKey(), wordNatureMap.get(entry.getKey()), entry.getValue());
                keywordsList.add(entry.getKey());
            }
        }

        return keywordsList;
    }

    /**
     * 获取关键短语(还可调整)
     *
     * @param count 关键短语个数
     * @param minOccurNum 关键短语最小出现次数
     */
    public List<String> getKeyphrasesList(int count, int minOccurNum) {
        List<String> keyphrasesList = new ArrayList<>();
        if (text == null) {
            return keyphrasesList;
        }

        List<String> keywordsList = new ArrayList<>();
        for (Map.Entry<String, Double> entry : keywordsMap.entrySet()) {
            if (keywordsList.size() >= 2 * count) {
                break;
            }
            if (filterByWord(entry.getKey())) {
                keywordsList.add(entry.getKey());
                if (entry.getKey().length() > 2) {
                    keyphrasesList.add(entry.getKey());
                }
            }
        }

        // 关键短语归集
        Set<String> set = new HashSet<>();
        for (String outerWord : keywordsList) {
            for (String innerWord : keywordsList) {
                if (outerWord.equals(innerWord)) {
                    continue;
                }

                String tmpStr = outerWord + innerWord;
                int num = getOccurNum(text, tmpStr);
                if (num > minOccurNum && !set.contains(tmpStr)) {
                    keyphrasesList.add(tmpStr);
                    set.add(tmpStr);
                }
            }
        }

        return keyphrasesList;
    }

    /**
     * 构建词语网络窗口(按句子划分)
     */
    private Map<String, List<Term>> buildWordWindowMap() {
        Map<String, List<Term>> wordWindowMap = new HashMap<>();

        for (List<Term> termList : sentences) {
            for (int i = 0; i < termList.size(); i++) {
                Term term = termList.get(i);
                List<Term> windowList;
                // 窗口滑动
                if ((i + window - 1) < termList.size()) {
                    windowList = termList.subList((i + 1), (i + window));
                } else {
                    windowList = termList.subList((i + 1), termList.size());
                }

                // 记录
                String word = term.word;
                if (wordWindowMap.containsKey(word)) {
                    List<Term> oldWindowList = wordWindowMap.get(word);
                    // 引用值传递
                    List<Term> tmpList = new ArrayList<>(windowList);
                    tmpList.addAll(oldWindowList);
                    wordWindowMap.put(word, tmpList);
                } else {
                    wordWindowMap.put(word, windowList);
                }
            }
        }

        return wordWindowMap;
    }

    /**
     * 单词投票整合、迭代计算(依据词性权重投票)
     *
     * @return wordRankMap key为单词,value对单词权重
     */
    private Map<String, Double> getWordRankMap() {

        Map<String, Double> wordRankMap = new HashMap<>();

        for (int i = 0; i < max_iter; i++) {
            Map<String, Double> map = new HashMap<>();
            double max_diff = 0;
            for (Map.Entry<String, List<Term>> entry : wordWindowMap.entrySet()) {
                String key = entry.getKey();
                List<Term> value = entry.getValue();
                double weight = wordWeightMap.get(key);
                // 初始单词权重均置为词性权重
                map.put(key, (1 - d) + d * weight);
                for (Term term : value) {
                    int size = wordWindowMap.get(term.word).size();
                    if (key.equals(term.word) || size == 0 || !filter(term)) {
                        continue;
                    }
                    double innerWeight = wordWeightMap.get(term.word);
                    // 公式整合(词性权重投票)
                    map.put(key, map.get(key) + d / size * (wordRankMap.get(term.word) == null ? 0 : wordRankMap.get(term.word) * innerWeight));
                }
                max_diff = Math.max(max_diff, Math.abs(map.get(key) - (wordRankMap.get(key) == null ? 0 : wordRankMap.get(key))));
            }
            wordRankMap = map;
            if (max_diff <= min_diff) {
                break;
            }
        }

        return wordRankMap;
    }

    /**
     * 词性记录
     */
    private Map<String, String> recordWordMap() {
        Map<String, String> wordMap = new HashMap<>();
        for (Term term : words) {
            wordMap.put(term.word, term.nature.toString());
        }

        return wordMap;
    }

    /**
     * 词语权重记录
     */
    private Map<String, Double> recordWordWeightMap() {
        Map<String, Double> wordWeightMap = new HashMap<>();
        for (Term term : words) {
            String pos = term.nature.toString();
            if (String.valueOf(pos.charAt(0)).equals("n")) {
                wordWeightMap.put(term.word, 0.8);
            } else {
                double weight = natureWeightMap.get(pos) == null ? 0 : natureWeightMap.get(pos);
                wordWeightMap.put(term.word, weight);
            }
        }

        return wordWeightMap;
    }

    /**
     * 词性权重设定
     */
    private Map<String, Double> natureWeight() {
        Map<String, Double> natureWeightMap = new HashMap<>();

        // hanlp词性设置(hanlp中名词权重直接设为0.8)
        natureWeightMap.put("a", 0.5);    // 形容词
        natureWeightMap.put("ad", 0.3);   // 副形词
        natureWeightMap.put("an", 0.6);   // 名形词
        natureWeightMap.put("i", 0.6);    // 成语
        natureWeightMap.put("j", 0.7);    // 简称略语
        natureWeightMap.put("l", 0.6);    // 习用语
        natureWeightMap.put("v", 0.3);    // 动词
        natureWeightMap.put("vg", 0.2);   // 动语素
        natureWeightMap.put("vd", 0.4);   // 副动词
        natureWeightMap.put("vn", 0.6);   // 名动词

        return natureWeightMap;
    }

    /**
     * 句子拆分
     */
    private List<List<Term>> seg2sentence() {
        List<List<Term>> sentencesList = new ArrayList<>();

        List<String> sentenceDelimitersList = Arrays.asList(sentence_delimiters);
        List<Term> list = new ArrayList<>();
        for (Term term : words) {
            if (sentenceDelimitersList.contains(term.word)) {
                if (list.size() > 0) {
                    // 引用值传递
                    List<Term> tmpList = new ArrayList<>(list);
                    sentencesList.add(tmpList);
                    list.clear();
                }
            } else {
                list.add(term);
            }
        }

        return sentencesList;
    }

    /**
     * 文本过滤
     */
    private boolean filter(Term term) {
        List<String> allowSpeechTagsList = Arrays.asList(allow_speech_tags);
        if (term.nature == null) {
            return true;
        } else {
            String pos = term.nature.toString();
            // 词性过滤&长度过滤&停用词过滤
            return (allowSpeechTagsList.contains(pos) || String.valueOf(pos.charAt(0)).equals("n"))
                    && term.word.trim().length() > 1
                    && !coreStopWordDictionary.contains(term.word);
        }
    }

    /**
     * 文本过滤(仅取名词)
     */
    private boolean filterByWord(String word) {
        List<String> allowSpeechTagsList = Arrays.asList(allow_speech_tags);
        String pos = wordNatureMap.get(word);
        if (pos == null) {
            return true;
        } else {
            // 词性过滤&长度过滤&停用词过滤
            //return (allowSpeechTagsList.contains(pos) || String.valueOf(pos.charAt(0)).equals("n"))
            return String.valueOf(pos.charAt(0)).equals("n") && word.trim().length() > 1
                    && !coreStopWordDictionary.contains(word);
        }
    }

    /**
     * Map排序
     *
     */
    private Map<String, Double> sortMap(Map<String, Double> map) {
        Map<String, Double> sortedMap = new LinkedHashMap<>();

        List<Map.Entry<String, Double>> list = new ArrayList<Map.Entry<String, Double>>(map.entrySet());
        Collections.sort(list, new Comparator<Map.Entry<String, Double>>() {
            public int compare(Map.Entry<String, Double> o1, Map.Entry<String, Double> o2) {
                return Double.compare(o2.getValue(), o1.getValue());
            }
        });

        for (Map.Entry<String, Double> mapping : list) {
            sortedMap.put(mapping.getKey(), mapping.getValue());
        }

        return sortedMap;
    }

    /**
     * 获取字符串中子字符串出现的次数
     */
    private int getOccurNum(String str, String pattern) {
        int count = 0;
        int res = -1;
        do {
            res = str.indexOf(pattern);

            if (res != -1) {
                str = str.substring(res + pattern.length());
                count++;
            }

        } while (res != -1);

        return count;
    }

}

