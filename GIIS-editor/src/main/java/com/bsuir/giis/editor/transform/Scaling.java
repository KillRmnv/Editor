package com.bsuir.giis.editor.transform;

public class Scaling implements Transformation {
    private final double sx;
    private final double sy;

    public Scaling(double sx, double sy) {
        this.sx = sx;
        this.sy = sy;
    }

    @Override
    public double[][] getMatrix() {
        return new double[][] {
            { sx,  0.0, 0.0 },
            { 0.0, sy,  0.0 },
            { 0.0, 0.0, 1.0 }
        };
    }
}
