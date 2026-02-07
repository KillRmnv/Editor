package com.bsuir.giis.editor.controllers.handlers;

import com.bsuir.giis.editor.Mode;
import com.bsuir.giis.editor.model.AlgorithmParameters;
import com.bsuir.giis.editor.model.Pen;
import com.bsuir.giis.editor.model.Point;
import com.bsuir.giis.editor.model.lines.LinesParameters;
import com.bsuir.giis.editor.service.lines.Antialiasing;
import com.bsuir.giis.editor.service.lines.StraightLineAlgorithm;
import com.bsuir.giis.editor.utils.PenStep;
import com.bsuir.giis.editor.utils.PreviousStep;
import com.bsuir.giis.editor.utils.ToolContainer;
import com.bsuir.giis.editor.view.Canvas;

import java.awt.event.MouseEvent;
//TODO:might add check for instance

public class PenHandler implements Handler {
    private PreviousStep previousStep;

    public PenHandler(PreviousStep previousStep) {
        this.previousStep = previousStep;
    }

    @Override
    public void handlePress(Canvas canvas, MouseEvent mouseEvent, ToolContainer tool, Mode mode) {
        if (tool.getTool() instanceof Pen) {
            canvas.paintPixel(mouseEvent.getX(), mouseEvent.getY());
            PenStep step = (PenStep) previousStep.getStep();
            step.setX(mouseEvent.getX());
            step.setY(mouseEvent.getY());
        }
    }

    @Override
    public void handleMove(Canvas canvas, MouseEvent mouseEvent, ToolContainer tool, Mode mode) {

    }

    @Override
    public void handleDrag(Canvas canvas, MouseEvent mouseEvent, ToolContainer tool, Mode mode) {
        if (tool.getTool() instanceof Pen) {
            PenStep step = (PenStep) previousStep.getStep();
            StraightLineAlgorithm lineDrawer = new Antialiasing();
            AlgorithmParameters parameters = new LinesParameters(new Point(step.getX(), step.getY()), new Point(mouseEvent.getX(), mouseEvent.getY()));
            lineDrawer.draw(canvas, parameters);

            step.setX(mouseEvent.getX());
            step.setY(mouseEvent.getY());
//        canvas.paintPixel(mouseEvent.getX(), mouseEvent.getY());
        }
    }
}
