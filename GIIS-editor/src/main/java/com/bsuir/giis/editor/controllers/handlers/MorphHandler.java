package com.bsuir.giis.editor.controllers.handlers;

import com.bsuir.giis.editor.model.Point;
import com.bsuir.giis.editor.model.PointArea;
import com.bsuir.giis.editor.model.shapes.MorphableShape;
import com.bsuir.giis.editor.rendering.BaseLayer;
import com.bsuir.giis.editor.rendering.Canvas;
import com.bsuir.giis.editor.rendering.TwoDimensionLayer;
import com.bsuir.giis.editor.utils.ModeContainer;
import com.bsuir.giis.editor.utils.ModifierState;
import com.bsuir.giis.editor.utils.MorphStep;
import com.bsuir.giis.editor.utils.Step;
import com.bsuir.giis.editor.utils.ToolContainer;
import java.awt.event.MouseEvent;
import java.util.List;

public class MorphHandler implements DrawableHandler {

    private int tryCounter = 0;
    private PointArea previousPoint;
    private Step step;

    public MorphHandler(BaseLayer canvas, Step step) {
        previousPoint = new PointArea(
            new Point(0, 0),
            canvas.getPixelSize(),
            canvas.getHitTestPolicy().calculateTolerance(canvas.getPixelSize())
        );
        this.step = step;
    }

    @Override
    public void handlePress(
        Canvas canvas,
        MouseEvent mouseEvent,
        ToolContainer tool,
        ModeContainer mode,
        ModifierState modifierState
    ) {
        int tolerance = canvas
            .getLayerMorphable()
            .getHitTestPolicy()
            .calculateTolerance(canvas.getLayerMorphable().getPixelSize());
        MorphStep morphStep = (MorphStep) step;
        TwoDimensionLayer layer = canvas.getLayer();
        var morphs = canvas.getLayerMorphable().getState().getLayersMap();

        Point currentPoint = new Point(mouseEvent.getX(), mouseEvent.getY());
        PointArea currentArea = new PointArea(
            currentPoint,
            canvas.getLayer().getPixelSize(),
            tolerance
        );

        boolean intersects = previousPoint.strongIntersects(currentArea);

        canvas.getLayerMorphable().cleanLayer();

        if (intersects) {
            morphs.clear();
            tryCounter++;

            List<MorphableShape<?>> shapes = layer
                .getState()
                .getMorphShapesInArea(
                    previousPoint,
                    canvas.getLayer().getPixelSize()
                );

            if (shapes != null && !shapes.isEmpty()) {
                MorphableShape<?> shape = shapes.get(
                    tryCounter % shapes.size()
                );

                for (var point : shape.getParameters().getPoints()) {
                    canvas.getLayerMorphable().addShape(point, shape);
                    canvas.getLayer().getState().removeMorphShape(point, shape);

                    if (previousPoint.contains(point.getX(), point.getY())) {
                        morphStep.setup(point, shape);
                    }
                    System.out.println("Remove 2D(previous): " + point);
                }

                canvas
                    .getLayerMorphable()
                    .repaintShape(morphStep.getMorphableShape());
                canvas.getLayer().repaintAll();
            }
        } else {
            tryCounter = 0;
            previousPoint = currentArea;

            List<MorphableShape<?>> shapes = layer
                .getState()
                .getMorphShapesInArea(
                    previousPoint,
                    canvas.getLayer().getPixelSize()
                );

            if (shapes != null && !shapes.isEmpty()) {
                MorphableShape<?> morphableShape = shapes.get(
                    tryCounter % shapes.size()
                );
                morphStep.setup(currentPoint, morphableShape);

                for (var point : morphableShape.getParameters().getPoints()) {
                    canvas.getLayerMorphable().addShape(point, morphableShape);
                    canvas.getLayer().getState().removeMorphShape(point, 0);
                    System.out.println("Remove 2D(new): " + point);
                }

                if (!morphs.isEmpty()) {
                    for (var point : morphs.keySet()) {
                        PointArea area = new PointArea(
                            point,
                            canvas.getLayerMorphable().getPixelSize(),
                            tolerance
                        );
                        if (
                            area.contains(mouseEvent.getX(), mouseEvent.getY())
                        ) {
                            morphStep.setup(
                                point,
                                morphs.get(point).getFirst()
                            );
                            break;
                        }
                    }
                }

                morphs.clear();
                canvas
                    .getLayerMorphable()
                    .repaintShape(morphStep.getMorphableShape());
                canvas.getLayer().repaintAll();
            } else {
                canvas.getLayerMorphable().cleanLayer();
            }
        }
    }

    @Override
    public void handleMove(
        Canvas canvas,
        MouseEvent mouseEvent,
        ToolContainer tool,
        ModeContainer mode,
        ModifierState modifierState
    ) {
        // pass
    }

    @Override
    public void handleDrag(
        Canvas canvas,
        MouseEvent mouseEvent,
        ToolContainer tool,
        ModeContainer mode,
        ModifierState modifierState
    ) {
        MorphStep morphStep = (MorphStep) step;
        if (morphStep.isReady()) {
            morphStep.setPoint(mouseEvent.getX(), mouseEvent.getY());
            canvas.getLayerMorphable().cleanLayer();
            canvas
                .getLayerMorphable()
                .repaintShape(morphStep.getMorphableShape());
        }
    }

    @Override
    public void handleRelease(
        Canvas canvas,
        MouseEvent mouseEvent,
        ToolContainer tool,
        ModeContainer mode,
        ModifierState modifierState
    ) {
        MorphStep morphStep = (MorphStep) step;
        if (morphStep.isReady()) {
            for (var point : morphStep
                .getMorphableShape()
                .getParameters()
                .getPoints()) {
                canvas
                    .getLayerMorphable()
                    .getState()
                    .addMorphShape(point, morphStep.getMorphableShape());
            }

            var morphs = canvas.getLayerMorphable().getState().getLayersMap();
            if (morphs != null && !morphs.isEmpty()) {
                for (var point : morphs.keySet()) {
                    MorphableShape<?> shape = morphs.get(point).getFirst();
                    System.out.println("Repaint 2D: " + point);
                    canvas.getLayer().getState().addMorphShape(point, shape);
                }
                canvas.getLayer().repaintAll();
            }
            canvas
                .getLayerMorphable()
                .repaintShape(morphStep.getMorphableShape());
            morphStep.clean();

            int tolerance = canvas
                .getLayerMorphable()
                .getHitTestPolicy()
                .calculateTolerance(canvas.getLayerMorphable().getPixelSize());
            previousPoint = new PointArea(
                new Point(mouseEvent.getX(), mouseEvent.getY()),
                canvas.getLayer().getPixelSize(),
                tolerance
            );
        }
    }
}
