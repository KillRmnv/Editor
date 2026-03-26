package com.bsuir.giis.editor.rendering;

import com.bsuir.giis.editor.model.CanvasState;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;


public class Canvas extends JPanel {

    private JLabel coordinates;
    private final JFormattedTextField pixelField;
    private final List<TwoDimensionLayer> userLayers;
    private int activeLayerIndex;
    private TwoDimensionLayer layerMoveable;
    private TwoDimensionLayer layerMorphable;

    public Canvas(int width, int height, JLabel coordinates, JFormattedTextField pixelField) {
        this.coordinates = coordinates;
        this.pixelField = pixelField;
        this.userLayers = new ArrayList<>();
        this.activeLayerIndex = 0;

        setLayout(new OverlayLayout(this));

        TwoDimensionLayer firstLayer = new TwoDimensionLayer(width, height, pixelField);
        userLayers.add(firstLayer);

        layerMoveable = new TwoDimensionLayer(width, height, pixelField);
        layerMoveable.setOpaque(false);

        layerMorphable = new TwoDimensionLayer(width, height, pixelField);
        layerMorphable.setOpaque(false);
        //don't change order of layers
        add(layerMorphable);
        add(layerMoveable);
        add(firstLayer);
    }

    public TwoDimensionLayer getLayer() {
        return userLayers.get(activeLayerIndex);
    }

    public TwoDimensionLayer getUserLayer(int index) {
        return userLayers.get(index);
    }

    public List<TwoDimensionLayer> getUserLayers() {
        return userLayers;
    }

    public int getUserLayerCount() {
        return userLayers.size();
    }

    public int getActiveLayerIndex() {
        return activeLayerIndex;
    }

    public void setActiveLayerIndex(int index) {
        if (index >= 0 && index < userLayers.size()) {
            this.activeLayerIndex = index;
        }
    }

    public TwoDimensionLayer addUserLayer() {
        int width = getLayer().getLayerWidth();
        int height = getLayer().getLayerHeight();
        TwoDimensionLayer newLayer = new TwoDimensionLayer(width, height, pixelField);
        newLayer.setOpaque(false);
        newLayer.setDefaultPixelSize();
        userLayers.add(newLayer);

        int overlayIndex = getComponentZOrder(layerMoveable);
        add(newLayer, overlayIndex + 1);
        setComponentZOrder(newLayer, overlayIndex + 1);

        return newLayer;
    }

    public void removeUserLayer(int index) {
        if (userLayers.size() <= 1) return;
        TwoDimensionLayer removed = userLayers.remove(index);
        remove(removed);
        if (activeLayerIndex >= userLayers.size()) {
            activeLayerIndex = userLayers.size() - 1;
        }
    }

    public TwoDimensionLayer getLayerMoveable() {
        return layerMoveable;
    }

    public TwoDimensionLayer getLayerMorphable() {
        return layerMorphable;
    }

    public JLabel getCoordinates() {
        return coordinates;
    }

    public CanvasState getState() {
        return getLayer().getState();
    }

}
