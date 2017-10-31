package com.helencoder.textrank;

import com.hankcs.hanlp.corpus.tag.Nature;

/**
 * Created by helencoder on 2017/10/31.
 */
public class Word {
    public String word;
    public Nature nature;
    public float weight;

    public Word(String word, Nature nature, float weight) {
        this.word = word;
        this.nature = nature;
        this.weight = weight;
    }

    public String toString() {return this.word + "\t" + this.nature + "\t" + this.weight;}
    public String getWord() {return this.word;}
    public String getNature() {return this.nature.toString();}
    public float getWeight() {return this.weight;}
    public int length() {
        return this.word.length();
    }
}
