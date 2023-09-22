package com.example.mdpgroup35.RpiHelper;

import com.example.mdpgroup35.Algo.DijkstraPath;
import com.example.mdpgroup35.Algo.Node;
import com.example.mdpgroup35.Algo.STMCommands;
import com.example.mdpgroup35.Algo.State;
import org.json.*;
import java.util.ArrayList;
import java.util.HashMap;
import org.json.JSONException;
public class NewAction {
    //constants
    public static String MOVE = "move";
    public static String NOOP = "noop";
    public static String SERIES = "series";
    public static String SERIES_INTERLEAVE = "series_interleave";
    public static String RESET = "reset";
    public static String CAPTURE = "capture";
    public static String BULLSEYE = "bullseye";



    // attributes

    public String type; //type of action MOVE ETC.

    public String action; //"F" and "B"

    public String direction; //"C","L","R"

    public int distance; //may need to change to string  , determines distance to travel


    //Constructor
    public NewAction(String type, String action, String direction, int distance)
    {
        this.type =notNull(type);
        this.action = notNull(action);
        this.direction =notNull(direction);
        this.distance =distance;

    }


    //methods
    public String convertToSTMFormat(NewAction newaction)
    {
        String temp;
        String distancestring;

        if (newaction.distance<10)
        {
            distancestring = "00" + newaction.distance;
        }
        else if (newaction.distance<100)
        {
            distancestring = "0" + newaction.distance;
        }
        else {
            distancestring = String.valueOf(newaction.distance);
        }

        temp = newaction.action + newaction.direction + distancestring;
        System.out.println(temp);


        return temp;
    }



























    public String notNull(String s) {
        return s == null ? "" : s;
    }




}
