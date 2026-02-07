package com.bsuir.giis.editor.service.lines;

import com.bsuir.giis.editor.model.AlgorithmParameters;
import com.bsuir.giis.editor.model.lines.LinesParameters;
import com.bsuir.giis.editor.view.Canvas;
//TODO:something off
public class Antialiasing implements StraightLineAlgorithm {

    @Override
    public void draw(Canvas canvas, AlgorithmParameters parameters) {
        LinesParameters linesParameters = (LinesParameters) parameters;

        int x1 = linesParameters.getStartPoint().getX();
        int y1 = linesParameters.getStartPoint().getY();
        int x2 = linesParameters.getEndPoint().getX();
        int y2 = linesParameters.getEndPoint().getY();

        boolean steep = Math.abs(y2 - y1) > Math.abs(x2 - x1);

        // Если наклон слишком крутой, отражаем координаты относительно y=x
        if (steep) {
            int temp = x1;
            x1 = y1;
            y1 = temp;
            temp = x2;
            x2 = y2;
            y2 = temp;
        }
        if (x1 > x2) {
            int temp = x1;
            x1 = x2;
            x2 = temp;
            temp = y1;
            y1 = y2;
            y2 = temp;
        }

        drawPixel(canvas, x1, y1, 1.0, steep);
        drawPixel(canvas, x2, y2, 1.0, steep);

        double dx = x2 - x1;
        double dy = y2 - y1;
        double gradient = (dx == 0) ? 1.0 : dy / dx;

        double intery = y1 + gradient;

        for (int x = x1 + 1; x < x2; x++) {
            // Рисуем две точки по обе стороны от идеальной прямой
            drawPixel(canvas, x, (int) intery, 1 - (intery - (int) intery), steep);
            drawPixel(canvas, x, (int) intery + 1, intery - (int) intery, steep);
            intery += gradient;
        }
    }

    private void drawPixel(Canvas canvas, int x, int y, double brightness, boolean steep) {
        if (steep) {
            canvas.paintPixel(y, x, (int)brightness);
        } else {
            canvas.paintPixel(x, y, (int)brightness);
        }
    }
}