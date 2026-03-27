package com.bsuir.giis.editor.service.polygons;

import com.bsuir.giis.editor.model.AlgorithmParameters;
import com.bsuir.giis.editor.model.Point;
import com.bsuir.giis.editor.model.PointShapeParameters;
import com.bsuir.giis.editor.rendering.BaseLayer;
import com.bsuir.giis.editor.service.flow.Mode;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class JarvisMarchAlgorithm implements PolygonsAlgorithm {

    private List<Point> computedHull;

    public List<Point> getComputedHull() {
        return computedHull;
    }

    @Override
    public void draw(BaseLayer canvas, AlgorithmParameters parameters, Mode mode) {
        PointShapeParameters params = (PointShapeParameters) parameters;
        List<Point> inputPoints = params.getPoints();

        if (inputPoints.size() < 3) {
            mode.onFinish();
            return;
        }

        List<Point> hull = new ArrayList<>();

     
        Point start = inputPoints.stream()
                .min(Comparator.comparingInt(Point::getX)
                        .thenComparingInt(Point::getY))
                .orElse(inputPoints.get(0));

        Point current = start;

        do {
            hull.add(current);
            Point next = null;

            for (Point candidate : inputPoints) {
                
                if (samePoint(candidate, current)) continue;

                if (next == null) {
                    next = candidate;
                    continue;
                }

                double cross = crossProduct(current, next, candidate);


                if (cross > 0) {
                    next = candidate;
                } else if (cross == 0) {
                    
                    if (distSq(current, candidate) < distSq(current, next)) {
                        next = candidate;
                    }
                }
            }

            if (next == null) break;
            current = next;

        } while (!samePoint(current, start));

        computedHull = new ArrayList<>(hull);
        drawPolygonFromList(canvas, hull, mode);
    }

    private void drawPolygonFromList(BaseLayer canvas, List<Point> points, Mode mode) {
        SimplePolygonAlgorithm helper = new SimplePolygonAlgorithm();
        helper.draw(canvas, new PointShapeParameters(points), mode);
    }


    private boolean samePoint(Point a, Point b) {
        return a.getX() == b.getX() && a.getY() == b.getY();
    }


    private double crossProduct(Point a, Point b, Point c) {
        return (double) (b.getX() - a.getX()) * (c.getY() - a.getY())
             - (double) (b.getY() - a.getY()) * (c.getX() - a.getX());
    }

    private double distSq(Point a, Point b) {
        double dx = a.getX() - b.getX();
        double dy = a.getY() - b.getY();
        return dx * dx + dy * dy;
    }
}