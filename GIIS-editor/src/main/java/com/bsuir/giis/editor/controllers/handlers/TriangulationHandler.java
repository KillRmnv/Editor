package com.bsuir.giis.editor.controllers.handlers;

import com.bsuir.giis.editor.model.AlgorithmParameters;
import com.bsuir.giis.editor.model.Point;
import com.bsuir.giis.editor.model.PointShapeParameters;
import com.bsuir.giis.editor.model.shapes.Drawable;
import com.bsuir.giis.editor.model.shapes.Shape;
import com.bsuir.giis.editor.rendering.BaseLayer;
import com.bsuir.giis.editor.rendering.Canvas;
import com.bsuir.giis.editor.service.curves.CircleAlgorithm;
import com.bsuir.giis.editor.service.flow.HitTestPolicy;
import com.bsuir.giis.editor.service.flow.Mode;
import com.bsuir.giis.editor.service.flow.Regular;
import com.bsuir.giis.editor.service.triangulation.TriangulationAlgorithm;
import com.bsuir.giis.editor.utils.ModeContainer;
import com.bsuir.giis.editor.utils.ModifierState;
import com.bsuir.giis.editor.utils.ToolContainer;

import java.awt.Color;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

public class TriangulationHandler implements DrawableHandler {

    private final List<Point> points = new ArrayList<>();
    private final CircleAlgorithm markerAlgorithm = new CircleAlgorithm();
    private final Regular regularMode = new Regular();
    private final HitTestPolicy hitTestPolicy = new HitTestPolicy();
    private PointShapeParameters lastParams;

    @Override
    public void handlePress(Canvas canvas, MouseEvent mouseEvent, ToolContainer tool, ModeContainer mode, ModifierState modifierState) {
        if (!(tool.getTool() instanceof TriangulationAlgorithm)) return;

        if (mouseEvent.getButton() == MouseEvent.BUTTON1) {
            points.add(new Point(mouseEvent.getX(), mouseEvent.getY()));
            drawMarkers(canvas);
        } else if (mouseEvent.getButton() == MouseEvent.BUTTON3) {
            if (points.size() >= 3) {
                finalizeTriangulation(canvas, tool);
            }
        }
    }

    @Override
    public void handleMove(Canvas canvas, MouseEvent mouseEvent, ToolContainer tool, ModeContainer mode, ModifierState modifierState) {
    }

    @Override
    public void handleDrag(Canvas canvas, MouseEvent mouseEvent, ToolContainer tool, ModeContainer mode, ModifierState modifierState) {
    }

    @Override
    public void handleRelease(Canvas canvas, MouseEvent mouseEvent, ToolContainer tool, ModeContainer mode, ModifierState modifierState) {
    }

    private void drawMarkers(Canvas canvas) {
        canvas.getLayerMoveable().cleanLayer();
        int radius = hitTestPolicy.calculateTolerance(canvas.getLayer().getPixelSize());
        for (Point p : points) {
            markerAlgorithm.draw(canvas.getLayerMoveable(),
                    new PointShapeParameters(List.of(p, new Point(p.getX() + radius, p.getY()))),
                    regularMode);
        }
        canvas.getLayerMoveable().repaint();
    }

    private void finalizeTriangulation(Canvas canvas, ToolContainer tool) {
        canvas.getLayerMoveable().cleanLayer();

        lastParams = buildParams();
        TriangulationAlgorithm algorithm = (TriangulationAlgorithm) tool.getTool();
        algorithm.draw(canvas.getLayer(), lastParams, regularMode);

        BaseLayer layer = canvas.getLayer();
        Shape<?> shape = new Shape<>((Drawable) tool.getTool(), lastParams, Color.BLACK);
        layer.addShape(shape);

        canvas.getLayer().repaint();
        points.clear();
    }

    private PointShapeParameters buildParams() {
        return new PointShapeParameters(new ArrayList<>(points));
    }
}
