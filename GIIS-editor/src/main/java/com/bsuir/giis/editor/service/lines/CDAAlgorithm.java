package com.bsuir.giis.editor.service.lines;

import com.bsuir.giis.editor.model.AlgorithmParameters;
import com.bsuir.giis.editor.model.lines.LinesParameters;
import com.bsuir.giis.editor.service.flow.Mode;
import com.bsuir.giis.editor.view.Canvas;

public class CDAAlgorithm implements StraightLineAlgorithm {

    @Override
    public void draw(Canvas canvas, AlgorithmParameters parameters, Mode mode) {
        LinesParameters linesParameters = (LinesParameters) parameters;

        int x1 = linesParameters.getStartPoint().getX();
        int y1 = linesParameters.getStartPoint().getY();
        int x2 = linesParameters.getEndPoint().getX();
        int y2 = linesParameters.getEndPoint().getY();

        int dx = x2 - x1;
        int dy = y2 - y1;

        int steps = Math.max(Math.abs(dx), Math.abs(dy));

        if (steps == 0) {

            canvas.paintPixel(x1, y1);
            return;
        }

        double xIncrement = (double) dx / steps;
        double yIncrement = (double) dy / steps;

        double x = x1;
        double y = y1;

        for (int i = 0; i <= steps; i++) {
            canvas.paintPixel((int) Math.round(x), (int) Math.round(y));

            x += xIncrement;
            y += yIncrement;
        }
    }
}