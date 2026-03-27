package com.bsuir.giis.editor.service.polygons;

import com.bsuir.giis.editor.model.AlgorithmParameters;
import com.bsuir.giis.editor.model.Point;
import com.bsuir.giis.editor.model.PointShapeParameters;
import com.bsuir.giis.editor.rendering.BaseLayer;
import com.bsuir.giis.editor.service.flow.Mode;

import java.awt.*;
import java.util.List;

public class PolygonNormalDrawer {

   
    private static final int NORMAL_LENGTH_PIXELS = 30;


    public void drawNormals(BaseLayer canvas, AlgorithmParameters parameters, Mode mode) {
        PointShapeParameters params = (PointShapeParameters) parameters;
        List<Point> points = params.getPoints();

        if (points.size() < 3) {
            return;
        }

        int pixelSize = canvas.getPixelSize();

      
        double centerX = 0;
        double centerY = 0;
        for (Point p : points) {
            centerX += p.getX();
            centerY += p.getY();
        }
        centerX /= points.size();
        centerY /= points.size();

        int n = points.size();
        for (int i = 0; i < n; i++) {
            Point p1 = points.get(i);
            Point p2 = points.get((i + 1) % n);

            
            double midX = (p1.getX() + p2.getX()) / 2.0;
            double midY = (p1.getY() + p2.getY()) / 2.0;

            double edgeDx = p2.getX() - p1.getX();
            double edgeDy = p2.getY() - p1.getY();

            double normalDx = -edgeDy;
            double normalDy = edgeDx;

            double toCenterX = centerX - midX;
            double toCenterY = centerY - midY;

            double dot = normalDx * toCenterX + normalDy * toCenterY;

            if (dot < 0) {
                normalDx = -normalDx;
                normalDy = -normalDy;
            }

            double length = Math.sqrt(normalDx * normalDx + normalDy * normalDy);
            if (length == 0) continue; 

            double unitX = normalDx / length;
            double unitY = normalDy / length;

            double endX = midX + unitX * NORMAL_LENGTH_PIXELS;
            double endY = midY + unitY * NORMAL_LENGTH_PIXELS;


            int x1 = (int) (midX / pixelSize);
            int y1 = (int) (midY / pixelSize);
            int x2 = (int) (endX / pixelSize);
            int y2 = (int) (endY / pixelSize);

            drawNormalLine(canvas, x1, y1, x2, y2, pixelSize, mode);
        }
        
        canvas.repaint();
    }

    private void drawNormalLine(BaseLayer canvas, int x1, int y1, int x2, int y2, int pixelSize, Mode mode) {
        int dx = Math.abs(x2 - x1);
        int dy = Math.abs(y2 - y1);
        int sx = (x1 < x2) ? 1 : -1;
        int sy = (y1 < y2) ? 1 : -1;
        int err = dx - dy;

        Color normalColor = Color.RED; 

        while (true) {
            int screenX = x1 * pixelSize;
            int screenY = y1 * pixelSize;
            
            canvas.paintPixel(screenX, screenY, normalColor);

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