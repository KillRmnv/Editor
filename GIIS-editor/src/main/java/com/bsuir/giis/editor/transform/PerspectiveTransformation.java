package com.bsuir.giis.editor.transform;

import com.bsuir.giis.editor.model.dimensions.Point3D;
import com.bsuir.giis.editor.utils.MatrixUtils;

public class PerspectiveTransformation {
    private static final double FOCAL_LENGTH = 600.0;
    private final int centerX;
    private final int centerY;

    public PerspectiveTransformation(int width, int height) {
        this.centerX = width / 2;
        this.centerY = height / 2;
    }

    public Point3D project(Point3D point) {
        double[][] projectionMatrix = MatrixUtils.identityMatrix(4);
        projectionMatrix[3][2] = 1.0 / FOCAL_LENGTH;

        double[] vector = {point.getX(), point.getY(), point.getZ(), 1.0};
        double[] result = MatrixUtils.multiplyVector(projectionMatrix, vector);

        double w = result[3];

        if (Math.abs(w) > 1e-10) {
            return new Point3D(
                (result[0] / w) + centerX,
                (result[1] / w) + centerY,
                result[2] / w
            );
        }

        return new Point3D(result[0] + centerX, result[1] + centerY, result[2]);
    }

    public static double getFocalLength() {
        return FOCAL_LENGTH;
    }

    public int getCenterX() {
        return centerX;
    }

    public int getCenterY() {
        return centerY;
    }
}
