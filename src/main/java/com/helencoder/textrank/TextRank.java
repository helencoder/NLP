package com.helencoder.textrank;

import com.hankcs.hanlp.corpus.tag.Nature;
import com.hankcs.hanlp.seg.common.Term;
import com.helencoder.segmentation.Segmentation;

import java.util.*;

/**
 * textrank实现步骤
 * 1、进行分词操作(有过滤、无过滤等多种不同的分割方式)
 * 2、进行句子的划分
 * 3、创建窗口,(整篇文章 vs 句子)
 * 4、构建有向有权图
 * 5、迭代计算,直至收敛
 * <p>
 * Created by helencoder on 2017/8/4.
 */
public class TextRank {

    private int nKeyword = 10;
    private int window = 5;
    // 阻尼系数(DampingFactor),一般取值为0.85
    protected final static float d = 0.85f;
    // 迭代次数
    private final static int max_iter = 200;
    private final static float min_diff = 0.001f;
    // 词性过滤、HanLP词性文件(首字母)
    private String[] allow_speech_tags = {"d", "f", "g", "h", "i", "j", "k", "l", "n", "s", "t", "v", "x"};
    // 句子停用词
    private String[] sentence_delimiters = {"?", "!", ";", "？", "！", "。", "；", "……", "…", "\n"};
    // 分词组件
    private Segmentation segmentation;

    /**
     * 构造函数,加载默认配置
     * <p>
     * 加载分词组件
     */
    public TextRank() {
        this.segmentation = new Segmentation();
    }

    /**
     * 获取文本关键词
     *
     * @param text     文本内容
     * @param nKeyword 提取关键词个数
     * @param window   词语网络窗口大小
     * @return list<word>   Word(word, nature, weight)(单词、词性、权重)
     */
    public List<Word> getKeywordsList(String text, int nKeyword, int window) {
        this.nKeyword = nKeyword;
        this.window = window;
        return this.getKeyword(text);
    }

    /**
     * 关键词提取流程方法
     *
     * @param content 文本内容
     * @return List<Word> 关键词list
     */
    private List<Word> getKeyword(String content) {
        // 首先进行分词操作
        List<Term> termList = segmentation.segToList(content, false);
        // 有效词语数量记录
        int wordsCount = this.wordsCount(termList);
        // 词性记录
        Map<String, Nature> natureMap = this.wordNatureMap(termList);
        // 构建词语网络窗口
        Map<String, List<String>> wordMap = this.buildWordMap(termList);
        // 迭代计算有向有权图
        Map<String, Float> wordRankMap = this.getWordRank(wordMap);
        // 排序输出
        Map<String, Float> sortedMap = this.sortMap(wordRankMap);

        List<Word> keyword = new ArrayList<Word>();
        for (Map.Entry<String, Float> entry : sortedMap.entrySet()) {
            if (keyword.size() >= nKeyword) {
                break;
            }
            Word word = new Word(entry.getKey(), natureMap.get(entry.getKey()), entry.getValue() / wordsCount);
            keyword.add(word);
        }

        return keyword;
    }

