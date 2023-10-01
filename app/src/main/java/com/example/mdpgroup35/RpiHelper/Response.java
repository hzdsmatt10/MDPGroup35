package com.example.mdpgroup35.RpiHelper;

import org.json.JSONException;
import org.json.JSONObject;

public class Response extends Message { /////////////might need to refactor the entire thing
    public int status;// An integer representing the status of the response.
    public String type;//A string indicating the type of response.
    public String action;//A string describing the action associated with the response
    public String result;//A string representing the result or content of the response.
    public String name;
    public String coordinate;//A string storing coordinate information.
    public String prevCoordinate;//A string storing previous coordinate information (optional).
    public int distance;//An integer indicating distance (optional)

    /*
    public Response(int status, String type, String action, String result, int distance)  {
        this.status = status;
        this.type = type;
        this.action = action;
        this.result = result;
        this.coordinate = "";
        this.name = "";
        this.distance = distance;
    }
*/
    //multiple constructors to create instaces of the response class
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

    public static Response getReset() {//returns a predefined Response object. It appears to be a special response used for resetting certain properties. The returned response has specific default values.
        return new Response(1, "", "", "", "", "2,2,1", 0);
    }

    public String toJSON() throws JSONException { //this method converts the Response object into a JSON representation
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

    public static Response fromJSON(String jsonString) throws JSONException { //creates a response object from a JSON string
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
