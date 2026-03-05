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
import com.bsuir.giis.editor.utils.MatrixUtils;
import com.bsuir.giis.editor.view.BaseLayer;

import java.util.List;

/**
 * Алгоритм отрисовки кубического равномерного B-сплайна с использованием матричных преобразований.
 *
 * Матричная форма: P(t) = T × M_B × G
 * где:
 *   T = [t³ t² t 1] — вектор параметра
 *   M_B — базисная матрица B-сплайна
 *   G = [P₀ P₁ P₂ P₃]ᵀ — контрольные точки
 */
public class BSplineAlgorithm implements ParameterCurveAlgorithm {

    private static final int STEPS = 100;
    private final StraightLineAlgorithm straightLineAlgorithm = new Antialiasing();
    private final HitTestPolicy hitTestPolicy = new HitTestPolicy();
    private final CurvesAlgorithm curvesAlgorithm = new CircleAlgorithm();

    /**
     * Базисная матрица кубического равномерного B-сплайна
     * M_B = 1/6 × | -1  3 -3  1 |
     *             |  3 -6  3  0 |
     *             | -3  0  3  0 |
     *             |  1  4  1  0 |
     */
    private static final double[][] BSPLINE_MATRIX = {
            { -1.0 / 6.0,  3.0 / 6.0, -3.0 / 6.0,  1.0 / 6.0 },
            {  3.0 / 6.0, -6.0 / 6.0,  3.0 / 6.0,  0.0       },
            { -3.0 / 6.0,  0.0,       3.0 / 6.0,  0.0       },
            {  1.0 / 6.0,  4.0 / 6.0,  1.0 / 6.0,  0.0       }
    };

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

        drawControlPolygon(canvas, controlPoints, mode);
        drawBSplineCurve(canvas, controlPoints, mode);
    }

    /**
     * Подготавливает массив из 4 контрольных точек
     */
    private Point[] preparePoints(AlgorithmParameters parameters) {
        List<Point> points = parameters.getPoints();
        if (points == null || points.size() < 4) return null;

        return new Point[] {
                points.get(0),
                points.get(1),
                points.get(2),
                points.get(3)
        };
    }

    /**
     * Рисует кривую B-сплайна с использованием матричных вычислений
     */
    private void drawBSplineCurve(BaseLayer canvas, Point[] controlPoints, Mode mode) {
        // Вычисляем коэффициенты: C = M_B × G
        double[][] coefficients = MatrixUtils.multiplyPoints(BSPLINE_MATRIX, controlPoints);

        // Вычисляем первую точку
        Point prevPoint = MatrixUtils.evaluateCubicCurve(coefficients, 0.0);

        // Отрисовываем кривую по шагам
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

    /**
     * Рисует контрольный полигон и маркеры контрольных точек
     */
    private void drawControlPolygon(BaseLayer canvas, Point[] pts, Mode mode) {
        if (pts == null || pts.length < 2) return;

        // Линии между контрольными точками
        for (int i = 0; i < pts.length - 1; i++) {
            straightLineAlgorithm.draw(
                    canvas,
                    new PointShapeParameters(List.of(pts[i], pts[i + 1])),
                    mode
            );
        }

        // Маркеры в контрольных точках
        int radius = hitTestPolicy.calculateTolerance(canvas.getPixelSize());
        for (Point pt : pts) {
            curvesAlgorithm.draw(
                    canvas,
                    new PointShapeParameters(List.of(
                            pt,
                            new Point(pt.getX() + radius, pt.getY() + radius)
                    )),
                    mode
            );
        }
    }
}