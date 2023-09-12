package com.example.mdpgroup35.Algo;

import java.util.*;


public class DijkstraPath {
    private State start;
    private double cost;
    final int SATE_DIST = 1;
    final int MAP_SIZE = 20;

    private ArrayList<Node> path = new ArrayList<Node>();
    private int[][] map;

    public DijkstraPath(State start) {
        this.start = start;
    }

    public int[][][] getMotion() {
        int t = 6;
        int backT = 6;
//        int[][][] motion3 = {
//                { { 1, 0, 0, 1 }, { 3, 3, 1, t }, { 3, -3, 3, t }, { -1, 0, 0, 1 }, { -3, -3, 1, backT },
//                        { -3, 3, 3, backT } },
//                { { 0, 1, 1, 1 }, { -3, 3, 2, t }, { 3, 3, 0, t }, { 0, -1, 1, 1 }, { -3, -3, 0, backT },
//                        { 3, -3, 2, backT } },
//                { { -1, 0, 2, 1 }, { -3, 3, 1, t }, { -3, -3, 3, t }, { 1, 0, 2, 1 }, { 3, 3, 3, backT },
//                        { 3, -3, 1, backT } },
//                { { 0, -1, 3, 1 }, { -3, -3, 2, t }, { 3, -3, 0, t }, { 0, 1, 3, 1 }, { -3, 3, 0, backT },
//                        { 3, 3, 2, backT } } };

//        int[][][] motion3 = {
//                { { 1, 0, 0, 1 }, { 2, 3, 1, t }, { 2, -3, 3, t }, { -1, 0, 0, 1 }, { -3, -2, 1, backT },
//                        { -3, 2, 3, backT } },
//                { { 0, 1, 1, 1 }, { -3, 2, 2, t }, { 3, 2, 0, t }, { 0, -1, 1, 1 }, { -2, -3, 0, backT },
//                        { 2, -3, 2, backT } },
//                { { -1, 0, 2, 1 }, { -2, 3, 1, t }, { -2, -3, 3, t }, { 1, 0, 2, 1 }, { 3, 2, 3, backT },
//                        { 3, -2, 1, backT } },
//                { { 0, -1, 3, 1 }, { -3, -2, 2, t }, { 3, -2, 0, t }, { 0, 1, 3, 1 }, { -2, 3, 0, backT },
//                        { 2, 3, 2, backT } } };

        int[][][] motion3 = {
                { { 1, 0, 0, 1 }, { 2, 4, 1, t }, { 2, -4, 3, t }, { -1, 0, 0, 1 }, { -4, -2, 1, backT },
                        { -4, 2, 3, backT } },
                { { 0, 1, 1, 1 }, { -4, 2, 2, t }, { 4, 2, 0, t }, { 0, -1, 1, 1 }, { -2, -4, 0, backT },
                        { 2, -4, 2, backT } },
                { { -1, 0, 2, 1 }, { -2, 4, 1, t }, { -2, -4, 3, t }, { 1, 0, 2, 1 }, { 4, 2, 3, backT },
                        { 4, -2, 1, backT } },
                { { 0, -1, 3, 1 }, { -4, -2, 2, t }, { 4, -2, 0, t }, { 0, 1, 3, 1 }, { -2, 4, 0, backT },
                        { 2, 4  , 2, backT } } };
        return motion3;
    }

    public boolean CheckPoint(int x, int y, int[][] map) {
        if (x < 0 || y < 0 || x >= MAP_SIZE || y >= MAP_SIZE) {
            return false;
        }
        if (map[x][y] == 1) {
            return false;
        }
        return true;
    }

    public int[][] SetUpMap(ArrayList<State> obstacles) {
        int[][] map = new int[20][20];
        for (int a = 0; a < 20; a++)
            for (int b = 0; b < 20; b++)
                map[a][b] = 0;
        for (State i : obstacles) {
            map[i.x][i.y] = 1;
            for (int dx = -SATE_DIST; dx < SATE_DIST + 1; dx++) {
                for (int dy = -SATE_DIST; dy < SATE_DIST + 1; dy++) {
                    if ((i.x + dx) >= MAP_SIZE || (i.x + dx) < 0 || (i.y + dy) >= MAP_SIZE || (i.y + dy) < 0) {
                        continue;
                    }
                    map[i.x + dx][i.y + dy] = 1;
                }
            }
        }
        for (int i = 0; i < MAP_SIZE; i++) {
//            map[i][1] = 1;
            map[i][0] = 1;
            map[i][MAP_SIZE - 1] = 1;
            map[0][i] = 1;
//            map[1][i] = 1;
            map[MAP_SIZE - 1][i] = 1;
//            map[MAP_SIZE - 2][i] = 1;
//            map[i][MAP_SIZE - 2] = 1;
        }
        // for i in range(21):
        // map[20][i]=1
        // for i in range(21):
        // map[i][20]=1
        // print(map)
        return map;
    }

