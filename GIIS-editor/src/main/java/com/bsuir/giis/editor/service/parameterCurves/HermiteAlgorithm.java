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
 * Алгоритм отрисовки кубической кривой Эрмита с использованием матричных преобразований.
 *
 * Матричная форма: P(t) = T × M_H × G
 * где:
 *   T = [t³ t² t 1] — вектор параметра
 *   M_H — базисная матрица Эрмита
 *   G = [P₀ P₁ V₀ V₁]ᵀ — геометрия (точки и касательные)
 */
public class HermiteAlgorithm implements ParameterCurveAlgorithm {

    private static final int STEPS = 100;
    private final StraightLineAlgorithm straightLineAlgorithm = new Antialiasing();
    private final HitTestPolicy hitTestPolicy = new HitTestPolicy();
    private final CurvesAlgorithm curvesAlgorithm = new CircleAlgorithm();

    /**
     * Базисная матрица кубической кривой Эрмита
     * M_H = |  2  -2   1   1 |
     *       | -3   3  -2  -1 |
     *       |  0   0   1   0 |
     *       |  1   0   0   0 |
     */
    private static final double[][] HERMITE_MATRIX = {
            {  2.0, -2.0,  1.0,  1.0 },
            { -3.0,  3.0, -2.0, -1.0 },
            {  0.0,  0.0,  1.0,  0.0 },
            {  1.0,  0.0,  0.0,  0.0 }
    };

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

        drawControlPolygon(canvas, pts, mode);
        drawHermiteCurve(canvas, pts, mode);
    }


    private void drawControlPolygon(BaseLayer canvas, Point[] pts, Mode mode) {
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

    private void drawHermiteCurve(BaseLayer canvas, Point[] pts, Mode mode) {
        double v0x = pts[2].getX() - pts[0].getX();
        double v0y = pts[2].getY() - pts[0].getY();
        double v1x = pts[3].getX() - pts[1].getX();
        double v1y = pts[3].getY() - pts[1].getY();


        double[][] coefficients = MatrixUtils.multiplyHermiteGeometry(
                HERMITE_MATRIX,
                pts[0], pts[1],
                v0x, v0y,
                v1x, v1y
        );

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


    private Point[] getPointsArray(AlgorithmParameters parameters) {
        List<Point> points = parameters.getPoints();
        if (points == null || points.size() < 4) return null;

        Point[] pts = new Point[4];
        pts[0] = points.get(0);
        pts[1] = points.get(1);
        pts[2] = points.get(2); // P₂ — ручка для P₀
        pts[3] = points.get(3); // P₃ — ручка для P₁
        return pts;
    }
}