package com.example.mdpgroup35.Algo;
public class State {
    public int x;
    public int y;
    public int dir;
    public String dirStr;
    public String id;
    public String name;

    public static final int LEFT = 2;
    public static final int RIGHT = 0;
    public static final int UP = 1;
    public static final int DOWN = 3;

    public State(int x, int y, int dir) {
        this.x = x;
        this.y = y;
        this.dir = dir;
        this.id = "";
        this.name = "";
    }

    public State(int x, int y, String dir) {
        this.x = x;
        this.y = y;
        this.dirStr = dir;
        this.dir = compassToDirection(dir);
    }

    public State(int x, int y, int dir, String id, String name) {
        this.x = x;
        this.y = y;
        this.dir = dir;
        this.id = id;
        this.name = name;
    }


    public static int compassToDirection(String dir) {
        if (dir.equalsIgnoreCase("S"))
            return DOWN;
        else if (dir.equalsIgnoreCase("E"))
            return RIGHT;
        else if (dir.equalsIgnoreCase("W"))
            return LEFT;
        else
            return UP;
    }

    public static String directionToCompass(int dir) {
        if (dir == DOWN)
            return "S";
        else if (dir == RIGHT)
            return "E";
        else if (dir == LEFT)
            return "W";
        else
            return "N";
    }

    public String getCoord() {
        return String.format("%d,%d,%d", x, y, dir);
    }

    public State copy() {
        return new State(this.x, this.y, this.dir, this.id, this.name);
    }

    /**
     * Returns X Y coordinates with | delimiter
     *
     * @return
     */
    public String getXYPair() {
        return x + ", " + y;
    }

}
