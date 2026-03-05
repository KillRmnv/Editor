package com.bsuir.giis.editor.controllers.handlers;

import com.bsuir.giis.editor.model.Point;
import com.bsuir.giis.editor.model.PointArea;
import com.bsuir.giis.editor.model.shapes.MorphableShape;
import com.bsuir.giis.editor.utils.ModeContainer;
import com.bsuir.giis.editor.utils.ModifierState;
import com.bsuir.giis.editor.utils.MorphStep;
import com.bsuir.giis.editor.utils.Step;
import com.bsuir.giis.editor.utils.ToolContainer;
import com.bsuir.giis.editor.view.BaseLayer;
import com.bsuir.giis.editor.view.Canvas;
import com.bsuir.giis.editor.view.TwoDimensionLayer;

import java.awt.event.MouseEvent;
import java.util.List;

//TODO:fix basic 2dlayer update problem
public class MorphHandler implements DrawableHandler {
    private int tryCounter = 0;
    private PointArea previousPoint;
    private Step Step;



    public MorphHandler(BaseLayer canvas, Step step) {
        previousPoint = new PointArea(new Point(0, 0), canvas.getPixelSize(), canvas.getHitTestPolicy().calculateTolerance(canvas.getPixelSize()));
        this.Step = step;
    }

    @Override
    public void handlePress(Canvas canvas, MouseEvent mouseEvent, ToolContainer tool, ModeContainer mode, ModifierState modifierState) {
        int tolerance = canvas.getLayer2DMorphable().getHitTestPolicy().calculateTolerance(canvas.getLayer2DMorphable().getPixelSize());
        MorphStep morphStep = (MorphStep) Step;
        TwoDimensionLayer layer = canvas.getLayer2D();
        var morphs = canvas.getLayer2DMorphable().getState().getLayersMap();
        if (previousPoint.strongIntersects(new PointArea(
                new Point(mouseEvent.getX(), mouseEvent.getY()), canvas.getLayer2D().getPixelSize(), tolerance))) {
            morphs.clear();

            tryCounter++;
            List<MorphableShape<?>> shapes = layer.getState().getMorphShapesInArea(previousPoint, canvas.getLayer2D().getPixelSize());
            if (shapes != null && !shapes.isEmpty()) {
                MorphableShape<?> shape = shapes.get(tryCounter % shapes.size());

                for (var point : shape.getParameters().getPoints()) {
                    canvas.getLayer2DMorphable().addShape(point, shape);
                    canvas.getLayer2D().getState().removeMorphShape(point, shape);
                    if (previousPoint.contains(point.getX(), point.getY())) {
                        morphStep.setup(point, shape);
                        canvas.getLayer2DMorphable().cleanLayer();
                        canvas.getLayer2DMorphable().repaintShape(morphStep.getMorphableShape());
                    }
                    System.out.println("Remove 2D(previous): " + point.toString());
                }
                Thread.ofVirtual().start(() -> {
                    canvas.getLayer2D().repaintAll();
                });
            }
        } else {
            tryCounter = 0;
            Point newPoint = new Point(mouseEvent.getX(), mouseEvent.getY());
            previousPoint = new PointArea(newPoint, canvas.getLayer2D().getPixelSize(), tolerance);
            List<MorphableShape<?>> shapes = layer.getState().getMorphShapesInArea(previousPoint, canvas.getLayer2D().getPixelSize());
            if (shapes != null && !shapes.isEmpty()) {
                MorphableShape<?> morphableShape = shapes.get(tryCounter % shapes.size());
                morphStep.setup(newPoint, morphableShape);
                canvas.getLayer2DMorphable().cleanLayer();
                canvas.getLayer2DMorphable().repaintShape(morphStep.getMorphableShape());


                for (var point : morphableShape.getParameters().getPoints()) {
                    canvas.getLayer2DMorphable().addShape(point, morphableShape);
                    canvas.getLayer2D().getState().removeMorphShape(point, 0);

                    System.out.println("Remove 2D(new): " + point.toString());
                }
                if (!morphs.isEmpty()) {
                    for (var point : morphs.keySet()) {
                        PointArea area = new PointArea(point, canvas.getLayer2DMorphable().getPixelSize(), tolerance);
                        if (area.contains(mouseEvent.getX(), mouseEvent.getY())) {
                            morphStep.setup(point, morphs.get(point).getFirst());
                            break;
                        }
                    }
                }
                morphs.clear();
                canvas.getLayer2DMorphable().cleanLayer();
                canvas.getLayer2DMorphable().repaintShape(morphStep.getMorphableShape());
                Thread.ofVirtual().start(() -> {
                    canvas.getLayer2D().repaintAll();
                });
            } else
                canvas.getLayer2DMorphable().cleanLayer();
        }
    }

    @Override
    public void handleMove(Canvas canvas, MouseEvent mouseEvent, ToolContainer tool, ModeContainer mode, ModifierState modifierState) {
        //pass
    }

    @Override
    public void handleDrag(Canvas canvas, MouseEvent mouseEvent, ToolContainer tool, ModeContainer mode, ModifierState modifierState) {
        MorphStep morphStep = (MorphStep) Step;
        if (morphStep.isReady()) {
            morphStep.setPoint(mouseEvent.getX(), mouseEvent.getY());
            canvas.getLayer2DMorphable().cleanLayer();
            canvas.getLayer2DMorphable().repaintShape(morphStep.getMorphableShape());
        }
    }

    @Override
    public void handleRelease(Canvas canvas, ToolContainer tool, ModeContainer mode, ModifierState modifierState) {
        MorphStep morphStep = (MorphStep) Step;
        if (morphStep.isReady()) {
            for (var point : morphStep.getMorphableShape().getParameters().getPoints()) {
                canvas.getLayer2DMorphable().getState().addMorphShape(point, morphStep.getMorphableShape());
            }

            var morphs = canvas.getLayer2DMorphable().getState().getLayersMap();
            if (morphs != null && !morphs.isEmpty()) {
                for (var point : morphs.keySet()) {
                    MorphableShape<?> shape = morphs.get(point).getFirst();
                    System.out.println("Repaint 2D: " + point.toString());
                    canvas.getLayer2D().getState().addMorphShape(point, shape);
                }

                canvas.getLayer2D().repaintAll();
            }
            canvas.getLayer2DMorphable().repaintShape(morphStep.getMorphableShape());
            morphStep.clean();

        }

    }
}
