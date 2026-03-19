package com.bsuir.giis.editor.model.shapes;

import com.bsuir.giis.editor.model.AlgorithmParameters;
import com.bsuir.giis.editor.model.Model3DParameters;
import com.bsuir.giis.editor.model.dimensions.Face3D;
import com.bsuir.giis.editor.model.dimensions.Model3D;
import com.bsuir.giis.editor.model.dimensions.Point3D;
import com.bsuir.giis.editor.rendering.BaseLayer;
import com.bsuir.giis.editor.service.flow.Mode;
import com.bsuir.giis.editor.service.lines.BresenhamAlgorithm;
import com.bsuir.giis.editor.transform.PerspectiveTransformation;
import com.bsuir.giis.editor.transform.Rotation3D;
import com.bsuir.giis.editor.utils.MatrixUtils;

import java.awt.Color;
import java.util.List;

public class Model3DDrawable implements Drawable {
    private static final double FOCAL_LENGTH = PerspectiveTransformation.getFocalLength();

    private final BresenhamAlgorithm lineDrawer;

    public Model3DDrawable() {
        this.lineDrawer = new BresenhamAlgorithm();
    }

    @Override
    public void draw(BaseLayer canvas, AlgorithmParameters parameters, Mode mode) {
        if (!(parameters instanceof Model3DParameters params)) {
            return;
        }

        Model3D model = params.getModel();
        Rotation3D rotation = params.getRotation();
        PerspectiveTransformation projection = params.getProjection();

        if (model == null || model.getVertices().isEmpty()) {
            return;
        }

        double[][] rotationMatrix = rotation.getCombinedMatrix();
        double normScale = model.getNormalizedScale();
        double cx = model.getCenterX();
        double cy = model.getCenterY();
        double cz = model.getCenterZ();

        double[][] centerMatrix = MatrixUtils.translate3D(-cx, -cy, -cz);
        double[][] normalizeMatrix = MatrixUtils.scale3D(normScale, normScale, normScale);
        double[][] screenScaleMatrix = MatrixUtils.scale3D(400, 400, 400);
        double[][] translationMatrix = MatrixUtils.translate3D(0, 0, FOCAL_LENGTH + 200);

        double[][] tm = MatrixUtils.multiply(centerMatrix, normalizeMatrix);
        tm = MatrixUtils.multiply(rotationMatrix, tm);
        tm = MatrixUtils.multiply(screenScaleMatrix, tm);
        tm = MatrixUtils.multiply(translationMatrix, tm);

        double invFocal = 1.0 / FOCAL_LENGTH;
        double centerX = projection.getCenterX();
        double centerY = projection.getCenterY();

        List<Point3D> vertices = model.getVertices();
        int vertexCount = vertices.size();
        int[] screenX = new int[vertexCount];
        int[] screenY = new int[vertexCount];

        for (int i = 0; i < vertexCount; i++) {
            Point3D v = vertices.get(i);
            double vx = v.getX();
            double vy = v.getY();
            double vz = v.getZ();

            double tx = tm[0][0]*vx + tm[0][1]*vy + tm[0][2]*vz + tm[0][3];
            double ty = tm[1][0]*vx + tm[1][1]*vy + tm[1][2]*vz + tm[1][3];
            double tz = tm[2][0]*vx + tm[2][1]*vy + tm[2][2]*vz + tm[2][3];

            double w = tz * invFocal + 1.0;
            if (Math.abs(w) > 1e-10) {
                screenX[i] = (int) Math.round(tx / w + centerX);
                screenY[i] = (int) Math.round(ty / w + centerY);
            } else {
                screenX[i] = (int) Math.round(tx + centerX);
                screenY[i] = (int) Math.round(ty + centerY);
            }
        }

        for (Face3D face : model.getFaces()) {
            List<Integer> indices = face.getVertexIndices();
            int n = indices.size();

            for (int i = 0; i < n; i++) {
                int idx1 = indices.get(i);
                int idx2 = indices.get((i + 1) % n);

                if (idx1 >= 0 && idx1 < vertexCount && idx2 >= 0 && idx2 < vertexCount) {
                    lineDrawer.drawLine(canvas, screenX[idx1], screenY[idx1],
                            screenX[idx2], screenY[idx2], Color.BLACK);
                }
            }
        }
    }
}