    /**
     * 构建词语网络窗口(依句子拆分构建)
     *
     * @param termList 初期分词结果
     * @return wordMap 词语网络 key为当前词语,键值为其共现的词语集合
     */
    private Map<String, List<String>> buildWordMap(List<Term> termList) {
        // 句子拆分
        List<List<String>> sentences = new ArrayList<List<String>>();

        // 句子停止词转换为list
        List<String> sentenceDelimitersList = Arrays.asList(sentence_delimiters);

        // 文本句子拆分
        List<String> list = new ArrayList<String>();
        for (Term term : termList) {
            // 句子拆分
            if (sentenceDelimitersList.contains(term.word)) {
                if (list.size() > 0) {
                    // 引用值传递
                    List<String> tmpList = new ArrayList<String>(list);
                    sentences.add(tmpList);
                    list.clear();
                }
            } else {
                // 单词过滤
                if (segmentation.isWordAllow(term)) {
                    list.add(term.word);
                }
            }
        }

        Map<String, List<String>> wordMap = new HashMap<String, List<String>>();

        // 构建词语网络窗口(依句子)
        for (List<String> sentence : sentences) {
            for (int i = 0; i < sentence.size(); i++) {
                String word = sentence.get(i);
                List<String> windowList = new ArrayList<String>();
                // 窗口滑动
                // subList: 前包后不包
                if ((i + window - 1) < sentence.size()) {
                    windowList = sentence.subList((i + 1), (i + window));
                } else {
                    windowList = sentence.subList((i + 1), sentence.size());
                }

                // 记录
                if (wordMap.containsKey(word)) {
                    List<String> oldWindowList = wordMap.get(word);
                    // 引用值传递
                    List<String> tmpList = new ArrayList<String>(windowList);
                    tmpList.addAll(oldWindowList);
                    wordMap.put(word, tmpList);
                } else {
                    wordMap.put(word, windowList);
                }
            }
        }

        return wordMap;
    }

    /**
     * 单词投票整合、迭代计算
     *
     * @param wordMap key为单词,value为窗口共现单词list
     * @return wordRankMap key为单词,value对单词权重
     */
    private Map<String, Float> getWordRank(Map<String, List<String>> wordMap) {
        Map<String, Float> wordRankMap = new HashMap<String, Float>();

        for (int i = 0; i < max_iter; i++) {
            Map<String, Float> m = new HashMap<String, Float>();
            float max_diff = 0;
            for (Map.Entry<String, List<String>> entry : wordMap.entrySet()) {
                String key = entry.getKey();
                List<String> value = entry.getValue();
                // 按照textrank公式构造对应关系
                // 初始单词权重均置为0
                m.put(key, 1 - d);
                for (String other : value) {
                    int size = wordMap.get(other).size();
                    if (key.equals(other) || size == 0) continue;
                    // 公式整合
                    m.put(key, m.get(key) + d / size * (wordRankMap.get(other) == null ? 0 : wordRankMap.get(other)));
                }
                max_diff = Math.max(max_diff, Math.abs(m.get(key) - (wordRankMap.get(key) == null ? 0 : wordRankMap.get(key))));
            }
            wordRankMap = m;
            if (max_diff <= min_diff) break;
        }

        return wordRankMap;
    }

    /**
     * Map排序
     *
     * @param map 待排序map
     * @return sortedMap
     */
    private Map<String, Float> sortMap(Map<String, Float> map) {
        Map<String, Float> sortedMap = new LinkedHashMap<String, Float>();

        List<Map.Entry<String, Float>> list = new ArrayList<Map.Entry<String, Float>>(map.entrySet());
        Collections.sort(list, new Comparator<Map.Entry<String, Float>>() {

            public int compare(Map.Entry<String, Float> o1, Map.Entry<String, Float> o2) {
                // 升序
                //return Float.compare(o1.getValue(), o2.getValue());
                // 降序
                return Float.compare(o2.getValue(), o1.getValue());
            }
        });

        for (Map.Entry<String, Float> mapping : list) {
            sortedMap.put(mapping.getKey(), mapping.getValue());
        }

        return sortedMap;
    }

    /**
     * 词性记录
     *
     * @param termList
     * @return natureMap 词性Map
     */
    private Map<String, Nature> wordNatureMap(List<Term> termList) {
        Map<String, Nature> natureMap = new HashMap<String, Nature>();

        for (Term term : termList) {
            if (segmentation.isWordAllow(term)) {
                natureMap.put(term.word, term.nature);
            }
        }

        return natureMap;
    }

    /**
     * 有效词语数量记录
     */
    private int wordsCount(List<Term> termList) {
        int count = 0;

        for (Term term : termList) {
            if (segmentation.isWordAllow(term)) {
                count++;
            }
        }

        return count;
    }

}
