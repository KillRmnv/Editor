package com.bsuir.giis.editor.model.dimensions;

import com.bsuir.giis.editor.model.Point;
import com.bsuir.giis.editor.service.readers.ProjectionType;
import com.bsuir.giis.editor.utils.MatrixUtils;

public class Point3D {
    private final double x;
    private final double y;
    private final double z;

    public Point3D(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getZ() {
        return z;
    }

    public Point toPoint() {
        return new Point((int) Math.round(x), (int) Math.round(y));
    }

    public Point toPoint(ProjectionType projection) {
        return switch (projection) {
            case XY -> new Point((int) Math.round(x), (int) Math.round(y));
            case XZ -> new Point((int) Math.round(x), (int) Math.round(z));
            case YZ -> new Point((int) Math.round(y), (int) Math.round(z));
        };
    }

    @Override
    public String toString() {
        return "(" + x + ", " + y + ", " + z + ")";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Point3D point3D = (Point3D) o;
        return Double.compare(point3D.x, x) == 0 &&
               Double.compare(point3D.y, y) == 0 &&
               Double.compare(point3D.z, z) == 0;
    }

    @Override
    public int hashCode() {
        int result = Double.hashCode(x);
        result = 31 * result + Double.hashCode(y);
        result = 31 * result + Double.hashCode(z);
        return result;
    }

    public Point3D applyMatrix(double[][] matrix) {
        double[] vector = {x, y, z, 1.0};
        double[] result = MatrixUtils.multiplyVector(matrix, vector);
        return new Point3D(result[0], result[1], result[2]);
    }
}
