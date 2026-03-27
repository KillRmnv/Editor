package com.bsuir.giis.editor.service.fill;

import com.bsuir.giis.editor.model.FillParameters;
import com.bsuir.giis.editor.model.FillTool;
import com.bsuir.giis.editor.model.Point;
import com.bsuir.giis.editor.rendering.BaseLayer;
import com.bsuir.giis.editor.service.flow.Mode;
import com.bsuir.giis.editor.utils.FillStep;

import java.awt.Color;
import java.util.ArrayDeque;
import java.util.Deque;

/**
 * Построчный алгоритм заполнения с затравкой (scanline seed fill).
 *
 * Для каждой затравочной точки:
 * 1. Распространяемся влево и вправо по строке до границы, запоминаем [xLeft, xRight].
 * 2. В строках y+1 и y-1 сканируем отрезок [xLeft, xRight] и добавляем
 *    новые затравочные точки для незакрашенных промежутков.
 *
 * Эффективнее простого seed fill: на стек кладётся одна точка на промежуток,
 * а не на каждый пиксель.
 */
public class ScanlineSeedFill implements FillTool {

    @Override
    public void fill(BaseLayer canvas, FillParameters params, Mode mode) {
        Point seed = params.getSeedPoint();
        if (seed == null) return;

        Color fillColor   = params.getFillColor();
        Color borderColor = params.getBorderColor();

        int pixelSize = canvas.getPixelSize();
        int w = canvas.getLayerWidth();
        int h = canvas.getLayerHeight();

        int seedX = seed.getX() / pixelSize;
        int seedY = seed.getY() / pixelSize;

        Color startColor = canvas.getRenderer().getPixelColor(canvas, seedX * pixelSize, seedY * pixelSize);
        if (startColor.equals(fillColor) || startColor.equals(borderColor)) return;

        Deque<int[]> stack = new ArrayDeque<>();
        stack.push(new int[]{seedX, seedY});

        while (!stack.isEmpty()) {
            int[] p = stack.pop();
            int sx = p[0], sy = p[1];

            if (sy < 0 || sy >= h) continue;
            if (isFilled(canvas, sx, sy, fillColor, borderColor, pixelSize)) continue;

            int xLeft = sx;
            while (xLeft - 1 >= 0 && !isFilled(canvas, xLeft - 1, sy, fillColor, borderColor, pixelSize)) {
                xLeft--;
            }
            int xRight = sx;
            while (xRight + 1 < w && !isFilled(canvas, xRight + 1, sy, fillColor, borderColor, pixelSize)) {
                xRight++;
            }
            for (int x = xLeft; x <= xRight; x++) {
                canvas.getRenderer().paintPixel(canvas, x * pixelSize, sy * pixelSize, fillColor);
            }

            mode.onStep(new FillStep(xLeft, sy, xRight - xLeft + 1, 1),
                    "Scanline Seed Fill(span): ");
            canvas.repaint();

            scanForSeeds(canvas, stack, xLeft, xRight, sy - 1, fillColor, borderColor, w, h, pixelSize);
            scanForSeeds(canvas, stack, xLeft, xRight, sy + 1, fillColor, borderColor, w, h, pixelSize);
        }

        mode.onFinish();
    }

    private void scanForSeeds(BaseLayer canvas, Deque<int[]> stack,
                              int xLeft, int xRight, int y,
                              Color fillColor, Color borderColor,
                              int w, int h, int pixelSize) {
        if (y < 0 || y >= h) return;

        boolean insideSpan = false;
        for (int x = xLeft; x <= xRight; x++) {
            if (!isFilled(canvas, x, y, fillColor, borderColor, pixelSize)) {
                if (!insideSpan) {
                    stack.push(new int[]{x, y}); 
                    insideSpan = true;
                }
            } else {
                insideSpan = false;
            }
        }
    }

    private boolean isFilled(BaseLayer canvas, int x, int y,
                             Color fillColor, Color borderColor, int pixelSize) {
        Color c = canvas.getRenderer().getPixelColor(canvas, x * pixelSize, y * pixelSize);
        return c.equals(fillColor) || c.equals(borderColor);
    }
}