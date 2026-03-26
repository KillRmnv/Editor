package com.bsuir.giis.editor.service.polygons;

import com.bsuir.giis.editor.model.AlgorithmParameters;
import com.bsuir.giis.editor.model.Point;
import com.bsuir.giis.editor.model.PointShapeParameters;
import com.bsuir.giis.editor.rendering.BaseLayer;
import com.bsuir.giis.editor.service.flow.Mode;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Stack;

public class GrahamScanAlgorithm implements PolygonsAlgorithm {

    @Override
    public void draw(BaseLayer canvas, AlgorithmParameters parameters, Mode mode) {
        PointShapeParameters params = (PointShapeParameters) parameters;
        List<Point> inputPoints = params.getPoints();

        if (inputPoints.size() < 3) {
            mode.onFinish();
            return;
        }

        
        Point start = inputPoints.stream()
                .min(Comparator.comparingInt(Point::getY)
                        .thenComparingInt(Point::getX))
                .orElse(inputPoints.get(0));

        List<Point> sortedPoints = new ArrayList<>(inputPoints);
        sortedPoints.sort((a, b) -> {
            if (a.equals(start)) return -1;
            if (b.equals(start)) return 1;
            
            double angleA = Math.atan2(a.getY() - start.getY(), a.getX() - start.getX());
            double angleB = Math.atan2(b.getY() - start.getY(), b.getX() - start.getX());
            
            if (angleA < angleB) return -1;
            if (angleA > angleB) return 1;
            
            double distA = Math.pow(a.getX() - start.getX(), 2) + Math.pow(a.getY() - start.getY(), 2);
            double distB = Math.pow(b.getX() - start.getX(), 2) + Math.pow(b.getY() - start.getY(), 2);
            return Double.compare(distA, distB);
        });

        Stack<Point> stack = new Stack<>();
        stack.push(start);
        
        for (Point point : sortedPoints) {
            while (stack.size() > 1 && crossProduct(stack.get(stack.size() - 2), stack.peek(), point) <= 0) {
                stack.pop();
            }
            stack.push(point);
        }

        List<Point> hull = new ArrayList<>(stack);
        drawPolygonFromList(canvas, hull, mode);
    }

    private void drawPolygonFromList(BaseLayer canvas, List<Point> points, Mode mode) {
        if (points.isEmpty()) return;
        
        SimplePolygonAlgorithm helper = new SimplePolygonAlgorithm();
        helper.draw(canvas, new PointShapeParameters(points), mode);
    }


    private double crossProduct(Point a, Point b, Point c) {
        return (double) (b.getX() - a.getX()) * (c.getY() - a.getY()) -
               (double) (b.getY() - a.getY()) * (c.getX() - a.getX());
    }


}