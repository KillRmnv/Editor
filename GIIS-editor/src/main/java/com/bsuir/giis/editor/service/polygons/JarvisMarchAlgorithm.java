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

        // Находим самую левую точку (при равенстве X — самую нижнюю по экрану, т.е. max Y)
        Point start = inputPoints.stream()
                .min(Comparator.comparingInt(Point::getX)
                        .thenComparingInt(Point::getY))
                .orElse(inputPoints.get(0));

        Point current = start;

        do {
            hull.add(current);
            Point next = null;

            for (Point candidate : inputPoints) {
                // FIX: сравниваем по значению координат, а не по ссылке.
                // Это защищает от зависания, если Point не переопределяет equals(),
                // либо если объекты были пересозданы (десериализация и т.п.).
                if (samePoint(candidate, current)) continue;

                if (next == null) {
                    next = candidate;
                    continue;
                }

                double cross = crossProduct(current, next, candidate);

                // Экранные координаты: Y растёт вниз.
                // cross > 0 → правый поворот (по часовой) → обход CW.
                if (cross > 0) {
                    next = candidate;
                } else if (cross == 0) {
                    // Коллинеарные: берём ближайшую, чтобы не пропускать
                    // промежуточные точки, лежащие на рёбрах оболочки.
                    // Если промежуточные точки не нужны — замените на дальнюю:
                    //   if (distSq(current, candidate) > distSq(current, next)) next = candidate;
                    if (distSq(current, candidate) < distSq(current, next)) {
                        next = candidate;
                    }
                }
            }

            if (next == null) break;
            current = next;

        } while (!samePoint(current, start));

        drawPolygonFromList(canvas, hull, mode);
    }

    private void drawPolygonFromList(BaseLayer canvas, List<Point> points, Mode mode) {
        SimplePolygonAlgorithm helper = new SimplePolygonAlgorithm();
        helper.draw(canvas, new PointShapeParameters(points), mode);
    }

    /**
     * Сравнение точек по значению координат.
     * Не зависит от того, переопределён ли equals() в Point.
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