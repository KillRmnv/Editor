package com.bsuir.giis.editor.controllers.handlers;

import com.bsuir.giis.editor.model.FillParameters;
import com.bsuir.giis.editor.model.FillTool;
import com.bsuir.giis.editor.model.Point;
import com.bsuir.giis.editor.model.PointShapeParameters;
import com.bsuir.giis.editor.model.Tool;
import com.bsuir.giis.editor.rendering.Canvas;
import com.bsuir.giis.editor.service.flow.Mode;
import com.bsuir.giis.editor.service.flow.Regular;
import com.bsuir.giis.editor.service.lines.Antialiasing;
import com.bsuir.giis.editor.service.lines.StraightLineAlgorithm;
import com.bsuir.giis.editor.utils.ModeContainer;
import com.bsuir.giis.editor.utils.ModifierState;
import com.bsuir.giis.editor.utils.ToolContainer;

import java.awt.Color;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

public class FillHandler implements DrawableHandler {

    private final List<Point> polygonPoints = new ArrayList<>();
    private final StraightLineAlgorithm lineAlgorithm = new Antialiasing();
    private final Regular regularMode = new Regular();
    private Color fillColor = Color.BLACK;
    private Color borderColor = Color.BLACK;

    public FillHandler() {
    }

    public void setFillColor(Color fillColor) {
        this.fillColor = fillColor;
    }

    public void setBorderColor(Color borderColor) {
        this.borderColor = borderColor;
    }

    public Color getFillColor() {
        return fillColor;
    }

    public Color getBorderColor() {
        return borderColor;
    }

    @Override
    public void handlePress(Canvas canvas, MouseEvent mouseEvent, ToolContainer tool, ModeContainer mode, ModifierState modifierState) {
        if (!(tool.getTool() instanceof FillTool fillTool)) return;

        if (mouseEvent.getButton() == MouseEvent.BUTTON1) {
            if (isSeedFill(fillTool)) {
                new Thread(()->{
                      handleSeedFill(canvas, mouseEvent, fillTool, mode);
                }).start();
              
            } else {
                handlePolygonPoint(canvas, mouseEvent);
            }
        } else if (mouseEvent.getButton() == MouseEvent.BUTTON3) {
            if (!isSeedFill(fillTool) && polygonPoints.size() >= 3) {
                 new Thread(()->{
                handlePolygonFill(canvas, fillTool, mode);
                 }).start();
            }
        }
    }

    @Override
    public void handleMove(Canvas canvas, MouseEvent mouseEvent, ToolContainer tool, ModeContainer mode, ModifierState modifierState) {
        if (!(tool.getTool() instanceof FillTool fillTool)) return;
        if (isSeedFill(fillTool)) return;
        if (polygonPoints.isEmpty()) return;

        canvas.getLayerMoveable().cleanLayer();
        Point last = polygonPoints.getLast();
        PointShapeParameters previewParams = new PointShapeParameters(
            last,
            new Point(mouseEvent.getX(), mouseEvent.getY())
        );
        lineAlgorithm.draw(canvas.getLayerMoveable(), previewParams, regularMode);
        canvas.getLayerMoveable().repaint();
    }

    @Override
    public void handleDrag(Canvas canvas, MouseEvent mouseEvent, ToolContainer tool, ModeContainer mode, ModifierState modifierState) {
    }

    @Override
    public void handleRelease(Canvas canvas, MouseEvent mouseEvent, ToolContainer tool, ModeContainer mode, ModifierState modifierState) {
    }

    private boolean isSeedFill(FillTool fillTool) {
        return fillTool instanceof com.bsuir.giis.editor.service.fill.SimpleSeedFill
            || fillTool instanceof com.bsuir.giis.editor.service.fill.ScanlineSeedFill;
    }

    private void handleSeedFill(Canvas canvas, MouseEvent mouseEvent, FillTool fillTool, ModeContainer mode) {
        Point seedPoint = new Point(mouseEvent.getX(), mouseEvent.getY());
        FillParameters params = new FillParameters(seedPoint, fillColor, borderColor);
        fillTool.fill(canvas.getLayer(), params, mode.getMode());
        canvas.getLayer().repaint();
    }

    private void handlePolygonPoint(Canvas canvas, MouseEvent mouseEvent) {
        Point point = new Point(mouseEvent.getX(), mouseEvent.getY());
        polygonPoints.add(point);

        if (polygonPoints.size() >= 2) {
            Point prev = polygonPoints.get(polygonPoints.size() - 2);
            PointShapeParameters lineParams = new PointShapeParameters(prev, point);
            lineAlgorithm.draw(canvas.getLayerMorphable(), lineParams, regularMode);
            canvas.getLayerMorphable().repaint();
        }
    }

    private void handlePolygonFill(Canvas canvas, FillTool fillTool, ModeContainer mode) {
        canvas.getLayerMoveable().cleanLayer();
        canvas.getLayerMorphable().cleanLayer();

        FillParameters params = new FillParameters(new ArrayList<>(polygonPoints), fillColor);
        fillTool.fill(canvas.getLayer(), params, mode.getMode());
        canvas.getLayer().repaint();

        polygonPoints.clear();
    }

    public static class FillToolWrapper implements Tool {
        private final FillTool fillTool;

        public FillToolWrapper(FillTool fillTool) {
            this.fillTool = fillTool;
        }

        public FillTool getFillTool() {
            return fillTool;
        }
    }
}
