package com.example.mdpgroup35.Algo;

import com.example.mdpgroup35.RpiHelper.Action;

import java.util.ArrayList;

public class STMCommands {

    public static void getSTMCommands(ArrayList<ArrayList<Node>> paths, ArrayList<State> obstacles, ArrayList<Action> commands) {

        int obstacleID = 0;
        for (ArrayList<Node> path : paths) {
            for (int i = 1; i < path.size(); i += 1) {
                Node cur = path.get(i - 1);
                Node next = path.get(i);
                String cmd;
//                System.out.println("debugging"+i+" "+(i+1));

                if (cur.dir == next.dir) {
                    if (cur.x == next.x && cur.y == next.y) {
                        continue;
                    } else if ((cur.dir == State.RIGHT & next.x > cur.x) || (cur.dir == State.UP & next.y > cur.y) || (cur.dir == State.LEFT & next.x < cur.x) || (cur.dir == State.DOWN && next.y < cur.y)) {
                        cmd = Action.FORWARD;
                    } else {
                        cmd = Action.BACK;
                    }
                    Action command = new Action(Action.MOVE, cmd, 0, cmd == Action.FORWARD ? Action.CMD_FORWARD : Action.CMD_BACK, next.getState());
                    commands.add(command);
                } else if (cur.dir == 0 & next.dir == 1 & next.y > cur.y) {
                    //put the radius of the turn as the distance
                    Action command = new Action(Action.MOVE, Action.FORWARD_LEFT, 90, Action.CMD_FORWARD_LEFT, next.getState());
                    commands.add(command);

                } else if (cur.dir == 0 & next.dir == 1 & next.y < cur.y) {

                    Action command = new Action(Action.MOVE, Action.BACK_RIGHT, 90, Action.CMD_BACK_RIGHT, next.getState());
                    commands.add(command);

                } else if (cur.dir == 0 & next.dir == 3 & next.y < cur.y) {
                    Action command = new Action(Action.MOVE, Action.FORWARD_RIGHT, 90, Action.CMD_FORWARD_RIGHT, next.getState());
                    commands.add(command);

                } else if (cur.dir == 0 & next.dir == 3 & next.y > cur.y) {
                    Action command = new Action(Action.MOVE, Action.BACK_LEFT, 90, Action.CMD_BACK_LEFT, next.getState());
                    commands.add(command);

                } else if (cur.dir == 1 & next.dir == 2 & next.y > cur.y) {

                    Action command = new Action(Action.MOVE, Action.FORWARD_LEFT, 90, Action.CMD_FORWARD_LEFT, next.getState());
                    commands.add(command);

                } else if (cur.dir == 1 & next.dir == 2 & next.y < cur.y) {

                    Action command = new Action(Action.MOVE, Action.BACK_RIGHT, 90, Action.CMD_BACK_RIGHT, next.getState());
                    commands.add(command);

                } else if (cur.dir == 1 & next.dir == 0 & next.y > cur.y) {

                    Action command = new Action(Action.MOVE, Action.FORWARD_RIGHT, 90, Action.CMD_FORWARD_RIGHT, next.getState());
                    commands.add(command);

                } else if (cur.dir == 1 & next.dir == 0 & next.y < cur.y) {

                    Action command = new Action(Action.MOVE, Action.BACK_LEFT, 90, Action.CMD_BACK_LEFT, next.getState());
                    commands.add(command);


                } else if (cur.dir == 2 & next.dir == 1 & next.y > cur.y) {

                    Action command = new Action(Action.MOVE, Action.FORWARD_RIGHT, 90, Action.CMD_FORWARD_RIGHT, next.getState());
                    commands.add(command);

                } else if (cur.dir == 2 & next.dir == 1 & next.y < cur.y) {

                    Action command = new Action(Action.MOVE, Action.BACK_LEFT, 90, Action.CMD_BACK_LEFT, next.getState());
                    commands.add(command);

                } else if (cur.dir == 2 & next.dir == 3 & next.y < cur.y) {

                    Action command = new Action(Action.MOVE, Action.FORWARD_LEFT, 90, Action.CMD_FORWARD_LEFT, next.getState());
                    commands.add(command);


                } else if (cur.dir == 2 & next.dir == 3 & next.y > cur.y) {

                    Action command = new Action(Action.MOVE, Action.BACK_RIGHT, 90, Action.CMD_BACK_RIGHT, next.getState());
                    commands.add(command);

                } else if (cur.dir == 3 & next.dir == 0 & next.y < cur.y) {


                    Action command = new Action(Action.MOVE, Action.FORWARD_LEFT, 90, Action.CMD_FORWARD_LEFT, next.getState());
                    commands.add(command);

                } else if (cur.dir == 3 & next.dir == 0 & next.y > cur.y) {

                    Action command = new Action(Action.MOVE, Action.BACK_RIGHT, 90, Action.CMD_BACK_RIGHT, next.getState());
                    commands.add(command);

                } else if (cur.dir == 3 & next.dir == 2 & next.y < cur.y) {

                    Action command = new Action(Action.MOVE, Action.FORWARD_RIGHT, 90, Action.CMD_FORWARD_RIGHT, next.getState());
                    commands.add(command);

                } else if (cur.dir == 3 & next.dir == 2 & next.y > cur.y) {
                    Action command = new Action(Action.MOVE, Action.BACK_LEFT, 90, Action.CMD_BACK_LEFT, next.getState());
                    commands.add(command);
                }
            }

            //put the action as stop for type capture
            // Take last index as previous coordinate
            Action path_end = new Action(Action.CAPTURE, "", 0, Action.DISTANCE_ALLOWABLE, commands.get(commands.size() - 1).coordinate, obstacles.get(obstacleID).getCoord());
            obstacleID++;
            commands.add(path_end);
        }
    }
}
