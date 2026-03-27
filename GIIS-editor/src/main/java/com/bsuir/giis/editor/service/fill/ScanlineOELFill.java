package com.bsuir.giis.editor.service.fill;

import com.bsuir.giis.editor.model.FillParameters;
import com.bsuir.giis.editor.model.FillTool;
import com.bsuir.giis.editor.model.Point;
import com.bsuir.giis.editor.rendering.BaseLayer;
import com.bsuir.giis.editor.service.flow.Mode;
import com.bsuir.giis.editor.utils.FillStep;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Алгоритм растровой развёртки с упорядоченным списком рёбер (OEL).
 * Для каждой строки Y перебирает все рёбра полигона, находит пересечения,
 * сортирует их и закрашивает пиксели между каждой парой.
 */
public class ScanlineOELFill implements FillTool {

    @Override
    public void fill(BaseLayer canvas, FillParameters params, Mode mode) {
        List<Point> vertices = params.getPolygonPoints();
        if (vertices == null || vertices.size() < 3) return;

        int pixelSize = canvas.getPixelSize();
        int n = vertices.size();

        int yMin = vertices.stream().mapToInt(p -> p.getY() / pixelSize).min().orElse(0);
        int yMax = vertices.stream().mapToInt(p -> p.getY() / pixelSize).max().orElse(0);

        for (int y = yMin; y <= yMax; y++) {
            List<Integer> intersections = new ArrayList<>();

            for (int i = 0; i < n; i++) {
                Point p1 = vertices.get(i);
                Point p2 = vertices.get((i + 1) % n);

                int y1 = p1.getY() / pixelSize, y2 = p2.getY() / pixelSize;
                int x1 = p1.getX() / pixelSize, x2 = p2.getX() / pixelSize;
                if (y1 == y2) continue; 
                
                if (y >= Math.min(y1, y2) && y < Math.max(y1, y2)) {
                    double x = x1
                            + (double)(y - y1) * (x2 - x1) / (y2 - y1);
                    intersections.add((int) Math.round(x));
                }
            }

            Collections.sort(intersections);

          
            for (int i = 0; i + 1 < intersections.size(); i += 2) {
                int xLeft  = intersections.get(i);
                int xRight = intersections.get(i + 1);
                for (int x = xLeft; x <= xRight; x++) {
                    canvas.getRenderer().paintPixel(canvas, x * pixelSize, y * pixelSize, params.getFillColor());
                }

                mode.onStep(new FillStep(xLeft, y, xRight - xLeft + 1, 1),
                        "OEL Fill(row segment): ");
                canvas.repaint();
            }
        }

        mode.onFinish();
    }
}