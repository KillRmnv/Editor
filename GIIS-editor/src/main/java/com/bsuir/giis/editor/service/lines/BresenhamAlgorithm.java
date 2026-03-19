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

public class BresenhamAlgorithm implements StraightLineAlgorithm {

    private final CurvesAlgorithm curvesAlgorithm = new CircleAlgorithm();
    private final HitTestPolicy hitTestPolicy = new HitTestPolicy();

    @Override
    public void draw(BaseLayer canvas, AlgorithmParameters parameters, Mode mode) {
        PointShapeParameters linesParameters = (PointShapeParameters) parameters;

        int pixelSize = canvas.getPixelSize();

        int x1 = (linesParameters.getPoint(0)).getX() / pixelSize;
        int y1 = (linesParameters.getPoint(0)).getY() / pixelSize;
        int x2 = (linesParameters.getPoint(1)).getX() / pixelSize;
        int y2 = (linesParameters.getPoint(1)).getY() / pixelSize;

        int dx = Math.abs(x2 - x1);
        int dy = Math.abs(y2 - y1);

        int sx = (x1 < x2) ? 1 : -1;
        int sy = (y1 < y2) ? 1 : -1;

        int err = dx - dy;

        while (true) {

            canvas.paintPixel(x1 * pixelSize, y1 * pixelSize,Color.BLACK);
            mode.onStep(new PenStep(x1,y1,255),"Bresenham Algorithm: ");

            if (x1 == x2 && y1 == y2) {
                break;
            }

            int e2 = 2 * err;

            if (e2 > -dy) {
                err -= dy;
                x1 += sx;
            }

            if (e2 < dx) {
                err += dx;
                y1 += sy;
            }
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
        curvesAlgorithm.draw(canvas, new PointShapeParameters(List.of(start,
                new Point(start.getX() + radius, start.getY() + radius))), mode);
        curvesAlgorithm.draw(canvas, new PointShapeParameters(List.of(end,
                new Point(end.getX() + radius, end.getY() + radius))), mode);
    }
}