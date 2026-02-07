package com.bsuir.giis.editor.model;

import com.bsuir.giis.editor.userTools.Tool;
import com.bsuir.giis.editor.view.Canvas;


public interface Drawable extends Tool {
     void draw(Canvas canvas,AlgorithmParameters parameters);
}
