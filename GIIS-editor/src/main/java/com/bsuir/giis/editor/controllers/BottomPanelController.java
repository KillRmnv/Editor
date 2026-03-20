package com.bsuir.giis.editor.controllers;

import com.bsuir.giis.editor.controllers.handlers.MorphHandler;
import com.bsuir.giis.editor.controllers.handlers.Transform3DHandler;
import com.bsuir.giis.editor.model.CanvasState;
import com.bsuir.giis.editor.rendering.Canvas;
import com.bsuir.giis.editor.service.flow.Debug;
import com.bsuir.giis.editor.service.flow.Regular;
import com.bsuir.giis.editor.utils.ModeContainer;
import com.bsuir.giis.editor.utils.MorphStep;
import com.bsuir.giis.editor.utils.ToolContainer;
import com.bsuir.giis.editor.view.BottomToolbar;

import javax.swing.*;
import java.awt.Component;

public final class BottomPanelController {

    public BottomPanelController(
        BottomToolbar bottomToolbar,
        ModeContainer mode,
        JFrame debugFrame,
        Canvas canvas,
        ToolContainer tool
    ) {
        setupDebugPopup(bottomToolbar, mode, debugFrame, canvas, tool);
        
        setupTransformPopup(bottomToolbar, canvas, tool);

        bottomToolbar
            .getRegularModeButton()
            .addActionListener(e -> {
                mode.setMode(new Regular());
                bottomToolbar.getRegularModeButton().setSelected(true);
                debugFrame.setVisible(false);
                if(!tool.getTool().getClass().equals(MorphHandler.class)) {
                    canvas.getLayer2D().setDefaultPixelSize();
                    canvas.getLayer2DMoveable().setDefaultPixelSize();
                }
                canvas.getLayer2DMorphable().setDefaultPixelSize();
            });

        bottomToolbar
            .getMorphButton()
            .addActionListener(e -> {
                canvas.getLayer2DMorphable().setDefaultPixelSize();
                tool.setHandler(
                    new MorphHandler(
                        canvas.getLayer2DMorphable(),
                        new MorphStep()
                    )
                );
            });
    }
    private void setupDebugPopup(
        BottomToolbar bottomToolbar,
        ModeContainer mode,
        JFrame debugFrame,
        Canvas canvas,
        ToolContainer tool
    ) {
        JPopupMenu debugPopup = bottomToolbar.getDebugPopup();
        Component[] items = debugPopup.getComponents();
        
        for (Component item : items) {
            if (item instanceof JMenuItem menuItem) {
                String text = menuItem.getText();
                
                switch (text) {
                 
                    case "Debug Mode" -> menuItem.addActionListener(e -> {
                        if (!tool.getTool().getClass().equals(MorphHandler.class)) {
                            canvas.getLayer2D().setPixelSizeFromField();
                            canvas.getLayer2DMoveable().setPixelSizeFromField();
                        }
                        mode.setMode(new Debug());
                        bottomToolbar.getRegularModeButton().setSelected(false);
                        canvas.getLayer2DMorphable().setDefaultPixelSize();
                    });
                    
                    case "Next Step" -> menuItem.addActionListener(e -> {
                        Debug debug = (Debug) mode.getMode();
                        debug.nextStep();
                    });
                    
                    case "Skip" -> menuItem.addActionListener(e -> {
                        Debug debug = (Debug) mode.getMode();
                        debug.skip();
                    });
                    
                    case "Show Dlog" -> menuItem.addActionListener(e -> {
                        debugFrame.setVisible(!debugFrame.isVisible());
                    });
                }
            }
        }
    }

    private void setupTransformPopup(BottomToolbar bottomToolbar, Canvas canvas, ToolContainer tool) {
        bottomToolbar.getTransform3DButton().addActionListener(e -> {
            canvas.getLayer2D().cleanLayer();
            tool.setHandler(new Transform3DHandler());
        });

        bottomToolbar.getReflectHButton().addActionListener(e -> {
            CanvasState state = canvas.getLayer2D().getState();
            state.setReflectX(!state.isReflectX());
            canvas.getLayer2D().cleanLayer();
            canvas.getLayer2D().renderAndRepaint();
        });

        bottomToolbar.getReflectVButton().addActionListener(e -> {
            CanvasState state = canvas.getLayer2D().getState();
            state.setReflectY(!state.isReflectY());
            canvas.getLayer2D().cleanLayer();
            canvas.getLayer2D().renderAndRepaint();
        });
    }
}
