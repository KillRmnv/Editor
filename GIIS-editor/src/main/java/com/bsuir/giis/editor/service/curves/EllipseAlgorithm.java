package com.bsuir.giis.editor.service.curves;

import com.bsuir.giis.editor.model.AlgorithmParameters;
import com.bsuir.giis.editor.model.Point;
import com.bsuir.giis.editor.model.PointShapeParameters;
import com.bsuir.giis.editor.rendering.BaseLayer;
import com.bsuir.giis.editor.service.flow.HitTestPolicy;
import com.bsuir.giis.editor.service.flow.Mode;
import com.bsuir.giis.editor.service.flow.Regular;
import com.bsuir.giis.editor.service.lines.Antialiasing;
import com.bsuir.giis.editor.service.lines.StraightLineAlgorithm;
import com.bsuir.giis.editor.utils.MultiStep;
import com.bsuir.giis.editor.utils.PenStep;

import java.awt.Color;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

public class EllipseAlgorithm implements CurvesAlgorithm {

    private StraightLineAlgorithm straightLineAlgorithm;
    private CurvesAlgorithm curvesAlgorithm;
    private final HitTestPolicy hitTestPolicy = new HitTestPolicy();

    private StraightLineAlgorithm getStraightLineAlgorithm() {
        if (straightLineAlgorithm == null) {
            straightLineAlgorithm = new Antialiasing();
        }
        return straightLineAlgorithm;
    }

    private CurvesAlgorithm getCurvesAlgorithm() {
        if (curvesAlgorithm == null) {
            curvesAlgorithm = new CircleAlgorithm();
        }
        return curvesAlgorithm;
    }

    @Override
    public void draw(BaseLayer canvas, AlgorithmParameters parameters, Mode mode) {
        PointShapeParameters curvesParameters = (PointShapeParameters) parameters;

        Point pCenter = curvesParameters.getPoint(0);
        Point pX = curvesParameters.getPoint(1); // Точка для определения радиуса A
        Point pY = curvesParameters.getPoint(2); // Точка для определения радиуса B

        if (pCenter == null || pX == null || pY == null) {
            return;
        }

        int pixelSize = 1;
        pixelSize = canvas.getPixelSize();

        int xc = pCenter.getX() / pixelSize;
        int yc = pCenter.getY() / pixelSize;

        int a = Math.abs((pX.getX() / pixelSize) - xc);
        int b = Math.abs((pY.getY() / pixelSize) - yc);

        if (a == 0 && b == 0) {
            drawPixel(canvas, xc, yc, pixelSize);
            return;
        }


        int x = 0;
        int y = b;

        long a2 = (long) a * a;
        long b2 = (long) b * b;
        long twoA2 = 2 * a2;
        long twoB2 = 2 * b2;

        // d1 = b^2 - a^2 * b + a^2 / 4
        long d1 = b2 - (a2 * b) + (a2 >> 2);

        long dx = 0;             // 2 * b^2 * x
        long dy = twoA2 * y;     // 2 * a^2 * y

        drawSymmetricPoints(canvas, xc, yc, x, y, pixelSize);
        try {
            mode.onStep(new MultiStep(3, PenStep.class),"Ellipse(center): ");
            canvas.repaint();
        } catch (NoSuchMethodException | InvocationTargetException | InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
        while (dx < dy) {
            x++;
            dx += twoB2;

            if (d1 < 0) {
                d1 += dx + b2;
            } else {
                y--;
                dy -= twoA2;
                d1 += dx - dy + b2;
            }
            drawSymmetricPoints(canvas, xc, yc, x, y, pixelSize);
            try {
                mode.onStep(new MultiStep(3, PenStep.class),"Ellipse(region 1): ");
                canvas.repaint();
            } catch (NoSuchMethodException | InvocationTargetException | InstantiationException | IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }

        // d2 = b^2 * (x + 0.5)^2 + a^2 * (y - 1)^2 - a^2 * b^2
        long d2 = (long) (b2 * (x + 0.5) * (x + 0.5) + a2 * (y - 1) * (y - 1) - a2 * b2);

        while (y > 0) {
            y--;
            dy -= twoA2;

            if (d2 > 0) {
                d2 += a2 - dy;
            } else {
                x++;
                dx += twoB2;
                d2 += dx - dy + a2;
            }
            drawSymmetricPoints(canvas, xc, yc, x, y, pixelSize);
            try {
                mode.onStep(new MultiStep(3, PenStep.class),"Ellipse(region 2): ");
                canvas.repaint();
            } catch (NoSuchMethodException | InvocationTargetException | InstantiationException | IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }

         mode.onFinish() ;
    }

    @Override
    public void morph(BaseLayer canvas, AlgorithmParameters parameters, Mode mode) {
        draw(canvas, parameters, mode);
        PointShapeParameters curvesParameters = (PointShapeParameters) parameters;
        Point pCenter = curvesParameters.getPoint(0);
        Point pX = curvesParameters.getPoint(1);
        Point pY = curvesParameters.getPoint(2);
        int radius = hitTestPolicy.calculateTolerance(canvas.getPixelSize());
        getStraightLineAlgorithm().draw(canvas, new PointShapeParameters(List.of(pCenter, pX)), mode);
        getStraightLineAlgorithm().draw(canvas, new PointShapeParameters(List.of(pCenter, pY)), mode);
        getCurvesAlgorithm().draw(canvas, new PointShapeParameters(List.of(pCenter,
                new Point(pCenter.getX() + radius, pCenter.getY()))), new Regular());
        getCurvesAlgorithm().draw(canvas, new PointShapeParameters(List.of(pX,
                new Point(pX.getX() + radius, pX.getY()))), new Regular());
        getCurvesAlgorithm().draw(canvas, new PointShapeParameters(List.of(pY,
                new Point(pY.getX() + radius, pY.getY()))), new Regular());
    }

    private void drawSymmetricPoints(BaseLayer canvas, int xc, int yc, int x, int y, int pixelSize) {
        drawPixel(canvas, xc + x, yc + y, pixelSize);
        drawPixel(canvas, xc - x, yc + y, pixelSize);
        drawPixel(canvas, xc + x, yc - y, pixelSize);
        drawPixel(canvas, xc - x, yc - y, pixelSize);
    }

    private void drawPixel(BaseLayer canvas, int cx, int cy, int pixelSize) {
        canvas.paintPixel(cx * pixelSize, cy * pixelSize, Color.BLACK);
    }
}