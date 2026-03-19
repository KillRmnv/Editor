package com.bsuir.giis.editor.model.shapes;

import com.bsuir.giis.editor.model.AlgorithmParameters;
import com.bsuir.giis.editor.model.Tool;
import com.bsuir.giis.editor.rendering.BaseLayer;
import com.bsuir.giis.editor.service.flow.Mode;


public interface Drawable extends Tool {
     void draw(BaseLayer canvas, AlgorithmParameters parameters, Mode mode);

}
