package com.bsuir.giis.editor.model.lines;

import com.bsuir.giis.editor.model.AlgorithmParameters;
import com.bsuir.giis.editor.model.Point;
import com.bsuir.giis.editor.utils.MultiStep;
import com.bsuir.giis.editor.utils.PenStep;
import com.bsuir.giis.editor.utils.Step;

import java.lang.reflect.InvocationTargetException;

//TODO:get rid of it
public class LinesParameters implements AlgorithmParameters {
    private final MultiStep multiStep;
    public LinesParameters(final MultiStep multiStep) {
        this.multiStep = multiStep;
    }
    public LinesParameters(Point startPoint, Point endPoint) {
        try {
            this.multiStep = new MultiStep(2, PenStep.class);
        } catch (NoSuchMethodException | InvocationTargetException | InstantiationException |
                 IllegalAccessException e) {
            throw new RuntimeException(e);
        }
        multiStep.setStep(new PenStep(startPoint.getX(), startPoint.getY()));
        multiStep.setStep(new PenStep(endPoint.getX(), endPoint.getY()));

    }

    public Step getStartPoint() {
        return multiStep.getStep(0);
    }

    public Step getEndPoint() {
        return multiStep.getStep(1);
    }
}
