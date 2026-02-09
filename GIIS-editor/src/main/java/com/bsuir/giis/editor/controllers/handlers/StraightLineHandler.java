package com.bsuir.giis.editor.controllers.handlers;

import com.bsuir.giis.editor.model.AlgorithmParameters;
import com.bsuir.giis.editor.model.Point;
import com.bsuir.giis.editor.model.lines.Line;
import com.bsuir.giis.editor.model.lines.LinesParameters;
import com.bsuir.giis.editor.service.flow.Regular;
import com.bsuir.giis.editor.service.lines.BresenhamAlgorithm;
import com.bsuir.giis.editor.service.lines.StraightLineAlgorithm;
import com.bsuir.giis.editor.utils.LineStep;
import com.bsuir.giis.editor.utils.ModeContainer;
import com.bsuir.giis.editor.utils.PreviousStep;
import com.bsuir.giis.editor.utils.ToolContainer;
import com.bsuir.giis.editor.view.Canvas;

import java.awt.event.MouseEvent;

public class StraightLineHandler implements Handler{
    private PreviousStep previousStep;
    public StraightLineHandler(PreviousStep previousStep) {
        this.previousStep = previousStep;
    }
    @Override
    public void handlePress(Canvas canvas, MouseEvent mouseEvent, ToolContainer tool, ModeContainer mode) {
        if(tool.getTool() instanceof Line){
            Line line = (Line) tool.getTool();
            LineStep step = (LineStep) previousStep.getStep();
            step.setPoint(mouseEvent.getX(), mouseEvent.getY());

            if(step.isReady()){
                StraightLineAlgorithm lineDrawer = line.getStraightLineAlgorithm();
                AlgorithmParameters parameters = new LinesParameters(step.getStartPoint(), step.getEndPoint());
                canvas.getLayer2DMoveable().cleanLayer();
                Thread.ofVirtual().start(()->lineDrawer.draw(canvas.getLayer2D(), parameters,mode.getMode()));
                step.clean();
            }
        }
    }

    @Override
    public void handleMove(Canvas canvas, MouseEvent mouseEvent, ToolContainer tool, ModeContainer mode) {
        if(tool.getTool() instanceof Line){
            Line line = (Line) tool.getTool();
            LineStep step = (LineStep) previousStep.getStep();
            if(step.isStarted()){
                canvas.getLayer2DMoveable().cleanLayer();
                StraightLineAlgorithm lineDrawer = line.getStraightLineAlgorithm();
                AlgorithmParameters parameters = new LinesParameters(step.getStartPoint(), new Point(mouseEvent.getX(), mouseEvent.getY()));
                Thread.ofVirtual().start(()->lineDrawer.draw(canvas.getLayer2DMoveable(), parameters,new Regular()));

            }
        }
    }

    @Override
    public void handleDrag(Canvas canvas, MouseEvent mouseEvent, ToolContainer tool, ModeContainer mode) {
//        if(tool instanceof Line){
//            LineStep step = (LineStep) previousStep.getStep();
//
//        }
        handleMove(canvas, mouseEvent, tool, mode);
    }
}
