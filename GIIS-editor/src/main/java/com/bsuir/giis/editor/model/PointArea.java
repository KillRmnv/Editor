package com.bsuir.giis.editor.model;

import java.util.ArrayList;
import java.util.List;

public class PointArea {

    private final Point point;

    private final int minX;
    private final int maxX;
    private final int minY;
    private final int maxY;

    public PointArea(Point point, int pixelSize, int tolerance) {
        this.point = point;

        int centerX = point.getX() * pixelSize + pixelSize / 2;
        int centerY = point.getY() * pixelSize + pixelSize / 2;

        this.minX = centerX - tolerance;
        this.maxX = centerX + tolerance;

        this.minY = centerY - tolerance;
        this.maxY = centerY + tolerance;
    }

    public boolean contains(int x, int y) {
        return x >= minX && x <= maxX &&
                y >= minY && y <= maxY;
    }

    public Point getPoint() {
        return point;
    }
    public boolean strongIntersects(PointArea other) {
        int overlapWidth = Math.min(this.maxX, other.maxX)
                - Math.max(this.minX, other.minX);

        int overlapHeight = Math.min(this.maxY, other.maxY)
                - Math.max(this.minY, other.minY);

        if (overlapWidth <= 0 || overlapHeight <= 0) {
            return false;
        }

        int overlapArea = overlapWidth * overlapHeight;
        int thisArea = (maxX - minX) * (maxY - minY);
        int otherArea = (other.maxX - other.minX) * (other.maxY - other.minY);

        return overlapArea >= thisArea / 2 &&
                overlapArea >= otherArea / 2;
    }
    public int getMinX() {
        return minX;
    }
    public int getMaxX() {
        return maxX;
    }
    public int getMinY() {
        return minY;
    }
    public int getMaxY() {
        return maxY;
    }
    public List<Point> getAllPoints() {
        List<Point> areaPoints = new ArrayList<>();

        for (int x = minX; x <= maxX; x++) {
            for (int y = minY; y <= maxY; y++) {
                areaPoints.add(new Point(x, y));
            }
        }

        return areaPoints;
    }
}


