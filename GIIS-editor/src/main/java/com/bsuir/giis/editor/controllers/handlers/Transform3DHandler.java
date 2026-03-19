package com.bsuir.giis.editor.controllers.handlers;

import com.bsuir.giis.editor.model.dimensions.Model3D;
import com.bsuir.giis.editor.rendering.Canvas;
import com.bsuir.giis.editor.transform.Rotation3D;
import com.bsuir.giis.editor.utils.ModeContainer;
import com.bsuir.giis.editor.utils.ModifierState;
import com.bsuir.giis.editor.utils.ToolContainer;

import java.awt.Point;
import java.awt.event.MouseEvent;

public class Transform3DHandler implements DrawableHandler {
    private static final double SENSITIVITY = 0.5;
    private Point lastMousePos;

    @Override
    public void handlePress(Canvas canvas, MouseEvent mouseEvent, ToolContainer tool, 
                           ModeContainer mode, ModifierState modifierState) {
        lastMousePos = mouseEvent.getPoint();
        
        Model3D model = canvas.getLayer2D().getState().getCurrentModel();
        
        if (model != null) {
            Rotation3D srcRotation = canvas.getLayer2D().getState().getCurrentRotation();
            canvas.getLayer2DMoveable().getState().setCurrentModel(model);
            canvas.getLayer2DMoveable().getState().setCurrentRotation(
                new Rotation3D(srcRotation.getAngleX(), srcRotation.getAngleY(), srcRotation.getAngleZ())
            );
            canvas.getLayer2DMoveable().renderAndRepaint();
            canvas.getLayer2D().getState().clearCurrentModel();
            canvas.getLayer2D().repaintAll();
        } 
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

        double deltaXRadians = Math.toRadians(deltaX * SENSITIVITY);
        double deltaYRadians = Math.toRadians(deltaY * SENSITIVITY);

        canvas.getLayer2DMoveable().getState().getCurrentRotation().addDeltaY(deltaXRadians);
        canvas.getLayer2DMoveable().getState().getCurrentRotation().addDeltaX(deltaYRadians);

        lastMousePos = mouseEvent.getPoint();
        
        canvas.getLayer2DMoveable().cleanLayer();
        canvas.getLayer2DMoveable().renderAndRepaint();
    }

    @Override
    public void handleRelease(Canvas canvas, MouseEvent mouseEvent, ToolContainer tool,
                             ModeContainer mode, ModifierState modifierState) {
        Model3D model = canvas.getLayer2DMoveable().getState().getCurrentModel();
        Rotation3D rotation = canvas.getLayer2DMoveable().getState().getCurrentRotation();
        
        if (model != null) {
            canvas.getLayer2D().getState().setCurrentModel(model);
            canvas.getLayer2D().getState().setCurrentRotation(
                new Rotation3D(rotation.getAngleX(), rotation.getAngleY(), rotation.getAngleZ())
            );
            canvas.getLayer2DMoveable().getState().clearCurrentModel();
        }
        
        canvas.getLayer2D().cleanLayer();
        canvas.getLayer2D().renderAndRepaint();
        
        lastMousePos = null;
    }
}
