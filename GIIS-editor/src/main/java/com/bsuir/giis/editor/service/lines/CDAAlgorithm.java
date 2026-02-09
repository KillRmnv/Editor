package com.bsuir.giis.editor.service.lines;

import com.bsuir.giis.editor.model.AlgorithmParameters;
import com.bsuir.giis.editor.model.lines.LinesParameters;
import com.bsuir.giis.editor.service.flow.Mode;
import com.bsuir.giis.editor.utils.PenStep;
import com.bsuir.giis.editor.view.BaseLayer;
import com.bsuir.giis.editor.view.Canvas;

import java.awt.*;

public class CDAAlgorithm implements StraightLineAlgorithm {

    @Override
    public void draw(BaseLayer canvas, AlgorithmParameters parameters, Mode mode) {
        LinesParameters linesParameters = (LinesParameters) parameters;

        int pixelSize = canvas.getPixelSize();

        // 2. Нормализуем координаты: переводим из экранных координат в логические (индексы сетки)
        int x1 = linesParameters.getStartPoint().getX() / pixelSize;
        int y1 = linesParameters.getStartPoint().getY() / pixelSize;
        int x2 = linesParameters.getEndPoint().getX() / pixelSize;
        int y2 = linesParameters.getEndPoint().getY() / pixelSize;

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
}