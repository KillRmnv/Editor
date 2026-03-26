package com.bsuir.giis.editor.controllers.handlers;

import com.bsuir.giis.editor.model.CanvasState;
import com.bsuir.giis.editor.model.Point;
import com.bsuir.giis.editor.model.PointShapeParameters;
import com.bsuir.giis.editor.model.shapes.MorphableShape;
import com.bsuir.giis.editor.rendering.Canvas;
import com.bsuir.giis.editor.service.curves.CircleAlgorithm;
import com.bsuir.giis.editor.service.flow.HitTestPolicy;
import com.bsuir.giis.editor.service.flow.Regular;
import com.bsuir.giis.editor.service.polygons.PolygonNormalDrawer;
import com.bsuir.giis.editor.service.polygons.PolygonValidator;
import com.bsuir.giis.editor.service.polygons.PolygonsAlgorithm;
import com.bsuir.giis.editor.service.polygons.GrahamScanAlgorithm;
import com.bsuir.giis.editor.service.polygons.JarvisMarchAlgorithm;
import com.bsuir.giis.editor.utils.ModeContainer;
import com.bsuir.giis.editor.utils.ModifierState;
import com.bsuir.giis.editor.utils.ToolContainer;

import javax.swing.*;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ConvexHullHandler implements DrawableHandler {

    private final List<Point> points = new ArrayList<>();
    private final CircleAlgorithm markerAlgorithm = new CircleAlgorithm();
    private final Regular regularMode = new Regular();
    private final HitTestPolicy hitTestPolicy = new HitTestPolicy();
    private final PolygonNormalDrawer normalDrawer = new PolygonNormalDrawer();
    private final PolygonValidator validator = new PolygonValidator();
    private PointShapeParameters lastParams;

    @Override
    public void handlePress(
        Canvas canvas,
        MouseEvent mouseEvent,
        ToolContainer tool,
        ModeContainer mode,
        ModifierState modifierState
    ) {
        if (!(tool.getTool() instanceof PolygonsAlgorithm)) return;

        if (mouseEvent.getButton() == MouseEvent.BUTTON1) {
            if (modifierState.isAltPressed() || modifierState.isCtrlPressed()) {
                PointShapeParameters hit = hitTestExistingPolygon(canvas, mouseEvent);
                if (hit != null) {
                    if (modifierState.isAltPressed()) {
                        normalDrawer.drawNormals(canvas.getLayer(), hit, mode.getMode());
                    }
                    if (modifierState.isCtrlPressed()) {
                        boolean convex = validator.isConvex(hit);
                        JOptionPane.showMessageDialog(
                            canvas,
                            convex ? "Polygon is convex" : "Polygon is not convex",
                            "Convexity Check",
                            JOptionPane.INFORMATION_MESSAGE
                        );
                    }
                }
                modifierState.setAltPressed(false);
                modifierState.setCtrlPressed(false);
                return;
            }

            points.add(new Point(mouseEvent.getX(), mouseEvent.getY()));
            drawMarkers(canvas);
        } else if (mouseEvent.getButton() == MouseEvent.BUTTON3) {
            if (points.size() >= 3) {
                finalizePolygon(canvas, tool);
            }
        }
    }

    @Override
    public void handleMove(
        Canvas canvas,
        MouseEvent mouseEvent,
        ToolContainer tool,
        ModeContainer mode,
        ModifierState modifierState
    ) {
    }

    @Override
    public void handleDrag(
        Canvas canvas,
        MouseEvent mouseEvent,
        ToolContainer tool,
        ModeContainer mode,
        ModifierState modifierState
    ) {
    }

    @Override
    public void handleRelease(
        Canvas canvas,
        MouseEvent mouseEvent,
        ToolContainer tool,
        ModeContainer mode,
        ModifierState modifierState
    ) {
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

    private void finalizePolygon(Canvas canvas, ToolContainer tool) {
        canvas.getLayerMoveable().cleanLayer();

        lastParams = buildParams();
        PolygonsAlgorithm algorithm = (PolygonsAlgorithm) tool.getTool();
        algorithm.draw(canvas.getLayer(), lastParams, regularMode);

        // берём результатные точки из алгоритма (оболочка)
        if (algorithm instanceof GrahamScanAlgorithm graham) {
            List<Point> hull = graham.getComputedHull();
            if (hull != null && !hull.isEmpty()) {
                lastParams = new PointShapeParameters(hull);
            }
        } else if (algorithm instanceof JarvisMarchAlgorithm jarvis) {
            List<Point> hull = jarvis.getComputedHull();
            if (hull != null && !hull.isEmpty()) {
                lastParams = new PointShapeParameters(hull);
            }
        }

        addToLayer(canvas.getLayer(), tool, lastParams, null);
        canvas.getLayer().repaint();
        points.clear();
    }

    private PointShapeParameters hitTestExistingPolygon(Canvas canvas, MouseEvent mouseEvent) {
        CanvasState state = canvas.getLayer().getState();
        int pixelSize = canvas.getLayer().getPixelSize();
        Map<Point, List<MorphableShape<?>>> layersMap = state.getLayersMap();

        for (Point p : layersMap.keySet()) {
            Point hit = hitTestPolicy.resolvePoint(
                mouseEvent.getX(), mouseEvent.getY(),
                java.util.Set.of(p), pixelSize
            ).orElse(null);
            if (hit != null) {
                List<MorphableShape<?>> shapes = layersMap.get(p);
                if (shapes != null && !shapes.isEmpty()) {
                    return (PointShapeParameters) shapes.getFirst().getParameters();
                }
            }
        }
        return null;
    }

    private PointShapeParameters buildParams() {
        return new PointShapeParameters(new ArrayList<>(points));
    }
}
