package com.bsuir.giis.editor.service.parameterCurves;

import com.bsuir.giis.editor.model.AlgorithmParameters;
import com.bsuir.giis.editor.model.shapes.Drawable;
import com.bsuir.giis.editor.model.shapes.Morphable;
import com.bsuir.giis.editor.service.flow.Mode;
import com.bsuir.giis.editor.view.BaseLayer;

public interface ParameterCurveAlgorithm extends Drawable, Morphable {
    @Override
    void draw(BaseLayer canvas, AlgorithmParameters parameters, Mode mode);
}