    public boolean HasIntersect(int x0, int y0, int dir, int dx, int dy, int[][] map) {


        if (dir == 0 & dx > 0 || dir == 2 & dx < 0) {
            if (map[x0+ (int) Math.floor(dx / 2)][y0] == 1)
                return true;
            if(map[x0+dx][y0] == 1)
                return true;
            if (map[x0 + dx][y0 +(int) Math.floor(dy / 4)] == 1)
                return true;
            if (map[x0 + dx][y0 +2*(int) Math.floor(dy / 4)] == 1)
                return true;
            if (map[x0 + dx][y0 +3*(int) Math.floor(dy / 4)] == 1)
                return true;
        } else if (dir == 1 & dy > 0 || dir == 3 & dy < 0) {

            if (map[x0][y0 + (int) Math.floor(dy / 2)] == 1)
                return true;
            if(map[x0][y0 + dy] == 1)
                return true;
            if (map[x0 + (int) Math.floor(dx / 4)][y0 + dy] == 1)
                return true;
            if (map[x0 + 2*(int) Math.floor(dx / 4)][y0 + dy] == 1)
                return true;
            if (map[x0 + 3*(int) Math.floor(dx / 4)][y0 + dy] == 1)
                return true;


        } else if (dir == 0 & dx < 0 || dir == 2 & dx > 0) {
            if (map[x0+(int) Math.floor(dx / 4)][y0] == 1)
                return true;
            if(map[x0+2*(int) Math.floor(dx / 4)][y0] == 1)
                return true;
            if(map[x0+3*(int) Math.floor(dx / 4)][y0] == 1)
                return true;
            if (map[x0 + dx][y0] == 1)
                return true;
            if (map[x0+dx][y0 +(int) Math.floor(dy / 2)] == 1)
                return true;
        } else if (dir == 1 & dy < 0 || dir == 3 & dy > 0) {

            if (map[x0][y0+(int) Math.floor(dy / 4)] == 1)
                return true;
            if(map[x0][y0+2*(int) Math.floor(dy / 4)] == 1)
                return true;
            if(map[x0][y0+3*(int) Math.floor(dy / 4)] == 1)
                return true;
            if (map[x0][y0+dy] == 1)
                return true;
            if (map[x0+(int) Math.floor(dx / 2)][y0 +dy] == 1)
                return true;
        }

        return false;
//        if (dir == 0 & dx > 0 || dir == 2 & dx < 0) {
//            // print(x0+(int)Math.floor(dx/2),y0+(int)Math.floor(dy/3))
//            if (map[x0 + (int) Math.floor(dx / 2)][y0] == 1
//                    & map[x0 + (int) Math.floor(dx / 2)][y0 + (int) Math.floor(dy / 3)] == 1)
//                return true;
//            if (map[x0 + (int) Math.floor(dx / 2)][y0 + (int) Math.floor((int) Math.floor(dy / 3))] == 1
//                    & map[x0 + dx][y0 + (int) Math.floor((int) Math.floor(dy / 3))] == 1)
//                return true;
//            if (map[x0 + (int) Math.floor(dx / 2)][y0 + 2 * (int) Math.floor(dy / 3)] == 1
//                    & map[x0 + dx][y0 + 2 * (int) Math.floor(dy / 3)] == 1)
//                return true;
//        } else if (dir == 1 & dy > 0 || dir == 3 & dy < 0) {
//
//            if (map[x0][y0 + (int) Math.floor(dy / 2)] == 1
//                    & map[x0 + (int) Math.floor(dx / 3)][y0 + (int) Math.floor(dy / 2)] == 1)
//                return true;
//            if (map[x0 + (int) Math.floor(dx / 3)][y0 + (int) Math.floor(dy / 2)] == 1
//                    & map[x0 + (int) Math.floor(dx / 3)][y0 + dy] == 1)
//                return true;
//            if (map[x0 + 2 * (int) Math.floor(dx / 3)][y0 + (int) Math.floor(dy / 2)] == 1
//                    & map[x0 + 2 * (int) Math.floor(dx / 3)][y0 + dy] == 1)
//                return true;
//
//        } else if (dir == 0 & dx < 0 || dir == 2 & dx > 0) {
//            if (map[x0 + (int) Math.floor(dx / 3)][y0 + (int) Math.floor(dy / 2)] == 1
//                    & map[x0 + 2 * (int) Math.floor(dx / 3)][y0 + (int) Math.floor(dy / 2)] == 1)
//                return true;
//            if (map[x0 + (int) Math.floor(dx / 3)][y0 + (int) Math.floor(dy / 2)] == 1
//                    & map[x0 + (int) Math.floor(dx / 3)][y0] == 1)
//                return true;
//            if (map[x0 + 2 * (int) Math.floor(dx / 3)][y0 + (int) Math.floor(dy / 2)] == 1
//                    & map[x0 + 2 * (int) Math.floor(dx / 3)][y0] == 1)
//                return true;
//        } else if (dir == 1 & dy < 0 || dir == 3 & dy > 0) {
//
//            if (map[x0 + (int) Math.floor(dx / 2)][y0] == 1
//                    & map[x0 + (int) Math.floor(dx / 2)][y0 + (int) Math.floor(dy / 3)] == 1)
//                return true;
//            if (map[x0 + (int) Math.floor(dx / 2)][y0 + (int) Math.floor(dy / 3)] == 1
//                    & map[x0 + dx][y0 + (int) Math.floor(dy / 3)] == 1)
//                return true;
//            if (map[x0 + (int) Math.floor(dx / 2)][y0 + 2 * (int) Math.floor(dy / 3)] == 1
//                    & map[x0 + dx][y0 + 2 * (int) Math.floor(dy / 3)] == 1)
//                return true;
//        }
//
//        return false;


//        if (dir == 0 || dir == 2 ) {
//            // print(x0+(int)Math.floor(dx/2),y0+(int)Math.floor(dy/3))
//            //x 1 step, y 0 step as well as x 1 step and y 1 step
//            if (map[x0 + (int) Math.floor(dx / 3)][y0] == 1
//                    & map[x0 + (int) Math.floor(dx / 3)][y0 + (int) Math.floor(dy / 3)] == 1)
//                return true;
//
//            //x 2 step, y 0 step as well as x 2 step and y 1 step
//            if (map[x0 + 2*(int) Math.floor(dx / 3)][y0] == 1
//                    & map[x0 + 2*(int) Math.floor(dx / 3)][y0 + (int) Math.floor(dy / 3)] == 1)
//                return true;
//
//            //x 3 step, y 1 step as well as x 2 step and y 1 step
//            if (map[x0 + dx][y0 + (int) Math.floor(dy / 3)] == 1
//                    & map[x0 + 2*(int) Math.floor(dx / 3)][y0 + (int) Math.floor(dy / 3)] == 1)
//                return true;
//
//            //x 3 step, y 2 steps as well as x 2 steps and y 2 steps
//            if (map[x0 + dx][y0 + 2*(int) Math.floor(dy / 3)] == 1
//                    & map[x0 + 2*(int) Math.floor(dx / 3)][y0 + 2*(int) Math.floor(dy / 3)] == 1)
//                return true;
//        } else if (dir == 1 || dir == 3 ) {
//            // x 0 step y 1 step as well as x 1 step and y 1 step
//            if (map[x0][y0 + (int) Math.floor(dy / 3)] == 1
//                    & map[x0 + (int) Math.floor(dx / 3)][y0 + (int) Math.floor(dy / 3)] == 1)
//                return true;
//
//            // x 0 step y 2 step as well as x 1 step and y 2 step
//            if (map[x0 ][y0 + 2*(int) Math.floor(dy / 3)] == 1
//                    & map[x0 + (int) Math.floor(dx / 3)][y0 + 2*(int) Math.floor(dy / 3)] == 1)
//                return true;
//
//            // x 1 step y 2 step as well as x 1 step and y 3 step
//            if (map[x0 + (int) Math.floor(dx / 3)][y0 + 2*(int) Math.floor(dy / 3)] == 1
//                    & map[x0 + (int) Math.floor(dx / 3)][y0 + dy] == 1)
//                return true;
//
//            // x 2 step y 2 step as well as x 2 step and y 3 step
//            if (map[x0 + 2*(int) Math.floor(dx / 3)][y0 + 2*(int) Math.floor(dy / 3)] == 1
//                    & map[x0 + 2*(int) Math.floor(dx / 3)][y0 + dy] == 1)
//                return true;
//        }
//        return false;
    }

