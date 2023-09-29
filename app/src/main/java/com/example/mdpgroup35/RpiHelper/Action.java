package com.example.mdpgroup35.RpiHelper;



import com.example.mdpgroup35.State.Node;
import com.example.mdpgroup35.State.State;
import org.json.*;
import java.util.ArrayList;
import java.util.HashMap;

public class Action extends Message {
    public static int DISTANCE_ALLOWABLE = 10;
    public static int MAX_CORRECTION = 3;

    public static String MOVE = "move";
    public static String NOOP = "noop";
    public static String SERIES = "series";
    public static String SERIES_INTERLEAVE = "series_interleave";
    public static String RESET = "reset";
    public static String CAPTURE = "capture";
    public static String BULLSEYE = "bullseye";
    // BRICK NEW
    public static String CMD_FORWARD = "0180000";

    public static String CMD_FORWARD_ = "0110000";
    public static String CMD_BACK_ = "0140000";


    public static String CMD_BACK = "0200000";
    public static String CMD_FORWARD_RIGHT = "2124020";
    public static String CMD_BACK_RIGHT = "1934520";
    public static String CMD_FORWARD_LEFT = "2052040";
    public static String CMD_BACK_LEFT = "2002540";

    public static String CMD_SMOL_FORWARD = "0100000";
    public static String CMD_SMOL_BACK = "0120000";
    public static String CMD_SMOL_FORWARD_RIGHT = "0283515";
    public static String CMD_SMOL_BACK_RIGHT = "0303515";
    public static String CMD_SMOL_FORWARD_LEFT = "0301535";
    public static String CMD_SMOL_BACK_LEFT = "0321535";

    public static String CMD_SPOT_FORWARD = "0140000";
    public static String CMD_SPOT_BACK = "0200000";
    public static String CMD_SPOT_FORWARD_RIGHT = "0503505";
    public static String CMD_SPOT_BACK_RIGHT = "0603505";
    public static String CMD_SPOT_FORWARD_LEFT = "0080535";
    public static String CMD_SPOT_BACK_LEFT = "0080535";

    public static final String CALIBRATE = "calibrate:";
    public static final String STRATEGY = "strategy:";
    public static final String FORWARD = "forward";
    public static final String BACK = "back";
    public static final String STOP = "stop";
    public static final String FORWARD_RIGHT = "forward_right";
    public static final String FORWARD_LEFT = "forward_left";
    public static final String BACK_RIGHT = "back_right";
    public static final String BACK_LEFT = "back_left";

    public String type; //A string indicating the type of the action.
    public String action;//A string describing the action itself.
    public int length;//An integer representing the length of the action data.
    public int angle;//An integer representing the angle of the action (used for certain actions).
    public String distance;//A string representing the distance associated with the action
    public int allowable;//: An integer representing an allowable threshold.
    public int delay;//An integer representing a delay associated with the action.
    public String coordinate;//A string representing a coordinate associated with the action.
    public String prevCoordinate;//A string representing a previous coordinate (optional).
    public ArrayList<Action> data;//    An ArrayList of Action objects, allowing for sequences of actions. These fields are used to store information about the action.

    public Action(String type, String action, String distance, int delay) {
        this.type = notNull(type);
        this.action = notNull(action);
        this.angle = 0;
        this.distance = distance;
        this.delay = delay;
        this.coordinate = notNull(coordinate);
        this.prevCoordinate = notNull(prevCoordinate);
        this.data = new ArrayList<>();
        this.length = this.data.size();
    }

    public Action(String type, String action, int angle, String distance, String coordinate) {
        this.type = notNull(type);
        this.action = notNull(action);
        this.angle = angle;
        this.distance = notNull(distance);
        this.coordinate = notNull(coordinate);
        this.allowable = 0;
        this.delay = 350;
        this.prevCoordinate = "";
        this.data = new ArrayList<>();
        this.length = 0;
    }

    public Action(String type, String action, int angle, int allowable, String prevCoordinate,  String coordinate) {
        this.type = notNull(type);
        this.action = notNull(action);
        this.angle = angle;
        this.distance = "";
        this.delay = 350;
        this.allowable = allowable;
        this.coordinate = notNull(coordinate);
        this.prevCoordinate = notNull(prevCoordinate);
        this.data = new ArrayList<>();
        this.length = this.data.size();
    }



