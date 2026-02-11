package com.bsuir.giis.editor.service.parameterCurves;

import com.bsuir.giis.editor.model.AlgorithmParameters;
import com.bsuir.giis.editor.model.Drawable;
import com.bsuir.giis.editor.service.flow.Mode;
import com.bsuir.giis.editor.view.BaseLayer;
// first 2 points(a ,b) create line.2 others(c,d) create vectors(a,c) and (b,d).As fake steps just make (c,d) equal (a,b)
public class HermiteAlgorithm implements ParameterCurveAlgorithm {
    @Override
    public void draw(BaseLayer canvas, AlgorithmParameters parameters, Mode mode) {

    }

    @Override
    public void morph(BaseLayer canvas, AlgorithmParameters parameters, Mode mode) {

    }
}
