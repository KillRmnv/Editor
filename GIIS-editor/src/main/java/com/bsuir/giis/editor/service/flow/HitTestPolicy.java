package com.bsuir.giis.editor.service.flow;

import com.bsuir.giis.editor.model.Point;
import com.bsuir.giis.editor.model.PointArea;
import com.bsuir.giis.editor.view.BaseLayer;

public class HitTestPolicy {

    public int calculateTolerance(int pixelSize) {
        return Math.max(3, pixelSize / 2);
    }

    public PointArea createPointArea(Point point, int pixelSize) {
        int tolerance = calculateTolerance(pixelSize);
        return new PointArea(point, pixelSize, tolerance);
    }

    public Point resolvePoint(int x, int y, BaseLayer layer) {
        int px = x / layer.getPixelSize();
        int py = y / layer.getPixelSize();

        int tolerance = calculateTolerance(layer.getPixelSize());

        for (int dx = -1; dx <= 1; dx++) {
            for (int dy = -1; dy <= 1; dy++) {
                Point candidate = new Point(px + dx, py + dy);
                PointArea area =
                        new PointArea(candidate, layer.getPixelSize(), tolerance);

                if (area.contains(x, y)) {
                    return candidate;
                }
            }
        }
        return null;
    }
}

