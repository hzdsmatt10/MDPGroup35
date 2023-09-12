package com.example.mdpgroup35.RpiHelper;


import org.json.JSONException;
/**
 * Mixin class
 */
public abstract class Message {
    public abstract String toJSON() throws JSONException;

    public String notNull(String s) {
        return s == null ? "" : s;
    }
}
