package com.bsuir.giis.editor.rendering;

import com.bsuir.giis.editor.model.CanvasState;
import com.bsuir.giis.editor.model.Model3DParameters;
import com.bsuir.giis.editor.model.shapes.Model3DDrawable;
import com.bsuir.giis.editor.model.shapes.MorphableShape;
import com.bsuir.giis.editor.model.shapes.Shape;
import com.bsuir.giis.editor.service.flow.Mode;

import org.w3c.dom.css.RGBColor;

import java.awt.*;
import java.util.HashSet;
import java.util.Set;

public class CanvasRenderer {
    private final Mode mode;

    public CanvasRenderer(Mode mode) {
        this.mode = mode;
    }
    //TODO:add parallelism?
    public void renderAll(CanvasState state, BaseLayer layer) {
        Set<Shape> shapes = new HashSet<>();
        for (var entry : state.getLayersMap().entrySet()) {
            for (MorphableShape<?> shape : entry.getValue()) {
                if (shape.isVisible()&&!shapes.contains(shape)) {
                    shape.getDrawable().draw(layer, shape.getParameters(), mode);
                    shapes.add(shape);
                }
            }
        }
        for (Shape<?> shape : state.getAllShapes()) {
            if (shape.isVisible()&&!shapes.contains(shape)) {
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
    }

    public void renderShape(Shape<?> shape, BaseLayer layer) {
        shape.getDrawable().draw(layer, shape.getParameters(), mode);
    }


    public void paintPixel(BaseLayer layer, int x, int y, Color color) {
        int pixelSize = layer.getPixelSize();
        int px = x / pixelSize;
        int py = y / pixelSize;

        if (px >= 0 && px < layer.getWidth() && py >= 0 && py < layer.getHeight()) {
            layer.getCanvasImage().setRGB(px, py, color.getRGB());
            layer.repaint(
                    px * pixelSize,
                    py * pixelSize,
                    pixelSize,
                    pixelSize
            );
        }
    }

    public void paintPixel(BaseLayer layer, int x, int y, RGBColor color, int brightness) {
        int pixelSize = layer.getPixelSize();
        int width = layer.getWidth();
        int height = layer.getHeight();
        int px = x / pixelSize;
        int py = y / pixelSize;

        if (px >= 0 && px < width && py >= 0 && py < height) {
            brightness = Math.max(0, Math.min(255, brightness));
            Color newColor = new Color(
                    color.getRed().getPrimitiveType(),
                    color.getGreen().getPrimitiveType(),
                    color.getBlue().getPrimitiveType(),
                    brightness
            );
            layer.getCanvasImage().setRGB(px, py, newColor.getRGB());
            layer.repaint(
                    px * pixelSize,
                    py * pixelSize,
                    pixelSize,
                    pixelSize
            );
        }
    }

}