    public Action(String type, ArrayList<Action> data) {
        this.type = notNull(type);
        this.action = " ";
        this.angle = 0;
        this.delay = 350;
        this.distance = notNull(distance);
        this.coordinate = notNull(coordinate);
        this.prevCoordinate = notNull(prevCoordinate);
        this.allowable = 0;
        this.data = data;
        this.length = this.data.size();
    }

    public static Action getReset() { //resets by creating a new action in a default state
        return new Action(
                Action.RESET,
                "",
                0,
                "0000000",
                "2,2,1"
        );
    }
/*
    public static Action getSpotForwardRight(State origin) {
        ArrayList<Action> actions = new ArrayList<>();
        actions.add(new Action(Action.MOVE, Action.FORWARD_RIGHT, 0, Action.CMD_SPOT_FORWARD_RIGHT, origin.getCoord()));
        actions.add(new Action(Action.MOVE, Action.FORWARD_RIGHT, 0, Action.CMD_SPOT_FORWARD_RIGHT, origin.getCoord()));
        actions.add(new Action(Action.MOVE, Action.BACK, 0, Action.CMD_SPOT_BACK, origin.getCoord()));
        actions.add(new Action(Action.MOVE, Action.FORWARD_RIGHT, 0, Action.CMD_SPOT_FORWARD_RIGHT, origin.getCoord()));
        actions.add(new Action(Action.MOVE, Action.FORWARD_RIGHT, 0, Action.CMD_SPOT_FORWARD_RIGHT, origin.getCoord()));
        actions.add(new Action(Action.MOVE, Action.BACK, 0, Action.CMD_SPOT_BACK, origin.getCoord()));
        actions.add(new Action(Action.MOVE, Action.FORWARD_RIGHT, 0, Action.CMD_SPOT_FORWARD_RIGHT, origin.getCoord()));
        return new Action(Action.SERIES, actions);
    }

    public static Action getSpotBackRight(State origin) {
        ArrayList<Action> actions = new ArrayList<>();
        actions.add(new Action(Action.MOVE, Action.BACK_RIGHT, 0, Action.CMD_SPOT_BACK_RIGHT, origin.getCoord()));
        actions.add(new Action(Action.MOVE, Action.FORWARD, 0, Action.CMD_SPOT_FORWARD, origin.getCoord()));
        actions.add(new Action(Action.MOVE, Action.BACK_RIGHT, 0, Action.CMD_SPOT_BACK_RIGHT, origin.getCoord()));
        actions.add(new Action(Action.MOVE, Action.BACK_RIGHT, 0, Action.CMD_SPOT_BACK_RIGHT, origin.getCoord()));
        actions.add(new Action(Action.MOVE, Action.FORWARD, 0, Action.CMD_SPOT_FORWARD, origin.getCoord()));

        actions.add(new Action(Action.MOVE, Action.BACK_RIGHT, 0, Action.CMD_SPOT_BACK_RIGHT, origin.getCoord()));
        return new Action(Action.SERIES, actions);
    }

    public static Action getSpotForwardLeft(State origin) {
        ArrayList<Action> actions = new ArrayList<>();
        actions.add(new Action(Action.MOVE, Action.FORWARD_LEFT, 0, Action.CMD_SPOT_FORWARD_LEFT, origin.getCoord()));
        actions.add(new Action(Action.MOVE, Action.FORWARD_LEFT, 0, Action.CMD_SPOT_FORWARD_LEFT, origin.getCoord()));
        actions.add(new Action(Action.MOVE, Action.BACK, 0, Action.CMD_SPOT_BACK, origin.getCoord()));
        actions.add(new Action(Action.MOVE, Action.FORWARD_LEFT, 0, Action.CMD_SPOT_FORWARD_LEFT, origin.getCoord()));
        actions.add(new Action(Action.MOVE, Action.FORWARD_LEFT, 0, Action.CMD_SPOT_FORWARD_LEFT, origin.getCoord()));
        actions.add(new Action(Action.MOVE, Action.BACK, 0, Action.CMD_SPOT_BACK, origin.getCoord()));
        actions.add(new Action(Action.MOVE, Action.FORWARD_LEFT, 0, Action.CMD_SPOT_FORWARD_LEFT, origin.getCoord()));
        return new Action(Action.SERIES, actions);
    }

    public static Action getSpotBackLeft(State origin) {
        ArrayList<Action> actions = new ArrayList<>();
        actions.add(new Action(Action.MOVE, Action.BACK_LEFT, 0, Action.CMD_SPOT_BACK_LEFT, origin.getCoord()));
        actions.add(new Action(Action.MOVE, Action.FORWARD, 0, Action.CMD_SPOT_FORWARD, origin.getCoord()));
        actions.add(new Action(Action.MOVE, Action.BACK_LEFT, 0, Action.CMD_SPOT_BACK_LEFT, origin.getCoord()));
        actions.add(new Action(Action.MOVE, Action.BACK_LEFT, 0, Action.CMD_SPOT_BACK_LEFT, origin.getCoord()));
        actions.add(new Action(Action.MOVE, Action.FORWARD, 0, Action.CMD_SPOT_FORWARD, origin.getCoord()));
        actions.add(new Action(Action.MOVE, Action.BACK_LEFT, 0, Action.CMD_SPOT_BACK_LEFT, origin.getCoord()));
        actions.add(new Action(Action.MOVE, Action.BACK_LEFT, 0, Action.CMD_SPOT_BACK_LEFT, origin.getCoord()));
        return new Action(Action.SERIES, actions);
    }
*/


