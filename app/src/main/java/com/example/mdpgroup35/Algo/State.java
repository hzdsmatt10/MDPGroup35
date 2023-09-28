package com.example.mdpgroup35.Algo;
public class State {
    public int x;
    public int y;
    public int dir; ///////////////////////may need to change format////////////////////////////////
    public String dirStr;
    public String id;
    public String name;




//may need to change////////////////////////////////
    public static final int LEFT = 180;
    public static final int RIGHT = 0;
    public static final int UP = 90;
    public static final int DOWN = -90;
    //may need to change////////////////////////////////

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

    public String sendCoord2(int index)
    {
        return String.format("%d,%d,%d,%d", (x+1)*10, (y+1)*10, dir,index);


    }
    ////to delete//////
    public String sendCoord4times(int index)
    {
        if(dir==180)
        {
            return String.format("[[%d,%d,%d,%d],[%d,%d,%d,%d],[%d,%d,%d,%d],[%d,%d,%d,%d]]", (x+1)*10, (y+1)*10, dir,index, (x+1)*10, (y+1)*10, dir-90,index, (x+1)*10, (y+1)*10, dir-180,index, (x+1)*10, (y+1)*10, dir-270,index);
        }
        else if(dir==90)
        {
            return String.format("[[%d,%d,%d,%d],[%d,%d,%d,%d],[%d,%d,%d,%d],[%d,%d,%d,%d]]", (x+1)*10, (y+1)*10, dir+90,index, (x+1)*10, (y+1)*10, dir,index, (x+1)*10, (y+1)*10, dir-90,index, (x+1)*10, (y+1)*10, dir-180,index);
        }
        else if(dir ==0)
        {
            return String.format("[[%d,%d,%d,%d],[%d,%d,%d,%d],[%d,%d,%d,%d],[%d,%d,%d,%d]]", (x+1)*10, (y+1)*10, dir+180,index, (x+1)*10, (y+1)*10, dir+90,index, (x+1)*10, (y+1)*10, dir,index, (x+1)*10, (y+1)*10, dir-90,index);
        }
        else if (dir ==-90)
        {
            return String.format("[[%d,%d,%d,%d],[%d,%d,%d,%d],[%d,%d,%d,%d],[%d,%d,%d,%d]]", (x+1)*10, (y+1)*10, dir+270,index, (x+1)*10, (y+1)*10, dir+180,index, (x+1)*10, (y+1)*10, dir+90,index, (x+1)*10, (y+1)*10, dir,index);
        }
        else return String.format("[[%d,%d,%d,%d],[%d,%d,%d,%d],[%d,%d,%d,%d],[%d,%d,%d,%d]]", (x+1)*10, (y+1)*10, dir,index, (x+1)*10, (y+1)*10, dir-90,index, (x+1)*10, (y+1)*10, dir-180,index, (x+1)*10, (y+1)*10, dir-270,index);
    }



}
