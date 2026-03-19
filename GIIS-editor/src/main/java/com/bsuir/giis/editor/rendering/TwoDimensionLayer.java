package com.bsuir.giis.editor.rendering;

import javax.swing.*;



public class TwoDimensionLayer extends BaseLayer {
    protected JFormattedTextField pixelSizeField;

    public TwoDimensionLayer(int width, int height, JFormattedTextField pixelField) {
        super(width, height);
        this.pixelSizeField = pixelField;
    }
    public void setPixelSizeFromField() {
        int buff= (int) pixelSizeField.getValue();
        if(buff<0||buff>10){
            pixelSize=8;
        }else {
            pixelSize=buff;
        }

        setupCanvas(width, height);

    }
    public void setDefaultPixelSize() {
        pixelSize=1;
        setupCanvas(width, height);
    }
}
