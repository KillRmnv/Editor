package com.bsuir.giis.editor.controllers.handlers;

import com.bsuir.giis.editor.model.AlgorithmParameters;
import com.bsuir.giis.editor.model.Point;
import com.bsuir.giis.editor.model.PointArea;
import com.bsuir.giis.editor.model.PointShapeParameters;
import com.bsuir.giis.editor.model.shapes.Drawable;
import com.bsuir.giis.editor.model.shapes.MorphableShape;
import com.bsuir.giis.editor.rendering.Canvas;
import com.bsuir.giis.editor.service.flow.Regular;
import com.bsuir.giis.editor.utils.*;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Robot;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.Optional;

public class ParametersCurveHandler implements DrawableHandler {

    private MultiStep multiStep;

    public ParametersCurveHandler(Step step) {
        multiStep = (MultiStep) step;
    }

    @Override
    public void handlePress(Canvas canvas, MouseEvent mouseEvent, ToolContainer tool, ModeContainer mode, ModifierState modifierState) {
        int x=mouseEvent.getX();
        int y=mouseEvent.getY();
        System.out.println("(not snapped)x: "+x+" y: "+y);
        if(modifierState.isShiftPressed()&&(multiStep.getStepIndex()==4||multiStep.getStepIndex()==1)) {
            System.out.println("To snap point");
            Point snappedPoint = snapToExistingPoint(canvas, mouseEvent.getX(), mouseEvent.getY(), modifierState);
             x = snappedPoint.getX();
             y = snappedPoint.getY();
            System.out.println("(snapped)x: "+x+" y: "+y);
        }
        multiStep.setStep(new PenStep(x, y));


        if (multiStep.isReady()) {
            AlgorithmParameters parameters = new PointShapeParameters(multiStep);

            canvas.getLayer2DMoveable().cleanLayer();

            new Thread(() -> ((Drawable) tool.getTool()).draw(canvas.getLayer2D(), parameters, mode.getMode())).start();
            addToLayer(canvas.getLayer2D(), tool, parameters, mouseEvent);
            multiStep.clean();
        }
    }

    @Override
    public void handleMove(Canvas canvas, MouseEvent mouseEvent, ToolContainer tool, ModeContainer mode, ModifierState modifierState) {
        int x=mouseEvent.getX();
        int y=mouseEvent.getY();

        MultiStep fakeMultistep = null;
        if (multiStep.getStep(2).isReady()) {

            fakeMultistep = new MultiStep(multiStep.getSteps(), 3);
            fakeMultistep.setStep(3, new PenStep(x, y));

        } else if (multiStep.getStep(1).isReady()) {
            PenStep penStepStart = (PenStep) multiStep.getStep(0);
            PenStep penStepEnd = (PenStep) multiStep.getStep(1);
            fakeMultistep = new MultiStep(multiStep.getSteps(), 2);
            fakeMultistep.setStep(3, new PenStep(
                    getPointAtThreeQuarters(penStepStart.getPoint(),penStepEnd.getPoint())
            ));
            fakeMultistep.setStep(2, new PenStep(x, y));

        } else if (multiStep.getStep(0).isReady()) {

            fakeMultistep = new MultiStep(multiStep.getSteps(), 1);
            fakeMultistep.setStep(1, new PenStep(x, y));


            PenStep penStep = (PenStep) fakeMultistep.getStep(0);
            fakeMultistep.setStep(2, new PenStep(getPointAtQuarter(
                    penStep.getPoint(),new Point(x,y)
            )));
            fakeMultistep.setStep(3, new PenStep(getPointAtThreeQuarters(
                    penStep.getPoint(),new Point(x,y)
            )));

        }
        if (fakeMultistep != null) {

            canvas.getLayer2DMoveable().cleanLayer();
            

            
            AlgorithmParameters parameters = new PointShapeParameters(fakeMultistep);
            new Thread(() ->
                    ((Drawable) tool.getTool())
                            .draw(canvas.getLayer2DMoveable(), parameters, new Regular())
            ).start();
        }
        
    }

    private  Point getPointAtQuarter(Point start, Point end) {
        int x = start.getX() + (end.getX() - start.getX()) / 4;
        int y = start.getY() + (end.getY() - start.getY()) / 4;
        return new Point(x, y);
    }

    private  Point getPointAtThreeQuarters(Point start, Point end) {
        int x = start.getX() + 3 * (end.getX() - start.getX()) / 4;
        int y = start.getY() + 3 * (end.getY() - start.getY()) / 4;
        return new Point(x, y);
    }
    
    @Override
    public void handleDrag(Canvas canvas, MouseEvent mouseEvent, ToolContainer tool, ModeContainer mode, ModifierState modifierState) {
        //pass
    }

    @Override
    public void handleRelease(Canvas canvas,MouseEvent mouseEvent, ToolContainer tool, ModeContainer mode, ModifierState modifierState) {

    }
    private Point snapToExistingPoint(Canvas canvas, int screenX, int screenY, ModifierState modifierState) {
        if (!modifierState.isShiftPressed()) {
            return new Point(screenX, screenY);
        }

        int pixelSize = canvas.getLayer2D().getPixelSize();

        int gridX = screenX / pixelSize;
        int gridY = screenY / pixelSize;
        Point currentGridPoint = new Point(gridX, gridY);

        PointArea area = new PointArea(new Point(screenX, screenY), pixelSize, canvas.getLayer2D().getHitTestPolicy().calculateTolerance(pixelSize));

        List<MorphableShape<?>> shapes = canvas.getLayer2D().getState()
                .getMorphShapesInArea(area, pixelSize);


        Optional<Point> nearestGridPoint = findNearestPoint(shapes, currentGridPoint);

        if (nearestGridPoint.isPresent()) {
            Point snappedGrid = nearestGridPoint.get();
            return new Point(snappedGrid.getX() * pixelSize, snappedGrid.getY() * pixelSize);
        }

        return new Point(screenX, screenY);
    }

    private Optional<Point> findNearestPoint(List<MorphableShape<?>> shapes, Point currentGridPoint) {
        if (shapes == null || shapes.isEmpty()) {
            return Optional.empty();
        }

        Point nearest = null;
        double minDistance = Double.MAX_VALUE;

        for (MorphableShape<?> shape : shapes) {
            for (var paramPoint : shape.getParameters().getPoints()) {
                double dx = paramPoint.getX() - currentGridPoint.getX();
                double dy = paramPoint.getY() - currentGridPoint.getY();
                double distance = Math.sqrt(dx * dx + dy * dy);

                if (distance < minDistance) {
                    minDistance = distance;
                    nearest = paramPoint;
                }
            }
        }

        return Optional.ofNullable(nearest);
    }

}
