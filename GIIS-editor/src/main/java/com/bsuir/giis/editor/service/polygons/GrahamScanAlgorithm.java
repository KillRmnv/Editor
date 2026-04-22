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

        // Опорная точка: минимальный Y (визуально верхняя в экранных координатах),
        // при равенстве — минимальный X.
        Point start = inputPoints.stream()
                .min(Comparator.comparingInt(Point::getY)
                        .thenComparingInt(Point::getX))
                .orElse(inputPoints.get(0));

        // Сортируем все точки по полярному углу относительно start.
        // start намеренно не добавляем в список заранее — он войдёт первым после сортировки.
        List<Point> sorted = new ArrayList<>(inputPoints);
        sorted.sort((a, b) -> {
            if (samePoint(a, start)) return -1;
            if (samePoint(b, start)) return  1;

            double angleA = Math.atan2(a.getY() - start.getY(), a.getX() - start.getX());
            double angleB = Math.atan2(b.getY() - start.getY(), b.getX() - start.getX());

            if (Double.compare(angleA, angleB) != 0) {
                return Double.compare(angleA, angleB);
            }

            
            double distA = distSq(start, a);
            double distB = distSq(start, b);
            return Double.compare(distA, distB);
        });

        //  стартуем с sorted[0] (= start) и итерируемся с sorted[1].
        // В оригинале start пушился до цикла, а затем sorted[0] == start
        // пушился ещё раз, давая дубликат в начале hull.
        Stack<Point> stack = new Stack<>();
        stack.push(sorted.get(0)); // start
        stack.push(sorted.get(1));

        //  цикл начинается с индекса 2, а не с 0.
        for (int i = 2; i < sorted.size(); i++) {
            Point point = sorted.get(i);

            // Удаляем точки, образующие невыпуклый (левый/прямой) поворот.
            // <= 0: удаляем коллинеарные → строгая выпуклая оболочка.
            // <  0: оставляем коллинеарные → все точки на рёбрах включаются.
            while (stack.size() > 1
                    && crossProduct(stack.get(stack.size() - 2), stack.peek(), point) <= 0) {
                stack.pop();
            }
            stack.push(point);
        }

        List<Point> hull = new ArrayList<>(stack);
        computedHull = new ArrayList<>(hull);
        drawPolygonFromList(canvas, hull, mode);
    }

    private void drawPolygonFromList(BaseLayer canvas, List<Point> points, Mode mode) {
        if (points.isEmpty()) return;
        SimplePolygonAlgorithm helper = new SimplePolygonAlgorithm();
        helper.draw(canvas, new PointShapeParameters(points), mode);
    }

    /**
     * Сравнение точек по значению координат.
     */
    private boolean samePoint(Point a, Point b) {
        return a.getX() == b.getX() && a.getY() == b.getY();
    }

    /**
     * Псевдоскалярное произведение (a→b) × (a→c).
     * > 0: правый поворот (CW) в экранных координатах (Y↓)
     * < 0: левый поворот (CCW)
     * = 0: коллинеарно
     */
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