package com.bsuir.giis.editor.model;

import java.awt.*;

public class Shape<Draw extends Drawable> {
    protected AlgorithmParameters parameters;
    protected Color color;
    protected Draw drawable;
    public Shape(Draw drawable, AlgorithmParameters parameters, Color color) {
        this.drawable = drawable;
        this.parameters = parameters;
        this.color = color;
    }
    public Draw getDrawable() {
        return drawable;
    }
    public AlgorithmParameters getParameters() {
        return parameters;
    }
    public Color getColor() {
        return color;
    }


}
