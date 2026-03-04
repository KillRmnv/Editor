package com.bsuir.giis.editor.utils;

import com.bsuir.giis.editor.model.Point;
import com.bsuir.giis.editor.model.shapes.MorphableShape;

public class MorphStep implements Step {
    private PenStep penStep;
    private int pointIndex;
    private Point currentPoint;
    private MorphableShape<?> morphableShape;

    public MorphStep(PenStep penStep, int pointIndex) {
        this.penStep = penStep;
        this.pointIndex = pointIndex;

    }

    public MorphStep() {
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
        return penStep.isReady();
    }

    @Override
    public void clean() {
        morphableShape = null;
        currentPoint = null;
        penStep.clean();
    }

    public void setup(Point point, MorphableShape<?> morphableShape) {
        pointIndex = 0;
        this.morphableShape = morphableShape;
        currentPoint = point;
        penStep = new PenStep(point.getX(), point.getY());
        pointIndex++;

    }

    public void updatePoint(int x, int y) {
        currentPoint.setX(x);
        currentPoint.setY(y);
    }
    public void setPoint(int x, int y) {
        currentPoint.setX(x);
        currentPoint.setY(y);
    }

    public void setPenStep(PenStep penStep) {
        this.penStep = penStep;
    }
}