    public static Action slideToLeft(State origin, int correction) {
        ArrayList<Action> actions = new ArrayList<>();
        actions.add(new Action(Action.MOVE, Action.BACK_LEFT, 0, Action.CMD_SMOL_BACK_LEFT, origin.getCoord()));
        actions.add(new Action(Action.MOVE, Action.BACK, 0, Action.CMD_SMOL_BACK, origin.getCoord()));
        actions.add(new Action(Action.MOVE, Action.FORWARD_LEFT, 0, Action.CMD_SMOL_FORWARD_LEFT, origin.getCoord()));
        actions.add(new Action(Action.MOVE, Action.FORWARD, 0, Action.CMD_SMOL_FORWARD, origin.getCoord()));
        actions.add(new Action(Action.CAPTURE, Action.CALIBRATE+correction, 0, DISTANCE_ALLOWABLE,  origin.getCoord(), origin.getCoord()));
        return new Action(Action.SERIES_INTERLEAVE, actions);
    }

    public static Action slideToRight(State origin, int correction) {
        ArrayList<Action> actions = new ArrayList<>();
        actions.add(new Action(Action.MOVE, Action.BACK_RIGHT, 0, Action.CMD_SMOL_BACK_RIGHT, origin.getCoord()));
        actions.add(new Action(Action.MOVE, Action.BACK, 0, Action.CMD_SMOL_BACK, origin.getCoord()));
        actions.add(new Action(Action.MOVE, Action.FORWARD_RIGHT, 0, Action.CMD_SMOL_FORWARD_RIGHT, origin.getCoord()));
        actions.add(new Action(Action.MOVE, Action.FORWARD, 0, Action.CMD_SMOL_FORWARD, origin.getCoord()));
        actions.add(new Action(Action.CAPTURE, Action.CALIBRATE+correction, 0, DISTANCE_ALLOWABLE,  origin.getCoord(), origin.getCoord()));
        return new Action(SERIES_INTERLEAVE, actions);
    }

    public static Action forwardStrategy(State robotCoord, State obstacleCoord) {
        ArrayList<Action> actions = new ArrayList<>();
        actions.add(new Action(Action.MOVE, Action.FORWARD, 0, Action.CMD_FORWARD_, robotCoord.getCoord()));
        actions.add(new Action(Action.CAPTURE, Action.STRATEGY+Action.FORWARD, 0, Action.DISTANCE_ALLOWABLE,  robotCoord.getCoord(), obstacleCoord.getCoord()));
        actions.add(new Action(Action.MOVE, Action.BACK, 0, Action.CMD_BACK_, robotCoord.getCoord()));
        return new Action(Action.SERIES_INTERLEAVE, actions);
    }

