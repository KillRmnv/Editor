package com.bsuir.giis.editor.model.shapes;

import com.bsuir.giis.editor.model.AlgorithmParameters;
import com.bsuir.giis.editor.model.Model3DParameters;
import com.bsuir.giis.editor.model.dimensions.Face3D;
import com.bsuir.giis.editor.model.dimensions.Model3D;
import com.bsuir.giis.editor.model.dimensions.Point3D;
import com.bsuir.giis.editor.rendering.BaseLayer;
import com.bsuir.giis.editor.rendering.RenderThreadPool;
import com.bsuir.giis.editor.service.flow.Mode;
import com.bsuir.giis.editor.service.lines.BresenhamAlgorithm;
import com.bsuir.giis.editor.transform.PerspectiveTransformation;
import com.bsuir.giis.editor.transform.Rotation3D;
import com.bsuir.giis.editor.utils.MatrixUtils;

import java.awt.Color;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;

public class Model3DDrawable implements Drawable {
    private static final double FOCAL_LENGTH = PerspectiveTransformation.getFocalLength();
    private static final int PARALLEL_THRESHOLD = 5000;
    private static final int BLACK = Color.BLACK.getRGB();

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

        List<Face3D> faces = model.getFaces();
        int faceCount = faces.size();

        if (faceCount < PARALLEL_THRESHOLD) {
            drawSingleThreaded(vertices, vertexCount, screenX, screenY,
                    tm, invFocal, centerX, centerY,
                    canvas, faces);
        } else {
            drawParallel(vertices, vertexCount, screenX, screenY,
                    tm, invFocal, centerX, centerY,
                    canvas, faces, faceCount);
        }
    }

    private void drawSingleThreaded(List<Point3D> vertices, int vertexCount,
                                    int[] screenX, int[] screenY,
                                    double[][] tm, double invFocal,
                                    double centerX, double centerY,
                                    BaseLayer canvas, List<Face3D> faces) {
        projectVertices(vertices, vertexCount, screenX, screenY,
                tm, invFocal, centerX, centerY, 0, vertexCount);

        int width = canvas.getBufferWidth();
        int height = canvas.getBufferHeight();
        int[] pixels = canvas.getPixelBuffer();

        for (Face3D face : faces) {
            drawFaceEdges(face, pixels, width, height, screenX, screenY, vertexCount);
        }
    }

    private void drawParallel(List<Point3D> vertices, int vertexCount,
                              int[] screenX, int[] screenY,
                              double[][] tm, double invFocal,
                              double centerX, double centerY,
                              BaseLayer canvas, List<Face3D> faces, int faceCount) {
        ExecutorService pool = RenderThreadPool.getPool();
        int threads = RenderThreadPool.THREAD_COUNT;

        parallelProjectVertices(pool, threads, vertices, vertexCount,
                screenX, screenY, tm, invFocal, centerX, centerY);

        int width = canvas.getBufferWidth();
        int height = canvas.getBufferHeight();
        int[] pixels = canvas.getPixelBuffer();

        parallelDrawFaces(pool, threads, faces, faceCount,
                pixels, width, height, screenX, screenY, vertexCount);
    }

    private void parallelProjectVertices(ExecutorService pool, int threads,
                                         List<Point3D> vertices, int vertexCount,
                                         int[] screenX, int[] screenY,
                                         double[][] tm, double invFocal,
                                         double centerX, double centerY) {
        int chunkSize = (vertexCount + threads - 1) / threads;
        CountDownLatch latch = new CountDownLatch(threads);

        for (int t = 0; t < threads; t++) {
            int start = t * chunkSize;
            int end = Math.min(start + chunkSize, vertexCount);
            pool.submit(() -> {
                projectVertices(vertices, vertexCount, screenX, screenY,
                        tm, invFocal, centerX, centerY, start, end);
                latch.countDown();
            });
        }

        try {
            latch.await();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private void projectVertices(List<Point3D> vertices, int vertexCount,
                                 int[] screenX, int[] screenY,
                                 double[][] tm, double invFocal,
                                 double centerX, double centerY,
                                 int start, int end) {
        for (int i = start; i < end; i++) {
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
    }

    private void parallelDrawFaces(ExecutorService pool, int threads,
                                   List<Face3D> faces, int faceCount,
                                   int[] pixels, int width, int height,
                                   int[] screenX, int[] screenY, int vertexCount) {
        int chunkSize = (faceCount + threads - 1) / threads;
        CountDownLatch latch = new CountDownLatch(threads);

        for (int t = 0; t < threads; t++) {
            int start = t * chunkSize;
            int end = Math.min(start + chunkSize, faceCount);
            pool.submit(() -> {
                for (int f = start; f < end; f++) {
                    drawFaceEdges(faces.get(f), pixels, width, height,
                            screenX, screenY, vertexCount);
                }
                latch.countDown();
            });
        }

        try {
            latch.await();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private void drawFaceEdges(Face3D face, int[] pixels, int width, int height,
                               int[] screenX, int[] screenY, int vertexCount) {
        List<Integer> indices = face.getVertexIndices();
        int n = indices.size();

        for (int i = 0; i < n; i++) {
            int idx1 = indices.get(i);
            int idx2 = indices.get((i + 1) % n);

            if (idx1 >= 0 && idx1 < vertexCount && idx2 >= 0 && idx2 < vertexCount) {
                BresenhamAlgorithm.drawLineDirect(pixels, width, height,
                        screenX[idx1], screenY[idx1],
                        screenX[idx2], screenY[idx2], BLACK);
            }
        }
    }
}
