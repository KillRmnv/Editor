package com.bsuir.giis.editor.controllers;

import com.bsuir.giis.editor.utils.ModeContainer;
import com.bsuir.giis.editor.utils.ToolContainer;
import com.bsuir.giis.editor.view.Canvas;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public final class CanvasController extends MouseAdapter {

    private Canvas canvas;
    private ToolContainer tool;
    private ModeContainer mode;

    public CanvasController(
        Canvas canvas,
        ToolContainer tool,
        ModeContainer mode
    ) {
        this.canvas = canvas;
        this.tool = tool;
        this.mode = mode;
        canvas.addMouseListener(this);
        canvas.addMouseMotionListener(this);
    }

    @Override
    public void mousePressed(MouseEvent e) {
        tool.getHandler().handlePress(canvas, e, tool, mode);
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        canvas.getCoordinates().setText("x:+" + e.getX() + ", y:+" + e.getY());

        tool.getHandler().handleMove(canvas, e, tool, mode);
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        tool.getHandler().handleDrag(canvas, e, tool, mode);
    }
}
