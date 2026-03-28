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
 * Прямоугольник по 2 точкам (диагональ AB).
 * Вершины: A, (B.x, A.y), B, (A.x, B.y).
 */
public class RectangleAlgorithm implements PolygonsAlgorithm, Morphable {
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

        List<Point> vertices = new ArrayList<>(4);
        vertices.add(a);
        vertices.add(new Point(b.getX(), a.getY()));
        vertices.add(b);
        vertices.add(new Point(a.getX(), b.getY()));

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