    public static Action backStrategy(State robotCoord, State obstacleCoord) {
        ArrayList<Action> actions = new ArrayList<>();
        actions.add(new Action(Action.MOVE, Action.BACK, 0, Action.CMD_BACK_, robotCoord.getCoord()));
        actions.add(new Action(Action.CAPTURE, Action.STRATEGY+Action.BACK, 0, Action.DISTANCE_ALLOWABLE,  robotCoord.getCoord(), obstacleCoord.getCoord()));
        actions.add(new Action(Action.MOVE, Action.FORWARD, 0, Action.CMD_FORWARD_, robotCoord.getCoord()));
        return new Action(Action.SERIES_INTERLEAVE, actions);
    }

    public static Action getInterleaveNoop() {
        // Algo determine
        ArrayList<Action> actions = new ArrayList<>();
        actions.add(new Action(Action.NOOP, Action.NOOP, 0, Action.NOOP, Action.NOOP));
        return new Action(Action.SERIES_INTERLEAVE, actions);
    }

    public static Action findShortestPath(State start, ArrayList<State> obstacles) {
        DijkstraPath dp = new DijkstraPath(start);
        HashMap<String, Object> d = new HashMap<>();
        // Generate shortest path
        dp.execute(start, obstacles, d);
        // Get path sequence
        ArrayList<State> seq = (ArrayList<State>) d.get("path");

        ArrayList<ArrayList<Node>> fp = (ArrayList<ArrayList<Node>>) d.get("final_path");
        // Converted actions
        ArrayList<Action> actions = new ArrayList<>();
        STMCommands.getSTMCommands(fp, seq, actions);

        return new Action(Action.SERIES, actions);
    }

    public static String getActionValues() {
        return String.format("F %s,B %s,FR %s,BR %s,FL %s,BL %s", CMD_FORWARD, CMD_BACK, CMD_FORWARD_RIGHT, CMD_BACK_RIGHT, CMD_FORWARD_LEFT, CMD_BACK_LEFT);
    }

    public String getCommand() {
        return String.format("%s | %s", this.action, this.coordinate);
    }

    public static void setActionValues(String commandStr) {//calibration from the text box
        System.out.println("the string is " + commandStr);
        String[] commands  = commandStr.split(",");
        System.out.println("commands is " + commands);
        for (String command : commands) {
            //System.out.println("command come on "+ command);
            String [] pair =  command.split(" ");
            String type = pair[0];
            String value = pair[1];


            if (type.equalsIgnoreCase("F")) {
                CMD_FORWARD = value;
                System.out.println("value of CMD_FORWARD is " + value);
            }
            else if (type.equalsIgnoreCase("B"))
                CMD_BACK = value;
            else if (type.equalsIgnoreCase("FR"))
                CMD_FORWARD_RIGHT = value;
            else if (type.equalsIgnoreCase("BR"))
                CMD_BACK_RIGHT = value;
            else if (type.equalsIgnoreCase("FL"))
                CMD_FORWARD_LEFT = value;
            else if (type.equalsIgnoreCase("BL"))
                CMD_BACK_LEFT = value;
        }
    }

    public JSONObject toJSONObject() throws JSONException {
        JSONObject jsonObject = new JSONObject();

        jsonObject.put("action", action);
        jsonObject.put("type", type);
        jsonObject.put("angle", angle);
        jsonObject.put("delay", delay);
        jsonObject.put("distance", distance);
        jsonObject.put("coordinate", coordinate);
        jsonObject.put("allowable", allowable);
        jsonObject.put("prev_coordinate", prevCoordinate);
        jsonObject.put("length", length);

        JSONArray jsonArray = new JSONArray();

        for (Action a : data)
            jsonArray.put(a.toJSONObject());
        jsonObject.put("data", jsonArray);

        return jsonObject;
    }

    public String toJSON() throws JSONException {
        JSONObject jsonObject = toJSONObject();
        return jsonObject.toString();
    }

    public String toJSON(boolean childData) throws JSONException {
        return childData ? toJSON() : toJSONObject().toString();
    }


}
