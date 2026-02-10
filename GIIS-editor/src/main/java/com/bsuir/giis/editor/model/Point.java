package com.bsuir.giis.editor.model;

public class Point {
    private int x;
    private int y;
    public Point(final int x, final int y) {
        this.x = x;
        this.y = y;

    }
    public int getX() {
        return x;
    }
    public void setX(final int x) {
        this.x = x;
    }
    public int getY() {
        return y;
    }
    public void setY(final int y) {
        this.y = y;
    }
    public boolean isValid() {
        return x >= 0 && y >= 0;
    }
}

