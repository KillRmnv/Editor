package com.bsuir.giis.editor.rendering;

import com.bsuir.giis.editor.model.CanvasState;
import com.bsuir.giis.editor.model.Model3DInstance;
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
    private int[] pixelBuffer;
    private int bufferWidth;
    private int bufferHeight;

    public CanvasRenderer(Mode mode) {
        this.mode = mode;
    }

    public void renderAll(CanvasState state, BaseLayer layer) {
        refreshBuffer(layer);

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

        for (Model3DInstance instance : state.getModels()) {
            if (instance.getModel() != null) {
                Model3DDrawable modelDrawable = new Model3DDrawable();
                Model3DParameters params = new Model3DParameters(
                    instance.getModel(),
                    instance.getRotation(),
                    state.getPerspectiveProjection(),
                    instance.getTranslateX(),
                    instance.getTranslateY(),
                    instance.getScaleFactor(),
                    instance.isReflectX(),
                    instance.isReflectY(),
                    state.isPerspectiveEnabled()
                );
                modelDrawable.draw(layer, params, mode);
            }
        }
    }
    public Color getPixelColor(BaseLayer layer, int x, int y) {
        if (pixelBuffer == null) {
            refreshBuffer(layer);
        }
        int pixelSize = layer.getPixelSize();
        int px = x / pixelSize;
        int py = y / pixelSize;
    
        if (px >= 0 && px < bufferWidth && py >= 0 && py < bufferHeight) {
            int argb = pixelBuffer[py * bufferWidth + px];
            return new Color(argb, true); // true = учитывать alpha-канал
        }
        return Color.WHITE; // пиксель вне границ — считаем фоном
    }
    public void renderShape(MorphableShape<?> shape, BaseLayer layer) {
        refreshBuffer(layer);
        shape.getDrawable().morph(layer, shape.getParameters(), mode);
        layer.repaint();
    }

    public void renderShape(Shape<?> shape, BaseLayer layer) {
        refreshBuffer(layer);
        shape.getDrawable().draw(layer, shape.getParameters(), mode);
        layer.repaint();
    }

    public void refreshBuffer(BaseLayer layer) {
        java.awt.image.BufferedImage img = layer.getCanvasImage();
        pixelBuffer = layer.getPixelBuffer();
        bufferWidth = img.getWidth();
        bufferHeight = img.getHeight();
    }

    public void invalidateBuffer() {
        pixelBuffer = null;
    }

    public void paintPixel(BaseLayer layer, int x, int y, Color color) {
        if (pixelBuffer == null) {
            refreshBuffer(layer);
        }
        int pixelSize = layer.getPixelSize();
        int px = x / pixelSize;
        int py = y / pixelSize;

        if (px >= 0 && px < bufferWidth && py >= 0 && py < bufferHeight) {
            pixelBuffer[py * bufferWidth + px] = color.getRGB();
        }
    }

    public void paintPixel(
        BaseLayer layer,
        int x,
        int y,
        RGBColor color,
        int brightness
    ) {
        if (pixelBuffer == null) {
            refreshBuffer(layer);
        }
        int pixelSize = layer.getPixelSize();
        int px = x / pixelSize;
        int py = y / pixelSize;

        if (px >= 0 && px < bufferWidth && py >= 0 && py < bufferHeight) {
            brightness = Math.max(0, Math.min(255, brightness));
            int r = color.getRed().getPrimitiveType();
            int g = color.getGreen().getPrimitiveType();
            int b = color.getBlue().getPrimitiveType();
            pixelBuffer[py * bufferWidth + px] = (brightness << 24) | (r << 16) | (g << 8) | b;
        }
    }
}
