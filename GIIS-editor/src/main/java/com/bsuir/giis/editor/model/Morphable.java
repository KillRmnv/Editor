package com.bsuir.giis.editor.model;

import com.bsuir.giis.editor.service.flow.Mode;
import com.bsuir.giis.editor.view.BaseLayer;

public interface Morphable {
    void morph(BaseLayer canvas, AlgorithmParameters parameters, Mode mode);
}
