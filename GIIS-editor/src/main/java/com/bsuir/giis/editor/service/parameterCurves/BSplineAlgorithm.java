package com.bsuir.giis.editor.service.parameterCurves;

import com.bsuir.giis.editor.model.AlgorithmParameters;
import com.bsuir.giis.editor.service.flow.Mode;
import com.bsuir.giis.editor.view.BaseLayer;
//first 2 points create straight line.Then and parameter points.As fake steps choose one's on the straight line
public class BSplineAlgorithm implements ParameterCurveAlgorithm{
    @Override
    public void draw(BaseLayer canvas, AlgorithmParameters parameters, Mode mode) {

    }

    @Override
    public void morph(BaseLayer canvas, AlgorithmParameters parameters, Mode mode) {

    }
}
