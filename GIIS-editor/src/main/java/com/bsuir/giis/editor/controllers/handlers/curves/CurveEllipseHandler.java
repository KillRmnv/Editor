package com.bsuir.giis.editor.controllers.handlers.curves;

import com.bsuir.giis.editor.controllers.handlers.DrawableHandler;
import com.bsuir.giis.editor.model.AlgorithmParameters;
import com.bsuir.giis.editor.model.Drawable;
import com.bsuir.giis.editor.model.curves.CurvesParameters;
import com.bsuir.giis.editor.service.flow.Regular;
import com.bsuir.giis.editor.utils.*;
import com.bsuir.giis.editor.view.Canvas;

import java.awt.event.MouseEvent;

public class CurveEllipseHandler implements DrawableHandler {
    private Step Step;

    public CurveEllipseHandler(Step Step) {

        this.Step = Step;
    }

    @Override
    public void handlePress(Canvas canvas, MouseEvent mouseEvent, ToolContainer tool, ModeContainer mode) {
        int x = mouseEvent.getX();
        int y = mouseEvent.getY();
        MultiStep multiStep = (MultiStep) Step;
        multiStep.setStep(new PenStep(x, y));



        if (multiStep.isReady()) {
            AlgorithmParameters parameters = new PointShapeParameters(Step);

            canvas.getLayer2DMoveable().cleanLayer();

            Thread.ofVirtual().start(() -> ((Drawable) tool.getTool()).draw(canvas.getLayer2D(), parameters, mode.getMode()));
            addToLayer(canvas.getLayer2D(),tool,parameters,mouseEvent);
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

            Step center = copy.getStep(0);
            int cx = ((PenStep) center).getX();
            int cy = ((PenStep) center).getY();

            int dx = x - cx;
            int dy = y - cy;
            int r = Math.max(Math.abs(dx), Math.abs(dy));

            int fx = cx + (dx < 0 ? -r : r);
            int fy = cy + (dy < 0 ? -r : r);

            copy.setStep(new PenStep(fx, fy));
            copy.setStep(new PenStep(fx, fy)); // теперь это осмысленно

            AlgorithmParameters parameters = new PointShapeParameters(copy);
            Thread.ofVirtual().start(() ->
                    ((Drawable) tool.getTool())
                            .draw(canvas.getLayer2DMoveable(), parameters, new Regular())
            );
        }else if(multiStep.getStep(0).isReady() && multiStep.getStep(1).isReady()){
            canvas.getLayer2DMoveable().cleanLayer();
            MultiStep copy = multiStep.copy();
            copy.setStep(new PenStep(x, y));
            AlgorithmParameters parameters = new PointShapeParameters(copy);
            Thread.ofVirtual().start(() -> ((Drawable) tool.getTool()).draw(canvas.getLayer2DMoveable(), parameters, new Regular()));

        }
    }

    @Override
    public void handleDrag(Canvas canvas, MouseEvent mouseEvent, ToolContainer tool, ModeContainer mode) {

    }
}
