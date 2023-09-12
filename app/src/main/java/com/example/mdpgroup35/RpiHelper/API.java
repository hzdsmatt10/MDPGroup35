package com.example.mdpgroup35.RpiHelper;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

public class API {
    private static API INSTANCE = new API();
    private RequestQueue queue;
    private String url;
    private API() {
        url = "http://192.168.32.32:8080";
    }

    public static API getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new API();
        }
        return INSTANCE;

    }

    public void init(Context context) {
        queue = Volley.newRequestQueue(context);
    }

    public void post(JSONObject object) {
        // Request a string response from the provided URL.
        JsonObjectRequest req = new JsonObjectRequest(Request.Method.POST, url, object,
                response -> {

                }, error -> {
            System.out.println(error);
        });

        // Add the request to the RequestQueue.
        queue.add(req);
    }

}

