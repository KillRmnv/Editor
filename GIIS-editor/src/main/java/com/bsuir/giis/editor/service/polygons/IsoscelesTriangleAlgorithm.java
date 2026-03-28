package com.bsuir.giis.editor.service.polygons;

import com.bsuir.giis.editor.model.AlgorithmParameters;
import com.bsuir.giis.editor.model.Point;
import com.bsuir.giis.editor.model.PointShapeParameters;
import com.bsuir.giis.editor.model.shapes.Morphable;
import com.bsuir.giis.editor.rendering.BaseLayer;
import com.bsuir.giis.editor.service.curves.CircleAlgorithm;
import com.bsuir.giis.editor.service.flow.HitTestPolicy;
import com.bsuir.giis.editor.service.flow.Mode;

import java.util.ArrayList;
import java.util.List;

/**
 * Равнобедренный треугольник по 2 точкам (основание AB).
 * Вершина C = средняя точка основания, смещённая на высоту вверх.
 * Выота = длина основания.
 */
public class IsoscelesTriangleAlgorithm implements PolygonsAlgorithm, Morphable {
    private final SimplePolygonAlgorithm polygonAlgorithm = new SimplePolygonAlgorithm();
    private final CircleAlgorithm circleAlgorithm = new CircleAlgorithm();
    private final HitTestPolicy hitTestPolicy = new HitTestPolicy();

    @Override
    public void draw(BaseLayer canvas, AlgorithmParameters parameters, Mode mode) {
        PointShapeParameters params = (PointShapeParameters) parameters;
        List<Point> input = params.getPoints();
        if (input.size() < 2) return;

        Point a = input.get(0);
        Point b = input.get(1);

        int midX = (a.getX() + b.getX()) / 2;
        int midY = (a.getY() + b.getY()) / 2;

        int dx = b.getX() - a.getX();
        int dy = b.getY() - a.getY();
        int baseLength = (int) Math.round(Math.sqrt(dx * dx + dy * dy));

        int hx = -dy;
        int hy = dx;
        double len = Math.sqrt(hx * hx + hy * hy);
        if (len == 0) return;

        int cx = Math.max(0, midX + (int) Math.round(hx / len * baseLength));
        int cy = Math.max(0, midY + (int) Math.round(hy / len * baseLength));

        List<Point> vertices = new ArrayList<>(3);
        vertices.add(a);
        vertices.add(b);
        vertices.add(new Point(cx, cy));

        polygonAlgorithm.draw(canvas, new PointShapeParameters(vertices), mode);
    }

    @Override
    public void morph(BaseLayer canvas, AlgorithmParameters parameters, Mode mode) {
        draw(canvas, parameters, mode);
        PointShapeParameters params = (PointShapeParameters) parameters;
        int radius = hitTestPolicy.calculateTolerance(canvas.getPixelSize());
        for (Point p : params.getPoints()) {
            circleAlgorithm.draw(canvas, new PointShapeParameters(List.of(p,
                    new Point(p.getX() + radius, p.getY() + radius))), mode);
        }
    }
}
