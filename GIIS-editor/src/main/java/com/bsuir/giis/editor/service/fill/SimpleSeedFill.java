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
 * Простой алгоритм заполнения с затравкой (flood fill, 4-связность).
 *
 * Начиная от затравочной точки рекурсивно (через стек) распространяется
 * на все 4 соседних пикселя, пока не встретит границу (borderColor)
 * или уже закрашенный пиксель (fillColor).
 */
public class SimpleSeedFill implements FillTool {

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

        int[][] dirs = {{1, 0}, {-1, 0}, {0, 1}, {0, -1}};
        int counter = 0;

        while (!stack.isEmpty()) {
            int[] p = stack.pop();
            int x = p[0], y = p[1];

            if (x < 0 || x >= w || y < 0 || y >= h) continue;

            Color c = canvas.getRenderer().getPixelColor(canvas, x * pixelSize, y * pixelSize);
            if (c.equals(fillColor) || c.equals(borderColor)) continue;

            canvas.getRenderer().paintPixel(canvas, x * pixelSize, y * pixelSize, params.getFillColor());
            counter++;
            if (counter % 4 == 0) {
                canvas.repaint();
                mode.onStep(new FillStep(x, y, 1, 1),
                        "Simple Seed Fill(pixel " + counter + "): ");
            }

            for (int[] d : dirs) {
                stack.push(new int[]{x + d[0], y + d[1]});
            }
        }

        mode.onFinish();
    }
}