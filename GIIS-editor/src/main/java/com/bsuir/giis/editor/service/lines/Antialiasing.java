package com.bsuir.giis.editor.service.lines;

import com.bsuir.giis.editor.model.AlgorithmParameters;
import com.bsuir.giis.editor.model.lines.LinesParameters;
import com.bsuir.giis.editor.service.flow.Mode;
import com.bsuir.giis.editor.utils.PenStep;
import com.bsuir.giis.editor.view.Canvas;

public class Antialiasing implements StraightLineAlgorithm {

    @Override
    public void draw(Canvas canvas, AlgorithmParameters parameters, Mode mode) {
        LinesParameters linesParameters = (LinesParameters) parameters;

        // 1. Получаем размер пикселя для корректной работы с сеткой
        // Предполагается, что у Canvas есть геттер. Если нет — добавьте: public int getPixelSize() { return pixelSize; }
        int pixelSize = canvas.getPixelSize();

        // 2. Нормализуем координаты: переводим из экранных в логические (индексы сетки)
        int x1 = linesParameters.getStartPoint().getX() / pixelSize;
        int y1 = linesParameters.getStartPoint().getY() / pixelSize;
        int x2 = linesParameters.getEndPoint().getX() / pixelSize;
        int y2 = linesParameters.getEndPoint().getY() / pixelSize;

        // Определяем "крутизну" линии
        boolean steep = Math.abs(y2 - y1) > Math.abs(x2 - x1);

        // Если угол больше 45 градусов, меняем оси местами
        if (steep) {
            int temp = x1; x1 = y1; y1 = temp;
            temp = x2; x2 = y2; y2 = temp;
        }
        // Упорядочиваем координаты слева направо
        if (x1 > x2) {
            int temp = x1; x1 = x2; x2 = temp;
            temp = y1; y1 = y2; y2 = temp;
        }

        double dx = x2 - x1;
        double dy = y2 - y1;
        double gradient = (dx == 0) ? 1.0 : dy / dx;

        // Первое пересечение Y
        double intery = y1 + gradient;

        // Рисуем начальную и конечную точки (с полной яркостью)

        drawPixel(canvas, x1, y1, 1.0, steep, pixelSize);
        mode.onStep(new PenStep(x1,y1,255),"Wu Algorithm(start point): ");
        drawPixel(canvas, x2, y2, 1.0, steep, pixelSize);
        mode.onStep(new PenStep(x2,y2,255),"Wu Algorithm(end point): ");


        // Основной цикл алгоритма Ву
        for (int x = x1 + 1; x < x2; x++) {
            // Рисуем основную точку (яркость = перевернутая дробная часть)
            drawPixel(canvas, x, (int) intery, 1.0 - (intery - (int) intery), steep, pixelSize);
            mode.onStep(new PenStep(x,(int)intery,(int)(1.0 - (intery - (int) intery)*255)),"Wu Algorithm(main point): ");
            // Рисуем соседнюю точку (яркость = дробная часть)
            drawPixel(canvas, x, (int) intery + 1, intery - (int) intery, steep, pixelSize);
            mode.onStep(new PenStep(x, (int) intery + 1,(int)(intery - (int) intery)),"Wu Algorithm(neighbor point): ");
            intery += gradient;
        }
        mode.onFinish();
    }

    private void drawPixel(Canvas canvas, int x, int y, double brightness, boolean steep, int pixelSize) {
        // 3. Масштабируем яркость из 0..1 в 0..255 (alpha канал)
        int alpha = (int) (brightness * 255);

        // 4. Переводим координаты обратно в "экранные" для корректной работы canvas.paintPixel,
        // так как внутри paintPixel происходит деление: px = inputX / pixelSize
        int screenX = x * pixelSize;
        int screenY = y * pixelSize;

        if (steep) {
            // Если оси были перевернуты, возвращаем их на место при отрисовке
            canvas.paintPixel(screenY, screenX, alpha);
        } else {
            canvas.paintPixel(screenX, screenY, alpha);
        }
    }
}