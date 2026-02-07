package com.bsuir.giis.editor.controllers.handlers;

import com.bsuir.giis.editor.userTools.Mode;
import com.bsuir.giis.editor.userTools.Tool;
import com.bsuir.giis.editor.view.Canvas;

import java.awt.event.MouseEvent;

public interface Handler {
 void handlePress(Canvas canvas, MouseEvent mouseEvent, Tool tool, Mode mode);
 void handleMove(Canvas canvas, MouseEvent mouseEvent, Tool tool, Mode mode);
 void handleDrag(Canvas canvas, MouseEvent mouseEvent, Tool tool, Mode mode);

}
