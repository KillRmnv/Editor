package com.bsuir.giis.editor.controllers.handlers;

import com.bsuir.giis.editor.model.AlgorithmParameters;
import com.bsuir.giis.editor.model.Point;
import com.bsuir.giis.editor.model.PointShapeParameters;
import com.bsuir.giis.editor.service.flow.Regular;
import com.bsuir.giis.editor.service.lines.StraightLineAlgorithm;
import com.bsuir.giis.editor.utils.*;
import com.bsuir.giis.editor.view.Canvas;

import java.awt.event.MouseEvent;

public class StraightLineHandler implements DrawableHandler {
    private Step Step;
    public StraightLineHandler(Step Step) {
        this.Step = Step;
    }
    @Override
    public void handlePress(Canvas canvas, MouseEvent mouseEvent, ToolContainer tool, ModeContainer mode, ModifierState modifierState) {
        if(tool.getTool() instanceof StraightLineAlgorithm lineAlgorithm){
            MultiStep step = (MultiStep) Step;
            step.setStep(new PenStep(mouseEvent.getX(), mouseEvent.getY()));

            if(step.isReady()){
                Point first= ((PenStep)step.getStep(0)).getPoint();
                Point second= ((PenStep)step.getStep(1)).getPoint();
                AlgorithmParameters parameters = new PointShapeParameters(first, second);
                canvas.getLayer2DMoveable().cleanLayer();
                Thread.ofVirtual().start(()-> ((StraightLineAlgorithm) lineAlgorithm).draw(canvas.getLayer2D(), parameters,mode.getMode()));
                addToLayer(canvas.getLayer2D(),tool,parameters,mouseEvent);
                step.clean();
            }
        }
    }

    @Override
    public void handleMove(Canvas canvas, MouseEvent mouseEvent, ToolContainer tool, ModeContainer mode, ModifierState modifierState) {
        if(tool.getTool() instanceof StraightLineAlgorithm lineAlgorithm){
            MultiStep step = (MultiStep) Step;
            if(step.getStep(0).isReady()){
                canvas.getLayer2DMoveable().cleanLayer();
                Point first= ((PenStep)step.getStep(0)).getPoint();
                AlgorithmParameters parameters = new PointShapeParameters(first, new Point(mouseEvent.getX(), mouseEvent.getY()));
                Thread.ofVirtual().start(()-> ((StraightLineAlgorithm) lineAlgorithm).draw(canvas.getLayer2DMoveable(), parameters,new Regular()));

            }
        }
    }

    @Override
    public void handleDrag(Canvas canvas, MouseEvent mouseEvent, ToolContainer tool, ModeContainer mode, ModifierState modifierState) {
        handleMove(canvas, mouseEvent, tool, mode, modifierState);
    }

    @Override
    public void handleRelease(Canvas canvas, ToolContainer tool, ModeContainer mode, ModifierState modifierState) {

    }
}
