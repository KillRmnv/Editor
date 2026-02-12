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

public class HyperbolaAlgorithm implements CurvesAlgorithm {

    @Override
    public void draw(BaseLayer canvas, AlgorithmParameters parameters, Mode mode) {
        PointShapeParameters curvesParameters = (PointShapeParameters) parameters;
        Point center = curvesParameters.getPoint(0);
        Point pX = curvesParameters.getPoint(1);
        Point pY = curvesParameters.getPoint(2);

        if (center == null || pX == null || pY == null) {
            return;
        }

        int pixelSize = 1;
        int layerWidth = 1920;
        int layerHeight = 1080;
        pixelSize = canvas.getPixelSize();

        layerWidth = canvas.getWidth();

        layerHeight = canvas.getHeight();

        int xc = center.getX() / pixelSize;
        int yc = center.getY() / pixelSize;

        int a = Math.abs((pX.getX() / pixelSize) - xc);
        int b = Math.abs((pY.getY() / pixelSize) - yc);

        if (a == 0 || b == 0) return;

        // Квадраты полуосей
        long a2 = (long) a * a;
        long b2 = (long) b * b;
        long twoA2 = 2 * a2;
        long twoB2 = 2 * b2;

        // Начальная точка (вершина гиперболы справа от центра)
        int x = a;
        int y = 0;

        // --- Регион 1 ---
        // Касательная крутая (dy/dx > 1). Для гиперболы x^2/a^2 - y^2/b^2 = 1 это начало кривой при x=a.
        // Мы шагаем по Y, вычисляем X.

        // Начальный параметр решения для средней точки
        // d = b^2 * (x + 0.5)^2 - a^2 * (y + 1)^2 - a^2 * b^2
        // При x=a, y=0:
        long d = twoB2 * a + b2 - twoA2 + a2; // Упрощенная инициализация может отличаться, считаем честно:

        // Точная формула d для первого шага: f(x+0.5, y+1)
        // f(x,y) = b^2*x^2 - a^2*y^2 - a^2*b^2
        d = b2 * (x * x + x) + b2 / 4 - a2 - a2 * b2; // (с учетом float -> int могут быть нюансы, используем инкрементальный метод ниже)

        // Инкрементальный подход Брезенхема для гиперболы:
        // Уравнение: b^2 * x^2 - a^2 * y^2 = a^2 * b^2

        // Пересчитаем d для старта (Region 1: Step Y)
        // Midpoint check at (x + 0.5, y + 1)
        d = b2 * (2 * a + 1) - 4 * a2; // Масштабированное значение (x4 для избавления от 0.25)
        // Примечание: формулы d зависят от конкретной вариации Bresenham/Midpoint.
        // Ниже классическая реализация.

        d = (long) (b2 * (a + 0.5) * (a + 0.5) - a2 * 1 * 1 - a2 * b2);

        drawHyperbolaPoints(canvas, xc, yc, x, y, pixelSize);
        try {
            mode.onStep(new MultiStep(3, PenStep.class),"Hyperbola(center): ");
        } catch (NoSuchMethodException | InvocationTargetException | InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }

        // Условие перехода: Наклон становится < 1.
        // Градиент dy/dx = (b^2 * x) / (a^2 * y).
        // Пока b^2 * x > a^2 * y (т.е. круто), идем по Y.
        // Но гипербола начинается вертикально, так что start condition is valid.

        while ((b2 * x > a2 * y) && x < layerWidth && y < layerHeight) {
            y++;
            // Если midpoint внутри кривой (ближе к оси X), сдвигаем X вправо
            if (d < 0) {
                d += b2 * (2 * x + 2) - a2 * (2 * y + 1);
                x++;
            } else {
                d += -a2 * (2 * y + 1);
            }
            drawHyperbolaPoints(canvas, xc, yc, x, y, pixelSize);
            try {
                mode.onStep(new MultiStep(2, PenStep.class),"Hyperbola: ");
            } catch (NoSuchMethodException | InvocationTargetException | InstantiationException | IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }

        // --- Регион 2 ---
        // Касательная пологая (dy/dx <= 1). Шагаем по X, вычисляем Y.
        // Пересчет d для точки (x + 1, y + 0.5)
        d = (long) (b2 * (x + 1) * (x + 1) - a2 * (y + 0.5) * (y + 0.5) - a2 * b2);

        while (x < layerWidth && y < layerHeight) {
            x++;
            if (d >= 0) {
                // Точка слишком далеко по Y, нужно поднять Y
                d += b2 * (2 * x + 1) - a2 * (2 * y + 2);
                y++;
            } else {
                d += b2 * (2 * x + 1);
            }
            drawHyperbolaPoints(canvas, xc, yc, x, y, pixelSize);
            try {
                mode.onStep(new MultiStep(2, PenStep.class),"Hyperbola: ");
            } catch (NoSuchMethodException | InvocationTargetException | InstantiationException | IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
        mode.onFinish();
    }

    private void drawHyperbolaPoints(BaseLayer canvas, int xc, int yc, int x, int y, int pixelSize) {
        // Отрисовка 4-х симметричных частей
        // Правая ветвь
        canvas.paintPixel((xc + x) * pixelSize, (yc + y) * pixelSize, Color.BLACK);
        canvas.paintPixel((xc + x) * pixelSize, (yc - y) * pixelSize, Color.BLACK);

        // Левая ветвь
        canvas.paintPixel((xc - x) * pixelSize, (yc + y) * pixelSize, Color.BLACK);
        canvas.paintPixel((xc - x) * pixelSize, (yc - y) * pixelSize, Color.BLACK);
    }
}