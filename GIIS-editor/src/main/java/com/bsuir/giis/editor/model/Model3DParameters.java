package com.bsuir.giis.editor.model;

import com.bsuir.giis.editor.model.dimensions.Model3D;
import com.bsuir.giis.editor.transform.PerspectiveTransformation;
import com.bsuir.giis.editor.transform.Rotation3D;

import java.util.List;

public class Model3DParameters implements AlgorithmParameters {
    private final Model3D model;
    private final Rotation3D rotation;
    private final PerspectiveTransformation projection;
    private final double translateX;
    private final double translateY;
    private final double scaleFactor;
    private final boolean reflectX;
    private final boolean reflectY;
    private final boolean perspectiveEnabled;

    public Model3DParameters(Model3D model, Rotation3D rotation, PerspectiveTransformation projection) {
        this(model, rotation, projection, 0, 0, 1.0, false, false, true);
    }

    public Model3DParameters(Model3D model, Rotation3D rotation, PerspectiveTransformation projection,
                             double translateX, double translateY, double scaleFactor,
                             boolean reflectX, boolean reflectY, boolean perspectiveEnabled) {
        this.model = model;
        this.rotation = rotation;
        this.projection = projection;
        this.translateX = translateX;
        this.translateY = translateY;
        this.scaleFactor = scaleFactor;
        this.reflectX = reflectX;
        this.reflectY = reflectY;
        this.perspectiveEnabled = perspectiveEnabled;
    }

    public Model3D getModel() {
        return model;
    }

    public Rotation3D getRotation() {
        return rotation;
    }

    public PerspectiveTransformation getProjection() {
        return projection;
    }

    public double getTranslateX() {
        return translateX;
    }

    public double getTranslateY() {
        return translateY;
    }

    public double getScaleFactor() {
        return scaleFactor;
    }

    public boolean isReflectX() {
        return reflectX;
    }

    public boolean isReflectY() {
        return reflectY;
    }

    public boolean isPerspectiveEnabled() {
        return perspectiveEnabled;
    }

    @Override
    public List<Point> getStartEndPoint() {
        return List.of();
    }

    @Override
    public List<Point> getPoints() {
        return List.of();
    }
}
