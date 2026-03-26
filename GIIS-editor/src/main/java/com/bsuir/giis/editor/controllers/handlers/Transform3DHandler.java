package com.bsuir.giis.editor.controllers.handlers;

import com.bsuir.giis.editor.model.CanvasState;
import com.bsuir.giis.editor.model.Model3DInstance;
import com.bsuir.giis.editor.rendering.Canvas;
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
    private int savedModelIndex = -1;

    private void moveToMoveable(Canvas canvas) {
        CanvasState srcState = canvas.getLayer().getState();
        Model3DInstance activeModel = srcState.getActiveModel();
        if (activeModel == null) return;

        savedModelIndex = srcState.getActiveModelIndex();

        Model3DInstance moveableInstance = new Model3DInstance(
            activeModel.getModel(),
            activeModel.getObjFilePath()
        );
        moveableInstance.copyTransformFrom(activeModel);

        canvas.getLayerMoveable().getState().addModel(moveableInstance);

        canvas.getLayerMoveable().renderAndRepaint();

        srcState.removeModel(savedModelIndex);
        canvas.getLayer().repaintAll();
    }

    @Override
    public void handlePress(
        Canvas canvas,
        MouseEvent mouseEvent,
        ToolContainer tool,
        ModeContainer mode,
        ModifierState modifierState
    ) {
        lastMousePos = mouseEvent.getPoint();
        activeButton = mouseEvent.getButton();
        moveToMoveable(canvas);
    }

    @Override
    public void handleMove(
        Canvas canvas,
        MouseEvent mouseEvent,
        ToolContainer tool,
        ModeContainer mode,
        ModifierState modifierState
    ) {}

    @Override
    public void handleDrag(
        Canvas canvas,
        MouseEvent mouseEvent,
        ToolContainer tool,
        ModeContainer mode,
        ModifierState modifierState
    ) {
        if (lastMousePos == null) {
            lastMousePos = mouseEvent.getPoint();
            return;
        }

        double deltaX = mouseEvent.getX() - lastMousePos.x;
        double deltaY = mouseEvent.getY() - lastMousePos.y;
        lastMousePos = mouseEvent.getPoint();

        CanvasState moveableState = canvas.getLayerMoveable().getState();
        Model3DInstance instance = moveableState.getActiveModel();
        if (instance == null) return;

        if (activeButton == MouseEvent.BUTTON1) {
            double deltaXRadians = Math.toRadians(deltaX * SENSITIVITY);
            double deltaYRadians = Math.toRadians(deltaY * SENSITIVITY);
            instance.getRotation().addDeltaY(deltaXRadians);
            instance.getRotation().addDeltaX(deltaYRadians);
        } else if (activeButton == MouseEvent.BUTTON3) {
            instance.setTranslateX(instance.getTranslateX() + deltaX);
            instance.setTranslateY(instance.getTranslateY() + deltaY);
        }

        canvas.getLayerMoveable().cleanLayer();
        canvas.getLayerMoveable().renderAndRepaint();
    }

    @Override
    public void handleRelease(
        Canvas canvas,
        MouseEvent mouseEvent,
        ToolContainer tool,
        ModeContainer mode,
        ModifierState modifierState
    ) {
        CanvasState moveableState = canvas.getLayerMoveable().getState();
        CanvasState mainState = canvas.getLayer().getState();

        Model3DInstance moveableInstance = moveableState.getActiveModel();
        if (moveableInstance != null) {
            Model3DInstance restoredInstance = new Model3DInstance(
                moveableInstance.getModel(),
                moveableInstance.getObjFilePath()
            );
            restoredInstance.copyTransformFrom(moveableInstance);
            mainState.addModel(restoredInstance);
            savedModelIndex = mainState.getActiveModelIndex();
        }

        moveableState.getModels().clear();
        moveableState.setActiveModelIndex(-1);

        canvas.getLayer().repaintAll();

        lastMousePos = null;
        activeButton = MouseEvent.NOBUTTON;
    }

    @Override
    public void handleWheel(
        Canvas canvas,
        MouseWheelEvent mouseWheelEvent,
        ToolContainer tool,
        ModeContainer mode,
        ModifierState modifierState
    ) {
        if (!modifierState.isCtrlPressed()) return;

        CanvasState state = canvas.getLayer().getState();
        Model3DInstance instance = state.getActiveModel();
        if (instance == null) return;

        double delta = -mouseWheelEvent.getWheelRotation() * ZOOM_SENSITIVITY;
        double newScale = instance.getScaleFactor() * (1.0 + delta);
        newScale = Math.max(MIN_SCALE, newScale);
        instance.setScaleFactor(newScale);

        canvas.getLayer().repaintAll();
    }
}
