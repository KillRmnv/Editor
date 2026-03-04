package com.bsuir.giis.editor.model.shapes;

import com.bsuir.giis.editor.model.AlgorithmParameters;

import java.awt.*;

public class ColorableShape<ColorDraw extends Drawable&Colorable> extends Shape<ColorDraw> {
    public ColorableShape(ColorDraw drawable, AlgorithmParameters parameters, Color color) {
        super(drawable, parameters, color);
    }
}
