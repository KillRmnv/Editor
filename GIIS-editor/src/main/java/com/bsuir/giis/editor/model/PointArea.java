package com.bsuir.giis.editor.model;


public class PointArea {

    private final Point point;

    private final int minX;
    private final int maxX;
    private final int minY;
    private final int maxY;

    public PointArea(Point point, int pixelSize, int tolerance) {
        this.point = point;

        int baseX = point.getX() * pixelSize;
        int baseY = point.getY() * pixelSize;

        this.minX = baseX - tolerance;
        this.maxX = baseX + pixelSize + tolerance;

        this.minY = baseY - tolerance;
        this.maxY = baseY + pixelSize + tolerance;
    }

    public boolean contains(int clickX, int clickY) {
        return clickX >= minX && clickX <= maxX
                && clickY >= minY && clickY <= maxY;
    }

    public Point getPoint() {
        return point;
    }
}
