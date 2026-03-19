package com.bsuir.giis.editor.transform;

import com.bsuir.giis.editor.model.Point;
import com.bsuir.giis.editor.utils.MatrixUtils;

import java.util.ArrayList;
import java.util.List;

public class CompositeTransformation implements Transformation {
    
    private final List<Transformation> transformations = new ArrayList<>();

    public CompositeTransformation add(Transformation transformation) {
        this.transformations.add(transformation);
        return this;
    }

    @Override
    public double[][] getMatrix() {
        double[][] resultMatrix = MatrixUtils.identityMatrix(3);

        for (Transformation t : transformations) {
            resultMatrix = MatrixUtils.multiply(t.getMatrix(), resultMatrix);
        }

        return resultMatrix;
    }

    public static CompositeTransformation rotateAroundPoint(double angleDegrees, Point center) {
        CompositeTransformation composite = new CompositeTransformation();
        composite.add(new Translation(-center.getX(), -center.getY()));
        composite.add(new Rotation(angleDegrees));
        composite.add(new Translation(center.getX(), center.getY()));
        return composite;
    }
}
