package com.bsuir.giis.editor.controllers;

import com.bsuir.giis.editor.utils.ModeContainer;
import com.bsuir.giis.editor.utils.ModifierState;
import com.bsuir.giis.editor.utils.ToolContainer;
import com.bsuir.giis.editor.view.Canvas;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public final class CanvasController extends MouseAdapter {

    private Canvas canvas;
    private ToolContainer tool;
    private ModeContainer mode;
    private ModifierState modifierState = new ModifierState();

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
        
        canvas.setFocusable(true);
        canvas.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_SHIFT) {
                    modifierState.setShiftPressed(true);
                } else if (e.getKeyCode() == KeyEvent.VK_CONTROL) {
                    modifierState.setCtrlPressed(true);
                } else if (e.getKeyCode() == KeyEvent.VK_ALT) {
                    modifierState.setAltPressed(true);
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_SHIFT) {
                    modifierState.setShiftPressed(false);
                } else if (e.getKeyCode() == KeyEvent.VK_CONTROL) {
                    modifierState.setCtrlPressed(false);
                } else if (e.getKeyCode() == KeyEvent.VK_ALT) {
                    modifierState.setAltPressed(false);
                }
            }
        });
    }

    @Override
    public void mousePressed(MouseEvent e) {
        canvas.requestFocusInWindow();
        tool.getHandler().handlePress(canvas, e, tool, mode, modifierState);
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        canvas.getCoordinates().setText("x:+" + e.getX() + ", y:+" + e.getY());

        tool.getHandler().handleMove(canvas, e, tool, mode, modifierState);
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        tool.getHandler().handleDrag(canvas, e, tool, mode, modifierState);
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        tool.getHandler().handleRelease(canvas, tool, mode, modifierState);
    }
}
