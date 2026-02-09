package com.bsuir.giis.editor.model;

import com.bsuir.giis.editor.service.flow.Mode;
import com.bsuir.giis.editor.view.BaseLayer;
import com.bsuir.giis.editor.view.Canvas;


public interface Drawable extends Tool {
     void draw(BaseLayer canvas, AlgorithmParameters parameters, Mode mode);
}
