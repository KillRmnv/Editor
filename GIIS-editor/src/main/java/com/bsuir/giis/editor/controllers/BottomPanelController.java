package com.bsuir.giis.editor.controllers;

import com.bsuir.giis.editor.controllers.handlers.MorphHandler;
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
        
        setupTransformPopup(bottomToolbar, canvas);

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
                    case "Regular Mode" -> menuItem.addActionListener(e -> {
                        mode.setMode(new Regular());
                        bottomToolbar.getRegularModeButton().setSelected(true);
                        debugFrame.setVisible(false);
                        if (!tool.getTool().getClass().equals(MorphHandler.class)) {
                            canvas.getLayer2D().setDefaultPixelSize();
                            canvas.getLayer2DMoveable().setDefaultPixelSize();
                        }
                        canvas.getLayer2DMorphable().setDefaultPixelSize();
                    });
                    
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

    private void setupTransformPopup(BottomToolbar bottomToolbar, Canvas canvas) {
        JPopupMenu transformPopup = bottomToolbar.getTransformPopup();
        
        for (Component panel : transformPopup.getComponents()) {
            if (panel instanceof JPanel jPanel) {
                Component[] components = jPanel.getComponents();
                
                for (Component comp : components) {
                    if (comp instanceof JButton button) {
                        String text = button.getText();
                        
                        switch (text) {
                            case "Apply" -> {
                                JPanel parentPanel = (JPanel) button.getParent().getParent();
                                JLabel label = (JLabel) parentPanel.getComponent(0);
                                String transformType = label.getText().replace(":", "");
                                
                                button.addActionListener(e -> {
                                    JPanel fieldsPanel = (JPanel) parentPanel.getComponent(1);
                                    applyTransformation(fieldsPanel, transformType, canvas);
                                });
                            }
                            
                            case "Around Point" -> button.addActionListener(e -> {
                                // TODO: Implement rotation around point
                                System.out.println("Rotation around point clicked");
                            });
                        }
                    }
                }
            }
        }
    }

    private void applyTransformation(JPanel fieldsPanel, String transformType, Canvas canvas) {
        Component[] components = fieldsPanel.getComponents();
        
        switch (transformType) {
            case "Translation" -> {
                double dx = 0, dy = 0;
                for (Component c : components) {
                    if (c instanceof JTextField tf) {
                        try {
                            if (dx == 0) dx = Double.parseDouble(tf.getText());
                            else dy = Double.parseDouble(tf.getText());
                        } catch (NumberFormatException ignored) {}
                    }
                }
                System.out.println("Translation: dx=" + dx + ", dy=" + dy);
            }
            
            case "Scaling" -> {
                double sx = 1, sy = 1;
                for (Component c : components) {
                    if (c instanceof JTextField tf) {
                        try {
                            if (sx == 1) sx = Double.parseDouble(tf.getText());
                            else sy = Double.parseDouble(tf.getText());
                        } catch (NumberFormatException ignored) {}
                    }
                }
                System.out.println("Scaling: sx=" + sx + ", sy=" + sy);
            }
            
            case "Rotation" -> {
                double angle = 0;
                for (Component c : components) {
                    if (c instanceof JTextField tf) {
                        try {
                            angle = Double.parseDouble(tf.getText());
                        } catch (NumberFormatException ignored) {}
                    }
                }
                System.out.println("Rotation: angle=" + angle);
            }
        }
    }
}
