package com.helencoder.lda;

import java.util.Vector;

/**
 * LDA文档处理类
 *
 * Created by helencoder on 2017/12/6.
 */
public class Document {

    public int[] words;
    public String rawStr;
    public int length;

    public Document() {
        length = 0;
        rawStr = "";
        words = null;
    }

    public Document(int length) {
        this.length = length;
        rawStr = "";
        words = new int[length];
    }

    public Document(int length, int[] words) {
        this.length = length;
        rawStr = "";

        this.words = new int[length];
        for (int i = 0; i < length; i++) {
            this.words[i] = words[i];
        }
    }

    public Document(int length, int[] words, String rawStr) {
        this.length = length;
        this.rawStr = rawStr;

        this.words = new int[length];
        for (int i = 0; i < length; i++) {
            this.words[i] = words[i];
        }
    }

    public Document(Vector<Integer> doc) {
        this.length = doc.size();
        rawStr = "";

        this.words = new int[length];
        for (int i = 0; i < length; i++) {
            this.words[i] = doc.get(i);
        }
    }

    public Document(Vector<Integer> doc, String rawStr) {
        this.length = doc.size();
        this.rawStr = rawStr;

        this.words = new int[length];
        for (int i = 0; i < length; i++) {
            this.words[i] = doc.get(i);
        }
    }
}
