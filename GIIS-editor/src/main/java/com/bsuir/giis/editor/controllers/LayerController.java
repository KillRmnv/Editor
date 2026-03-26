package com.bsuir.giis.editor.controllers;

import com.bsuir.giis.editor.rendering.Canvas;
import com.bsuir.giis.editor.view.LayerPanel;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.*;
//TODO:fix 3D model update after loading and transforming
public class LayerController {

    private final LayerPanel layerPanel;
    private final Canvas canvas;

    public LayerController(LayerPanel layerPanel, Canvas canvas) {
        this.layerPanel = layerPanel;
        this.canvas = canvas;

        setupLayerButtons();
        setupAddButton();
    }

    private void refreshAllLayers() {
        int activeIndex = canvas.getActiveLayerIndex();
        for (int i = 0; i < canvas.getUserLayerCount(); i++) {
            if (i == 0) {
                canvas.getUserLayer(i).setOpaque(true);
            } else {
                canvas.getUserLayer(i).setOpaque(false);
            }
            canvas.getUserLayer(i).setVisible(i <= activeIndex);
        }
    }

    private void setupLayerButtons() {
        for (JToggleButton button : layerPanel.getLayerButtons()) {
            attachLayerButtonListener(button);
        }
    }

    private void attachLayerButtonListener(JToggleButton button) {
        button.addActionListener(e -> {
            int index = Integer.parseInt(button.getText()) - 1;
            canvas.setActiveLayerIndex(index);
            layerPanel.setActiveIndex(index);
            refreshAllLayers();
        });

        button.addMouseListener(
            new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e) {
                    if (
                        SwingUtilities.isRightMouseButton(e) &&
                        canvas.getUserLayerCount() > 1
                    ) {
                        JPopupMenu popup = new JPopupMenu();
                        JMenuItem deleteItem = new JMenuItem("Delete");
                        deleteItem.addActionListener(ev -> {
                            int index = Integer.parseInt(button.getText()) - 1;
                            canvas.removeUserLayer(index);
                            layerPanel.removeLayerButton(index);
                            int newActive = canvas.getActiveLayerIndex();
                            layerPanel.setActiveIndex(newActive);
                        });
                        popup.add(deleteItem);
                        popup.show(button, e.getX(), e.getY());
                    }
                }
            }
        );
    }

    private void setupAddButton() {
        layerPanel
            .getAddLayerButton()
            .addActionListener(e -> {
                canvas.addUserLayer();
                int layerNumber = canvas.getUserLayerCount();
                layerPanel.addLayerButton(layerNumber);
                canvas.setActiveLayerIndex(layerNumber - 1);
                layerPanel.setActiveIndex(layerNumber - 1);

                JToggleButton newButton = layerPanel
                    .getLayerButtons()
                    .get(layerNumber - 1);
                attachLayerButtonListener(newButton);
            });
    }

    public void syncPanel() {
        int canvasCount = canvas.getUserLayerCount();

        while (layerPanel.getLayerButtons().size() < canvasCount) {
            int layerNumber = layerPanel.getLayerButtons().size() + 1;
            layerPanel.addLayerButton(layerNumber);

            JToggleButton newButton = layerPanel
                .getLayerButtons()
                .get(layerNumber - 1);
            attachLayerButtonListener(newButton);
        }

        int active = canvas.getActiveLayerIndex();
        layerPanel.setActiveIndex(active);
    }
}
