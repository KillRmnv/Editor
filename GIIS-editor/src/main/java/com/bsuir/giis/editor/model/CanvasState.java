package com.bsuir.giis.editor.model;

import com.bsuir.giis.editor.service.flow.HitTestPolicy;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.*;
import java.util.List;

public class CanvasState {
    private final int width;
    private final int height;
    private volatile BufferedImage canvasImage;
    private Map<PointArea, List<MorphableShape<?>>> layersMap;
    private List<Shape<?>> shapes;

    public CanvasState(int width, int height) {
        this.width = width;
        this.height = height;
        initialize();
    }

    private void initialize() {
        canvasImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        layersMap = new HashMap<>();
        shapes = new ArrayList<>();
    }

    public void setupCanvas(boolean isTransparentLayer) {
        canvasImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = canvasImage.createGraphics();
        layersMap = new HashMap<>();

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

    public void addMorphShape(Point point, MorphableShape<?> shape, HitTestPolicy hitTestPolicy, int pixelSize) {
        PointArea area = hitTestPolicy.createPointArea(point, pixelSize);
        if (!layersMap.containsKey(area)) {
            List<MorphableShape<?>> shapeList = new ArrayList<>();
            shapeList.add(shape);
            layersMap.put(area, shapeList);
        } else {
            layersMap.get(area).add(shape);
        }
    }

    public List<MorphableShape<?>> getMorphShapes(Point point, HitTestPolicy hitTestPolicy, int pixelSize) {
        PointArea area = hitTestPolicy.createPointArea(point, pixelSize);
        return layersMap.get(area);
    }

    public Optional<MorphableShape<?>> getMorphShape(Point point, int tryCounter, HitTestPolicy hitTestPolicy, int pixelSize) {
        PointArea area = hitTestPolicy.createPointArea(point, pixelSize);
        if (!layersMap.containsKey(area)) {
            List<MorphableShape<?>> morphShapes=layersMap.get(area);
            return Optional.of(morphShapes.get(tryCounter%morphShapes.size()));
        }
        return Optional.empty();
    }

    public void removeMorphShape(Point point, int tryCounter, HitTestPolicy hitTestPolicy, int pixelSize) {
        PointArea area = hitTestPolicy.createPointArea(point, pixelSize);
        layersMap.get(area).remove(tryCounter);
    }

    public Map<PointArea, List<MorphableShape<?>>> getLayersMap() {
        return layersMap;
    }

    public BufferedImage getCanvasImage() {
        return canvasImage;
    }

    public void setCanvasImage(BufferedImage canvasImage) {
        this.canvasImage = canvasImage;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public void clear() {
        initialize();
    }

    // Методы для работы с обычными Shape
    public void addShape(Shape<?> shape) {
        shapes.add(shape);
    }

    public List<Shape<?>> getAllShapes() {
        return new ArrayList<>(shapes);
    }

    public void removeShape(Shape<?> shape) {
        shapes.remove(shape);
    }

    public void clearShapes() {
        shapes.clear();
    }
}
