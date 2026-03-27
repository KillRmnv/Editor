package com.bsuir.giis.editor.model;

import com.bsuir.giis.editor.rendering.BaseLayer;
import com.bsuir.giis.editor.service.flow.Mode;

public interface FillTool extends Tool {
    void fill(BaseLayer canvas, FillParameters parameters, Mode mode);
}