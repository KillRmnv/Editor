package com.bsuir.giis.editor.utils;

public class FillStep implements Step {
    private int x;
    private int y;
    private int width;
    private int height;

    public FillStep() {
        x = -1;
        y = -1;
        width = 0;
        height = 0;
    }

    public FillStep(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    @Override
    public String toString() {
        return "x:" + x + " y:" + y + " width:" + width + " height:" + height + '\n';
    }

    @Override
    public boolean isReady() {
        return x >= 0 && y >= 0;
    }

    @Override
    public void clean() {
        x = -1;
        y = -1;
        width = 0;
        height = 0;
    }
}
