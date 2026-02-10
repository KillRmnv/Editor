package com.bsuir.giis.editor.service.curves;

import com.bsuir.giis.editor.model.AlgorithmParameters;
import com.bsuir.giis.editor.model.Point;
import com.bsuir.giis.editor.model.curves.CurvesParameters;
import com.bsuir.giis.editor.service.flow.Mode;
import com.bsuir.giis.editor.utils.MultiStep;
import com.bsuir.giis.editor.utils.PenStep;
import com.bsuir.giis.editor.view.BaseLayer;
import java.awt.Color;
import java.lang.reflect.InvocationTargetException;

public class EllipseAlgorithm implements CurvesAlgorithm {

    @Override
    public void draw(BaseLayer canvas, AlgorithmParameters parameters, Mode mode) {
        CurvesParameters curvesParameters = (CurvesParameters) parameters;

        Point pCenter = curvesParameters.getPoint1();
        Point pX = curvesParameters.getPoint2(); // Точка для определения радиуса A
        Point pY = curvesParameters.getPoint3(); // Точка для определения радиуса B

        if (pCenter == null || pX == null || pY == null) {
            return;
        }

        // 1. Получаем размер пикселя.
        // Если у BaseLayer нет публичного геттера, добавьте: public int getPixelSize() { return pixelSize; }
        // Или используйте canvas.pixelSize, если поле доступно (protected и пакет тот же).
        // Здесь предполагаем наличие геттера или доступность поля.
        int pixelSize = 1;
        pixelSize = canvas.getPixelSize();

        // 2. Переводим координаты в пространство сетки (Grid Space)
        int xc = pCenter.getX() / pixelSize;
        int yc = pCenter.getY() / pixelSize;

        // Вычисляем радиусы A и B в клетках сетки
        int a = Math.abs((pX.getX() / pixelSize) - xc);
        int b = Math.abs((pY.getY() / pixelSize) - yc);

        // Если радиусы нулевые, рисуем одну точку и выходим
        if (a == 0 && b == 0) {
            drawPixel(canvas, xc, yc, pixelSize);
            return;
        }

        // --- Алгоритм средней точки (Midpoint Ellipse Algorithm) ---

        int x = 0;
        int y = b;

        // Квадраты радиусов для оптимизации
        long a2 = (long) a * a;
        long b2 = (long) b * b;
        long twoA2 = 2 * a2;
        long twoB2 = 2 * b2;

        // Начальные параметры принятия решений для Региона 1
        // d1 = b^2 - a^2 * b + a^2 / 4
        long d1 = b2 - (a2 * b) + (a2 >> 2);

        long dx = 0;             // 2 * b^2 * x
        long dy = twoA2 * y;     // 2 * a^2 * y

        drawSymmetricPoints(canvas, xc, yc, x, y, pixelSize);
        try {
            mode.onStep(new MultiStep(3, PenStep.class),"Ellipse(center): ");
        } catch (NoSuchMethodException | InvocationTargetException | InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
        // Регион 1: Наклон касательной < 1 (двигаемся по X быстрее)
        while (dx < dy) {
            x++;
            dx += twoB2;

            if (d1 < 0) {
                d1 += dx + b2;
            } else {
                y--;
                dy -= twoA2;
                d1 += dx - dy + b2;
            }
            drawSymmetricPoints(canvas, xc, yc, x, y, pixelSize);
            try {
                mode.onStep(new MultiStep(3, PenStep.class),"Ellipse: ");
            } catch (NoSuchMethodException | InvocationTargetException | InstantiationException | IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }

        // Начальные параметры принятия решений для Региона 2
        // d2 = b^2 * (x + 0.5)^2 + a^2 * (y - 1)^2 - a^2 * b^2
        long d2 = (long) (b2 * (x + 0.5) * (x + 0.5) + a2 * (y - 1) * (y - 1) - a2 * b2);

        // Регион 2: Наклон касательной > 1 (двигаемся по Y быстрее)
        while (y > 0) {
            y--;
            dy -= twoA2;

            if (d2 > 0) {
                d2 += a2 - dy;
            } else {
                x++;
                dx += twoB2;
                d2 += dx - dy + a2;
            }
            drawSymmetricPoints(canvas, xc, yc, x, y, pixelSize);
            try {
                mode.onStep(new MultiStep(3, PenStep.class),"Ellipse: ");
            } catch (NoSuchMethodException | InvocationTargetException | InstantiationException | IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }

         mode.onFinish() ;
    }

    // Вспомогательный метод для отрисовки 4 симметричных точек
    private void drawSymmetricPoints(BaseLayer canvas, int xc, int yc, int x, int y, int pixelSize) {
        drawPixel(canvas, xc + x, yc + y, pixelSize);
        drawPixel(canvas, xc - x, yc + y, pixelSize);
        drawPixel(canvas, xc + x, yc - y, pixelSize);
        drawPixel(canvas, xc - x, yc - y, pixelSize);
    }

    // Метод отрисовки с учетом масштаба (Grid -> Screen)
    private void drawPixel(BaseLayer canvas, int cx, int cy, int pixelSize) {
        // Умножаем на pixelSize, чтобы вернуть экранные координаты для paintPixel
        canvas.paintPixel(cx * pixelSize, cy * pixelSize, Color.BLACK);
    }
}