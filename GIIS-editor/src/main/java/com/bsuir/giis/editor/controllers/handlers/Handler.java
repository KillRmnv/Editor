package com.bsuir.giis.editor.controllers.handlers;

import com.bsuir.giis.editor.Mode;
import com.bsuir.giis.editor.utils.ToolContainer;
import com.bsuir.giis.editor.view.Canvas;

import java.awt.event.MouseEvent;

public interface Handler {
    void handlePress(Canvas canvas, MouseEvent mouseEvent, ToolContainer tool, Mode mode);

    void handleMove(Canvas canvas, MouseEvent mouseEvent, ToolContainer tool, Mode mode);

    void handleDrag(Canvas canvas, MouseEvent mouseEvent, ToolContainer tool, Mode mode);

}
