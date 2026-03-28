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

public class RegularPolygonAlgorithm implements PolygonsAlgorithm, Morphable {
    private final SimplePolygonAlgorithm polygonAlgorithm = new SimplePolygonAlgorithm();
    private final CircleAlgorithm circleAlgorithm = new CircleAlgorithm();
    private final HitTestPolicy hitTestPolicy = new HitTestPolicy();
    private int sides;

    public RegularPolygonAlgorithm(int sides) {
        this.sides = sides;
    }

    public int getSides() {
        return sides;
    }

    public void setSides(int sides) {
        this.sides = sides;
    }

    @Override
    public void draw(BaseLayer canvas, AlgorithmParameters parameters, Mode mode) {
        PointShapeParameters params = (PointShapeParameters) parameters;
        List<Point> input = params.getPoints();
        if (input.size() < 2) return;

        Point center = input.get(0);
        Point vertex = input.get(1);

        int cx = center.getX();
        int cy = center.getY();
        int vx = vertex.getX();
        int vy = vertex.getY();

        int dx = vx - cx;
        int dy = vy - cy;
        double radius = Math.sqrt(dx * dx + dy * dy);
        double baseAngle = Math.atan2(dy, dx);

        List<Point> vertices = new ArrayList<>(sides);
        for (int i = 0; i < sides; i++) {
            double angle = baseAngle + 2 * Math.PI * i / sides;
            int px = Math.max(0, cx + (int) Math.round(radius * Math.cos(angle)));
            int py = Math.max(0, cy + (int) Math.round(radius * Math.sin(angle)));
            vertices.add(new Point(px, py));
        }

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
