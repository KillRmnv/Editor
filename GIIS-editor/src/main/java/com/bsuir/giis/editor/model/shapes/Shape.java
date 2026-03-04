package com.bsuir.giis.editor.model.shapes;

import com.bsuir.giis.editor.model.AlgorithmParameters;

import java.awt.*;

public class Shape<Draw extends Drawable> {
    protected AlgorithmParameters parameters;
    protected Draw drawable;
    public Shape(Draw drawable, AlgorithmParameters parameters, Color color) {
        this.drawable = drawable;
        this.parameters = parameters;
    }
    public Draw getDrawable() {
        return drawable;
    }
    public AlgorithmParameters getParameters() {
        return parameters;
    }


}
