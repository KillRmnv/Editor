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

    @Override
    public void draw(BaseLayer canvas, AlgorithmParameters parameters, Mode mode) {
        PointShapeParameters params = (PointShapeParameters) parameters;
        List<Point> inputPoints = params.getPoints();

        if (inputPoints.size() < 3) {
            mode.onFinish();
            return;
        }

        List<Point> hull = new ArrayList<>();

        // 1. Находим самую левую точку (старт)
        Point start = inputPoints.stream()
                .min(Comparator.comparingInt(Point::getX))
                .orElse(inputPoints.get(0));
        
        Point current = start;
        hull.add(current);

        do {
            Point next = null;
            
            for (Point candidate : inputPoints) {
                if (candidate.equals(current)) continue;
                
                if (next == null) {
                    next = candidate;
                    continue;
                }
                

                double cross = crossProduct(current, next, candidate);
                
                if (cross > 0) {
                    next = candidate;
                } else if (cross == 0) {
                    double distNext = distSq(current, next);
                    double distCand = distSq(current, candidate);
                    if (distCand > distNext) {
                        next = candidate;
                    }
                }
            }

            if (next != null && !next.equals(start)) {
                hull.add(next);
            }
            
            current = next;
            
        } while (current != null && !current.equals(start));

        drawPolygonFromList(canvas, hull, mode);
    }

    private void drawPolygonFromList(BaseLayer canvas, List<Point> points, Mode mode) {
        SimplePolygonAlgorithm helper = new SimplePolygonAlgorithm();
        helper.draw(canvas, new PointShapeParameters(points), mode);
    }

    private double crossProduct(Point a, Point b, Point c) {
        return (double) (b.getX() - a.getX()) * (c.getY() - a.getY()) -
               (double) (b.getY() - a.getY()) * (c.getX() - a.getX());
    }

    private double distSq(Point a, Point b) {
        return Math.pow(a.getX() - b.getX(), 2) + Math.pow(a.getY() - b.getY(), 2);
    }
}