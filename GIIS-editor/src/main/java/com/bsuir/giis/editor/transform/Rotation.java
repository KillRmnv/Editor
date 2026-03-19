package com.bsuir.giis.editor.transform;

public class Rotation implements Transformation {
    private final double angleRadians;

    public Rotation(double angleDegrees) {
        this.angleRadians = Math.toRadians(angleDegrees);
    }

    @Override
    public double[][] getMatrix() {
        double cos = Math.cos(angleRadians);
        double sin = Math.sin(angleRadians);

        return new double[][] {
            { cos, -sin, 0.0 },
            { sin,  cos, 0.0 },
            { 0.0,  0.0, 1.0 }
        };
    }
}
