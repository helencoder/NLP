package com.helencoder.lda;

import java.io.*;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.StringTokenizer;

/**
 * LDA模型数据存储类
 *
 * Created by helencoder on 2017/12/6.
 */
public class Dictionary {

    public Map<String, Integer> word2id;
    public Map<Integer, String> id2word;

    public Dictionary() {
        word2id = new HashMap<String, Integer>();
        id2word = new HashMap<Integer, String>();
    }

    public String getWord(int id) {
        return id2word.get(id);
    }

    public Integer getID(String word) {
        return word2id.get(word);
    }

    public boolean contains(int id) {
        return id2word.containsKey(id);
    }

    public boolean contains(String word) {
        return word2id.containsKey(word);
    }

    public int addWord(String word) {
        if (!contains(word)) {
            int id = word2id.size();

            word2id.put(word, id);
            id2word.put(id, word);

            return id;
        } else {
            return getID(word);
        }
    }

    public boolean readWordMap(String filepath) {
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(
                    new FileInputStream(filepath), "UTF-8"));

            for (String line = br.readLine(); line != null; line = br.readLine()) {
                StringTokenizer stk = new StringTokenizer(line, " \t\n\r");
                if (stk.countTokens() != 2) {
                    continue;
                }

                String word = stk.nextToken();
                int id = Integer.parseInt(stk.nextToken());

                id2word.put(id, word);
                word2id.put(word, id);
            }

            br.close();
            return true;
        } catch (IOException ex) {
            System.out.println("Error while reading dictionary:" + ex.getMessage());
            ex.printStackTrace();
            return false;
        }
    }

    public boolean writeWordMap(String filepath) {
        try {
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(
                    new FileOutputStream(filepath), "UTF-8"));

            Iterator<String> iterator = word2id.keySet().iterator();
            while (iterator.hasNext()) {
                String key = iterator.next();
                Integer value = word2id.get(key);

                bw.write(key + " " + value + "\n");
            }

            bw.close();
            return true;
        } catch (IOException ex) {
            System.out.println("Error while writing word map " + ex.getMessage());
            ex.printStackTrace();
            return false;
        }
    }

}
