package com.bsuir.giis.editor.controllers.handlers;

import com.bsuir.giis.editor.model.Pen;
import com.bsuir.giis.editor.model.Point;
import com.bsuir.giis.editor.model.PointShapeParameters;
import com.bsuir.giis.editor.model.shapes.Shape;
import com.bsuir.giis.editor.rendering.Canvas;
import com.bsuir.giis.editor.service.lines.Antialiasing;
import com.bsuir.giis.editor.utils.ModeContainer;
import com.bsuir.giis.editor.utils.ModifierState;
import com.bsuir.giis.editor.utils.Step;
import com.bsuir.giis.editor.utils.ToolContainer;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

public class PenHandler implements DrawableHandler {
    private final Antialiasing lineDrawer = new Antialiasing();

    public PenHandler(Step Step) {
    }

    @Override
    public void handlePress(Canvas canvas, MouseEvent mouseEvent, ToolContainer tool, ModeContainer mode, ModifierState modifierState) {
        if (tool.getTool() instanceof Pen pen) {
            pen.addPoint(mouseEvent.getX(), mouseEvent.getY());
            canvas.getLayer().paintPixel(mouseEvent.getX(), mouseEvent.getY(), Color.BLACK);
            canvas.getLayer().repaint();
        }
    }

    @Override
    public void handleMove(Canvas canvas, MouseEvent mouseEvent, ToolContainer tool, ModeContainer mode, ModifierState modifierState) {

    }

    @Override
    public void handleDrag(Canvas canvas, MouseEvent mouseEvent, ToolContainer tool, ModeContainer mode, ModifierState modifierState) {
        if (tool.getTool() instanceof Pen pen) {
            Point last = pen.getLastPoint();
            Point current = new Point(mouseEvent.getX(), mouseEvent.getY());
            new Thread(() -> {
                lineDrawer.drawLine(canvas.getLayer(), last, current, Color.BLACK);
                canvas.getLayer().repaint();
            }).start();
            pen.addPoint(mouseEvent.getX(), mouseEvent.getY());
        }
    }

    @Override
    public void handleRelease(Canvas canvas, MouseEvent mouseEvent, ToolContainer tool, ModeContainer mode, ModifierState modifierState) {
        if (tool.getTool() instanceof Pen pen) {
            List<Point> pts = pen.getPoints();
            if (pts.size() >= 2) {
                PointShapeParameters params = new PointShapeParameters(new ArrayList<>(pts));
                Shape<Pen> shape = new Shape<>(pen, params, Color.BLACK);
                canvas.getLayer().addShape(shape);
            }
          
            pen.resetPoints();
        }
    }
}
