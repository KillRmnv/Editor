package com.bsuir.giis.editor.service.triangulation;

import com.bsuir.giis.editor.model.AlgorithmParameters;
import com.bsuir.giis.editor.model.Point;
import com.bsuir.giis.editor.model.PointShapeParameters;
import com.bsuir.giis.editor.rendering.BaseLayer;
import com.bsuir.giis.editor.service.flow.Mode;
import com.bsuir.giis.editor.service.lines.BresenhamAlgorithm;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Диаграмма Вороного, построенная двойственно к триангуляции Делоне.
 *
 * Алгоритм:
 * 1. Строим триангуляцию Делоне (Боуэр-Уотсон).
 * 2. Для каждого ребра триангуляции (AB) находим два треугольника, его
 *    содержащих.
 * 3. Центры описанных окружностей этих двух треугольников — концы
 *    ребра диаграммы Вороного, двойственного ребру AB.
 * 4. Соединяем эти центры — получаем рёбра Вороного.
 */
public class VoronoiAlgorithm implements TriangulationAlgorithm {

    private final BresenhamAlgorithm lineAlgorithm = new BresenhamAlgorithm();
    private final DeloneAlgorithm delaunay = new DeloneAlgorithm();

    @Override
    public void draw(BaseLayer canvas, AlgorithmParameters parameters, Mode mode) {
        PointShapeParameters params = (PointShapeParameters) parameters;
        List<Point> pts = params.getPoints();
        if (pts.size() < 3) return;

        int pixelSize = canvas.getPixelSize();

        List<double[]> points = new ArrayList<>();
        for (Point p : pts) points.add(new double[]{p.getX(), p.getY()});

        List<DeloneAlgorithm.Triangle> triangles =
            delaunay.bowyerWatson(points,
                    canvas.getWidth() , canvas.getHeight());

        Set<String> drawnEdges = new java.util.HashSet<>();

        for (int i = 0; i < triangles.size(); i++) {
            DeloneAlgorithm.Triangle ti = triangles.get(i);
            double[] ci = circumcenter(ti);
            if (ci == null) continue;

            for (int j = i + 1; j < triangles.size(); j++) {
                DeloneAlgorithm.Triangle tj = triangles.get(j);

                if (!shareEdge(ti, tj)) continue;

                double[] cj = circumcenter(tj);
                if (cj == null) continue;

                String key = i + "-" + j;
                if (drawnEdges.contains(key)) continue;
                drawnEdges.add(key);

                drawClipped(canvas, ci, cj);
            }
        }

        for (double[] p : points) {
            int x = (int) Math.round(p[0]);
            int y = (int) Math.round(p[1]);
            for (int dx = -2; dx <= 2; dx++)
                for (int dy = -2; dy <= 2; dy++)
                     canvas.getRenderer().paintPixel(canvas,x + dx, y + dy, Color.RED);
        }

        canvas.repaint();
        mode.onFinish();
    }


    /**
     * Вычисляет центр описанной окружности по формуле через
     * перпендикулярные биссектрисы двух сторон.
     * Возвращает null, если треугольник вырожденный.
     */
    private double[] circumcenter(DeloneAlgorithm.Triangle t) {
        double ax = t.ax[0], ay = t.ax[1];
        double bx = t.bx[0], by = t.bx[1];
        double cx = t.cx[0], cy = t.cx[1];

        double D = 2 * (ax * (by - cy) + bx * (cy - ay) + cx * (ay - by));
        if (Math.abs(D) < 1e-10) return null; // вырожденный

        double ux = ((ax*ax + ay*ay) * (by - cy)
                   + (bx*bx + by*by) * (cy - ay)
                   + (cx*cx + cy*cy) * (ay - by)) / D;

        double uy = ((ax*ax + ay*ay) * (cx - bx)
                   + (bx*bx + by*by) * (ax - cx)
                   + (cx*cx + cy*cy) * (bx - ax)) / D;

        return new double[]{ux, uy};
    }


    private boolean shareEdge(DeloneAlgorithm.Triangle a, DeloneAlgorithm.Triangle b) {
        int shared = 0;
        double[][] va = {a.ax, a.bx, a.cx};
        double[][] vb = {b.ax, b.bx, b.cx};
        for (double[] pa : va)
            for (double[] pb : vb)
                if (samePoint(pa, pb)) shared++;
        return shared >= 2; 
    }

    private boolean samePoint(double[] a, double[] b) {
        return Math.abs(a[0] - b[0]) < 1e-9 && Math.abs(a[1] - b[1]) < 1e-9;
    }
    private void drawClipped(BaseLayer canvas, double[] c1, double[] c2) {
        int w = canvas.getWidth();
        int h = canvas.getHeight();
    
        
        double x1 = Math.max(0, Math.min(w - 1, c1[0]));
        double y1 = Math.max(0, Math.min(h - 1, c1[1]));
        double x2 = Math.max(0, Math.min(w - 1, c2[0]));
        double y2 = Math.max(0, Math.min(h - 1, c2[1]));
    
        lineAlgorithm.drawLine(canvas,
            (int) Math.round(x1), (int) Math.round(y1),
            (int) Math.round(x2), (int) Math.round(y2),
            Color.BLUE);
    }
}
