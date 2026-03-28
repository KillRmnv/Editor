package com.bsuir.giis.editor.service.triangulation;

import com.bsuir.giis.editor.model.AlgorithmParameters;
import com.bsuir.giis.editor.model.shapes.Drawable;
import com.bsuir.giis.editor.rendering.BaseLayer;
import com.bsuir.giis.editor.service.flow.Mode;

public interface TriangulationAlgorithm extends Drawable {
@Override
void draw(BaseLayer canvas, AlgorithmParameters parameters, Mode mode);
	
} 