package com.bsuir.giis.editor.utils;

import com.bsuir.giis.editor.model.Point;

public class LineStep implements Step{
    private int startX=-1;
    private int endX=-1;
    private int startY=-1;
    private int endY=-1;
    public LineStep() {}
    public LineStep(int startX, int endX) {
        this.startX = startX;
        this.endX = endX;
    }
    public LineStep(int startX, int endX, int startY, int endY) {
        this.startX = startX;
        this.endX = endX;
        this.startY = startY;
        this.endY = endY;
    }
    public void setPoint(int x, int y) {
        if(startX<0 && startY<0){
            startX=x;
            startY=y;
        }else if(endX<0 && endY<0){
            endX=x;
            endY=y;
        }
//        else
//            throw new IllegalArgumentException("startX="+startX+", endX="+endX);
    }
    public void clean(){
        startX=-1;
        endX=-1;
        startY=-1;
        endY=-1;
    }
    public boolean isReady(){
        return startX>=0 && endX>=0 && startY>=0 && endY>=0;
    }
    public boolean isStarted(){
        return startX>=0 && startY>=0 && endX<0 && endY<0;
    }
    public Point getStartPoint(){
        return new Point(startX, startY);
    }
    public Point getEndPoint(){
        return new Point(endX, endY);
    }
    @Override
    public String toString(){
        return " start x:"+startX +" end x:"+endX+" start y:"+startY+" end y:"+endY+'\n';
    }
}
