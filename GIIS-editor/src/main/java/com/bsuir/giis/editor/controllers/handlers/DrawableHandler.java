package com.bsuir.giis.editor.controllers.handlers;

import com.bsuir.giis.editor.utils.ModeContainer;
import com.bsuir.giis.editor.utils.ToolContainer;
import com.bsuir.giis.editor.view.Canvas;

import java.awt.event.MouseEvent;

public interface DrawableHandler {
    void handlePress(Canvas canvas, MouseEvent mouseEvent, ToolContainer tool, ModeContainer mode);

    void handleMove(Canvas canvas, MouseEvent mouseEvent, ToolContainer tool, ModeContainer mode);

    void handleDrag(Canvas canvas, MouseEvent mouseEvent, ToolContainer tool, ModeContainer mode);

}
