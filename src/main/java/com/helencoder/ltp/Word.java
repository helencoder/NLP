package com.helencoder.ltp;

import com.helencoder.util.json.JSONException;
import com.helencoder.util.json.JSONObject;

/**
 * LTP分词组件单词类
 *
 * Created by helencoder on 2017/11/7.
 */
public class Word {
    private String id;
    private String cont;
    private String ne;
    private String parent;
    private String pos;
    private String relate;

    public Word(JSONObject json) {
        try {
            if (json.has("id")) {
                this.id = json.getString("id");
            }
            if (json.has("cont")) {
                this.cont = json.getString("cont");
            }
            if (json.has("ne")) {
                this.ne = json.getString("ne");
            }
            if (json.has("parent")) {
                this.parent = json.getString("parent");
            }
            if (json.has("pos")) {
                this.pos = json.getString("pos");
            }
            if (json.has("relate")) {
                this.relate = json.getString("relate");
            }
        } catch (JSONException ex) {
            ex.printStackTrace();
        }
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCont() {
        return cont;
    }

    public void setCont(String cont) {
        this.cont = cont;
    }

    public String getNe() {
        return ne;
    }

    public void setNe(String ne) {
        this.ne = ne;
    }

    public String getParent() {
        return parent;
    }

    public void setParent(String parent) {
        this.parent = parent;
    }

    public String getPos() {
        return pos;
    }

    public void setPos(String pos) {
        this.pos = pos;
    }

    public String getRelate() {
        return relate;
    }

    public void setRelate(String relate) {
        this.relate = relate;
    }
}
