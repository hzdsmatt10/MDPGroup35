package com.example.mdpgroup35.RpiHelper;


import org.json.JSONException;

public abstract class Message { //This class appears to be designed as a base class for creating various message types with JSON serialization capabilities
    public abstract String toJSON() throws JSONException;

    public String notNull(String s) {
        return s == null ? "" : s;
    }
}
