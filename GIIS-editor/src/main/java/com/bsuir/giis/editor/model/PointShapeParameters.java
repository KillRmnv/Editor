package com.bsuir.giis.editor.model;

import com.bsuir.giis.editor.utils.MultiStep;
import com.bsuir.giis.editor.utils.PenStep;
import com.bsuir.giis.editor.utils.Step;

import java.util.ArrayList;
import java.util.List;

public class PointShapeParameters implements AlgorithmParameters {

    private final List<Point> controlPoints;

    public PointShapeParameters(List<Point> points) {
        this.controlPoints = new ArrayList<>(points);
    }


    public PointShapeParameters(Step step) {
        this.controlPoints = new ArrayList<>();
        if (step instanceof MultiStep multiStep) {
            for (int i = 0; i < multiStep.getSize(); i++) {
                Step s = multiStep.getStep(i);
                if (s instanceof PenStep penStep) {
                    this.controlPoints.add(penStep.getPoint());
                }
            }
        } else if (step instanceof PenStep penStep) {
            // Если вдруг пришел одиночный шаг
            this.controlPoints.add(penStep.getPoint());
        }
    }

    public PointShapeParameters(Point start, Point end) {
        this.controlPoints = new ArrayList<>();
        this.controlPoints.add(start);
        this.controlPoints.add(end);
    }

    @Override
    public List<Point> getPoints() {
        return controlPoints;
    }

    @Override
    public List<Point> getStartEndPoint() {
        if (controlPoints.isEmpty()) return List.of();
        if (controlPoints.size() == 1) return List.of(controlPoints.getFirst(), controlPoints.getFirst());

        return List.of(controlPoints.getFirst(), controlPoints.getLast());
    }

    public Point getPoint(int index) {
        if (index >= 0 && index < controlPoints.size()) {
            return controlPoints.get(index);
        }
        throw new IndexOutOfBoundsException("Invalid point index: " + index);
    }

    public void setPoint(int index, Point point) {
        if (index >= 0 && index < controlPoints.size()) {
            controlPoints.set(index, point);
        }
    }
}