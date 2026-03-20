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

import java.awt.*;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

public class CircleAlgorithm implements CurvesAlgorithm {

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
        Point point = curvesParameters.getPoint(1);

        if (center == null || point == null) {
            return;
        }
        int pixelSize=canvas.getPixelSize();
        int xc = center.getX() / pixelSize;
        int yc = center.getY() / pixelSize;
        int radius = (int) Math.sqrt(Math.pow((point.getX()/pixelSize) - xc, 2) +
                                    Math.pow((point.getY()/pixelSize) - yc, 2));
        
        int x = 0;
        int y = radius;
        int d = 3 - 2 * radius;
        
        drawCirclePoints(canvas, xc, yc, x, y,pixelSize);
        try {
            mode.onStep(new MultiStep(2, PenStep.class),"Circle(center): ");
            canvas.repaint();
        } catch (NoSuchMethodException | InvocationTargetException | InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
        while (x <= y) {
            //inside
            if (d < 0) {
                d = d + 4 * x + 6;
            } else {
                d = d + 4 * (x - y) + 10;
                y--;
            }
            x++;
            drawCirclePoints(canvas, xc, yc, x, y,pixelSize);
            try{
            mode.onStep(new MultiStep(2, PenStep.class),"Circle: ");
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
        Point point = curvesParameters.getPoint(1);
        int radius = hitTestPolicy.calculateTolerance(canvas.getPixelSize());
        getStraightLineAlgorithm().draw(canvas, new PointShapeParameters(List.of(center, point)), mode);
        getCurvesAlgorithm().draw(canvas, new PointShapeParameters(List.of(center,
                new Point(center.getX() + radius, center.getY()))), new Regular());
        getCurvesAlgorithm().draw(canvas, new PointShapeParameters(List.of(point,
                new Point(point.getX() + radius, point.getY()))), new Regular());
    }
    
    private void drawCirclePoints(BaseLayer canvas, int xc, int yc, int x, int y,int pixelSize) {
        drawPixel(canvas,xc + x, yc + y, pixelSize);
        drawPixel(canvas,xc - x, yc + y, pixelSize);
        drawPixel(canvas,xc + x, yc - y, pixelSize);
        drawPixel(canvas,xc - x, yc - y, pixelSize);
        drawPixel(canvas,xc + y, yc + x, pixelSize);
        drawPixel(canvas,xc - y, yc + x, pixelSize);
        drawPixel(canvas,xc + y, yc - x, pixelSize);
        drawPixel(canvas,xc - y, yc - x, pixelSize);
    }
    private void drawPixel(BaseLayer canvas, int cx, int cy, int pixelSize) {
        canvas.paintPixel(cx * pixelSize, cy * pixelSize, Color.BLACK);
    }
}