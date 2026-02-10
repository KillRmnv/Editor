package com.bsuir.giis.editor.model.curves;

import com.bsuir.giis.editor.model.AlgorithmParameters;
import com.bsuir.giis.editor.model.Point;
import com.bsuir.giis.editor.utils.MultiStep;
import com.bsuir.giis.editor.utils.PenStep;
import com.bsuir.giis.editor.utils.Step;
//TODO:get rid of it
public class CurvesParameters implements AlgorithmParameters {
    private Point point1;
    private Point point2;
    private Point point3;

    public CurvesParameters(Step step) {
        MultiStep step1 = (MultiStep) step;
        this.point1 = ((PenStep) step1.getStep(0)).getPoint();
        this.point2 = ((PenStep) step1.getStep(1)).getPoint();
        if (step1.getSize() > 2)
            this.point3 = ((PenStep) step1.getStep(2)).getPoint();
    }

    public Point getPoint1() {
        return point1;
    }

    public Point getPoint2() {
        return point2;
    }

    public Point getPoint3() {
        return point3;
    }
}
