package com.bsuir.giis.editor.model;

import java.awt.*;

public class MorphableShape<Draw extends Drawable&Morphable> extends Shape<Draw> {

    public MorphableShape(final AlgorithmParameters parameters, final Draw drawable) {
        super(drawable,parameters,Color.BLACK);

    }
    public MorphableShape(final AlgorithmParameters parameters, final Color color, Draw drawable) {
        super(drawable, parameters, color);
    }

}
