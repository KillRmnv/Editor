package com.bsuir.giis.editor.utils;

import com.bsuir.giis.editor.model.shapes.MorphableShape;
import com.bsuir.giis.editor.model.Point;

public class MorphStep implements Step {
    private PenStep penStep;
    private int pointIndex;
    private MorphableShape<?> morphableShape;

    public MorphStep(PenStep penStep, int pointIndex) {
        this.penStep = penStep;
        this.pointIndex = pointIndex;

    }
    public MorphStep(){
        this.penStep = new PenStep();
        this.pointIndex = 0;
    }


    public MorphableShape<?> getMorphableShape() {
        return morphableShape;
    }

    public PenStep getPenStep() {
        return penStep;
    }
    public int getPointIndex() {
        return pointIndex;
    }
    @Override
    public boolean isReady() {
        return penStep.isReady() ;
    }

    @Override
    public void clean() {
        morphableShape = null;
        penStep.clean();
    }
    public void setup(Point point,MorphableShape<?> morphableShape) {
        pointIndex=0;
        this.morphableShape = morphableShape;
        for(Point p:morphableShape.getParameters().getPoints()){
            if(p.equals(point)){
                penStep=new PenStep(p.getX(),p.getY());
                break;
            }
            pointIndex++;
        }
    }

    public void setPenStep(PenStep penStep) {
        this.penStep = penStep;
    }
}