    public int Index(State a) {
        return a.dir * 21 * 21 + a.y * 21 + a.x;
    }

    public int Index(Node a) {
        return a.dir * 21 * 21 + a.y * 21 + a.x;
    }

    public void Dijkstra(State start, State end, int[][] map) {
        Node startNode = new Node(start.x, start.y, start.dir, 0, -1);
        Node goalNode = new Node(end.x, end.y, end.dir, 13.0, -1);

        Map settled = new HashMap();
        Map unsettled = new HashMap();
        unsettled.put(Index(start), new Node(startNode));
        int[][][] motion = getMotion();

        while (unsettled.size() > 0) {
            double minCost = 100000000000000.0;
            int curId = 0;
            Set<Map.Entry<Integer, Node>> oneSet = unsettled.entrySet();

            for (Map.Entry<Integer, Node> oneEntry : oneSet) {
                if (oneEntry.getValue().cost < minCost) {
                    minCost = oneEntry.getValue().cost;
                    curId = oneEntry.getKey();
                }
            }

            Node curNode = (Node) unsettled.get(curId);

            if (curNode.dir == 0 && end.dir == 0) {
                if ((end.x - curNode.x <= 1) && (curNode.y == end.y) && (end.x >= curNode.x)) {
                    printPath(curNode, settled);
                }
            }

            if (curNode.dir == 1 && end.dir == 1) {
                if ((end.x == curNode.x) && (end.y - curNode.y <= 1) && (end.y >= curNode.y)) {
                    printPath(curNode, settled);
                }
            }

            if (curNode.dir == 2 && end.dir == 2) {
                if ((end.y == curNode.y) && (curNode.x - end.x <= 1) && (curNode.x >= end.x)) {
                    printPath(curNode, settled);
                }
            }

            if (curNode.dir == 3 && end.dir == 3) {
                if ((end.x == curNode.x) && (curNode.y - end.y <= 1 && curNode.y >= end.y)) {
                    printPath(curNode, settled);
                }
            }

            unsettled.remove(curId);

            settled.put(curId, new Node(curNode));

            for (int i = 0; i < motion[curNode.dir].length; i++) {
                int dx = motion[curNode.dir][i][0];
                int dy = motion[curNode.dir][i][1];
                int newDir = motion[curNode.dir][i][2];
                int cost = motion[curNode.dir][i][3];

                if (!CheckPoint(dx + curNode.x, dy + curNode.y, map)) {
                    continue;
                }

                Node nextNode = new Node(dx + curNode.x, dy + curNode.y, newDir, curNode.cost + cost, curId);

                if (curNode.dir != newDir) {
                    if (HasIntersect(curNode.x, curNode.y, curNode.dir, dx, dy, map)) {
                        continue;
                    }
                }

                int newId = Index(nextNode);

                if (settled.get(newId) instanceof Object) {
                    continue;
                }
                if (!(unsettled.get(newId) instanceof Object)) {
                    unsettled.put(newId, new Node(nextNode));
                } else if (((Node) unsettled.get(newId)).cost > nextNode.cost) {
                    unsettled.remove(newId);
                    unsettled.put(newId, new Node(nextNode));
                }
            }
        }
    }

