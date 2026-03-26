package com.bsuir.giis.editor.controllers.handlers;

import com.bsuir.giis.editor.model.CanvasState;
import com.bsuir.giis.editor.model.Point;
import com.bsuir.giis.editor.model.PointShapeParameters;
import com.bsuir.giis.editor.model.shapes.MorphableShape;
import com.bsuir.giis.editor.rendering.Canvas;
import com.bsuir.giis.editor.service.flow.HitTestPolicy;
import com.bsuir.giis.editor.service.flow.Regular;
import com.bsuir.giis.editor.service.lines.Antialiasing;
import com.bsuir.giis.editor.service.lines.StraightLineAlgorithm;
import com.bsuir.giis.editor.service.polygons.PolygonNormalDrawer;
import com.bsuir.giis.editor.service.polygons.PolygonValidator;
import com.bsuir.giis.editor.service.polygons.SimplePolygonAlgorithm;
import com.bsuir.giis.editor.utils.ModeContainer;
import com.bsuir.giis.editor.utils.ModifierState;
import com.bsuir.giis.editor.utils.ToolContainer;

import javax.swing.*;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SimplePolygonHandler implements DrawableHandler {

    private final List<Point> points = new ArrayList<>();
    private final StraightLineAlgorithm lineAlgorithm = new Antialiasing();
    private final Regular regularMode = new Regular();
    private final PolygonNormalDrawer normalDrawer = new PolygonNormalDrawer();
    private final PolygonValidator validator = new PolygonValidator();
    private final HitTestPolicy hitTestPolicy = new HitTestPolicy();
    private PointShapeParameters lastParams;

    @Override
    public void handlePress(
        Canvas canvas,
        MouseEvent mouseEvent,
        ToolContainer tool,
        ModeContainer mode,
        ModifierState modifierState
    ) {
        if (!(tool.getTool() instanceof SimplePolygonAlgorithm)) return;

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

            if (points.size() >= 2) {
                Point second = points.getLast();
                Point first = points.get(points.size() - 2);
                lineAlgorithm.draw(
                    canvas.getLayerMorphable(),
                    new PointShapeParameters(first, second),
                    regularMode
                );
            }
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
        if (!(tool.getTool() instanceof SimplePolygonAlgorithm)) return;
        if (points.isEmpty()) return;

        canvas.getLayerMoveable().cleanLayer();
        Point last = points.getLast();
        PointShapeParameters previewParams = new PointShapeParameters(
            last,
            new Point(mouseEvent.getX(), mouseEvent.getY())
        );
        lineAlgorithm.draw(canvas.getLayerMoveable(), previewParams, regularMode);
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

    private void finalizePolygon(Canvas canvas, ToolContainer tool) {
        canvas.getLayerMoveable().cleanLayer();
        canvas.getLayerMorphable().cleanLayer();

        lastParams = buildParams();
        SimplePolygonAlgorithm algorithm = (SimplePolygonAlgorithm) tool.getTool();
        algorithm.draw(canvas.getLayer(), lastParams, regularMode);
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
