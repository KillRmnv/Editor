package com.bsuir.giis.editor.service.flow;

import com.bsuir.giis.editor.model.Point;
import com.bsuir.giis.editor.model.PointArea;

import java.util.Optional;
import java.util.Set;

public class HitTestPolicy {

    public int calculateTolerance(int pixelSize) {
        return Math.max(8, pixelSize / 2);
    }

    public Optional<Point> resolvePoint(
            int clickX,
            int clickY,
            Set<Point> existingPoints,
            int pixelSize
    ) {
        int tolerance = calculateTolerance(pixelSize);

        for (Point p : existingPoints) {
            PointArea area = new PointArea(p, pixelSize, tolerance);
            if (area.contains(clickX, clickY)) {
                return Optional.of(p);
            }
        }
        return Optional.empty();
    }
}
