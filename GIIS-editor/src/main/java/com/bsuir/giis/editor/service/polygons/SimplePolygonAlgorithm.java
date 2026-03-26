package com.bsuir.giis.editor.service.polygons;

import com.bsuir.giis.editor.model.AlgorithmParameters;
import com.bsuir.giis.editor.model.Point;
import com.bsuir.giis.editor.model.PointShapeParameters;
import com.bsuir.giis.editor.rendering.BaseLayer;
import com.bsuir.giis.editor.service.flow.Mode;
import com.bsuir.giis.editor.service.lines.BresenhamAlgorithm;


import java.awt.*;
import java.util.List;

public class SimplePolygonAlgorithm implements PolygonsAlgorithm {
    private final BresenhamAlgorithm straightLineAlgorithm=new BresenhamAlgorithm();
    @Override
    public void draw(BaseLayer canvas, AlgorithmParameters parameters, Mode mode) {
        PointShapeParameters polygonParams = (PointShapeParameters) parameters;
        List<Point> points = polygonParams.getPoints();

        if (points.size() < 2) return;

        for (int i = 0; i < points.size(); i++) {
            Point p1 = points.get(i);
            Point p2 = points.get((i + 1) % points.size()); 
            straightLineAlgorithm.drawLine(canvas, p1.getX(), p1.getY(), p2.getX(), p2.getY(), Color.BLACK);
        }
        mode.onFinish();
    }
}