package com.bsuir.giis.editor.controllers.handlers.curves;

import com.bsuir.giis.editor.controllers.handlers.DrawableHandler;
import com.bsuir.giis.editor.model.AlgorithmParameters;
import com.bsuir.giis.editor.model.Drawable;
import com.bsuir.giis.editor.model.curves.CurvesParameters;
import com.bsuir.giis.editor.service.flow.Regular;
import com.bsuir.giis.editor.utils.*;
import com.bsuir.giis.editor.view.Canvas;

import java.awt.event.MouseEvent;

//TODO:add debug points
public class Curve2PointsHandler implements DrawableHandler {
    private Step Step;

    public Curve2PointsHandler(Step Step) {

        this.Step = Step;
    }

    @Override
    public void handlePress(Canvas canvas, MouseEvent mouseEvent, ToolContainer tool, ModeContainer mode) {
        int x = mouseEvent.getX();
        int y = mouseEvent.getY();
        MultiStep multiStep = (MultiStep) Step;

        multiStep.setStep(new PenStep(x, y));


        if (multiStep.isReady()) {
            AlgorithmParameters parameters = new CurvesParameters(Step);

            canvas.getLayer2DMoveable().cleanLayer();

            Thread.ofVirtual().start(() -> ((Drawable) tool.getTool()).draw(canvas.getLayer2D(), parameters, mode.getMode()));

            multiStep.clean();
        }
    }

    @Override
    public void handleMove(Canvas canvas, MouseEvent mouseEvent, ToolContainer tool, ModeContainer mode) {
        int x = mouseEvent.getX();
        int y = mouseEvent.getY();
        MultiStep multiStep = (MultiStep) Step;


        if (multiStep.getStep(0).isReady() && !multiStep.getStep(1).isReady()) {
            canvas.getLayer2DMoveable().cleanLayer();
            MultiStep copy = multiStep.copy();
            copy.setStep(new PenStep(x, y));
            AlgorithmParameters parameters = new CurvesParameters(copy);
            Thread.ofVirtual().start(() -> ((Drawable) tool.getTool()).draw(canvas.getLayer2DMoveable(), parameters, new Regular()));
        }
    }


    @Override
    public void handleDrag(Canvas canvas, MouseEvent mouseEvent, ToolContainer tool, ModeContainer mode) {
        handleMove(canvas, mouseEvent, tool, mode);
    }
}