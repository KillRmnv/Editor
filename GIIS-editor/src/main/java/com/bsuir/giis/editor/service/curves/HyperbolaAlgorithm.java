package com.bsuir.giis.editor.service.curves;

import com.bsuir.giis.editor.model.AlgorithmParameters;
import com.bsuir.giis.editor.model.Point;
import com.bsuir.giis.editor.model.PointShapeParameters;
import com.bsuir.giis.editor.service.flow.HitTestPolicy;
import com.bsuir.giis.editor.service.flow.Mode;
import com.bsuir.giis.editor.service.flow.Regular;
import com.bsuir.giis.editor.service.lines.Antialiasing;
import com.bsuir.giis.editor.service.lines.StraightLineAlgorithm;
import com.bsuir.giis.editor.utils.MultiStep;
import com.bsuir.giis.editor.utils.PenStep;
import com.bsuir.giis.editor.rendering.BaseLayer;

import java.awt.*;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
//TODO: fix out of bounds error for BufferedImage
public class HyperbolaAlgorithm implements CurvesAlgorithm {

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
        Point center = curvesParameters.getPoint(0);
        Point pX = curvesParameters.getPoint(1);
        Point pY = curvesParameters.getPoint(2);

        if (center == null || pX == null || pY == null) {
            return;
        }

        int pixelSize = 1;
        int layerWidth = 1920;
        int layerHeight = 1080;
        pixelSize = canvas.getPixelSize();

        layerWidth = canvas.getWidth();

        layerHeight = canvas.getHeight();

        int xc = center.getX() / pixelSize;
        int yc = center.getY() / pixelSize;

        int a = Math.abs((pX.getX() / pixelSize) - xc);
        int b = Math.abs((pY.getY() / pixelSize) - yc);

        if (a == 0 || b == 0) return;

        long a2 = (long) a * a;
        long b2 = (long) b * b;
        long twoA2 = 2 * a2;
        long twoB2 = 2 * b2;

        int x = a;
        int y = 0;


        long d = twoB2 * a + b2 - twoA2 + a2;


        d = b2 * (x * x + x) + b2 / 4 - a2 - a2 * b2;


        d = b2 * (2 * a + 1) - 4 * a2;

        d = (long) (b2 * (a + 0.5) * (a + 0.5) - a2 * 1 * 1 - a2 * b2);

        drawHyperbolaPoints(canvas, xc, yc, x, y, pixelSize);
        try {
            mode.onStep(new MultiStep(3, PenStep.class),"Hyperbola(center): ");
            canvas.repaint();
        } catch (NoSuchMethodException | InvocationTargetException | InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }


        while ((b2 * x > a2 * y) && x < layerWidth && y < layerHeight) {
            y++;
            if (d < 0) {
                d += b2 * (2 * x + 2) - a2 * (2 * y + 1);
                x++;
            } else {
                d += -a2 * (2 * y + 1);
            }
            drawHyperbolaPoints(canvas, xc, yc, x, y, pixelSize);
            try {
                mode.onStep(new MultiStep(2, PenStep.class),"Hyperbola(region 1): ");
                canvas.repaint();
            } catch (NoSuchMethodException | InvocationTargetException | InstantiationException | IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }


        d = (long) (b2 * (x + 1) * (x + 1) - a2 * (y + 0.5) * (y + 0.5) - a2 * b2);

        while (x < layerWidth && y < layerHeight) {
            x++;
            if (d >= 0) {
                d += b2 * (2 * x + 1) - a2 * (2 * y + 2);
                y++;
            } else {
                d += b2 * (2 * x + 1);
            }
            drawHyperbolaPoints(canvas, xc, yc, x, y, pixelSize);
            try {
                mode.onStep(new MultiStep(2, PenStep.class),"Hyperbola(region 2): ");
                canvas.repaint();
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
        Point center = curvesParameters.getPoint(0);
        Point pX = curvesParameters.getPoint(1);
        Point pY = curvesParameters.getPoint(2);
        int radius = hitTestPolicy.calculateTolerance(canvas.getPixelSize());
        getStraightLineAlgorithm().draw(canvas, new PointShapeParameters(List.of(center, pX)), mode);
        getStraightLineAlgorithm().draw(canvas, new PointShapeParameters(List.of(center, pY)), mode);
        getCurvesAlgorithm().draw(canvas, new PointShapeParameters(List.of(center,
                new Point(center.getX() + radius, center.getY()))), new Regular());
        getCurvesAlgorithm().draw(canvas, new PointShapeParameters(List.of(pX,
                new Point(pX.getX() + radius, pX.getY()))), new Regular());
        getCurvesAlgorithm().draw(canvas, new PointShapeParameters(List.of(pY,
                new Point(pY.getX() + radius, pY.getY()))), new Regular());
    }

    private void drawHyperbolaPoints(BaseLayer canvas, int xc, int yc, int x, int y, int pixelSize) {

        canvas.paintPixel((xc + x) * pixelSize, (yc + y) * pixelSize, Color.BLACK);
        canvas.paintPixel((xc + x) * pixelSize, (yc - y) * pixelSize, Color.BLACK);

        canvas.paintPixel((xc - x) * pixelSize, (yc + y) * pixelSize, Color.BLACK);
        canvas.paintPixel((xc - x) * pixelSize, (yc - y) * pixelSize, Color.BLACK);
    }
}