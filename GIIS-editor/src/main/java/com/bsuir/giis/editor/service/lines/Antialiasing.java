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

public class Antialiasing implements StraightLineAlgorithm {

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

        boolean steep = Math.abs(y2 - y1) > Math.abs(x2 - x1);

        if (steep) {
            int temp = x1; x1 = y1; y1 = temp;
            temp = x2; x2 = y2; y2 = temp;
        }
        if (x1 > x2) {
            int temp = x1; x1 = x2; x2 = temp;
            temp = y1; y1 = y2; y2 = temp;
        }

        double dx = x2 - x1;
        double dy = y2 - y1;
        double gradient = (dx == 0) ? 1.0 : dy / dx;

        double intery = y1 + gradient;


        drawPixel(canvas, x1, y1, 1.0, steep, pixelSize);
        mode.onStep(new PenStep(x1,y1,255),"Wu Algorithm(start point): ");
        drawPixel(canvas, x2, y2, 1.0, steep, pixelSize);
        mode.onStep(new PenStep(x2,y2,255),"Wu Algorithm(end point): ");


        for (int x = x1 + 1; x < x2; x++) {
            // Рисуем основную точку (яркость = перевернутая дробная часть)
            drawPixel(canvas, x, (int) intery, 1.0 - (intery - (int) intery), steep, pixelSize);
            mode.onStep(new PenStep(x,(int)intery,(int)(1.0 - (intery - (int) intery)*255)),"Wu Algorithm(main point): ");
            // Рисуем соседнюю точку (яркость = дробная часть)
            drawPixel(canvas, x, (int) intery + 1, intery - (int) intery, steep, pixelSize);
            mode.onStep(new PenStep(x, (int) intery + 1,(int)(intery - (int) intery)),"Wu Algorithm(neighbor point): ");
            intery += gradient;
        }
        mode.onFinish();
    }

    private void drawPixel(BaseLayer canvas, int x, int y, double brightness, boolean steep, int pixelSize) {
        int alpha = (int) (brightness * 255);

        int screenX = x * pixelSize;
        int screenY = y * pixelSize;
        Color color=new Color(0,0,0,alpha);
        if (steep) {

            canvas.paintPixel(screenY, screenX, color);
        } else {
            canvas.paintPixel(screenX, screenY, color);
        }
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