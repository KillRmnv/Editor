package com.bsuir.giis.editor.service.lines;

import com.bsuir.giis.editor.model.AlgorithmParameters;
import com.bsuir.giis.editor.model.lines.LinesParameters;
import com.bsuir.giis.editor.service.flow.Mode;
import com.bsuir.giis.editor.view.Canvas;

public class Antialiasing implements StraightLineAlgorithm {

    @Override
    public void draw(Canvas canvas, AlgorithmParameters parameters, Mode mode) {
        LinesParameters linesParameters = (LinesParameters) parameters;

        int pixelSize = 1;

        int x0 = linesParameters.getStartPoint().getX() / pixelSize;
        int y0 = linesParameters.getStartPoint().getY() / pixelSize;
        int x1 = linesParameters.getEndPoint().getX() / pixelSize;
        int y1 = linesParameters.getEndPoint().getY() / pixelSize;

        boolean steep = absolute(y1 - y0) > absolute(x1 - x0);

        if (steep) {
            int temp = x0; x0 = y0; y0 = temp;
            temp = x1; x1 = y1; y1 = temp;
        }
        if (x0 > x1) {
            int temp = x0; x0 = x1; x1 = temp;
            temp = y0; y0 = y1; y1 = temp;
        }

        float dx = x1 - x0;
        float dy = y1 - y0;
        float gradient = dy / dx;

        if (dx == 0.0f) {
            gradient = 1.0f;
        }

        float intersectY = y0 + gradient;

        drawPoint(canvas, steep, x0, y0, 1.0f, pixelSize);
        drawPoint(canvas, steep, x1, y1, 1.0f, pixelSize);

        if (steep) {
            for (int x = x0 + 1; x < x1; x++) {
                drawPoint(canvas, true, x, iPartOfNumber(intersectY), rfPartOfNumber(intersectY), pixelSize);
                drawPoint(canvas, true, x, iPartOfNumber(intersectY) + 1, fPartOfNumber(intersectY), pixelSize);
                intersectY += gradient;
            }
        } else {
            for (int x = x0 + 1; x < x1; x++) {
                drawPoint(canvas, false, x, iPartOfNumber(intersectY), rfPartOfNumber(intersectY), pixelSize);
                drawPoint(canvas, false, x, iPartOfNumber(intersectY) + 1, fPartOfNumber(intersectY), pixelSize);
                intersectY += gradient;
            }
        }
    }


    private void drawPoint(Canvas canvas, boolean steep, int x, int y, float brightness, int pixelSize) {
        int drawX = steep ? y : x;
        int drawY = steep ? x : y;

        canvas.paintPixel(drawX * pixelSize, drawY * pixelSize, (int)(brightness * 255));
    }

    public float absolute(float x) {
        return (x < 0) ? -x : x;
    }

    public int iPartOfNumber(float x) {
        return (int) x;
    }

    public float fPartOfNumber(float x) {
        if (x > 0) return x - iPartOfNumber(x);
        else return x - (iPartOfNumber(x) + 1);
    }

    public float rfPartOfNumber(float x) {
        return 1 - fPartOfNumber(x);
    }
}