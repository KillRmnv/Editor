package com.bsuir.giis.editor.service.polygons;

import com.bsuir.giis.editor.model.Point;
import com.bsuir.giis.editor.model.PointShapeParameters;

import java.util.List;

public class PolygonValidator {

    /**
     * Проверяет, является ли полигон, заданный параметрами, выпуклым.
     * Алгоритм основан на проверке знаков векторных произведений
     * для каждых трех последовательных вершин.
     *
     * @param параметры объект, содержащий список опорных точек
     * @return true, если полигон выпуклый; false, если невыпуклый или точек меньше 3
     */
    public boolean isConvex(PointShapeParameters параметры) {
        List<Point> points = параметры.getPoints();

        if (points.size() < 3) {
            return false;
        }

        int n = points.size();
        int prevSign = 0; 

        for (int i = 0; i < n; i++) {

            Point p1 = points.get(i);
            Point p2 = points.get((i + 1) % n);
            Point p3 = points.get((i + 2) % n);

            long cross = crossProduct(p1, p2, p3);

            int sign = Long.signum(cross);

            if (sign == 0) {
                continue;
            }

            if (prevSign == 0) {
                prevSign = sign;
            } 
            else if (prevSign != sign) {
                return false;
            }
        }

        return true;
    }

    /**
     * Вспомогательный метод вычисления псевдоскалярного произведения векторов.
     * Вычисляет определитель матрицы, составленной из векторов (p2-p1) и (p3-p2).
     *
     * Математически это:
     * | (x2-x1) (x3-x2) |
     * | (y2-y1) (y3-y2) |
     *
     * @return значение определителя.
     * > 0: левый поворот (против часовой)
     * < 0: правый поворот (по часовой)
     * = 0: точки коллинеарны
     */
    private long crossProduct(Point p1, Point p2, Point p3) {
        long ax = p2.getX() - p1.getX();
        long ay = p2.getY() - p1.getY();

        long bx = p3.getX() - p2.getX();
        long by = p3.getY() - p2.getY();

        return ax * by - ay * bx;
    }
}