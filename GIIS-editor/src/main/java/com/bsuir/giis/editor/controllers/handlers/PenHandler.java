package com.bsuir.giis.editor.controllers.handlers;

import com.bsuir.giis.editor.model.AlgorithmParameters;
import com.bsuir.giis.editor.model.Point;
import com.bsuir.giis.editor.service.lines.BresenhamAlgorithm;
import com.bsuir.giis.editor.service.lines.LinesParameters;
import com.bsuir.giis.editor.service.lines.StraightLineAlgorithm;
import com.bsuir.giis.editor.userTools.Mode;
import com.bsuir.giis.editor.userTools.Tool;
import com.bsuir.giis.editor.view.Canvas;

import java.awt.event.MouseEvent;

public class PenHandler implements Handler{
    private int prevX;
    private int prevY;
    public PenHandler() {

    }
    @Override
    public void handlePress(Canvas canvas, MouseEvent mouseEvent, Tool tool, Mode mode) {
        canvas.paintPixel(mouseEvent.getX(), mouseEvent.getY());
        prevX = mouseEvent.getX();
        prevY = mouseEvent.getY();
    }

    @Override
    public void handleMove(Canvas canvas, MouseEvent mouseEvent, Tool tool, Mode mode) {

    }

    @Override
    public void handleDrag(Canvas canvas, MouseEvent mouseEvent, Tool tool, Mode mode) {
        StraightLineAlgorithm lineDrawer=new BresenhamAlgorithm();
        AlgorithmParameters parameters=new LinesParameters(new Point(prevX,prevY),new Point(mouseEvent.getX(),mouseEvent.getY()));
        lineDrawer.draw(canvas,parameters);
        prevX=mouseEvent.getX();
        prevY=mouseEvent.getY();
//        canvas.paintPixel(mouseEvent.getX(), mouseEvent.getY());

    }
}
