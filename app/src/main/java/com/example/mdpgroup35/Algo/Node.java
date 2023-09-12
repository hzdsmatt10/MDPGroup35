package com.example.mdpgroup35.Algo;

public class Node {
    public int x;
    public int y;
    public int dir;
    public double cost;
    public int parent;

    public Node(int x, int y, int dir, double cost, int parent) {
        this.x = x;
        this.y = y;
        this.dir = dir;
        this.cost = cost;
        this.parent = parent;
    }

    public Node(Node node) {
        this.x = node.x;
        this.y = node.y;
        this.dir = node.dir;
        this.cost = node.cost;
        this.parent = node.parent;
    }

    public String getState() {
        return String.format("%d,%d,%d", x, y, dir);
    }

}
