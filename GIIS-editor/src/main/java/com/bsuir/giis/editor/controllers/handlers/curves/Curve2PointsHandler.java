package com.bsuir.giis.editor.controllers.handlers.curves;

import com.bsuir.giis.editor.controllers.handlers.DrawableHandler;
import com.bsuir.giis.editor.model.AlgorithmParameters;
import com.bsuir.giis.editor.model.shapes.Drawable;
import com.bsuir.giis.editor.rendering.Canvas;
import com.bsuir.giis.editor.model.PointShapeParameters;
import com.bsuir.giis.editor.service.flow.Regular;
import com.bsuir.giis.editor.utils.*;

import java.awt.event.MouseEvent;

//TODO:add debug points
public class Curve2PointsHandler implements DrawableHandler {
    private Step Step;

    public Curve2PointsHandler(Step Step) {

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

            new Thread(() -> {
                ((Drawable) tool.getTool()).draw(canvas.getLayer2D(), parameters, mode.getMode());
                javax.swing.SwingUtilities.invokeLater(() -> canvas.getLayer2D().repaint());
            }).start();
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
            copy.setStep(new PenStep(x, y));
            AlgorithmParameters parameters = new PointShapeParameters(copy);
            new Thread(() -> {
                ((Drawable) tool.getTool()).draw(canvas.getLayer2DMoveable(), parameters, new Regular());
                javax.swing.SwingUtilities.invokeLater(() -> canvas.getLayer2DMoveable().repaint());
            }).start();
        }
    }


    @Override
    public void handleDrag(Canvas canvas, MouseEvent mouseEvent, ToolContainer tool, ModeContainer mode, ModifierState modifierState) {
        handleMove(canvas, mouseEvent, tool, mode, modifierState);
    }

    @Override
    public void handleRelease(Canvas canvas,MouseEvent mouseEvent, ToolContainer tool, ModeContainer mode, ModifierState modifierState) {

    }
}
