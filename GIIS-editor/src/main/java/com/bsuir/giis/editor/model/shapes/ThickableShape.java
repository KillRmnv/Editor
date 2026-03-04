package com.bsuir.giis.editor.model.shapes;

import com.bsuir.giis.editor.model.AlgorithmParameters;

import java.awt.*;

public class ThickableShape<ThickDraw extends Drawable&Thickable> extends Shape<ThickDraw> {
    public ThickableShape(ThickDraw drawable, AlgorithmParameters parameters, Color color) {
        super(drawable, parameters, color);
    }
}
