package com.bsuir.giis.editor.service.curves;

import com.bsuir.giis.editor.model.shapes.Drawable;
import com.bsuir.giis.editor.model.shapes.Morphable;
import com.bsuir.giis.editor.model.AlgorithmParameters;
import com.bsuir.giis.editor.rendering.BaseLayer;
import com.bsuir.giis.editor.service.flow.Mode;

public interface CurvesAlgorithm extends Drawable, Morphable {
    @Override
    void draw(BaseLayer canvas, AlgorithmParameters parameters, Mode mode);
}