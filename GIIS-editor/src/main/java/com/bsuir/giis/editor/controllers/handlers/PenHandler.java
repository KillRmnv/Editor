package com.bsuir.giis.editor.controllers.handlers;

import com.bsuir.giis.editor.model.AlgorithmParameters;
import com.bsuir.giis.editor.model.Pen;
import com.bsuir.giis.editor.model.Point;
import com.bsuir.giis.editor.model.PointShapeParameters;
import com.bsuir.giis.editor.rendering.Canvas;
import com.bsuir.giis.editor.service.flow.Regular;
import com.bsuir.giis.editor.service.lines.Antialiasing;
import com.bsuir.giis.editor.service.lines.StraightLineAlgorithm;
import com.bsuir.giis.editor.utils.ModeContainer;
import com.bsuir.giis.editor.utils.ModifierState;
import com.bsuir.giis.editor.utils.PenStep;
import com.bsuir.giis.editor.utils.Step;
import com.bsuir.giis.editor.utils.ToolContainer;

import java.awt.*;
import java.awt.event.MouseEvent;
//TODO:might add check for instance

public class PenHandler implements DrawableHandler {
    private Step Step;

    public PenHandler(Step Step) {
        this.Step = Step;
    }

    @Override
    public void handlePress(Canvas canvas, MouseEvent mouseEvent, ToolContainer tool, ModeContainer mode, ModifierState modifierState) {
        if (tool.getTool() instanceof Pen) {
            canvas.getLayer2D().paintPixel(mouseEvent.getX(), mouseEvent.getY(), Color.BLACK);
            PenStep step = (PenStep) Step;
            step.setX(mouseEvent.getX());
            step.setY(mouseEvent.getY());
        }
    }

    @Override
    public void handleMove(Canvas canvas, MouseEvent mouseEvent, ToolContainer tool, ModeContainer mode, ModifierState modifierState) {

    }

    @Override
    public void handleDrag(Canvas canvas, MouseEvent mouseEvent, ToolContainer tool, ModeContainer mode, ModifierState modifierState) {
        if (tool.getTool() instanceof Pen) {
            PenStep step = (PenStep) Step;
            StraightLineAlgorithm lineDrawer = new Antialiasing();
            AlgorithmParameters parameters = new PointShapeParameters(new Point(step.getX(), step.getY()), new Point(mouseEvent.getX(), mouseEvent.getY()));
            Thread.ofVirtual().start(()-> lineDrawer.draw(canvas.getLayer2D(), parameters,new Regular()));


            step.setX(mouseEvent.getX());
            step.setY(mouseEvent.getY());
//        canvas.paintPixel(mouseEvent.getX(), mouseEvent.getY());
        }
    }

    @Override
    public void handleRelease(Canvas canvas,MouseEvent mouseEvent, ToolContainer tool, ModeContainer mode, ModifierState modifierState) {

    }
}
