package com.bsuir.giis.editor.rendering;

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

/**
 * Base rendering layer backed by a pixel buffer ({@link CanvasState}).
 *
 * <h3>Method categories</h3>
 * <ul>
 *   <li><b>State-clearing</b> — wipe {@code layersMap}, shapes, and pixel buffer:
 *       {@link #cleanLayer()}, {@link #setupCanvas(int, int)}</li>
 *   <li><b>Image-only clearing</b> — wipe pixel buffer but preserve {@code layersMap} / shapes:
 *       {@link #clearImage()}, {@link #repaintAll()}</li>
 *   <li><b>Rendering</b> — redraw existing state into the pixel buffer:
 *       {@link #renderAndRepaint()}, {@link #repaintShape(MorphableShape)},
 *       {@link #repaintShape(Shape)}, {@link #paintPixel(int, int, Color)}</li>
 *   <li><b>Data access / mutation</b> — read or modify stored shapes:
 *       {@link #addShape(Point, MorphableShape)}, {@link #addShape(Shape)},
 *       {@link #removeShape(Shape)}, {@link #getShape(Point)},
 *       {@link #getAllShapes()}, {@link #getState()}</li>
 * </ul>
 */
public class BaseLayer extends JPanel {
    protected int pixelSize;
    protected int width;
    protected int height;
    protected boolean isTransparentLayer;
    protected CanvasState state;
    protected CanvasRenderer renderer;
    public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	public boolean isTransparentLayer() {
		return isTransparentLayer;
	}

	public CanvasRenderer getRenderer() {
		return renderer;
	}

	protected HitTestPolicy hitTestPolicy = new HitTestPolicy();

    /**
     * Creates a new layer. Calls {@link #setupCanvas} internally, which
     * <b>clears all state</b> (layersMap, shapes, pixel buffer).
     */
    public BaseLayer(int width, int height) {
        this.width = width;
        this.height = height;
        this.state = new CanvasState(width, height);
        this.renderer = new CanvasRenderer(new Regular());
        setupCanvas(width, height);
    }

    /** @return the hit-test policy used for point-in-shape queries */
    public HitTestPolicy getHitTestPolicy() {
        return hitTestPolicy;
    }

    /**
     * Adds a morphable shape at the given grid point.
     * <b>State mutation only</b> — no rendering is performed.
     */
    public void addShape(Point point, MorphableShape<?> shape) {
        state.addMorphShape(point, shape);
    }

    /**
     * Returns all morphable shapes registered at the given grid point.
     * <b>Read-only</b> — no state or rendering changes.
     */
    public Optional<List<MorphableShape<?>>> getShape(Point point) {
        return state.getMorphShape(point);
    }

    /**
     * Re-renders a single morphable shape (via {@code morph()}) into the pixel buffer
     * and triggers a Swing repaint. <b>Rendering only</b> — does not modify state.
     */
    public void repaintShape(MorphableShape<?> shape) {
        renderer.renderShape(shape, this);
    }

    /**
     * Re-renders a single regular shape into the pixel buffer
     * and triggers a Swing repaint. <b>Rendering only</b> — does not modify state.
     */
    public void repaintShape(Shape<?> shape) {
        renderer.renderShape(shape, this);
    }

    /**
     * Sets opacity and updates {@link #isTransparentLayer} flag.
     * <b>No rendering or state clearing</b> — only metadata change.
     */
    @Override
    public void setOpaque(boolean isOpaque) {
        super.setOpaque(isOpaque);
        this.isTransparentLayer = !isOpaque;
    }

    /** @return current pixel size (zoom/grid factor) */
    public int getPixelSize() {
        return pixelSize;
    }

    /** @return logical layer width in grid cells */
    public int getLayerWidth() {
        return width;
    }

    /** @return logical layer height in grid cells */
    public int getLayerHeight() {
        return height;
    }

    /**
     * Full repaint: recreates the pixel buffer (preserving {@code layersMap} and shapes),
     * then re-renders everything. <b>Image-only clearing</b> — state is kept intact.
     * Equivalent to: {@code setupWithStateSave} → {@code renderAll} → Swing repaint.
     */
    public void repaintAll() {
        state.setupWithStateSave(isTransparentLayer);
        baseRepaintSetup(width, height);
        renderer.renderAll(state, this);
        repaintLayer();
    }

