package com.bsuir.giis.editor.service.curves;

import com.bsuir.giis.editor.model.Drawable;
import com.bsuir.giis.editor.model.AlgorithmParameters;
import com.bsuir.giis.editor.service.flow.Mode;
import com.bsuir.giis.editor.view.BaseLayer;

public interface CurvesAlgorithm extends Drawable {
    @Override
    void draw(BaseLayer canvas, AlgorithmParameters parameters, Mode mode);
}