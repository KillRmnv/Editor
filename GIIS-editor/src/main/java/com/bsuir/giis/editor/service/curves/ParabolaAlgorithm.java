package com.bsuir.giis.editor.service.curves;

import com.bsuir.giis.editor.model.AlgorithmParameters;
import com.bsuir.giis.editor.model.Point;
import com.bsuir.giis.editor.model.PointShapeParameters;
import com.bsuir.giis.editor.service.flow.HitTestPolicy;
import com.bsuir.giis.editor.service.flow.Mode;
import com.bsuir.giis.editor.service.lines.Antialiasing;
import com.bsuir.giis.editor.service.lines.StraightLineAlgorithm;
import com.bsuir.giis.editor.utils.MultiStep;
import com.bsuir.giis.editor.utils.PenStep;
import com.bsuir.giis.editor.rendering.BaseLayer;

import java.awt.*;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

public class ParabolaAlgorithm implements CurvesAlgorithm {

    private final StraightLineAlgorithm straightLineAlgorithm = new Antialiasing();
    private final HitTestPolicy hitTestPolicy = new HitTestPolicy();

    @Override
    public void draw(BaseLayer canvas, AlgorithmParameters parameters, Mode mode) {
        PointShapeParameters curvesParameters = (PointShapeParameters) parameters;
        Point vertex = curvesParameters.getPoint(0);
        Point endPoint = curvesParameters.getPoint(1);

        if (vertex == null || endPoint == null) {
            return;
        }

        int pixelSize = 1;

        pixelSize = canvas.getPixelSize();

        int x0 = vertex.getX() / pixelSize;
        int y0 = vertex.getY() / pixelSize;
        int x1 = endPoint.getX() / pixelSize;
        int y1 = endPoint.getY() / pixelSize;

        int dx = x1 - x0;
        int dy = y1 - y0;

        if (dy == 0) {

            return;
        }


        double twoP = (double) (dx * dx) / Math.abs(dy);

        int signY = (dy > 0) ? 1 : -1;


        double x = 0;
        double y = 0;

        double p_param = twoP / 2.0;
        double d = 1 - p_param;

        drawSymmetricPoints(canvas, x0, y0, (int) x, (int) y, signY, pixelSize);
        try {
            mode.onStep(new MultiStep(2, PenStep.class),"Parabola(center): ");
        } catch (NoSuchMethodException | InvocationTargetException | InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
        while (x < p_param) {
            x++;
            if (d < 0) {
                d += 2 * x + 1;
            } else {
                y++;
                d += 2 * x + 1 - twoP;
            }

            if (y > Math.abs(dy)) break;

            drawSymmetricPoints(canvas, x0, y0, (int) x, (int) y, signY, pixelSize);
            try {
                mode.onStep(new MultiStep(2, PenStep.class),"Parabola(x): ");
            } catch (NoSuchMethodException | InvocationTargetException | InstantiationException | IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }

        // d = (x + 0.5)^2 - 2p * (y + 1)
        d = ((x + 0.5) * (x + 0.5)) - twoP * (y + 1);

        while (y < Math.abs(dy)) {
            y++;
            if (d > 0) {
                d += -twoP;
            } else {
                x++;
                d += 2 * x - twoP;
            }
            drawSymmetricPoints(canvas, x0, y0, (int) x, (int) y, signY, pixelSize);
            try {
                mode.onStep(new MultiStep(2, PenStep.class),"Parabola(y): ");
            } catch (NoSuchMethodException | InvocationTargetException | InstantiationException | IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
        mode.onFinish();
    }

    @Override
    public void morph(BaseLayer canvas, AlgorithmParameters parameters, Mode mode) {
        draw(canvas, parameters, mode);
        PointShapeParameters curvesParameters = (PointShapeParameters) parameters;
        Point vertex = curvesParameters.getPoint(0);
        Point endPoint = curvesParameters.getPoint(1);
        int radius = hitTestPolicy.calculateTolerance(canvas.getPixelSize());
        straightLineAlgorithm.draw(canvas, new PointShapeParameters(List.of(vertex, endPoint)), mode);
        this.draw(canvas, new PointShapeParameters(List.of(vertex,
                new Point(vertex.getX() + radius, vertex.getY() + radius))), mode);
        this.draw(canvas, new PointShapeParameters(List.of(endPoint,
                new Point(endPoint.getX() + radius, endPoint.getY() + radius))), mode);
    }


    private void drawSymmetricPoints(BaseLayer canvas, int x0, int y0, int x, int y, int signY, int pixelSize) {
        int screenX1 = (x0 + x) * pixelSize;
        int screenY1 = (y0 + y * signY) * pixelSize;
        canvas.paintPixel(screenX1, screenY1, Color.BLACK);

        int screenX2 = (x0 - x) * pixelSize;
        int screenY2 = (y0 + y * signY) * pixelSize;
        canvas.paintPixel(screenX2, screenY2, Color.BLACK);
    }
}