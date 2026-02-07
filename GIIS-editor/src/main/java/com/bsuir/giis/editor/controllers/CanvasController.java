package com.bsuir.giis.editor.controllers;

import com.bsuir.giis.editor.userTools.Mode;
import com.bsuir.giis.editor.userTools.Tool;
import com.bsuir.giis.editor.view.Canvas;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class CanvasController extends MouseAdapter {
   private Canvas canvas;
   private Tool tool;
   private Mode mode;
    public CanvasController(Canvas canvas, Tool tool, Mode mode) {
        this.canvas = canvas;
        this.tool = tool;
        this.mode = mode;
        canvas.addMouseListener(this);
        canvas.addMouseMotionListener(this);
    }
    @Override
    public void mousePressed(MouseEvent e) {
        canvas.paintPixel(e.getX(), e.getY());
    }
    @Override
    public void mouseMoved(MouseEvent e){
        canvas.getCoordinates().setText("x:+" + e.getX() + ", y:+" + e.getY());
    }
    @Override
    public void mouseDragged(MouseEvent e) {
        canvas.paintPixel(e.getX(), e.getY());
    }

}
