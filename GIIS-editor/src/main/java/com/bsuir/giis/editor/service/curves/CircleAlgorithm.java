package com.bsuir.giis.editor.service.curves;

import com.bsuir.giis.editor.model.AlgorithmParameters;
import com.bsuir.giis.editor.model.Point;
import com.bsuir.giis.editor.model.curves.CurvesParameters;
import com.bsuir.giis.editor.service.flow.Mode;
import com.bsuir.giis.editor.utils.MultiStep;
import com.bsuir.giis.editor.utils.PenStep;
import com.bsuir.giis.editor.view.BaseLayer;

import java.awt.*;
import java.lang.reflect.InvocationTargetException;
//TODO:add pixel size approximation
public class CircleAlgorithm implements CurvesAlgorithm {
    @Override
    public void draw(BaseLayer canvas, AlgorithmParameters parameters, Mode mode) {
        CurvesParameters curvesParameters = (CurvesParameters) parameters;
        Point center = curvesParameters.getPoint1();
        Point point = curvesParameters.getPoint2();

        if (center == null || point == null) {
            return;
        }
        int pixelSize=canvas.getPixelSize();
        int xc = center.getX() / pixelSize;
        int yc = center.getY() / pixelSize;
        int radius = (int) Math.sqrt(Math.pow((point.getX()/pixelSize) - xc, 2) +
                                    Math.pow((point.getY()/pixelSize) - yc, 2));
        
        // Алгоритм построения окружности
        int x = 0;
        int y = radius;
        int d = 3 - 2 * radius;
        
        drawCirclePoints(canvas, xc, yc, x, y,pixelSize);
        try {
            mode.onStep(new MultiStep(2, PenStep.class),"Circle(center): ");
        } catch (NoSuchMethodException | InvocationTargetException | InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
        while (x <= y) {
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
            } catch (NoSuchMethodException | InvocationTargetException | InstantiationException | IllegalAccessException e) {
                throw new RuntimeException(e);
            }

        }
        mode.onFinish();
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
        // Умножаем на pixelSize, чтобы вернуть экранные координаты для paintPixel
        canvas.paintPixel(cx * pixelSize, cy * pixelSize, Color.BLACK);
    }
}