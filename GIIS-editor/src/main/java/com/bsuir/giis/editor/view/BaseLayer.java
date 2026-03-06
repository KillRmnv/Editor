package com.bsuir.giis.editor.view;

import com.bsuir.giis.editor.model.CanvasState;
import com.bsuir.giis.editor.model.shapes.MorphableShape;
import com.bsuir.giis.editor.model.Point;
import com.bsuir.giis.editor.model.shapes.Shape;
import com.bsuir.giis.editor.service.flow.HitTestPolicy;
import com.bsuir.giis.editor.service.flow.Regular;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.Optional;

public class BaseLayer extends JPanel {
    protected int pixelSize;
    protected int width;
    protected int height;
    protected boolean isTransparentLayer;
    protected CanvasState state;
    protected CanvasRenderer renderer;
    protected HitTestPolicy hitTestPolicy = new HitTestPolicy();

    public BaseLayer(int width, int height) {
        this.width = width;
        this.height = height;
        this.state = new CanvasState(width, height);
        this.renderer = new CanvasRenderer(new Regular());
        setupCanvas(width, height);
    }

    public HitTestPolicy getHitTestPolicy() {
        return hitTestPolicy;
    }

    public void addShape(Point point, MorphableShape<?> shape) {
        state.addMorphShape(point, shape);
    }

    public Optional<List<MorphableShape<?>>> getShape(Point point) {
        return state.getMorphShape(point);
    }


    public void repaintShape(MorphableShape<?> shape) {
        renderer.renderShape(shape, this);
    }

    public void repaintShape(Shape<?> shape) {
        renderer.renderShape(shape, this);
    }



    @Override
    public void setOpaque(boolean isOpaque) {
        super.setOpaque(isOpaque);
        this.isTransparentLayer = !isOpaque;
    }

    public int getPixelSize() {
        return pixelSize;
    }

    public int getLayerWidth() {
        return width;
    }

    public int getLayerHeight() {
        return height;
    }

    public void repaintAll(){
        state.setupWithStateSave(isTransparentLayer);
        baseRepaintSetup(width, height);
        renderer.renderAll(state, this);
    }

    private void baseRepaintSetup(int width, int height) {
        int displayWidth = width * (pixelSize > 0 ? pixelSize : 1);
        int displayHeight = height * (pixelSize > 0 ? pixelSize : 1);
        setPreferredSize(new Dimension(displayWidth, displayHeight));

        if (!isTransparentLayer) {
            setBackground(Color.WHITE);
        }
        super.repaint();
    }

    @Override
    public void repaint() {
        if (renderer == null || state == null) {
            return;
        }
        super.repaint();
        renderer.renderAll(state, this);
    }

    protected void setupCanvas(int width, int height) {
        state.setupCanvas(isTransparentLayer);

        baseRepaintSetup(width, height);
    }

    public void paintPixel(int x, int y, Color color) {
        renderer.paintPixel(this, x, y, color);
    }


    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(
                state.getCanvasImage(),
                0, 0,
                width * pixelSize,
                height * pixelSize,
                null
        );
    }

    public void cleanLayer() {
        setupCanvas(width, height);
    }

    public BufferedImage getCanvasImage() {
        return state.getCanvasImage();
    }

    public CanvasState getState() {
        return state;
    }

    public void addShape(Shape<?> shape) {
        state.addShape(shape);
    }

    public List<Shape<?>> getAllShapes() {
        return state.getAllShapes();
    }

    public void removeShape(Shape<?> shape) {
        state.removeShape(shape);
    }
}
