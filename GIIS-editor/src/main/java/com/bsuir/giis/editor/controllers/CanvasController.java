package com.bsuir.giis.editor.controllers;

import com.bsuir.giis.editor.Mode;
import com.bsuir.giis.editor.controllers.handlers.Handler;
import com.bsuir.giis.editor.controllers.handlers.PenHandler;
import com.bsuir.giis.editor.controllers.handlers.StraightLineHandler;
import com.bsuir.giis.editor.model.Tool;
import com.bsuir.giis.editor.utils.PreviousStep;
import com.bsuir.giis.editor.utils.ToolContainer;
import com.bsuir.giis.editor.view.Canvas;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Set;

public class CanvasController extends MouseAdapter {
    private Canvas canvas;
    private ToolContainer tool;
    private Mode mode;
    private Set<Handler> handlerSet;

    public CanvasController(Canvas canvas, ToolContainer tool, Mode mode, PreviousStep previousStep) {
        this.canvas = canvas;
        this.tool = tool;
        this.mode = mode;
        canvas.addMouseListener(this);
        canvas.addMouseMotionListener(this);
        handlerSet = Set.of(new PenHandler(previousStep),
                new StraightLineHandler(previousStep));
    }

    @Override
    public void mousePressed(MouseEvent e) {
        for (Handler handler : handlerSet) {
            handler.handlePress(canvas, e, tool, mode);
        }
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        canvas.getCoordinates().setText("x:+" + e.getX() + ", y:+" + e.getY());
        for (Handler handler : handlerSet) {
            handler.handleMove(canvas, e, tool, mode);
        }
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        for (Handler handler : handlerSet) {
            handler.handleDrag(canvas, e, tool, mode);
        }
    }

}
