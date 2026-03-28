package com.bsuir.giis.editor.controllers.handlers;

import com.bsuir.giis.editor.model.AlgorithmParameters;
import com.bsuir.giis.editor.model.shapes.Drawable;
import com.bsuir.giis.editor.rendering.Canvas;
import com.bsuir.giis.editor.model.PointShapeParameters;
import com.bsuir.giis.editor.service.flow.Regular;
import com.bsuir.giis.editor.utils.*;

import java.awt.event.MouseEvent;

public class TwoPointHandler implements DrawableHandler {
    private final Step step;

    public TwoPointHandler(Step step) {
        this.step = step;
    }

    @Override
    public void handlePress(Canvas canvas, MouseEvent mouseEvent, ToolContainer tool, ModeContainer mode, ModifierState modifierState) {
        int x = mouseEvent.getX();
        int y = mouseEvent.getY();
        MultiStep multiStep = (MultiStep) step;

        multiStep.setStep(new PenStep(x, y));

        if (multiStep.isReady()) {
            AlgorithmParameters parameters = new PointShapeParameters(step);

            canvas.getLayerMoveable().cleanLayer();

            new Thread(() -> {
                ((Drawable) tool.getTool()).draw(canvas.getLayer(), parameters, mode.getMode());
            }).start();
            addToLayer(canvas.getLayer(), tool, parameters, mouseEvent);
            multiStep.clean();
        }
    }

    @Override
    public void handleMove(Canvas canvas, MouseEvent mouseEvent, ToolContainer tool, ModeContainer mode, ModifierState modifierState) {
        int x = mouseEvent.getX();
        int y = mouseEvent.getY();
        MultiStep multiStep = (MultiStep) step;

        if (multiStep.getStep(0).isReady() && !multiStep.getStep(1).isReady()) {
            canvas.getLayerMoveable().cleanLayer();
            MultiStep copy = multiStep.copy();
            copy.setStep(new PenStep(x, y));
            AlgorithmParameters parameters = new PointShapeParameters(copy);
            new Thread(() -> {
                ((Drawable) tool.getTool()).draw(canvas.getLayerMoveable(), parameters, new Regular());
            }).start();
        }
    }

    @Override
    public void handleDrag(Canvas canvas, MouseEvent mouseEvent, ToolContainer tool, ModeContainer mode, ModifierState modifierState) {
        handleMove(canvas, mouseEvent, tool, mode, modifierState);
    }

    @Override
    public void handleRelease(Canvas canvas, MouseEvent mouseEvent, ToolContainer tool, ModeContainer mode, ModifierState modifierState) {
    }
}
