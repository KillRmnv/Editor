package com.bsuir.giis.editor.service.lines;

import com.bsuir.giis.editor.model.AlgorithmParameters;
import com.bsuir.giis.editor.model.Point;
import com.bsuir.giis.editor.model.PointShapeParameters;
import com.bsuir.giis.editor.rendering.BaseLayer;
import com.bsuir.giis.editor.service.curves.CircleAlgorithm;
import com.bsuir.giis.editor.service.curves.CurvesAlgorithm;
import com.bsuir.giis.editor.service.flow.HitTestPolicy;
import com.bsuir.giis.editor.service.flow.Mode;
import com.bsuir.giis.editor.utils.PenStep;

import java.awt.*;
import java.util.List;

public class CDAAlgorithm implements StraightLineAlgorithm {

    private CurvesAlgorithm curvesAlgorithm;
    private final HitTestPolicy hitTestPolicy = new HitTestPolicy();

    private CurvesAlgorithm getCurvesAlgorithm() {
        if (curvesAlgorithm == null) {
            curvesAlgorithm = new CircleAlgorithm();
        }
        return curvesAlgorithm;
    }

    @Override
    public void draw(BaseLayer canvas, AlgorithmParameters parameters, Mode mode) {
        PointShapeParameters linesParameters = (PointShapeParameters) parameters;

        int pixelSize = canvas.getPixelSize();

        int x1 = (linesParameters.getPoint(0)).getX() / pixelSize;
        int y1 = (linesParameters.getPoint(0)).getY() / pixelSize;
        int x2 = (linesParameters.getPoint(1)).getX() / pixelSize;
        int y2 = (linesParameters.getPoint(1)).getY() / pixelSize;

        int dx = x2 - x1;
        int dy = y2 - y1;

        int steps = Math.max(Math.abs(dx), Math.abs(dy));

        if (steps == 0) {

            canvas.paintPixel(x1, y1,Color.BLACK);
            return;
        }

        double xIncrement = (double) dx / steps;
        double yIncrement = (double) dy / steps;

        double x = x1;
        double y = y1;

        for (int i = 0; i <= steps; i++) {
            canvas.paintPixel((int) Math.round(x)*pixelSize, (int) Math.round(y)*pixelSize, Color.BLACK);
            mode.onStep(new PenStep((int) Math.round(x), (int) Math.round(y),255),"CDA Algorithm: ");
            x += xIncrement;
            y += yIncrement;
        }
        mode.onFinish();
    }

    @Override
    public void morph(BaseLayer canvas, AlgorithmParameters parameters, Mode mode) {
        draw(canvas, parameters, mode);
        PointShapeParameters linesParams = (PointShapeParameters) parameters;
        Point start = linesParams.getPoint(0);
        Point end = linesParams.getPoint(1);
        int radius = hitTestPolicy.calculateTolerance(canvas.getPixelSize());
        getCurvesAlgorithm().draw(canvas, new PointShapeParameters(List.of(start,
                new Point(start.getX() + radius, start.getY() + radius))), mode);
        getCurvesAlgorithm().draw(canvas, new PointShapeParameters(List.of(end,
                new Point(end.getX() + radius, end.getY() + radius))), mode);
    }
}