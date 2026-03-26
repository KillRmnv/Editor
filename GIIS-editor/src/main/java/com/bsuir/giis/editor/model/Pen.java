package com.bsuir.giis.editor.model;

import com.bsuir.giis.editor.model.shapes.Drawable;
import com.bsuir.giis.editor.rendering.BaseLayer;
import com.bsuir.giis.editor.service.flow.Mode;
import com.bsuir.giis.editor.service.lines.Antialiasing;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class Pen implements Drawable {

    private final List<Point> points = new ArrayList<>();
    private final Antialiasing lineDrawer = new Antialiasing();

    public Pen() {
    }

    public void addPoint(int x, int y) {
        points.add(new Point(x, y));
    }

    public Point getLastPoint() {
        return points.isEmpty() ? null : points.getLast();
    }

    public List<Point> getPoints() {
        return points;
    }

    public void resetPoints() {
        points.clear();
    }

    @Override
    public void draw(BaseLayer canvas, AlgorithmParameters parameters, Mode mode) {
        List<Point> pts = parameters.getPoints();
        for (int i = 0; i < pts.size() - 1; i++) {
            lineDrawer.drawLine(canvas, pts.get(i), pts.get(i + 1), Color.BLACK);
        }
    }
}
