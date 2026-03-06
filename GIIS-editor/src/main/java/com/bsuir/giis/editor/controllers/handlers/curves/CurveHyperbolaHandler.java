package com.bsuir.giis.editor.controllers.handlers.curves;

import com.bsuir.giis.editor.controllers.handlers.DrawableHandler;
import com.bsuir.giis.editor.model.AlgorithmParameters;
import com.bsuir.giis.editor.model.shapes.Drawable;
import com.bsuir.giis.editor.model.PointShapeParameters;
import com.bsuir.giis.editor.service.flow.Regular;
import com.bsuir.giis.editor.utils.*;
import com.bsuir.giis.editor.view.Canvas;

import java.awt.event.MouseEvent;

public class CurveHyperbolaHandler implements DrawableHandler {
    private Step Step;

    public CurveHyperbolaHandler(Step Step) {

        this.Step = Step;
    }

    @Override
    public void handlePress(Canvas canvas, MouseEvent mouseEvent, ToolContainer tool, ModeContainer mode, ModifierState modifierState) {
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
    public void handleMove(Canvas canvas, MouseEvent mouseEvent, ToolContainer tool, ModeContainer mode, ModifierState modifierState) {
        int x = mouseEvent.getX();
        int y = mouseEvent.getY();
        MultiStep multiStep = (MultiStep) Step;

        if (multiStep.getStep(0).isReady() && !multiStep.getStep(1).isReady()) {
            canvas.getLayer2DMoveable().cleanLayer();

            MultiStep copy = multiStep.copy();
            PenStep center = (PenStep) copy.getStep(0);

            copy.setStep(new PenStep(x, y));

            // To show a preview before the 3rd click, we "fake" the third point
            // by mirroring the vertical distance or using a default ratio.
            int fakeB_Y = center.getY() + Math.abs(y - center.getY());
            copy.setStep(new PenStep(x, fakeB_Y));

            AlgorithmParameters parameters = new PointShapeParameters(copy);
            Thread.ofVirtual().start(() ->
                    ((Drawable) tool.getTool())
                            .draw(canvas.getLayer2DMoveable(), parameters, new Regular())
            );
        }
        else if (multiStep.getStep(0).isReady() && multiStep.getStep(1).isReady()) {
            canvas.getLayer2DMoveable().cleanLayer();

            MultiStep copy = multiStep.copy();
            copy.setStep(new PenStep(x, y));

            AlgorithmParameters parameters = new PointShapeParameters(copy);
            Thread.ofVirtual().start(() ->
                    ((Drawable) tool.getTool())
                            .draw(canvas.getLayer2DMoveable(), parameters, new Regular())
            );
        }
    }

    @Override
    public void handleDrag(Canvas canvas, MouseEvent mouseEvent, ToolContainer tool, ModeContainer mode, ModifierState modifierState) {

    }

    @Override
    public void handleRelease(Canvas canvas,MouseEvent mouseEvent, ToolContainer tool, ModeContainer mode, ModifierState modifierState) {

    }
}
