package com.bsuir.giis.editor.controllers;

import com.bsuir.giis.editor.controllers.handlers.MorphHandler;
import com.bsuir.giis.editor.controllers.handlers.Transform3DHandler;
import com.bsuir.giis.editor.model.CanvasState;
import com.bsuir.giis.editor.model.Model3DInstance;
import com.bsuir.giis.editor.rendering.Canvas;
import com.bsuir.giis.editor.service.flow.Debug;
import com.bsuir.giis.editor.service.flow.Regular;
import com.bsuir.giis.editor.utils.ModeContainer;
import com.bsuir.giis.editor.utils.MorphStep;
import com.bsuir.giis.editor.utils.ToolContainer;
import com.bsuir.giis.editor.view.BottomToolbar;

import javax.swing.*;
import java.awt.Component;
import java.util.List;

public final class BottomPanelController {

    private final BottomToolbar bottomToolbar;
    private final Canvas canvas;

    public BottomPanelController(
        BottomToolbar bottomToolbar,
        ModeContainer mode,
        JFrame debugFrame,
        Canvas canvas,
        ToolContainer tool
    ) {
        this.bottomToolbar = bottomToolbar;
        this.canvas = canvas;

        setupDebugPopup(bottomToolbar, mode, debugFrame, canvas, tool);
        
        setupTransformPopup(bottomToolbar, canvas, tool);

        bottomToolbar
            .getRegularModeButton()
            .addActionListener(e -> {
                mode.setMode(new Regular());
                bottomToolbar.getRegularModeButton().setSelected(true);
                debugFrame.setVisible(false);
                if(!tool.getTool().getClass().equals(MorphHandler.class)) {
                    for (var layer : canvas.getUserLayers()) {
                        
                        layer.setDefaultPixelSize();
                    }
                    canvas.getLayerMoveable().setDefaultPixelSize();
                }
                canvas.getLayerMorphable().setDefaultPixelSize();
            });

        bottomToolbar
            .getMorphButton()
            .addActionListener(e -> {
                canvas.getLayerMorphable().setDefaultPixelSize();
                tool.setHandler(
                    new MorphHandler(
                        canvas.getLayerMorphable(),
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
                            for (var layer : canvas.getUserLayers()) {
                                layer.setPixelSizeFromField();
                            }
                            canvas.getLayerMoveable().setPixelSizeFromField();
                        }
                        mode.setMode(new Debug());
                        bottomToolbar.getRegularModeButton().setSelected(false);
                        canvas.getLayerMorphable().setDefaultPixelSize();
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
            canvas.getLayer().clearImage();
            tool.setHandler(new Transform3DHandler());
        });

        bottomToolbar.getReflectHButton().addActionListener(e -> {
            CanvasState state = canvas.getLayer().getState();
            Model3DInstance activeModel = state.getActiveModel();
            if (activeModel != null) {
                activeModel.setReflectX(!activeModel.isReflectX());
            }
            canvas.getLayer().cleanLayer();
            canvas.getLayer().renderAndRepaint();
        });

        bottomToolbar.getReflectVButton().addActionListener(e -> {
            CanvasState state = canvas.getLayer().getState();
            Model3DInstance activeModel = state.getActiveModel();
            if (activeModel != null) {
                activeModel.setReflectY(!activeModel.isReflectY());
            }
            canvas.getLayer().cleanLayer();
            canvas.getLayer().renderAndRepaint();
        });

        bottomToolbar.getPerspectiveCheckBox().addActionListener(e -> {
            CanvasState state = canvas.getLayer().getState();
            state.setPerspectiveEnabled(bottomToolbar.getPerspectiveCheckBox().isSelected());
            canvas.getLayer().cleanLayer();
            canvas.getLayer().renderAndRepaint();
        });

        bottomToolbar.getModelSelector().addActionListener(e -> {
            int selected = bottomToolbar.getModelSelector().getSelectedIndex();
            if (selected >= 0) {
                canvas.getLayer().getState().setActiveModelIndex(selected);
            }
        });

        bottomToolbar.getTransformButton().addActionListener(e -> {
            refreshModelSelector();
        });
    }

    public void refreshModelSelector() {
        JComboBox<String> selector = bottomToolbar.getModelSelector();
        selector.removeAllItems();
        List<Model3DInstance> models = canvas.getLayer().getState().getModels();
        for (int i = 0; i < models.size(); i++) {
            Model3DInstance inst = models.get(i);
            String name = inst.getModel() != null ? inst.getModel().getName() : "Model " + (i + 1);
            selector.addItem("Model " + (i + 1) + ": " + name);
        }
        int active = canvas.getLayer().getState().getActiveModelIndex();
        if (active >= 0 && active < selector.getItemCount()) {
            selector.setSelectedIndex(active);
        }
    }
}
