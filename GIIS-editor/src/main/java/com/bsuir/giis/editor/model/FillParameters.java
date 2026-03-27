package com.bsuir.giis.editor.model;

import java.awt.Color;
import java.util.List;

public class FillParameters implements AlgorithmParameters {

    private List<Point> polygonPoints;

    private Point seedPoint;

    private Color fillColor;
    private Color borderColor;

    public FillParameters(List<Point> polygonPoints, Color fillColor) {
        this.polygonPoints = polygonPoints;
        this.fillColor = fillColor;
    }

    /** Конструктор для затравочных алгоритмов */
    public FillParameters(Point seedPoint, Color fillColor, Color borderColor) {
        this.seedPoint = seedPoint;
        this.fillColor = fillColor;
        this.borderColor = borderColor;
    }

    public List<Point> getPolygonPoints() { return polygonPoints; }
    public Point getSeedPoint()           { return seedPoint; }
    public Color getFillColor()           { return fillColor; }
    public Color getBorderColor()         { return borderColor; }

	@Override
	public List<Point> getStartEndPoint() {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Unimplemented method 'getStartEndPoint'");
	}

	@Override
	public List<Point> getPoints() {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Unimplemented method 'getPoints'");
	}
}