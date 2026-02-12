package com.bsuir.giis.editor.controllers.handlers;

import com.bsuir.giis.editor.model.AlgorithmParameters;
import com.bsuir.giis.editor.model.MorphableShape;
import com.bsuir.giis.editor.model.Point;
import com.bsuir.giis.editor.model.PointArea;
import com.bsuir.giis.editor.service.flow.Regular;
import com.bsuir.giis.editor.utils.*;
import com.bsuir.giis.editor.view.BaseLayer;
import com.bsuir.giis.editor.view.Canvas;
import com.bsuir.giis.editor.view.TwoDimensionLayer;

import java.awt.event.MouseEvent;
import java.util.List;
import java.util.Optional;

public class MorphHandler implements DrawableHandler {
    private int tryCounter = 0;
    private PointArea previousPoint;
    private Step Step;


    public MorphHandler(BaseLayer canvas, Step step) {
        previousPoint = new PointArea(new Point(0, 0), canvas.getPixelSize(), canvas.getHitTestPolicy().calculateTolerance(canvas.getPixelSize()));
        this.Step = step;
    }

    @Override
    public void handlePress(Canvas canvas, MouseEvent mouseEvent, ToolContainer tool, ModeContainer mode) {
        int tolerance = canvas.getLayer2DMorphable().getHitTestPolicy().calculateTolerance(canvas.getLayer2DMorphable().getPixelSize());
        MorphStep morphStep = (MorphStep) Step;
        TwoDimensionLayer layer = canvas.getLayer2D();

        var morphs = canvas.getLayer2DMorphable().getState().getLayersMap();
        if (previousPoint.strongIntersects(new PointArea(
                new Point(mouseEvent.getX(), mouseEvent.getY()), canvas.getLayer2D().getPixelSize(), tolerance))) {
            boolean someChanges = false;
            morphs = canvas.getLayer2DMorphable().getState().getLayersMap();
            if (!morphs.isEmpty()) {
                for (var point : morphs.keySet()) {
                    MorphableShape<?> shape = morphs.get(point).getFirst();
                    canvas.getLayer2D().getState().addMorphShape(point, shape);
                    canvas.getLayer2D().repaintShape(shape);
                    someChanges = true;
                }
            }
            morphs.clear();
            tryCounter++;
            List<MorphableShape<?>> shapes = layer.getState().getMorphShapesInArea(previousPoint, canvas.getLayer2D().getPixelSize());
            if (shapes != null && !shapes.isEmpty()) {
                MorphableShape<?> shape = shapes.get(tryCounter % shapes.size());

                canvas.getLayer2DMorphable().addShape(shape.getParameters().getStartEndPoint().getFirst(), shape);
                canvas.getLayer2DMorphable().addShape(shape.getParameters().getStartEndPoint().getLast(), shape);
                canvas.getLayer2D().getState().removeMorphShape(shape.getParameters().getStartEndPoint().getFirst(), tryCounter % shapes.size());
                canvas.getLayer2D().getState().removeMorphShape(shape.getParameters().getStartEndPoint().getLast(), tryCounter % shapes.size());
                morphStep.setup(new Point(mouseEvent.getX(), mouseEvent.getY()), shape);
                someChanges = true;
            }
            if (someChanges)
                canvas.getLayer2DMorphable().repaint();
        } else {
            tryCounter = 0;
            Point newPoint = new Point(mouseEvent.getX(), mouseEvent.getY());
            if (!morphs.isEmpty()) {
                for (var point : morphs.keySet()) {
                    for (MorphableShape<?> shape : morphs.get(point)) {
                        List<Point> points = shape.getParameters().getPoints();
                        for (Point p : points) {
                            PointArea area = new PointArea(p, canvas.getLayer2DMorphable().getPixelSize(), tolerance);
                            if (area.contains(mouseEvent.getX(), mouseEvent.getY())) {
                                morphStep.setup(p, shape);
//                                canvas.getLayer2DMorphable().repaintShape(shape);
                                return;
                            }

                        }
                    }

                }
            }
            morphs.clear();
            morphStep.clean();
            previousPoint = new PointArea(newPoint, canvas.getLayer2DMorphable().getPixelSize(), tolerance);
            Optional<MorphableShape<?>> shape = layer.getShape(newPoint, tryCounter);
            shape.ifPresent(morphableShape -> {
                canvas.getLayer2DMorphable().repaintShape(morphableShape);
                canvas.getLayer2DMorphable().addShape(newPoint, morphableShape);
                canvas.getLayer2DMorphable().addShape(morphableShape.getParameters().getStartEndPoint().getLast(), morphableShape);
                canvas.getLayer2D().getState().removeMorphShape(newPoint, 0);
                canvas.getLayer2D().getState().removeMorphShape(morphableShape.getParameters().getStartEndPoint().getLast(), 0);
                canvas.getLayer2D().repaint();
            });

        }

    }

    @Override
    public void handleMove(Canvas canvas, MouseEvent mouseEvent, ToolContainer tool, ModeContainer mode) {
        //pass
    }

    @Override
    public void handleDrag(Canvas canvas, MouseEvent mouseEvent, ToolContainer tool, ModeContainer mode) {
        MorphStep morphStep = (MorphStep) Step;
        if (morphStep.isReady()) {
            AlgorithmParameters params = morphStep.getMorphableShape().getParameters();
            morphStep.getMorphableShape().getDrawable().morph(canvas.getLayer2DMorphable(), params, new Regular());
            morphStep.setPenStep(new PenStep(mouseEvent.getX(), mouseEvent.getY()));
        }
    }
}
