package com.bsuir.giis.editor.controllers;

import com.bsuir.giis.editor.controllers.handlers.MorphHandler;
import com.bsuir.giis.editor.controllers.handlers.Transform3DHandler;
import com.bsuir.giis.editor.rendering.Canvas;
import com.bsuir.giis.editor.service.flow.Debug;
import com.bsuir.giis.editor.service.flow.Regular;
import com.bsuir.giis.editor.utils.ModeContainer;
import com.bsuir.giis.editor.utils.MorphStep;
import com.bsuir.giis.editor.utils.ToolContainer;
import com.bsuir.giis.editor.view.BottomToolbar;

import javax.swing.*;
import java.awt.*;

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
    //TODO: add actions to menu items
    private void setupTransformPopup(BottomToolbar bottomToolbar, Canvas canvas, ToolContainer tool) {
        bottomToolbar.getTranslateApply().addActionListener(e -> {
            try {
                double dx = Double.parseDouble(bottomToolbar.getTranslateX().getText());
                double dy = Double.parseDouble(bottomToolbar.getTranslateY().getText());
                System.out.println("Translation: dx=" + dx + ", dy=" + dy);
            } catch (NumberFormatException ex) {
                System.out.println("Invalid translation values");
            }
        });

        bottomToolbar.getScaleApply().addActionListener(e -> {
            try {
                double sx = Double.parseDouble(bottomToolbar.getScaleX().getText());
                double sy = Double.parseDouble(bottomToolbar.getScaleY().getText());
                System.out.println("Scaling: sx=" + sx + ", sy=" + sy);
            } catch (NumberFormatException ex) {
                System.out.println("Invalid scale values");
            }
        });

        bottomToolbar.getRotateApply().addActionListener(e -> {
            try {
                double angle = Double.parseDouble(bottomToolbar.getAngleField().getText());
                System.out.println("Rotation: angle=" + angle);
            } catch (NumberFormatException ex) {
                System.out.println("Invalid angle value");
            }
        });

        bottomToolbar.getRotateAroundPoint().addActionListener(e -> {
            System.out.println("Rotation around point clicked");
        });

        bottomToolbar.getTransform3DButton().addActionListener(e -> {
            canvas.getLayer2D().cleanLayer();
            tool.setHandler(new Transform3DHandler());
        });
    }
}
