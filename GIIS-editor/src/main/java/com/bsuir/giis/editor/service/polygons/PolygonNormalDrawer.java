package com.bsuir.giis.editor.service.polygons;

import com.bsuir.giis.editor.model.AlgorithmParameters;
import com.bsuir.giis.editor.model.Point;
import com.bsuir.giis.editor.model.PointShapeParameters;
import com.bsuir.giis.editor.rendering.BaseLayer;
import com.bsuir.giis.editor.service.flow.Mode;
import com.bsuir.giis.editor.utils.PenStep;

import java.awt.*;
import java.util.List;

public class PolygonNormalDrawer {

    // Длина нормали на экране в пикселях (можно настроить)
    private static final int NORMAL_LENGTH_PIXELS = 30;

    /**
     * Рисует внутренние нормали к каждому ребру полигона.
     * Нормаль рисуется из центра ребра внутрь полигона.
     */
    public void drawNormals(BaseLayer canvas, AlgorithmParameters parameters, Mode mode) {
        PointShapeParameters params = (PointShapeParameters) parameters;
        List<Point> points = params.getPoints();

        if (points.size() < 3) {
            return;
        }

        int pixelSize = canvas.getPixelSize();

        // 1. Находим центр масс полигона (как "внутреннюю точку")
        // Работаем в экранных координатах
        double centerX = 0;
        double centerY = 0;
        for (Point p : points) {
            centerX += p.getX();
            centerY += p.getY();
        }
        centerX /= points.size();
        centerY /= points.size();

        // 2. Проходим по всем ребрам
        int n = points.size();
        for (int i = 0; i < n; i++) {
            Point p1 = points.get(i);
            Point p2 = points.get((i + 1) % n);

            // Координаты середины ребра (стартовая точка нормали)
            double midX = (p1.getX() + p2.getX()) / 2.0;
            double midY = (p1.getY() + p2.getY()) / 2.0;

            // Вектор ребра
            double edgeDx = p2.getX() - p1.getX();
            double edgeDy = p2.getY() - p1.getY();

            // Перпендикулярный вектор (кандидат на нормаль)
            // Поворот на 90 градусов: (-dy, dx)
            double normalDx = -edgeDy;
            double normalDy = edgeDx;

            // Проверяем, смотрит ли он внутрь (к центру масс)
            // Вектор от середины ребра к центру
            double toCenterX = centerX - midX;
            double toCenterY = centerY - midY;

            // Скалярное произведение
            double dot = normalDx * toCenterX + normalDy * toCenterY;

            // Если скалярное произведение < 0, значит нормаль смотрит наружу -> разворачиваем
            if (dot < 0) {
                normalDx = -normalDx;
                normalDy = -normalDy;
            }

            // Нормализуем вектор (делаем его единичной длины)
            double length = Math.sqrt(normalDx * normalDx + normalDy * normalDy);
            if (length == 0) continue; 

            double unitX = normalDx / length;
            double unitY = normalDy / length;

            // Вычисляем конечную точку нормали (с заданной длиной отрисовки)
            double endX = midX + unitX * NORMAL_LENGTH_PIXELS;
            double endY = midY + unitY * NORMAL_LENGTH_PIXELS;

            // Рисуем линию нормали (midX, midY) -> (endX, endY) через Брезенхэма
            // Сначала переводим в координаты сетки (как в твоем примере Antialiasing)
            int x1 = (int) (midX / pixelSize);
            int y1 = (int) (midY / pixelSize);
            int x2 = (int) (endX / pixelSize);
            int y2 = (int) (endY / pixelSize);

            drawNormalLine(canvas, x1, y1, x2, y2, pixelSize, mode);
        }
        
        canvas.repaint();
    }

    // Реализация алгоритма Брезенхэма для рисования линии нормали
    private void drawNormalLine(BaseLayer canvas, int x1, int y1, int x2, int y2, int pixelSize, Mode mode) {
        int dx = Math.abs(x2 - x1);
        int dy = Math.abs(y2 - y1);
        int sx = (x1 < x2) ? 1 : -1;
        int sy = (y1 < y2) ? 1 : -1;
        int err = dx - dy;

        // Цвет нормали (например, Красный или Синий, чтобы отличать от полигона)
        Color normalColor = Color.RED; 

        while (true) {
            // Рисуем пиксель (переводим обратно в экранные координаты для paintPixel)
            int screenX = x1 * pixelSize;
            int screenY = y1 * pixelSize;
            
            canvas.paintPixel(screenX, screenY, normalColor);
            // Можно раскомментировать для пошагового режима:
            // mode.onStep(new PenStep(x1, y1, 255), "Normal Pixel");

            if (x1 == x2 && y1 == y2) break;

            int e2 = 2 * err;
            if (e2 > -dy) {
                err -= dy;
                x1 += sx;
            }
            if (e2 < dx) {
                err += dx;
                y1 += sy;
            }
        }
    }
}