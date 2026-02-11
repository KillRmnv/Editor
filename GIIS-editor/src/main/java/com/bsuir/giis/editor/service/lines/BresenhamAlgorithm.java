package com.bsuir.giis.editor.service.lines;

import com.bsuir.giis.editor.model.AlgorithmParameters;
import com.bsuir.giis.editor.model.lines.LinesParameters;
import com.bsuir.giis.editor.service.flow.Mode;
import com.bsuir.giis.editor.utils.PenStep;
import com.bsuir.giis.editor.view.BaseLayer;
import com.bsuir.giis.editor.view.Canvas;

import java.awt.*;

public class BresenhamAlgorithm implements StraightLineAlgorithm {
    @Override
    public void draw(BaseLayer canvas, AlgorithmParameters parameters, Mode mode) {
        LinesParameters linesParameters = (LinesParameters) parameters;

        int pixelSize = canvas.getPixelSize();

        // 2. Нормализуем координаты: переводим из экранных координат в логические (индексы сетки)
        int x1 = ((PenStep)linesParameters.getStartPoint()).getX() / pixelSize;
        int y1 = ((PenStep)linesParameters.getStartPoint()).getY() / pixelSize;
        int x2 = ((PenStep)linesParameters.getEndPoint()).getX() / pixelSize;
        int y2 = ((PenStep)linesParameters.getEndPoint()).getY() / pixelSize;

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
}