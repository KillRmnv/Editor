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

public class BezierAlgorithm implements ParameterCurveAlgorithm {

    private static final int STEPS = 100; // 100 шагов обычно достаточно для гладкости
    private final StraightLineAlgorithm straightLineAlgorithm = new Antialiasing(); // Используем Antialiasing для красоты
    private final HitTestPolicy hitTestPolicy = new HitTestPolicy();
    private final CurvesAlgorithm curvesAlgorithm = new CircleAlgorithm();
    @Override
    public void draw(BaseLayer canvas, AlgorithmParameters parameters, Mode mode) {
        Point[] controlPoints = preparePoints(parameters);
        if (controlPoints == null) return;

        drawBezierCurve(canvas, controlPoints, mode);
    }

    @Override
    public void morph(BaseLayer canvas, AlgorithmParameters parameters, Mode mode) {
        Point[] controlPoints = preparePoints(parameters);
        if (controlPoints == null) return;

        // Рисуем каркас (полигон)
        drawControlPolygon(canvas, controlPoints, mode);
        // Рисуем кривую
        drawBezierCurve(canvas, controlPoints, mode);
    }


    private Point[] preparePoints(AlgorithmParameters parameters) {
        List<Point> pointsList = parameters.getPoints();
        if (pointsList == null || pointsList.size() < 4) return null;

        return new Point[]{
                pointsList.get(0), // Начало (P0)
                pointsList.get(2), // Опорная 1 (P1)
                pointsList.get(3), // Опорная 2 (P2)
                pointsList.get(1)  // Конец (P3)
        };
    }

    private void drawBezierCurve(BaseLayer canvas, Point[] controlPoints, Mode mode) {
        Point prevPoint = calculateBezierPoint(0.0, controlPoints);

        for (int i = 1; i <= STEPS; i++) {
            double t = (double) i / STEPS;
            Point currentPoint = calculateBezierPoint(t, controlPoints);

            straightLineAlgorithm.draw(
                    canvas,
                    new PointShapeParameters(List.of(prevPoint, currentPoint)),
                    mode
            );
            prevPoint = currentPoint;
        }
    }

    /**
     * Оптимизированный расчет кубической кривой Безье
     */
    private Point calculateBezierPoint(double t, Point[] p) {
        double u = 1 - t;
        double tt = t * t;
        double uu = u * u;
        double uuu = uu * u;
        double ttt = tt * t;

        // Явные формулы полиномов Бернштейна для n=3
        double x = uuu * p[0].getX() +             // (1-t)^3 * P0
                3 * uu * t * p[1].getX() +      // 3t(1-t)^2 * P1
                3 * u * tt * p[2].getX() +      // 3t^2(1-t) * P2
                ttt * p[3].getX();              // t^3 * P3

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