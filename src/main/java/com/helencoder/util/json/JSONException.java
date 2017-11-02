package com.helencoder.util.json;

/**
 * Created by helencoder on 2017/9/18.
 */
public class JSONException extends Exception {
    private Throwable cause;

    public JSONException(String var1) {
        super(var1);
    }

    public JSONException(Throwable var1) {
        super(var1.getMessage());
        this.cause = var1;
    }

    public Throwable getCause() {
        return this.cause;
    }
}
