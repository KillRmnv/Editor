package com.bsuir.giis.editor.model;

import com.bsuir.giis.editor.model.dimensions.Model3D;
import com.bsuir.giis.editor.model.shapes.MorphableShape;
import com.bsuir.giis.editor.model.shapes.Shape;
import com.bsuir.giis.editor.transform.PerspectiveTransformation;
import com.bsuir.giis.editor.transform.Rotation3D;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.util.*;
import java.util.List;

public class CanvasState {

    private final int width;
    private final int height;

    private volatile BufferedImage canvasImage;

    private Map<Point, List<MorphableShape<?>>> layersMap;

    private List<Shape<?>> shapes;

    private final List<Model3DInstance> models;
    private int activeModelIndex = -1;
    private PerspectiveTransformation perspectiveProjection;
    private boolean perspectiveEnabled = true;

    public CanvasState(int width, int height) {
        this.width = width;
        this.height = height;
        this.models = new ArrayList<>();
        this.perspectiveProjection = new PerspectiveTransformation(
            width,
            height
        );
        initialize();
    }

    private void initialize() {
        canvasImage = new BufferedImage(
            width,
            height,
            BufferedImage.TYPE_INT_ARGB
        );
        layersMap = new HashMap<>();
        shapes = new ArrayList<>();
    }

    public void setupWithStateSave(boolean isTransparentLayer) {
        baseSetup(isTransparentLayer);
    }

    public void setupCanvas(boolean isTransparentLayer) {
        baseSetup(isTransparentLayer);
        layersMap.clear();
    }

    private void baseSetup(boolean isTransparentLayer) {
        canvasImage = new BufferedImage(
            width,
            height,
            BufferedImage.TYPE_INT_ARGB
        );
        Graphics2D g2 = canvasImage.createGraphics();

        if (isTransparentLayer) {
            g2.setComposite(AlphaComposite.Clear);
            g2.fillRect(0, 0, width, height);
            g2.setComposite(AlphaComposite.SrcOver);
        } else {
            g2.setColor(Color.WHITE);
            g2.fillRect(0, 0, width, height);
        }

        g2.dispose();
    }

    public List<MorphableShape<?>> getMorphShapesInArea(
        PointArea area,
        int pixelSize
    ) {
        List<MorphableShape<?>> result = new ArrayList<>();

        int startGridX = Math.max(0, area.getMinX() / pixelSize);
        int endGridX = Math.min(width - 1, area.getMaxX() / pixelSize);

        int startGridY = Math.max(0, area.getMinY() / pixelSize);
        int endGridY = Math.min(height - 1, area.getMaxY() / pixelSize);

        for (int gx = startGridX; gx <= endGridX; gx++) {
            for (int gy = startGridY; gy <= endGridY; gy++) {
                Point targetPoint = new Point(gx, gy);

                List<MorphableShape<?>> shapesAtPoint = layersMap.get(
                    targetPoint
                );
                if (shapesAtPoint != null) {
                    result.addAll(shapesAtPoint);
                }
            }
        }

        return new ArrayList<>(new LinkedHashSet<>(result));
    }

    public void addMorphShape(Point point, MorphableShape<?> shape) {
        layersMap.computeIfAbsent(point, p -> new ArrayList<>()).add(shape);
    }

    public Optional<List<MorphableShape<?>>> getMorphShape(Point point) {
        List<MorphableShape<?>> list = layersMap.get(point);
        if (list == null || list.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(list);
    }

    public void removeMorphShape(Point point, int index) {
        List<MorphableShape<?>> list = layersMap.get(point);
        if (list != null && !list.isEmpty()) {
            list.remove(index % list.size());
            layersMap.put(point, list);
            if (list.isEmpty()) {
                layersMap.remove(point);
            }
        }
    }

    public void removeMorphShape(Point point, MorphableShape toRemove) {
        List<MorphableShape<?>> list = layersMap.get(point);
        if (list != null && !list.isEmpty()) {
            for (MorphableShape<?> shape : list) {
                if (shape.equals(toRemove)) {
                    list.remove(toRemove);
                    break;
                }
            }
            layersMap.put(point, list);
            if (list.isEmpty()) {
                layersMap.remove(point);
            }
        }
    }

    public void addShape(com.bsuir.giis.editor.model.shapes.Shape<?> shape) {
        shapes.add(shape);
    }

    public void removeShape(com.bsuir.giis.editor.model.shapes.Shape<?> shape) {
        shapes.remove(shape);
    }

    public List<Shape<?>> getAllShapes() {
        return new ArrayList<>(shapes);
    }

    public void clearShapes() {
        shapes.clear();
    }

    public BufferedImage getCanvasImage() {
        return canvasImage;
    }

    public int[] getPixelBuffer() {
        return ((DataBufferInt) canvasImage.getRaster().getDataBuffer()).getData();
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public Map<Point, List<MorphableShape<?>>> getLayersMap() {
        return layersMap;
    }

    public PerspectiveTransformation getPerspectiveProjection() {
        return perspectiveProjection;
    }

    public boolean isPerspectiveEnabled() {
        return perspectiveEnabled;
    }

    public void setPerspectiveEnabled(boolean perspectiveEnabled) {
        this.perspectiveEnabled = perspectiveEnabled;
    }


    public List<Model3DInstance> getModels() {
        return models;
    }

    public void addModel(Model3DInstance model) {
        models.add(model);
        activeModelIndex = models.size() - 1;
    }

    public void removeModel(int index) {
        if (index >= 0 && index < models.size()) {
            models.remove(index);
            if (activeModelIndex >= models.size()) {
                activeModelIndex = models.size() - 1;
            }
        }
    }

    public Model3DInstance getActiveModel() {
        if (activeModelIndex >= 0 && activeModelIndex < models.size()) {
            return models.get(activeModelIndex);
        }
        return null;
    }

    public void setActiveModelIndex(int index) {
        if (index >= -1 && index < models.size()) {
            this.activeModelIndex = index;
        }
    }

    public int getActiveModelIndex() {
        return activeModelIndex;
    }

    public int getModelCount() {
        return models.size();
    }

    public void clear() {
        initialize();
    }
}
