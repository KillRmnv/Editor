package com.bsuir.giis.editor.service.parameterCurves;

import com.bsuir.giis.editor.model.AlgorithmParameters;
import com.bsuir.giis.editor.model.Point;
import com.bsuir.giis.editor.model.PointShapeParameters;
import com.bsuir.giis.editor.service.curves.CircleAlgorithm;
import com.bsuir.giis.editor.service.curves.CurvesAlgorithm;
import com.bsuir.giis.editor.service.flow.HitTestPolicy;
import com.bsuir.giis.editor.service.flow.Mode;
import com.bsuir.giis.editor.service.lines.Antialiasing;
import com.bsuir.giis.editor.service.lines.StraightLineAlgorithm;
import com.bsuir.giis.editor.view.BaseLayer;

import java.util.List;

public class HermiteAlgorithm implements ParameterCurveAlgorithm {

    private static final int STEPS = 100;
    private final StraightLineAlgorithm straightLineAlgorithm = new Antialiasing();
    private final HitTestPolicy hitTestPolicy = new HitTestPolicy();
    private final CurvesAlgorithm curvesAlgorithm = new CircleAlgorithm();

    @Override
    public void draw(BaseLayer canvas, AlgorithmParameters parameters, Mode mode) {
        Point[] pts = getPointsArray(parameters);
        if (pts == null) return;

        drawHermiteCurve(canvas, pts, mode);
    }

    @Override
    public void morph(BaseLayer canvas, AlgorithmParameters parameters, Mode mode) {
        Point[] pts = getPointsArray(parameters);
        if (pts == null) return;



        // 1. Отрисовываем касательные векторы (ручки управления)
        // Линия от начала (P0) к контрольной точке вектора (P2)
        straightLineAlgorithm.draw(canvas, new PointShapeParameters(List.of(pts[0], pts[2])), mode);
        int radius = hitTestPolicy.calculateTolerance(canvas.getPixelSize());
        curvesAlgorithm.draw(canvas, new PointShapeParameters(List.of(pts[0],
                new Point(pts[0].getX() + radius, pts[0].getY() + radius))), mode);
        curvesAlgorithm.draw(canvas, new PointShapeParameters(List.of(pts[2],
                new Point(pts[2].getX() + radius, pts[2].getY() + radius))), mode);


        // Линия от конца (P1) к контрольной точке вектора (P3)
        straightLineAlgorithm.draw(canvas, new PointShapeParameters(List.of(pts[1], pts[3])), mode);
        curvesAlgorithm.draw(canvas, new PointShapeParameters(List.of(pts[1],
                new Point(pts[1].getX() + radius, pts[1].getY() + radius))), mode);
        curvesAlgorithm.draw(canvas, new PointShapeParameters(List.of(pts[3],
                new Point(pts[3].getX() + radius, pts[3].getY() + radius))), mode);

        drawHermiteCurve(canvas, pts, mode);
    }

    private void drawHermiteCurve(BaseLayer canvas, Point[] pts, Mode mode) {
        Point p0 = pts[0]; // Начало
        Point p1 = pts[1]; // Конец
        Point p2 = pts[2]; // Точка, задающая вектор начала
        Point p3 = pts[3]; // Точка, задающая вектор конца

        // Касательные векторы
        double v0x = p2.getX() - p0.getX();
        double v0y = p2.getY() - p0.getY();
        double v1x = p3.getX() - p1.getX();
        double v1y = p3.getY() - p1.getY();

        Point prevPoint = calculateHermitePoint(0.0, p0, p1, v0x, v0y, v1x, v1y);

        for (int i = 1; i <= STEPS; i++) {
            double t = (double) i / STEPS;
            Point currentPoint = calculateHermitePoint(t, p0, p1, v0x, v0y, v1x, v1y);

            // Используем твой алгоритм для отрисовки сегмента
            straightLineAlgorithm.draw(
                    canvas,
                    new PointShapeParameters(List.of(prevPoint, currentPoint)),
                    mode
            );

            prevPoint = currentPoint;
        }
    }

    private Point calculateHermitePoint(double t, Point p0, Point p1,
                                        double v0x, double v0y, double v1x, double v1y) {
        double t2 = t * t;
        double t3 = t2 * t;

        // Базисные функции Эрмита
        double h00 = 2 * t3 - 3 * t2 + 1;
        double h10 = t3 - 2 * t2 + t;
        double h01 = -2 * t3 + 3 * t2;
        double h11 = t3 - t2;

        double x = h00 * p0.getX() + h10 * v0x + h01 * p1.getX() + h11 * v1x;
        double y = h00 * p0.getY() + h10 * v0y + h01 * p1.getY() + h11 * v1y;

        return new Point((int) Math.round(x), (int) Math.round(y));
    }

    private Point[] getPointsArray(AlgorithmParameters parameters) {
        List<Point> points = parameters.getPoints();
        if (points == null || points.size() < 4) return null;

        Point[] pts = new Point[4];
        pts[0] = points.get(0); // P0
        pts[1] = points.get(1); // P1
        pts[2] = points.get(2); // P2 (Handle for P0)
        pts[3] = points.get(3); // P3 (Handle for P1)
        return pts;
    }
}