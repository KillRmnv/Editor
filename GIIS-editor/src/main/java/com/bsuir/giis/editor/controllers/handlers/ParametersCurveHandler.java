package com.bsuir.giis.editor.controllers.handlers;

import com.bsuir.giis.editor.model.AlgorithmParameters;
import com.bsuir.giis.editor.model.Point;
import com.bsuir.giis.editor.model.PointShapeParameters;
import com.bsuir.giis.editor.model.shapes.Drawable;
import com.bsuir.giis.editor.service.flow.Regular;
import com.bsuir.giis.editor.utils.*;
import com.bsuir.giis.editor.view.Canvas;

import java.awt.event.MouseEvent;

public class ParametersCurveHandler implements DrawableHandler {

    private MultiStep multiStep;

    public ParametersCurveHandler(Step step) {
        multiStep = (MultiStep) step;
    }

    @Override
    public void handlePress(Canvas canvas, MouseEvent mouseEvent, ToolContainer tool, ModeContainer mode) {
        int x = mouseEvent.getX();
        int y = mouseEvent.getY();
        multiStep.setStep(new PenStep(x, y));


        if (multiStep.isReady()) {
            AlgorithmParameters parameters = new PointShapeParameters(multiStep);

            canvas.getLayer2DMoveable().cleanLayer();

            Thread.ofVirtual().start(() -> ((Drawable) tool.getTool()).draw(canvas.getLayer2D(), parameters, mode.getMode()));
            addToLayer(canvas.getLayer2D(), tool, parameters, mouseEvent);
            multiStep.clean();
        }
    }

    @Override
    public void handleMove(Canvas canvas, MouseEvent mouseEvent, ToolContainer tool, ModeContainer mode) {
        MultiStep fakeMultistep = null;
        if (multiStep.getStep(2).isReady()) {

            fakeMultistep = new MultiStep(multiStep.getSteps(), 3);
            fakeMultistep.setStep(3, new PenStep(mouseEvent.getX(), mouseEvent.getY()));

        } else if (multiStep.getStep(1).isReady()) {
            PenStep penStepStart = (PenStep) multiStep.getStep(0);
            PenStep penStepEnd = (PenStep) multiStep.getStep(1);
            fakeMultistep = new MultiStep(multiStep.getSteps(), 2);
            fakeMultistep.setStep(3, new PenStep(
                    getPointAtThreeQuarters(penStepStart.getPoint(),penStepEnd.getPoint())
            ));
            fakeMultistep.setStep(2, new PenStep(mouseEvent.getX(), mouseEvent.getY()));

        } else if (multiStep.getStep(0).isReady()) {

            fakeMultistep = new MultiStep(multiStep.getSteps(), 1);
            int x=mouseEvent.getX();
            int y=mouseEvent.getY();
            fakeMultistep.setStep(1, new PenStep(x, y));


            PenStep penStep = (PenStep) fakeMultistep.getStep(0);
            fakeMultistep.setStep(2, new PenStep(getPointAtQuarter(
                    penStep.getPoint(),new Point(x,y)
            )));
            fakeMultistep.setStep(3, new PenStep(getPointAtThreeQuarters(
                    penStep.getPoint(),new Point(x,y)
            )));

        }
        if (fakeMultistep != null) {

            canvas.getLayer2DMoveable().cleanLayer();
            AlgorithmParameters parameters = new PointShapeParameters(fakeMultistep);
            Thread.ofVirtual().start(() ->
                    ((Drawable) tool.getTool())
                            .draw(canvas.getLayer2DMoveable(), parameters, new Regular())
            );
        }
        
    }

    private  Point getPointAtQuarter(Point start, Point end) {
        int x = start.getX() + (end.getX() - start.getX()) / 4;
        int y = start.getY() + (end.getY() - start.getY()) / 4;
        return new Point(x, y);
    }

    private  Point getPointAtThreeQuarters(Point start, Point end) {
        int x = start.getX() + 3 * (end.getX() - start.getX()) / 4;
        int y = start.getY() + 3 * (end.getY() - start.getY()) / 4;
        return new Point(x, y);
    }
    @Override
    public void handleDrag(Canvas canvas, MouseEvent mouseEvent, ToolContainer tool, ModeContainer mode) {
        //pass
    }

    @Override
    public void handleRelease(Canvas canvas, ToolContainer tool, ModeContainer mode) {

    }
}
