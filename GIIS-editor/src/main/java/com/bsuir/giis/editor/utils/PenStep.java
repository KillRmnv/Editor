package com.bsuir.giis.editor.utils;

import com.bsuir.giis.editor.model.Point;

public class PenStep implements Step{
    private int x;
    private int y;
    private int brightness;

    public PenStep() {
        x=-1;
        y=-1;
        brightness=255;
    }
    public PenStep(int x, int y) {
        this.x = x;
        this.y = y;
    }
    public PenStep(int x, int y, int brightness) {
        this.x = x;
        this.y = y;
        this.brightness = brightness;
    }
    public int getBrightness(){
        return brightness;
    }
    public void setBrightness(int brightness){
        this.brightness = brightness;
    }
    public int getX() {
        return x;
    }
    public int getY() {
        return y;
    }
    public void setX(int x) {
        this.x = x;
    }
    public void setY(int y) {
        this.y = y;
    }
    @Override
    public String toString(){
        return "x:"+x+" y:"+y+" brightness:"+brightness+'\n';
    }

    @Override
    public boolean isReady() {

        return x>=0 && y>=0;
    }

    @Override
    public void clean() {
        x=-1;
        y=-1;
    }
    public Point getPoint(){
        return new Point(x,y);
    }
}
