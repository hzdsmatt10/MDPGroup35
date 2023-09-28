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
    //constants Action.type

    public static String MOVE = "move";
    public static String CAPTURE = "capture";
    public static String NOOP = "noop";
    public static String SERIES = "series";
    public static String SERIES_INTERLEAVE = "series_interleave";
    public static String RESET = "reset";

    public static String BULLSEYE = "bullseye";



    // attributes

    public String type; //type of action MOVE ETC.

    public String action; //"F" and "B"

    public String direction; //"C","L","R"

    public int distance; //may need to change to string  , determines distance to travel

    public int angle; //may need to change to string, determines the angle
    public ArrayList<NewAction> data;//    An ArrayList of Action objects, allowing for sequences of actions. These fields are used to store information about the action.


    //Constructor
    public NewAction(String type, String action, String direction, int distance, int angle)
    {
        this.type =notNull(type);
        this.action = notNull(action);
        this.direction =notNull(direction);
        this.distance =distance;
        this.angle =angle;
        this.data = new ArrayList<>();

    }

    public NewAction(String type, ArrayList<NewAction> data)
    {
        this.type = notNull(type);//default
        this.action = " ";//default
        this.direction ="C";
        this.distance = 0;//default
        this.angle = 0;//default
        this.data = data;
    }


    //methods
    public static String convertToSTMFormat(NewAction newaction)
    {
        String temp;
        String distancestring;
        String anglestring;

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

        if (newaction.angle<10)
        {
            anglestring = "00" + newaction.angle;
        }
        else if (newaction.distance<100)
        {
            anglestring = "0" + newaction.angle;
        }
        else {
            anglestring = String.valueOf(newaction.action);
        }

        temp = newaction.action + newaction.direction + distancestring+anglestring;
        System.out.println(temp);


        return temp;
    }



    public static NewAction skirtRight()
    {
        ArrayList<NewAction> actions = new ArrayList<>();

        actions.add(new NewAction(NewAction.MOVE,"F","R",10,90));
        actions.add(new NewAction(NewAction.MOVE,"F","C",10,00));
        actions.add(new NewAction(NewAction.MOVE,"B","R",10,90));


        return new NewAction(NewAction.SERIES_INTERLEAVE,actions);
    }

    public static NewAction stop()
    {



        return new NewAction(NewAction.NOOP,"F","C",000,000);
    }























    public String notNull(String s) {
        return s == null ? "" : s;
    }




}
