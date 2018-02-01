package com.helencoder.jieba;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;
import java.util.Map.Entry;


public class WordDictionary {
    private static WordDictionary singleton;
    private static String USER_DICT_SUFFIX = ".dict";

    public final Map<String, Double> freqs = new HashMap<String, Double>();
    public final Set<String> loadedPath = new HashSet<String>();
    private Double minFreq = Double.MAX_VALUE;
    private Double total = 0.0;
    private DictSegment _dict;


    private WordDictionary(String main_dictPath) {
        this.loadDict(main_dictPath);
    }


    public static WordDictionary getInstance(String main_dictPath,String user_dictPath) {
        if (singleton == null) {
            synchronized (WordDictionary.class) {
                if (singleton == null) {
                    singleton = new WordDictionary(main_dictPath);
                    singleton.init(user_dictPath);
                    return singleton;
                }
            }
        }
        return singleton;
    }


    /**
     * for ES to initialize the user dictionary.
     * 
     * @param configFile
     */
    public void init(String configFile) {
        org.apache.hadoop.fs.Path path = new org.apache.hadoop.fs.Path(configFile);
        synchronized (WordDictionary.class) {
            if (loadedPath.contains(path))
                return;
            try {
                FileSystem fs = FileSystem.get(new Configuration());
                FileStatus[] status = fs.listStatus(path);
                for (FileStatus file : status) {
                    if (file.getPath().getName().contains(USER_DICT_SUFFIX)) {
                        singleton.loadUserDict(configFile + file.getPath().getName());
                        System.err.println(String.format(Locale.getDefault(), "loading dict %s", file.getPath().getName()));
                    }
                }
                loadedPath.add(configFile);
            } catch (IOException e) {
                //e.printStackTrace();
                System.err.println(String.format(Locale.getDefault(), "%s: load user dict failure!", configFile.toString()));
            }
        }
    }
    
    
    /**
     * let user just use their own dict instead of the default dict
     */
    public void resetDict(){
    	_dict = new DictSegment((char) 0);
    	freqs.clear();
    }


    public void loadDict(String main_dictPath) {
        _dict = new DictSegment((char) 0);
        BufferedReader br = null;
        try {
            Configuration conf = new Configuration();
            org.apache.hadoop.fs.Path path = new org.apache.hadoop.fs.Path(main_dictPath);
            FileSystem fs = path.getFileSystem(conf);
            br = new BufferedReader(new InputStreamReader(fs.open(path)));
            long s = System.currentTimeMillis();
            while (br.ready()) {
                String line = br.readLine();
                String[] tokens = line.split("[\t ]+");

                if (tokens.length < 2)
                    continue;

                String word = tokens[0];
                double freq = Double.valueOf(tokens[1]);
                total += freq;
                word = addWord(word);
                freqs.put(word, freq);
            }
            // normalize
            for (Entry<String, Double> entry : freqs.entrySet()) {
                entry.setValue((Math.log(entry.getValue() / total)));
                minFreq = Math.min(entry.getValue(), minFreq);
            }
            System.out.println(String.format(Locale.getDefault(), "main dict load finished, time elapsed %d ms",
                System.currentTimeMillis() - s));
        }
        catch (IOException e) {
            System.err.println(String.format(Locale.getDefault(), "%s load failure!", main_dictPath));
        }
        finally {
            try {
                if (null != br)
                    br.close();
            }
            catch (IOException e) {
                System.err.println(String.format(Locale.getDefault(), "%s close failure!", main_dictPath));
            }
        }
    }


    private String addWord(String word) {
        if (null != word && !"".equals(word.trim())) {
            String key = word.trim().toLowerCase(Locale.getDefault());
            _dict.fillSegment(key.toCharArray());
            return key;
        }
        else
            return null;
    }


    public void loadUserDict(String userDict) {
        BufferedReader br = null;
        try {
            Configuration conf = new Configuration();
            org.apache.hadoop.fs.Path path = new org.apache.hadoop.fs.Path(userDict);
            FileSystem fs = path.getFileSystem(conf);
            br = new BufferedReader(new InputStreamReader(fs.open(path)));
            long s = System.currentTimeMillis();
            int count = 0;
            while (br.ready()) {
                String line = br.readLine();
                String[] tokens = line.split("[\t ]+");
                if (tokens.length < 1) {
                    // Ignore empty line
                    continue;
                }

                String word = tokens[0];

                double freq = 3.0d;
                if (tokens.length == 2)
                    freq = Double.valueOf(tokens[1]);
                word = addWord(word); 
                freqs.put(word, Math.log(freq / total));
                count++;
            }
            System.out.println(String.format(Locale.getDefault(), "user dict %s load finished, tot words:%d, time elapsed:%dms", userDict.toString(), count, System.currentTimeMillis() - s));
            br.close();
        }
        catch (IOException e) {
            //e.printStackTrace();
            System.err.println(String.format(Locale.getDefault(), "%s: load user dict failure!", userDict.toString()));
        }
    }


    public DictSegment getTrie() {
        return this._dict;
    }


    public boolean containsWord(String word) {
        return freqs.containsKey(word);
    }


    public Double getFreq(String key) {
        if (containsWord(key))
            return freqs.get(key);
        else
            return minFreq;
    }
}
