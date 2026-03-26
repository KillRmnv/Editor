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

        //  Шаг 3: матрица вращения (Euler: Rx * Ry * Rz) 
        double[][] rotationMatrix = rotation.getCombinedMatrix();
        double normScale = model.getNormalizedScale();
        double cx = model.getCenterX();
        double cy = model.getCenterY();
        double cz = model.getCenterZ();

        //  Пользовательские трансформации 
        double scaleFactor = params.getScaleFactor();
        double translateX = params.getTranslateX();
        double translateY = params.getTranslateY();
        boolean reflectX = params.isReflectX();
        boolean reflectY = params.isReflectY();
        boolean perspective = params.isPerspectiveEnabled();

        //  Шаг 1: центрирование модели в начало координат 
        double[][] centerMatrix = MatrixUtils.translate3D(-cx, -cy, -cz);

        //  Шаг 2: нормализация (масштаб к единичному размеру) 
        double[][] normalizeMatrix = MatrixUtils.scale3D(normScale, normScale, normScale);

        //  Шаг 4: отражение (scale3D(-1,1,1) — горизонтально, scale3D(1,-1,1) — вертикально) 
        double[][] reflectMatrix = MatrixUtils.scale3D(reflectX ? -1.0 : 1.0, reflectY ? -1.0 : 1.0, 1.0);

        //  Шаг 5: масштаб экрана (400 пикселей на единицу модели × zoom) 
        double screenScale = 400 * scaleFactor;
        double[][] screenScaleMatrix = MatrixUtils.scale3D(screenScale, screenScale, screenScale);

        //  Шаг 6: перемещение к камере (Z = FOCAL_LENGTH + 200) 
        double[][] translationMatrix = MatrixUtils.translate3D(translateX, translateY, FOCAL_LENGTH + 200);

        //  Сборка составной матрицы: T = translate * screenScale * reflect * rotate * normalize * center 
        double[][] tm = MatrixUtils.multiply(centerMatrix, normalizeMatrix);
        tm = MatrixUtils.multiply(rotationMatrix, tm);
        tm = MatrixUtils.multiply(reflectMatrix, tm);
        tm = MatrixUtils.multiply(screenScaleMatrix, tm);
        tm = MatrixUtils.multiply(translationMatrix, tm);

        //  Шаг 7: настройка проекции (invFocal=0 → ортографическая, invFocal=1/F → перспективная) 
        double invFocal = perspective ? 1.0 / FOCAL_LENGTH : 0.0;
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
        // Проекция всех вершин
        projectVertices(vertices, vertexCount, screenX, screenY,
                tm, invFocal, centerX, centerY, 0, vertexCount);

        int width = canvas.getBufferWidth();
        int height = canvas.getBufferHeight();
        int[] pixels = canvas.getPixelBuffer();

        // Отрисовка рёбер каждой грани
        for (Face3D face : faces) {
            drawFaceEdges(face, pixels, width, height, screenX, screenY, vertexCount);
        }
    }

    /** Параллельная проекция + отрисовка граней через пул потоков */
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

    /** Распараллеливание проекции вершин: чанки по N/threads вершин */
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

    /**
     * Шаг 7: проекция вершин на экран.
     * Перспективная: w = tz * (1/F) + 1, screenX = tx / w + centerX
     * Ортографическая (invFocal=0): w = 1, screenX = tx + centerX
     */
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

            // Умножение вершины на составную матрицу tm (все трансформации за один проход)
            double tx = tm[0][0]*vx + tm[0][1]*vy + tm[0][2]*vz + tm[0][3];
            double ty = tm[1][0]*vx + tm[1][1]*vy + tm[1][2]*vz + tm[1][3];
            double tz = tm[2][0]*vx + tm[2][1]*vy + tm[2][2]*vz + tm[2][3];

            // Перспективное деление: w = tz / F + 1
            double w = tz * invFocal + 1.0;
            if (Math.abs(w) > 1e-10) {
                screenX[i] = (int) Math.round(tx / w + centerX);
                screenY[i] = (int) Math.round(ty / w + centerY);
            } else {
                // Fallback: точка на плоскости проекции → параллельная проекция
                screenX[i] = (int) Math.round(tx + centerX);
                screenY[i] = (int) Math.round(ty + centerY);
            }
        }
    }

    /** Распараллеливание отрисовки граней: чанки по faceCount/threads граней */
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

    /** Отрисовка рёбер грани: соединяет соседние вершины полигона линиями Брезенхема */
    private void drawFaceEdges(Face3D face, int[] pixels, int width, int height,
                               int[] screenX, int[] screenY, int vertexCount) {
        List<Integer> indices = face.getVertexIndices();
        int n = indices.size();

        for (int i = 0; i < n; i++) {
            int idx1 = indices.get(i);
            int idx2 = indices.get((i + 1) % n);

            if (idx1 >= 0 && idx1 < vertexCount && idx2 >= 0 && idx2 < vertexCount) {
                // Рисуем ребро между вершинами idx1 и idx2 алгоритмом Брезенхема
                BresenhamAlgorithm.drawLineDirect(pixels, width, height,
                        screenX[idx1], screenY[idx1],
                        screenX[idx2], screenY[idx2], BLACK);
            }
        }
    }
}
