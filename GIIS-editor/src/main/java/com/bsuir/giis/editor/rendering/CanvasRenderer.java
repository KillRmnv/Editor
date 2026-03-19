package com.bsuir.giis.editor.rendering;

import com.bsuir.giis.editor.model.CanvasState;
import com.bsuir.giis.editor.model.Model3DParameters;
import com.bsuir.giis.editor.model.shapes.Model3DDrawable;
import com.bsuir.giis.editor.model.shapes.MorphableShape;
import com.bsuir.giis.editor.model.shapes.Shape;
import com.bsuir.giis.editor.service.flow.Mode;
import java.awt.*;
import java.util.HashSet;
import java.util.Set;
import org.w3c.dom.css.RGBColor;

public class CanvasRenderer {

    private final Mode mode;

    public CanvasRenderer(Mode mode) {
        this.mode = mode;
    }

    public void renderAll(CanvasState state, BaseLayer layer) {
        Set<Shape> shapes = new HashSet<>();
        for (var entry : state.getLayersMap().entrySet()) {
            for (MorphableShape<?> shape : entry.getValue()) {
                if (shape.isVisible() && !shapes.contains(shape)) {
                    shape
                        .getDrawable()
                        .draw(layer, shape.getParameters(), mode);
                    shapes.add(shape);
                }
            }
        }
        for (Shape<?> shape : state.getAllShapes()) {
            if (shape.isVisible() && !shapes.contains(shape)) {
                shape.getDrawable().draw(layer, shape.getParameters(), mode);
                shapes.add(shape);
            }
        }

        if (state.getCurrentModel() != null) {
            Model3DDrawable modelDrawable = new Model3DDrawable();
            Model3DParameters params = state.getModelParameters();
            modelDrawable.draw(layer, params, mode);
        }
    }

    public void renderShape(MorphableShape<?> shape, BaseLayer layer) {
        shape.getDrawable().morph(layer, shape.getParameters(), mode);
        layer.repaint();
    }

    public void renderShape(Shape<?> shape, BaseLayer layer) {
        shape.getDrawable().draw(layer, shape.getParameters(), mode);
        layer.repaint();
    }

    public void paintPixel(BaseLayer layer, int x, int y, Color color) {
        int pixelSize = layer.getPixelSize();
        int px = x / pixelSize;
        int py = y / pixelSize;

        java.awt.image.BufferedImage img = layer.getCanvasImage();
        if (px >= 0 && px < img.getWidth() && py >= 0 && py < img.getHeight()) {
            img.setRGB(px, py, color.getRGB());
           
        }
    }

    public void paintPixel(
        BaseLayer layer,
        int x,
        int y,
        RGBColor color,
        int brightness
    ) {
        int pixelSize = layer.getPixelSize();
        java.awt.image.BufferedImage img = layer.getCanvasImage();
        int imgWidth = img.getWidth();
        int imgHeight = img.getHeight();
        int px = x / pixelSize;
        int py = y / pixelSize;

        if (px >= 0 && px < imgWidth && py >= 0 && py < imgHeight) {
            brightness = Math.max(0, Math.min(255, brightness));
            Color newColor = new Color(
                color.getRed().getPrimitiveType(),
                color.getGreen().getPrimitiveType(),
                color.getBlue().getPrimitiveType(),
                brightness
            );
            img.setRGB(px, py, newColor.getRGB());
            
        }
    }
}
