package com.bsuir.giis.editor.controllers.handlers;

import com.bsuir.giis.editor.model.AlgorithmParameters;
import com.bsuir.giis.editor.model.Drawable;
import com.bsuir.giis.editor.model.Point;
import com.bsuir.giis.editor.model.MorphableShape;
import com.bsuir.giis.editor.utils.ModeContainer;
import com.bsuir.giis.editor.utils.ToolContainer;
import com.bsuir.giis.editor.view.BaseLayer;
import com.bsuir.giis.editor.view.Canvas;

import java.awt.event.MouseEvent;

public interface DrawableHandler {
    void handlePress(Canvas canvas, MouseEvent mouseEvent, ToolContainer tool, ModeContainer mode);

    void handleMove(Canvas canvas, MouseEvent mouseEvent, ToolContainer tool, ModeContainer mode);

    void handleDrag(Canvas canvas, MouseEvent mouseEvent, ToolContainer tool, ModeContainer mode);

    default void addToLayer(BaseLayer canvas, ToolContainer tool, AlgorithmParameters parameters, MouseEvent mouseEvent) {
        MorphableShape<?> shape = new MorphableShape(parameters, (Drawable) tool.getTool());
        canvas.addShape(new Point(mouseEvent.getX(), mouseEvent.getY()), shape);
    }
}
