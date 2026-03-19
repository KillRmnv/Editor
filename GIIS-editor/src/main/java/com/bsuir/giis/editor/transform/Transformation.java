package com.bsuir.giis.editor.transform;

import com.bsuir.giis.editor.model.Point;
import com.bsuir.giis.editor.utils.MatrixUtils;

public interface Transformation {
    double[][] getMatrix();

    default Point apply(Point p) {
        double[] vector = { p.getX(), p.getY(), 1.0 };
        double[] result = MatrixUtils.multiplyVector(getMatrix(), vector);
        return new Point((int) Math.round(result[0]), (int) Math.round(result[1]));
    }
}
