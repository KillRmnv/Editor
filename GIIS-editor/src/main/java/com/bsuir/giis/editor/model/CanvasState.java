package com.bsuir.giis.editor.model;


import com.bsuir.giis.editor.model.shapes.MorphableShape;
import com.bsuir.giis.editor.model.shapes.Shape;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.*;
import java.util.List;

public class CanvasState {

    private final int width;
    private final int height;

    private volatile BufferedImage canvasImage;

    private Map<Point, List<MorphableShape<?>>> layersMap;

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

        if (isTransparentLayer) {
            g2.setComposite(AlphaComposite.Clear);
            g2.fillRect(0, 0, width, height);
            g2.setComposite(AlphaComposite.SrcOver);
        } else {
            g2.setColor(Color.WHITE);
            g2.fillRect(0, 0, width, height);
        }

        g2.dispose();
        layersMap.clear();
    }

    public List<MorphableShape<?>> getMorphShapesInArea(PointArea area, int pixelSize) {
        List<MorphableShape<?>> result = new ArrayList<>();

        int startGridX = Math.max(0, area.getMinX() / pixelSize);
        int endGridX = Math.min(width - 1, area.getMaxX() / pixelSize);

        int startGridY = Math.max(0, area.getMinY() / pixelSize);
        int endGridY = Math.min(height - 1, area.getMaxY() / pixelSize);

        for (int gx = startGridX; gx <= endGridX; gx++) {
            for (int gy = startGridY; gy <= endGridY; gy++) {
                Point targetPoint = new Point(gx, gy);

                List<MorphableShape<?>> shapesAtPoint = layersMap.get(targetPoint);
                if (shapesAtPoint != null) {

                    result.addAll(shapesAtPoint);

                }
            }
        }

        return new ArrayList<>(new LinkedHashSet<>(result));
    }
    public void addMorphShape(Point point, MorphableShape<?> shape) {
        layersMap
                .computeIfAbsent(point, p -> new ArrayList<>())
                .add(shape);
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

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public Map<Point, List<MorphableShape<?>>> getLayersMap() {
        return layersMap;
    }

    public void clear() {
        initialize();
    }
}
