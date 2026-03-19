package com.bsuir.giis.editor.model;

import com.bsuir.giis.editor.model.dimensions.Model3D;
import com.bsuir.giis.editor.transform.PerspectiveTransformation;
import com.bsuir.giis.editor.transform.Rotation3D;

import java.util.List;

public class Model3DParameters implements AlgorithmParameters {
    private final Model3D model;
    private final Rotation3D rotation;
    private final PerspectiveTransformation projection;

    public Model3DParameters(Model3D model, Rotation3D rotation, PerspectiveTransformation projection) {
        this.model = model;
        this.rotation = rotation;
        this.projection = projection;
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

    @Override
    public List<Point> getStartEndPoint() {
        return List.of();
    }

    @Override
    public List<Point> getPoints() {
        return List.of();
    }
}
