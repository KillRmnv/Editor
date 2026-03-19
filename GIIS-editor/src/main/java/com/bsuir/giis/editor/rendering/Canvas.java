package com.bsuir.giis.editor.rendering;

import com.bsuir.giis.editor.model.CanvasState;

import javax.swing.*;


public class Canvas extends JPanel {

    private JLabel coordinates;
    private TwoDimensionLayer layer2D;
    private BaseLayer layer3D;
    private TwoDimensionLayer layer2DMoveable;
    private BaseLayer layer3DMoveable;
    private TwoDimensionLayer layer2DMorphable;


    public Canvas(int width, int height, JLabel coordinates, JFormattedTextField pixelField) {
        this.coordinates = coordinates;

        setLayout(new OverlayLayout(this));


        layer2D = new TwoDimensionLayer(width, height, pixelField);

        layer2DMoveable = new TwoDimensionLayer(width, height, pixelField);
        layer2DMoveable.setOpaque(false);

        layer2DMorphable = new TwoDimensionLayer(width, height, pixelField);
        layer2DMorphable.setOpaque(false);
        //don't change order of layers
        add(layer2DMorphable);
        add(layer2DMoveable);
        add(layer2D);          
    }

    public TwoDimensionLayer getLayer2D() {
        return layer2D;
    }

    public BaseLayer getLayer3D() {
        return layer3D;
    }

    public TwoDimensionLayer getLayer2DMoveable() {
        return layer2DMoveable;
    }

    public BaseLayer getLayer3DMoveable() {
        return layer3DMoveable;
    }

    public TwoDimensionLayer getLayer2DMorphable() {
        return layer2DMorphable;
    }


    public JLabel getCoordinates() {
        return coordinates;
    }

    public CanvasState getState() {
        return layer2D.getState();
    }

}