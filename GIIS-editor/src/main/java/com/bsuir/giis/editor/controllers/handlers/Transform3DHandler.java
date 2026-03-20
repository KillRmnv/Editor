package com.bsuir.giis.editor.controllers.handlers;

import com.bsuir.giis.editor.model.CanvasState;
import com.bsuir.giis.editor.model.dimensions.Model3D;
import com.bsuir.giis.editor.rendering.Canvas;
import com.bsuir.giis.editor.transform.Rotation3D;
import com.bsuir.giis.editor.utils.ModeContainer;
import com.bsuir.giis.editor.utils.ModifierState;
import com.bsuir.giis.editor.utils.ToolContainer;

import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;

public class Transform3DHandler implements DrawableHandler {
    private static final double SENSITIVITY = 0.5;
    private static final double ZOOM_SENSITIVITY = 0.1;
    private static final double MIN_SCALE = 0.05;
    private Point lastMousePos;
    private int activeButton = MouseEvent.NOBUTTON;

    private void moveToMoveable(Canvas canvas) {
        Model3D model = canvas.getLayer2D().getState().getCurrentModel();
        if (model == null) return;

        CanvasState srcState = canvas.getLayer2D().getState();
        Rotation3D srcRotation = srcState.getCurrentRotation();
        CanvasState dstState = canvas.getLayer2DMoveable().getState();

        dstState.setCurrentModel(model);
        dstState.setCurrentRotation(
                new Rotation3D(srcRotation.getAngleX(), srcRotation.getAngleY(), srcRotation.getAngleZ())
        );
        dstState.setTranslateX(srcState.getTranslateX());
        dstState.setTranslateY(srcState.getTranslateY());
        dstState.setScaleFactor(srcState.getScaleFactor());
        dstState.setReflectX(srcState.isReflectX());
        dstState.setReflectY(srcState.isReflectY());

        canvas.getLayer2DMoveable().renderAndRepaint();
        srcState.clearCurrentModel();
        canvas.getLayer2D().repaintAll();
    }

    @Override
    public void handlePress(Canvas canvas, MouseEvent mouseEvent, ToolContainer tool,
                           ModeContainer mode, ModifierState modifierState) {
        lastMousePos = mouseEvent.getPoint();
        activeButton = mouseEvent.getButton();
        moveToMoveable(canvas);
    }

    @Override
    public void handleMove(Canvas canvas, MouseEvent mouseEvent, ToolContainer tool,
                          ModeContainer mode, ModifierState modifierState) {
    }

    @Override
    public void handleDrag(Canvas canvas, MouseEvent mouseEvent, ToolContainer tool,
                          ModeContainer mode, ModifierState modifierState) {
        if (lastMousePos == null) {
            lastMousePos = mouseEvent.getPoint();
            return;
        }

        double deltaX = mouseEvent.getX() - lastMousePos.x;
        double deltaY = mouseEvent.getY() - lastMousePos.y;
        lastMousePos = mouseEvent.getPoint();

        CanvasState moveableState = canvas.getLayer2DMoveable().getState();

        if (activeButton == MouseEvent.BUTTON1) {
            double deltaXRadians = Math.toRadians(deltaX * SENSITIVITY);
            double deltaYRadians = Math.toRadians(deltaY * SENSITIVITY);
            moveableState.getCurrentRotation().addDeltaY(deltaXRadians);
            moveableState.getCurrentRotation().addDeltaX(deltaYRadians);
        } else if (activeButton == MouseEvent.BUTTON3) {
            moveableState.setTranslateX(moveableState.getTranslateX() + deltaX);
            moveableState.setTranslateY(moveableState.getTranslateY() + deltaY);
        }

        canvas.getLayer2DMoveable().cleanLayer();
        canvas.getLayer2DMoveable().renderAndRepaint();
    }

    @Override
    public void handleRelease(Canvas canvas, MouseEvent mouseEvent, ToolContainer tool,
                             ModeContainer mode, ModifierState modifierState) {
        CanvasState moveableState = canvas.getLayer2DMoveable().getState();
        CanvasState mainState = canvas.getLayer2D().getState();

        Model3D model = moveableState.getCurrentModel();
        Rotation3D rotation = moveableState.getCurrentRotation();

        if (model != null) {
            mainState.setCurrentModel(model);
            mainState.setCurrentRotation(
                    new Rotation3D(rotation.getAngleX(), rotation.getAngleY(), rotation.getAngleZ())
            );
            mainState.setTranslateX(moveableState.getTranslateX());
            mainState.setTranslateY(moveableState.getTranslateY());
            mainState.setScaleFactor(moveableState.getScaleFactor());
            mainState.setReflectX(moveableState.isReflectX());
            mainState.setReflectY(moveableState.isReflectY());
            moveableState.clearCurrentModel();
        }

        canvas.getLayer2D().cleanLayer();
        canvas.getLayer2D().renderAndRepaint();

        lastMousePos = null;
        activeButton = MouseEvent.NOBUTTON;
    }

    @Override
    public void handleWheel(Canvas canvas, MouseWheelEvent mouseWheelEvent, ToolContainer tool,
                            ModeContainer mode, ModifierState modifierState) {
        if (!modifierState.isCtrlPressed()) return;

        CanvasState state = canvas.getLayer2D().getState();
        if (state.getCurrentModel() == null) return;

        double delta = -mouseWheelEvent.getWheelRotation() * ZOOM_SENSITIVITY;
        double newScale = state.getScaleFactor() * (1.0 + delta);
        newScale = Math.max(MIN_SCALE, newScale);
        state.setScaleFactor(newScale);

        canvas.getLayer2D().cleanLayer();
        canvas.getLayer2D().renderAndRepaint();
    }
}
