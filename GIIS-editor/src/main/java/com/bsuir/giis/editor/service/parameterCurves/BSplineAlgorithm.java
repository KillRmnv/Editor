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

public class BSplineAlgorithm implements ParameterCurveAlgorithm {

    private static final int STEPS = 100;
    private final StraightLineAlgorithm straightLineAlgorithm = new Antialiasing();
    private final HitTestPolicy hitTestPolicy = new HitTestPolicy();
    private final CurvesAlgorithm curvesAlgorithm = new CircleAlgorithm();

    @Override
    public void draw(BaseLayer canvas, AlgorithmParameters parameters, Mode mode) {
        Point[] controlPoints = preparePoints(parameters);
        if (controlPoints == null) return;

        drawBSplineCurve(canvas, controlPoints, mode);
    }

    @Override
    public void morph(BaseLayer canvas, AlgorithmParameters parameters, Mode mode) {
        Point[] controlPoints = preparePoints(parameters);
        if (controlPoints == null) return;

        // Рисуем "каркас" (контрольный полигон)
        drawControlPolygon(canvas, controlPoints, mode);
        // Рисуем саму зажатую кривую
        drawBSplineCurve(canvas, controlPoints, mode);
    }

    /**
     * Упорядочивает точки для корректной геометрии:
     * P0 (начало) -> P1 (опорная 1) -> P2 (опорная 2) -> P3 (конец)
     */
    private Point[] preparePoints(AlgorithmParameters parameters) {
        List<Point> points = parameters.getPoints();
        if (points == null || points.size() < 4) return null;

        return new Point[]{
                points.get(0), // Начало
                points.get(2), // Опорная 1 (из центра)
                points.get(3), // Опорная 2 (из центра)
                points.get(1)  // Конец
        };
    }

    private void drawBSplineCurve(BaseLayer canvas, Point[] controlPoints, Mode mode) {
        // Вычисляем самую первую точку (при t=0)
        Point prevPoint = calculateClampedBSplinePoint(0.0, controlPoints);

        for (int i = 1; i <= STEPS; i++) {
            double t = (double) i / STEPS;
            Point currentPoint = calculateClampedBSplinePoint(t, controlPoints);

            // Отрисовка сегмента через твой алгоритм прямой линии
            straightLineAlgorithm.draw(
                    canvas,
                    new PointShapeParameters(List.of(prevPoint, currentPoint)),
                    mode
            );

            prevPoint = currentPoint;
        }
    }

    /**
     * Вычисляет точку Clamped B-Spline.
     * Для 4-х точек это эквивалентно базису Безье (Bernstein Polynomials).
     */
    private Point calculateClampedBSplinePoint(double t, Point[] p) {
        double u = 1 - t;
        double tt = t * t;
        double uu = u * u;
        double uuu = uu * u;
        double ttt = tt * t;

        // Коэффициенты зажатого сплайна (они же Безье):
        // B0 = (1-t)³
        // B1 = 3t(1-t)²
        // B2 = 3t²(1-t)
        // B3 = t³
        double x = uuu * p[0].getX() +
                3 * uu * t * p[1].getX() +
                3 * u * tt * p[2].getX() +
                ttt * p[3].getX();

        double y = uuu * p[0].getY() +
                3 * uu * t * p[1].getY() +
                3 * u * tt * p[2].getY() +
                ttt * p[3].getY();

        return new Point((int) Math.round(x), (int) Math.round(y));
    }

    private void drawControlPolygon(BaseLayer canvas, Point[] pts, Mode mode) {

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

    }
}