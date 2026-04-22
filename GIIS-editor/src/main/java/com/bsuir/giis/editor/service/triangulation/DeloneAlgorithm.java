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

/**
 * Триангуляция Делоне методом Боуэра-Уотсона.
 *
 * Алгоритм:
 * 1. Создаём «супер-треугольник», охватывающий все точки.
 * 2. Для каждой новой точки находим все треугольники, чья описанная
 *    окружность содержит эту точку («плохие» треугольники).
 * 3. Из рёбер плохих треугольников формируем «полигональную дыру»
 *    (рёбра, которые встречаются только один раз).
 * 4. Соединяем новую точку с рёбрами дыры — получаем новые треугольники.
 * 5. В конце удаляем треугольники, имеющие вершины супер-треугольника.
 */
public class DeloneAlgorithm implements TriangulationAlgorithm {

    private final BresenhamAlgorithm lineAlgorithm = new BresenhamAlgorithm();

    static class Triangle {
        double[] ax, bx, cx; 

        Triangle(double[] a, double[] b, double[] c) {
            ax = a; bx = b; cx = c;
        }

        /**
         * Проверяет, лежит ли точка (px, py) строго внутри описанной окружности.
         * Использует детерминант 3×3.
         */
        boolean inCircumcircle(double px, double py) {
            double ax_ = ax[0] - px, ay_ = ax[1] - py;
            double bx_ = bx[0] - px, by_ = bx[1] - py;
            double cx_ = cx[0] - px, cy_ = cx[1] - py;

            double det = ax_ * (by_ * (cx_*cx_ + cy_*cy_) - cy_ * (bx_*bx_ + by_*by_))
                       - ay_ * (bx_ * (cx_*cx_ + cy_*cy_) - cx_ * (bx_*bx_ + by_*by_))
                       + (ax_*ax_ + ay_*ay_) * (bx_ * cy_ - by_ * cx_);
            return det > 0;
        }

        double[][][] edges() {
            return new double[][][]{{ax, bx}, {bx, cx}, {cx, ax}};
        }
    }


    @Override
    public void draw(BaseLayer canvas, AlgorithmParameters parameters, Mode mode) {
        PointShapeParameters params = (PointShapeParameters) parameters;
        List<Point> pts = params.getPoints();
        if (pts.size() < 3) return;

        int pixelSize = canvas.getPixelSize();

        List<double[]> points = new ArrayList<>();
        for (Point p : pts) points.add(new double[]{p.getX(), p.getY()});

        List<Triangle> triangles = bowyerWatson(points,
                canvas.getWidth(), canvas.getHeight() );

        for (Triangle t : triangles) {
            drawTriangle(canvas, t);
        }

        canvas.repaint();
        mode.onFinish();
    }


    List<Triangle> bowyerWatson(List<double[]> points, int w, int h) {
        List<Triangle> triangulation = new ArrayList<>();

        double margin = Math.max(w, h) * 10.0;
        double[] sA = {w / 2.0,          -margin      };
        double[] sB = {w / 2.0 + margin,  h + margin  };
        double[] sC = {w / 2.0 - margin,  h + margin  };
        triangulation.add(new Triangle(sA, sB, sC));

        for (double[] point : points) {
            double px = point[0], py = point[1];

            List<Triangle> bad = new ArrayList<>();
            for (Triangle t : triangulation) {
                if (t.inCircumcircle(px, py)) bad.add(t);
            }

            List<double[][]> boundary = new ArrayList<>();
            for (Triangle t : bad) {
                for (double[][] edge : t.edges()) {
                    boolean shared = false;
                    for (Triangle other : bad) {
                        if (other == t) continue;
                        for (double[][] otherEdge : other.edges()) {
                            if (edgesEqual(edge, otherEdge)) { shared = true; break; }
                        }
                        if (shared) break;
                    }
                    if (!shared) boundary.add(edge);
                }
            }

            triangulation.removeAll(bad);

            for (double[][] edge : boundary) {
                triangulation.add(new Triangle(edge[0], edge[1], new double[]{px, py}));
            }
        }

        triangulation.removeIf(t ->
            samePoint(t.ax, sA) || samePoint(t.ax, sB) || samePoint(t.ax, sC) ||
            samePoint(t.bx, sA) || samePoint(t.bx, sB) || samePoint(t.bx, sC) ||
            samePoint(t.cx, sA) || samePoint(t.cx, sB) || samePoint(t.cx, sC)
        );

        return triangulation;
    }


    private void drawTriangle(BaseLayer canvas, Triangle t) {
        drawEdge(canvas, t.ax, t.bx);
        drawEdge(canvas, t.bx, t.cx);
        drawEdge(canvas, t.cx, t.ax);
    }

    private void drawEdge(BaseLayer canvas, double[] a, double[] b) {
        lineAlgorithm.drawLine(canvas,
            (int) Math.round(a[0]), (int) Math.round(a[1]),
            (int) Math.round(b[0]), (int) Math.round(b[1]),
            Color.BLACK);
    }

    private boolean edgesEqual(double[][] e1, double[][] e2) {
        return (samePoint(e1[0], e2[0]) && samePoint(e1[1], e2[1]))
            || (samePoint(e1[0], e2[1]) && samePoint(e1[1], e2[0]));
    }

    private boolean samePoint(double[] a, double[] b) {
        return Math.abs(a[0] - b[0]) < 1e-9 && Math.abs(a[1] - b[1]) < 1e-9;
    }
}