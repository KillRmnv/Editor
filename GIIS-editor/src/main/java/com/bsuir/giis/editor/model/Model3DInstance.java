package com.bsuir.giis.editor.model;

import com.bsuir.giis.editor.model.dimensions.Model3D;
import com.bsuir.giis.editor.transform.PerspectiveTransformation;
import com.bsuir.giis.editor.transform.Rotation3D;

public class Model3DInstance {

    private Model3D model;
    private String objFilePath;
    private final Rotation3D rotation;
    private double translateX = 0;
    private double translateY = 0;
    private double scaleFactor = 1.0;
    private boolean reflectX = false;
    private boolean reflectY = false;
    private boolean perspectiveEnabled = true;

    public Model3DInstance(Model3D model, String objFilePath) {
        this.model = model;
        this.objFilePath = objFilePath;
        this.rotation = new Rotation3D();
    }

    public Model3DParameters getModelParameters(PerspectiveTransformation projection) {
        return new Model3DParameters(model, rotation, projection);
    }

    public Model3D getModel() {
        return model;
    }

    public void setModel(Model3D model) {
        this.model = model;
    }

    public String getObjFilePath() {
        return objFilePath;
    }

    public void setObjFilePath(String objFilePath) {
        this.objFilePath = objFilePath;
    }

    public Rotation3D getRotation() {
        return rotation;
    }

    public double getTranslateX() {
        return translateX;
    }

    public void setTranslateX(double translateX) {
        this.translateX = translateX;
    }

    public double getTranslateY() {
        return translateY;
    }

    public void setTranslateY(double translateY) {
        this.translateY = translateY;
    }

    public double getScaleFactor() {
        return scaleFactor;
    }

    public void setScaleFactor(double scaleFactor) {
        this.scaleFactor = scaleFactor;
    }

    public boolean isReflectX() {
        return reflectX;
    }

    public void setReflectX(boolean reflectX) {
        this.reflectX = reflectX;
    }

    public boolean isReflectY() {
        return reflectY;
    }

    public void setReflectY(boolean reflectY) {
        this.reflectY = reflectY;
    }

    public boolean isPerspectiveEnabled() {
        return perspectiveEnabled;
    }

    public void setPerspectiveEnabled(boolean perspectiveEnabled) {
        this.perspectiveEnabled = perspectiveEnabled;
    }

    public void copyTransformFrom(Model3DInstance source) {
        this.rotation.setAngleX(source.rotation.getAngleX());
        this.rotation.setAngleY(source.rotation.getAngleY());
        this.rotation.setAngleZ(source.rotation.getAngleZ());
        this.translateX = source.translateX;
        this.translateY = source.translateY;
        this.scaleFactor = source.scaleFactor;
        this.reflectX = source.reflectX;
        this.reflectY = source.reflectY;
        this.perspectiveEnabled = source.perspectiveEnabled;
    }
}
