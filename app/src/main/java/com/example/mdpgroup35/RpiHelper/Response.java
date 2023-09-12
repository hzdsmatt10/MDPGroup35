package com.example.mdpgroup35.RpiHelper;

import org.json.JSONException;
import org.json.JSONObject;

public class Response extends Message {
    public int status;
    public String type;
    public String action;
    public String result;
    public String name;
    public String coordinate;
    public String prevCoordinate;
    public int distance;

    public Response(int status, String type, String action, String result, int distance)  {
        this.status = status;
        this.type = type;
        this.action = action;
        this.result = result;
        this.coordinate = "";
        this.name = "";
        this.distance = distance;
    }

    public Response(int status, String type, String action, String result, String name, String coordinate, int distance)  {
        this.status = status;
        this.type = type;
        this.action = action;
        this.result = result;
        this.coordinate = coordinate;
        this.name = name;
        this.distance = distance;
    }

    public Response(int status, String type, String action, String result, String name, String coordinate, String prevCoordinate, int distance)  {
        this.status = status;
        this.type = type;
        this.action = action;
        this.result = result;
        this.coordinate = coordinate;
        this.prevCoordinate = prevCoordinate;
        this.name = name;
        this.distance = distance;
    }

    public static Response getReset() {
        return new Response(1, "", "", "", "", "2,2,1", 0);
    }

    public String toJSON() throws JSONException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("status", status);
        jsonObject.put("type", type);
        jsonObject.put("action", action);
        jsonObject.put("result", result);
        jsonObject.put("coordinate", coordinate);
        jsonObject.put("prev_coordinate", prevCoordinate);
        jsonObject.put("distance", distance);
        return jsonObject.toString();
    }

    public static Response fromJSON(String jsonString) throws JSONException {
        JSONObject parser = new JSONObject(jsonString);
        return new Response(
                parser.getInt("status"),
                parser.getString("type"),
                parser.getString("action"),
                parser.getString("result"),
                parser.getString("name"),
                parser.getString("coordinate"),
                parser.getString("prev_coordinate"),
                parser.getInt("distance")
        );
    }
}
