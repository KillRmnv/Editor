package com.bsuir.giis.editor.transform;

import com.bsuir.giis.editor.utils.MatrixUtils;

public class Rotation3D {
    private double angleX;
    private double angleY;
    private double angleZ;

    public Rotation3D() {
        this.angleX = 0;
        this.angleY = 0;
        this.angleZ = 0;
    }

    public Rotation3D(double angleX, double angleY, double angleZ) {
        this.angleX = angleX;
        this.angleY = angleY;
        this.angleZ = angleZ;
    }

    public double[][] getCombinedMatrix() {
        double[][] rx = MatrixUtils.rotateX(angleX);
        double[][] ry = MatrixUtils.rotateY(angleY);
        double[][] rz = MatrixUtils.rotateZ(angleZ);
        
        double[][] rxy = MatrixUtils.multiply(rx, ry);
        return MatrixUtils.multiply(rxy, rz);
    }

    public void addDeltaX(double delta) {
        this.angleX += delta;
    }

    public void addDeltaY(double delta) {
        this.angleY += delta;
    }

    public void addDeltaZ(double delta) {
        this.angleZ += delta;
    }

    public double getAngleX() {
        return angleX;
    }

    public void setAngleX(double angleX) {
        this.angleX = angleX;
    }

    public double getAngleY() {
        return angleY;
    }

    public void setAngleY(double angleY) {
        this.angleY = angleY;
    }

    public double getAngleZ() {
        return angleZ;
    }

    public void setAngleZ(double angleZ) {
        this.angleZ = angleZ;
    }

    public void reset() {
        this.angleX = 0;
        this.angleY = 0;
        this.angleZ = 0;
    }
}
