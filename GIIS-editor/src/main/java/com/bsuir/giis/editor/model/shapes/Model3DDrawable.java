package com.bsuir.giis.editor.model.shapes;

import com.bsuir.giis.editor.model.AlgorithmParameters;
import com.bsuir.giis.editor.model.Model3DParameters;
import com.bsuir.giis.editor.model.Point;
import com.bsuir.giis.editor.model.PointShapeParameters;
import com.bsuir.giis.editor.model.dimensions.Face3D;
import com.bsuir.giis.editor.model.dimensions.Model3D;
import com.bsuir.giis.editor.model.dimensions.Point3D;
import com.bsuir.giis.editor.rendering.BaseLayer;
import com.bsuir.giis.editor.service.flow.Mode;
import com.bsuir.giis.editor.service.lines.BresenhamAlgorithm;
import com.bsuir.giis.editor.transform.PerspectiveTransformation;
import com.bsuir.giis.editor.transform.Rotation3D;
import com.bsuir.giis.editor.utils.MatrixUtils;

import java.util.List;

public class Model3DDrawable implements Drawable {
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
                
                // НОВОЕ: Создаем матрицу сдвига модели вглубь экрана (например, на +600 по Z)
                // Предполагается, что агент реализовал MatrixUtils.translate3D
                double[][] translationMatrix = MatrixUtils.translate3D(0, 0, PerspectiveTransformation.getFocalLength() + 200);
                
                // Комбинируем поворот и сдвиг (сначала крутим, потом двигаем от камеры)
                double[][] transformMatrix = MatrixUtils.multiply(translationMatrix, rotationMatrix);
        
                List<Point3D> vertices = model.getVertices();
                Point[] projectedPoints = new Point[vertices.size()];
                
                for (int i = 0; i < vertices.size(); i++) {
                    // Применяем комбинированную матрицу
                    Point3D transformed = vertices.get(i).applyMatrix(transformMatrix);
                    // Проецируем
                    Point3D projected = projection.project(transformed);
                    projectedPoints[i] = projected.toPoint();
                }
        
        for (Face3D face : model.getFaces()) {
            List<Integer> indices = face.getVertexIndices();
            int n = indices.size();
            
            for (int i = 0; i < n; i++) {
                int idx1 = indices.get(i);
                int idx2 = indices.get((i + 1) % n);
                
                if (idx1 >= 0 && idx1 < projectedPoints.length &&
                    idx2 >= 0 && idx2 < projectedPoints.length) {
                    
                    Point p1 = projectedPoints[idx1];
                    Point p2 = projectedPoints[idx2];
                    
                    if (p1 != null && p2 != null) {
                        PointShapeParameters lineParams = 
                            new PointShapeParameters(List.of(p1, p2));
                        lineDrawer.draw(canvas, lineParams, mode);
                    }
                }
            }
        }
    }
}
