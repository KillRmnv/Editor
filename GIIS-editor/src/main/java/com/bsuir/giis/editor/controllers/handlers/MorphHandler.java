package com.bsuir.giis.editor.controllers.handlers;

import com.bsuir.giis.editor.model.Point;
import com.bsuir.giis.editor.model.MorphableShape;
import com.bsuir.giis.editor.utils.ModeContainer;
import com.bsuir.giis.editor.utils.ToolContainer;
import com.bsuir.giis.editor.view.Canvas;
import com.bsuir.giis.editor.view.TwoDimensionLayer;

import java.awt.event.MouseEvent;
import java.util.Objects;
import java.util.Optional;

public class MorphHandler implements DrawableHandler {
    private int tryCounter = 0;
    private Point previousPoint = new Point(0, 0);

    @Override
    public void handlePress(Canvas canvas, MouseEvent mouseEvent, ToolContainer tool, ModeContainer mode) {
        if (Objects.equals(previousPoint, new Point(mouseEvent.getX(), mouseEvent.getY()))) {
            tryCounter++;
        } else
            tryCounter = 0;
        TwoDimensionLayer layer = canvas.getLayer2D();
        Point point = new Point(mouseEvent.getX(), mouseEvent.getY());
        Optional<MorphableShape<?>> shape = layer.getShape(point, tryCounter);
        layer.repaintShapes(point, tryCounter);
        shape.ifPresent(morphableShape -> {
            canvas.getLayer2DMorphable().repaintShape(morphableShape);
            canvas.getLayer2DMorphable().addShape(point,morphableShape);
        });

    }

    @Override
    public void handleMove(Canvas canvas, MouseEvent mouseEvent, ToolContainer tool, ModeContainer mode) {

    }

    @Override
    public void handleDrag(Canvas canvas, MouseEvent mouseEvent, ToolContainer tool, ModeContainer mode) {

    }
}