    public void printPath(Node curNode, Map settled) {
        path.clear();
        path.add(curNode);
        int parentId = curNode.parent;
//        System.out.println("backtracking "+curNode.x+" "+ curNode.y+" "+ curNode.cost+" "+ curNode.dir);
        if (parentId != -1) {
            //cost = ((Node) settled.get(parentId)).cost;
            cost = curNode.cost;
//            System.out.println("last node"+curNode.x+" "+ curNode.y+" "+ curNode.cost+" "+ curNode.dir);
        }
        else
            cost = 0;

        while (parentId != -1) {
            Node parent = (Node) settled.get(parentId);
            path.add(parent);
            parentId = parent.parent;
//            System.out.println("prev node"+parent.x+" "+ parent.y+" "+ parent.cost+" "+ parent.dir);

        }

//        System.out.println("Final Dijkstra path");
//        for(Node p:path){
//            System.out.println(p.x+ " "+p.y+" "+p.dir);
//        }
    }

    public void execute(State start, ArrayList<State> obstacles, HashMap<String, Object> d) {
        ArrayList<Point> obs = new ArrayList<Point>();
        ArrayList<State> goals = new ArrayList<State>();
        ArrayList<State> sequence = new ArrayList<State>();

        // need to decide if map size should be 20 or 21
        double[][] costs = new double[20][20];
        goals.add(start);
        for (State o : obstacles) {
            obs.add(new Point(o.x, o.y));
            if (o.dir == 0) {
                goals.add(new State(o.x + 4, o.y, 2));
            } else if (o.dir == 1) {
                goals.add(new State(o.x, o.y + 4, 3));
            } else if (o.dir == 2) {
                goals.add(new State(o.x - 4, o.y, 0));
            } else if (o.dir == 3) {
                goals.add(new State(o.x, o.y - 4, 1));
            }

        }

        int[][] map = SetUpMap(obstacles);
        for (int i = 0; i < goals.size(); i += 1) {
            for (int j = 1; j < goals.size(); j += 1) {
                try {
                    Dijkstra(goals.get(i), goals.get(j), map);
                    Collections.reverse(path);
                    costs[i][j] = cost;
                    System.out.println(goals.get(i).x+"."+goals.get(i).y+"."+goals.get(i).dir+"  "+goals.get(j).x+"."+goals.get(j).y+"."+goals.get(j).dir+" "+cost);
                } catch (Exception e) {
                    costs[i][j] = 1000000000;
                    System.out.println("exception"+goals.get(i).x+"."+goals.get(i).y+"."+goals.get(i).dir+"  "+goals.get(j).x+"."+goals.get(j).y+"."+goals.get(j).dir+" "+cost);
                }
            }

        }

        PathPlanner pp = new PathPlanner(goals.size());
        pp.plan(costs, goals.size());

        // path is the result of the latest run of dijkstra while p is the path after
        // path planning
        int[] p = pp.path;
        double min_cost = pp.min_cost;
        // final_path definition
        ArrayList<ArrayList<Node>> final_path = new ArrayList<ArrayList<Node>>();
        State startBegin = new State(goals.get(p[0]).x, goals.get(p[0]).y, goals.get(p[0]).dir);

        for (int i = 1; i < goals.size(); i += 1) {
            cost = 1000000000;
            ArrayList<Node> one_step_path = new ArrayList<Node>();
            path.clear();

            Dijkstra(startBegin, goals.get(p[i]), map);
            ArrayList<Node> res_path = path;
            double res_cost = cost;
            if (res_path.isEmpty()) {
                System.out.println("hello");
                double c = 1000000000;
                if (goals.get(p[i]).dir % 2 == 0) {
                    State altgoal1 = new State(goals.get(p[i]).x, goals.get(p[i]).y + 1, goals.get(p[i]).dir);
                    State altgoal2 = new State(goals.get(p[i]).x, goals.get(p[i]).y - 1, goals.get(p[i]).dir);

                    Dijkstra(startBegin, altgoal1, map);
                    ArrayList<Node> path1 = path;
                    double cost1 = cost;

                    Dijkstra(startBegin, altgoal2, map);
                    ArrayList<Node> path2 = path;
                    double cost2 = cost;

                    if (!path1.isEmpty()) {
                        if (cost1 < c) {
                            one_step_path = path1;
                            c = cost1;
                        }
                    }

                    if (!path2.isEmpty()) {
                        if (cost2 < c) {
                            one_step_path = path2;
                            c = cost2;
                        }
                    }

                }

                else {
                    State altgoal1 = new State(goals.get(p[i]).x + 1, goals.get(p[i]).y, goals.get(p[i]).dir);
                    State altgoal2 = new State(goals.get(p[i]).x - 1, goals.get(p[i]).y, goals.get(p[i]).dir);

                    Dijkstra(startBegin, altgoal1, map);
                    ArrayList<Node> path1 = path;
                    double cost1 = cost;

                    Dijkstra(startBegin, altgoal2, map);
                    ArrayList<Node> path2 = path;
                    double cost2 = cost;

                    if (!path1.isEmpty()) {
                        if (cost1 < c) {
                            one_step_path = path1;
                            c = cost1;
                        }
                    }

                    if (!path2.isEmpty()) {
                        if (cost2 < c) {
                            one_step_path = path2;
                            c = cost2;
                        }
                    }

                }

            }

            if (!res_path.isEmpty()) {
                one_step_path = res_path;
            }

            if (!one_step_path.isEmpty()) {
                Node startNode = one_step_path.get(0);
                startBegin =  new State(startNode.x, startNode.y, startNode.dir);

                Collections.reverse(one_step_path);
                ArrayList<Node> clonePath = (ArrayList<Node>) one_step_path.clone();
                final_path.add(clonePath);
                State oneObstacle = new State(obstacles.get(p[i]-1).x, obstacles.get(p[i]-1).y, obstacles.get(p[i]-1).dir);
                sequence.add(obstacles.get(p[i]-1));
            }

        }

        d.put("path", sequence);
        d.put("final_path", final_path);
    }
}

