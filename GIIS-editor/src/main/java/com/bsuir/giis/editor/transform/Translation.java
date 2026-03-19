package com.bsuir.giis.editor.transform;

public class Translation implements Transformation {
    private final double dx;
    private final double dy;

    public Translation(double dx, double dy) {
        this.dx = dx;
        this.dy = dy;
    }

    @Override
    public double[][] getMatrix() {
        return new double[][] {
            { 1.0, 0.0, dx },
            { 0.0, 1.0, dy },
            { 0.0, 0.0, 1.0 }
        };
    }
}
