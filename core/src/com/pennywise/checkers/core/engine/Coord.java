package com.pennywise.checkers.core.engine;

public class Coord             /* coordinate structure for board coordinates */ {
    int x;
    int y;

    public Coord() {
    }

    public Coord(int x, int y) {

        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }
}
