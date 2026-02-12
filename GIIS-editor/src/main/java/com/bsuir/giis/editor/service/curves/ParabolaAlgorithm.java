package com.bsuir.giis.editor.service.curves;

import com.bsuir.giis.editor.model.AlgorithmParameters;
import com.bsuir.giis.editor.model.Point;
import com.bsuir.giis.editor.model.PointShapeParameters;
import com.bsuir.giis.editor.service.flow.Mode;
import com.bsuir.giis.editor.utils.MultiStep;
import com.bsuir.giis.editor.utils.PenStep;
import com.bsuir.giis.editor.view.BaseLayer;

import java.awt.*;
import java.lang.reflect.InvocationTargetException;

public class ParabolaAlgorithm implements CurvesAlgorithm {

    @Override
    public void draw(BaseLayer canvas, AlgorithmParameters parameters, Mode mode) {
        PointShapeParameters curvesParameters = (PointShapeParameters) parameters;
        Point vertex = curvesParameters.getPoint(0);
        Point endPoint = curvesParameters.getPoint(1);

        if (vertex == null || endPoint == null) {
            return;
        }

        // 1. Получаем pixelSize (через рефлексию или добавьте геттер в BaseLayer)
        int pixelSize = 1;

        pixelSize = canvas.getPixelSize();

        // 2. Переходим в координаты сетки
        int x0 = vertex.getX() / pixelSize;
        int y0 = vertex.getY() / pixelSize;
        int x1 = endPoint.getX() / pixelSize;
        int y1 = endPoint.getY() / pixelSize;

        // 3. Вычисляем смещение второй точки относительно вершины
        int dx = x1 - x0;
        int dy = y1 - y0;

        // Защита от деления на 0 (если точки совпадают по Y или X)
        if (dy == 0) {
            // Вырожденный случай: просто линия
            // Можно вызвать алгоритм линии или просто выйти
            return;
        }

        // 4. Определяем параметр 2p из уравнения x^2 = 2p * y
        // dx^2 = 2p * dy  =>  2p = dx^2 / dy
        // Мы используем float/double, чтобы точно попасть во вторую точку
        double twoP = (double) (dx * dx) / Math.abs(dy);

        // Направление отрисовки по Y (вверх или вниз)
        int signY = (dy > 0) ? 1 : -1;

        // --- Алгоритм Midpoint для параболы x^2 = 2p * y ---

        double x = 0;
        double y = 0;

        // Начальный параметр принятия решений для Региона 1 (наклон < 1)
        // d = x^2 - 2p * (y + 0.5)
        // При x=0, y=0: d = -p
        double p_param = twoP / 2.0;
        double d = 1 - p_param; // Упрощенная инициализация для Midpoint

        // Рисуем вершину
        drawSymmetricPoints(canvas, x0, y0, (int) x, (int) y, signY, pixelSize);
        try {
            mode.onStep(new MultiStep(2, PenStep.class),"Parabola(center): ");
        } catch (NoSuchMethodException | InvocationTargetException | InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
        // --- Регион 1: идем по X (slope <= 1, то есть x < p) ---
        while (x < p_param) {
            x++;
            if (d < 0) {
                // Midpoint внутри параболы, y не меняется
                d += 2 * x + 1;
            } else {
                // Midpoint снаружи, увеличиваем y
                y++;
                d += 2 * x + 1 - twoP;
            }

            // Прерываем, если вышли за пределы второй точки по высоте
            if (y > Math.abs(dy)) break;

            drawSymmetricPoints(canvas, x0, y0, (int) x, (int) y, signY, pixelSize);
            try {
                mode.onStep(new MultiStep(2, PenStep.class),"Parabola(x): ");
            } catch (NoSuchMethodException | InvocationTargetException | InstantiationException | IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }

        // --- Регион 2: идем по Y (slope > 1) ---
        // Пересчитываем параметр решения для второго региона
        // d = (x + 0.5)^2 - 2p * (y + 1)
        d = ((x + 0.5) * (x + 0.5)) - twoP * (y + 1);

        while (y < Math.abs(dy)) {
            y++;
            if (d > 0) {
                // x не меняется
                d += -twoP;
            } else {
                // x увеличивается
                x++;
                d += 2 * x - twoP;
            }
            drawSymmetricPoints(canvas, x0, y0, (int) x, (int) y, signY, pixelSize);
            try {
                mode.onStep(new MultiStep(2, PenStep.class),"Parabola(y): ");
            } catch (NoSuchMethodException | InvocationTargetException | InstantiationException | IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
        mode.onFinish();
    }


    // Метод для симметричной отрисовки (влево и вправо от вершины)
    private void drawSymmetricPoints(BaseLayer canvas, int x0, int y0, int x, int y, int signY, int pixelSize) {
        // Правая ветвь
        int screenX1 = (x0 + x) * pixelSize;
        int screenY1 = (y0 + y * signY) * pixelSize;
        canvas.paintPixel(screenX1, screenY1, Color.BLACK);

        // Левая ветвь
        int screenX2 = (x0 - x) * pixelSize;
        int screenY2 = (y0 + y * signY) * pixelSize;
        canvas.paintPixel(screenX2, screenY2, Color.BLACK);
    }
}