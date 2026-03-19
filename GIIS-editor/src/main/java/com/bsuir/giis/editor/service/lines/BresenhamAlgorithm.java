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
        getCurvesAlgorithm().draw(canvas, new PointShapeParameters(List.of(start,
                new Point(start.getX() + radius, start.getY() + radius))), mode);
        getCurvesAlgorithm().draw(canvas, new PointShapeParameters(List.of(end,
                new Point(end.getX() + radius, end.getY() + radius))), mode);
    }

    public void drawLine(BaseLayer canvas, int x1, int y1, int x2, int y2, Color color) {
        int pixelSize = canvas.getPixelSize();

        int gx1 = x1 / pixelSize;
        int gy1 = y1 / pixelSize;
        int gx2 = x2 / pixelSize;
        int gy2 = y2 / pixelSize;

        int dx = Math.abs(gx2 - gx1);
        int dy = Math.abs(gy2 - gy1);
        int sx = (gx1 < gx2) ? 1 : -1;
        int sy = (gy1 < gy2) ? 1 : -1;
        int err = dx - dy;

        while (true) {
            canvas.paintPixel(gx1 * pixelSize, gy1 * pixelSize, color);

            if (gx1 == gx2 && gy1 == gy2) break;

            int e2 = 2 * err;
            if (e2 > -dy) { err -= dy; gx1 += sx; }
            if (e2 < dx) { err += dx; gy1 += sy; }
        }
    }

    public static void drawLineDirect(int[] pixels, int width, int height,
                                      int x1, int y1, int x2, int y2, int color) {
        int dx = Math.abs(x2 - x1);
        int dy = Math.abs(y2 - y1);
        int sx = (x1 < x2) ? 1 : -1;
        int sy = (y1 < y2) ? 1 : -1;
        int err = dx - dy;

        while (true) {
            if (x1 >= 0 && x1 < width && y1 >= 0 && y1 < height) {
                pixels[y1 * width + x1] = color;
            }
            if (x1 == x2 && y1 == y2) break;

            int e2 = 2 * err;
            if (e2 > -dy) { err -= dy; x1 += sx; }
            if (e2 < dx) { err += dx; y1 += sy; }
        }
    }
}