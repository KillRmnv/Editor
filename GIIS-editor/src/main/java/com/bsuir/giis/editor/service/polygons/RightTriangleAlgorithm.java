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
 * Прямоугольный треугольник по 2 точкам (катет AB).
 * Прямой угол в точке C = (B.x, A.y).
 */
public class RightTriangleAlgorithm implements PolygonsAlgorithm, Morphable {
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

        Point c = new Point(b.getX(), a.getY());

        List<Point> vertices = new ArrayList<>(3);
        vertices.add(a);
        vertices.add(b);
        vertices.add(c);

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
