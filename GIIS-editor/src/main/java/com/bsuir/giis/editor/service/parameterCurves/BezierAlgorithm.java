package com.bsuir.giis.editor.service.parameterCurves;

import com.bsuir.giis.editor.model.AlgorithmParameters;
import com.bsuir.giis.editor.model.Point;
import com.bsuir.giis.editor.model.PointShapeParameters;
import com.bsuir.giis.editor.rendering.BaseLayer;
import com.bsuir.giis.editor.service.curves.CircleAlgorithm;
import com.bsuir.giis.editor.service.curves.CurvesAlgorithm;
import com.bsuir.giis.editor.service.flow.HitTestPolicy;
import com.bsuir.giis.editor.service.flow.Mode;
import com.bsuir.giis.editor.service.lines.Antialiasing;
import com.bsuir.giis.editor.service.lines.StraightLineAlgorithm;
import com.bsuir.giis.editor.utils.MatrixUtils;

import java.util.List;

/**
 * Алгоритм отрисовки кубической кривой Безье с использованием матричных преобразований.
 *
 * Матричная форма: P(t) = T × M_B × G
 * где:
 *   T = [t³ t² t 1] — вектор параметра
 *   M_B — базисная матрица Безье
 *   G = [P₀ P₁ P₂ P₃]ᵀ — контрольные точки
 */
public class BezierAlgorithm implements ParameterCurveAlgorithm {

    private static final int STEPS = 100;
    private final StraightLineAlgorithm straightLineAlgorithm = new Antialiasing();
    private final HitTestPolicy hitTestPolicy = new HitTestPolicy();
    private final CurvesAlgorithm curvesAlgorithm = new CircleAlgorithm();

    /**
     * Базисная матрица кубической кривой Безье
     * M_B = | -1  3 -3  1 |
     *       |  3 -6  3  0 |
     *       | -3  3  0  0 |
     *       |  1  0  0  0 |
     */
    private static final double[][] BEZIER_MATRIX = {
            { -1.0,  3.0, -3.0,  1.0 },
            {  3.0, -6.0,  3.0,  0.0 },
            { -3.0,  3.0,  0.0,  0.0 },
            {  1.0,  0.0,  0.0,  0.0 }
    };

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

        drawControlPolygon(canvas, controlPoints, mode);
        drawBezierCurve(canvas, controlPoints, mode);
    }


    private Point[] preparePoints(AlgorithmParameters parameters) {
        List<Point> pointsList = parameters.getPoints();
        if (pointsList == null || pointsList.size() < 4) return null;

        return new Point[] {
                pointsList.get(0),
                pointsList.get(2),
                pointsList.get(3),
                pointsList.get(1)
        };
    }


    private void drawBezierCurve(BaseLayer canvas, Point[] controlPoints, Mode mode) {
        // Вычисляем коэффициенты: C = M_B × G
        double[][] coefficients = MatrixUtils.multiplyPoints(BEZIER_MATRIX, controlPoints);

        Point prevPoint = MatrixUtils.evaluateCubicCurve(coefficients, 0.0);

        for (int i = 1; i <= STEPS; i++) {
            double t = (double) i / STEPS;
            Point currentPoint = MatrixUtils.evaluateCubicCurve(coefficients, t);

            straightLineAlgorithm.draw(
                    canvas,
                    new PointShapeParameters(List.of(prevPoint, currentPoint)),
                    mode
            );

            prevPoint = currentPoint;
        }
    }


    private void drawControlPolygon(BaseLayer canvas, Point[] pts, Mode mode) {
        if (pts == null) return;

        // Линия от начала (P0) к первой контрольной точке (P1/ручка)
        straightLineAlgorithm.draw(canvas, new PointShapeParameters(List.of(pts[0], pts[1])), mode);
        int radius = hitTestPolicy.calculateTolerance(canvas.getPixelSize());
        curvesAlgorithm.draw(canvas, new PointShapeParameters(List.of(pts[0],
                new Point(pts[0].getX() + radius, pts[0].getY() + radius))), mode);
        curvesAlgorithm.draw(canvas, new PointShapeParameters(List.of(pts[1],
                new Point(pts[1].getX() + radius, pts[1].getY() + radius))), mode);

        // Линия от конца (P3) ко второй контрольной точке (P2/ручка)
        straightLineAlgorithm.draw(canvas, new PointShapeParameters(List.of(pts[3], pts[2])), mode);
        curvesAlgorithm.draw(canvas, new PointShapeParameters(List.of(pts[3],
                new Point(pts[3].getX() + radius, pts[3].getY() + radius))), mode);
        curvesAlgorithm.draw(canvas, new PointShapeParameters(List.of(pts[2],
                new Point(pts[2].getX() + radius, pts[2].getY() + radius))), mode);
    }
}