    /**
     * Recalculates preferred size and sets background color.
     * <b>Utility only</b> — no state or pixel changes.
     */
    private void baseRepaintSetup(int width, int height) {
        int displayWidth = width * (pixelSize > 0 ? pixelSize : 1);
        int displayHeight = height * (pixelSize > 0 ? pixelSize : 1);
        setPreferredSize(new Dimension(displayWidth, displayHeight));

        if (!isTransparentLayer) {
            setBackground(Color.WHITE);
        }
    }

    /** Triggers Swing repaint on this layer and its parent container. */
    private void repaintLayer() {
        super.repaint();
        if (getParent() != null) {
            getParent().repaint();
        }
    }

    /**
     * Overridden to prevent unnecessary repainting when renderer/state are not ready.
     * Delegates to {@link #repaintLayer()}.
     */
    @Override
    public void repaint() {
        if (renderer == null || state == null) {
            return;
        }
        repaintLayer();
    }

    /**
     * Re-renders all shapes and 3D models from the current state into the pixel buffer,
     * then triggers a Swing repaint. <b>Rendering only</b> — does not clear or modify state.
     */
    public void renderAndRepaint() {
        if (renderer == null || state == null) {
            return;
        }
        renderer.renderAll(state, this);
        repaintLayer();
    }

    /**
     * <b>⚠ State-clearing:</b> recreates the pixel buffer, clears
     * {@code layersMap}, clears regular shapes, and invalidates the renderer buffer.
     * Use {@link #clearImage()} or {@link #repaintAll()} if you need to preserve state.
     */
    protected void setupCanvas(int width, int height) {
        state.setupCanvas(isTransparentLayer);
        renderer.invalidateBuffer();
        baseRepaintSetup(width, height);
    }

    /**
     * Paints a single pixel into the renderer's buffer.
     * <b>Rendering only</b> — no state changes.
     */
    public void paintPixel(int x, int y, Color color) {
        renderer.paintPixel(this, x, y, color);
    }

    /**
     * Swing painting callback — draws the current {@link CanvasState} image onto this panel.
     * <b>Read-only</b> — does not modify state or pixel buffer.
     */
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

    /**
     * <b>⚠ State-clearing:</b> full reset — clears pixel buffer, {@code layersMap},
     * and regular shapes. Equivalent to {@link #setupCanvas(int, int)}.
     * Prefer {@link #clearImage()} if you want to keep morphable shapes.
     */
    public void cleanLayer() {
        setupCanvas(width, height);
    }

    /**
     * <b>Image-only clearing:</b> recreates the pixel buffer (transparent) and
     * invalidates the renderer buffer, but <b>preserves</b> {@code layersMap}
     * and regular shapes in {@link CanvasState}.
     */
    public void clearImage() {
        state.setupWithStateSave(true);
        renderer.invalidateBuffer();
    }

    /** @return the underlying pixel buffer image. Read-only. */
    public BufferedImage getCanvasImage() {
        return state.getCanvasImage();
    }

    /** @return raw pixel int array for direct buffer manipulation. */
    public int[] getPixelBuffer() {
        return state.getPixelBuffer();
    }

    /** @return buffer width in pixels. */
    public int getBufferWidth() {
        return state.getWidth();
    }

    /** @return buffer height in pixels. */
    public int getBufferHeight() {
        return state.getHeight();
    }

    /**
     * @return the mutable {@link CanvasState} backing this layer.
     *         Direct modifications affect what {@link #renderAndRepaint()} draws.
     */
    public CanvasState getState() {
        return state;
    }

    /**
     * Adds a regular (non-morphable) shape to the state.
     * <b>State mutation only</b> — no rendering. Call {@link #renderAndRepaint()} afterward.
     */
    public void addShape(Shape<?> shape) {
        state.addShape(shape);
    }

    /** @return defensive copy of all regular shapes. Read-only. */
    public List<Shape<?>> getAllShapes() {
        return state.getAllShapes();
    }

    /**
     * Removes a regular shape from the state.
     * <b>State mutation only</b> — no rendering. Call {@link #renderAndRepaint()} afterward.
     */
    public void removeShape(Shape<?> shape) {
        state.removeShape(shape);
    }
}
