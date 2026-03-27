package com.bsuir.giis.editor.service.fill;

import com.bsuir.giis.editor.model.FillParameters;
import com.bsuir.giis.editor.model.FillTool;
import com.bsuir.giis.editor.model.Point;
import com.bsuir.giis.editor.rendering.BaseLayer;
import com.bsuir.giis.editor.service.flow.Mode;
import com.bsuir.giis.editor.utils.FillStep;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Алгоритм растровой развёртки с упорядоченным списком рёбер и
 * списком активных рёбер (AEL).
 *
 * Этапы:
 * 1. Строим таблицу рёбер (ET): корзина на каждый Y_min ребра.
 * 2. Проходим строки снизу вверх:
 *    - добавляем рёбра из ET в AEL, когда Y == Y_min ребра;
 *    - удаляем рёбра, чей Y_max достигнут;
 *    - сортируем AEL по текущей X;
 *    - закрашиваем попарно;
 *    - обновляем X каждого активного ребра (X += 1/slope).
 */
public class ScanlineAELFill implements FillTool {

    private static class Edge {
        int   yMax;  
        double x;   
        double dxDy; 

        Edge(int yMax, double xStart, double dxDy) {
            this.yMax = yMax;
            this.x    = xStart;
            this.dxDy = dxDy;
        }
    }

    @Override
    public void fill(BaseLayer canvas, FillParameters params, Mode mode) {
        List<Point> vertices = params.getPolygonPoints();
        if (vertices == null || vertices.size() < 3) return;

        int pixelSize = canvas.getPixelSize();
        int n = vertices.size();

        int yMin = vertices.stream().mapToInt(p -> p.getY() / pixelSize).min().orElse(0);
        int yMax = vertices.stream().mapToInt(p -> p.getY() / pixelSize).max().orElse(0);

        List<List<Edge>> ET = new ArrayList<>();
        for (int i = 0; i <= yMax - yMin; i++) ET.add(new ArrayList<>());

        for (int i = 0; i < n; i++) {
            Point p1 = vertices.get(i);
            Point p2 = vertices.get((i + 1) % n);

            int y1 = p1.getY() / pixelSize, y2 = p2.getY() / pixelSize;
            int x1 = p1.getX() / pixelSize, x2 = p2.getX() / pixelSize;
            if (y1 == y2) continue; 

            if (y1 > y2) { int tx = x1, ty = y1; x1 = x2; y1 = y2; x2 = tx; y2 = ty; }

            double dxDy = (double)(x2 - x1) / (y2 - y1);
            ET.get(y1 - yMin).add(new Edge(y2, x1, dxDy));
        }

        List<Edge> AEL = new ArrayList<>(); 

        for (int y = yMin; y <= yMax; y++) {
            AEL.addAll(ET.get(y - yMin));
            int copyY = y;
            AEL.removeIf(e -> e.yMax == copyY);

            AEL.sort(Comparator.comparingDouble(e -> e.x));

            for (int i = 0; i + 1 < AEL.size(); i += 2) {
                int xLeft  = (int) Math.round(AEL.get(i).x);
                int xRight = (int) Math.round(AEL.get(i + 1).x);
                for (int x = xLeft; x <= xRight; x++) {
                    canvas.getRenderer().paintPixel(canvas, x * pixelSize, y * pixelSize, params.getFillColor());
                }

                mode.onStep(new FillStep(xLeft, y, xRight - xLeft + 1, 1),
                        "AEL Fill(row segment): ");
                canvas.repaint();
            }

            for (Edge e : AEL) e.x += e.dxDy;
        }

        mode.onFinish();
    }
}