package com.bsuir.giis.editor.model.dimensions;

import java.util.ArrayList;
import java.util.List;

public class Model3D {
    private final List<Point3D> vertices;
    private final List<Face3D> faces;
    private String name;

    private double centerX, centerY, centerZ;
    private double normalizedScale;

    public Model3D() {
        this.vertices = new ArrayList<>();
        this.faces = new ArrayList<>();
        this.name = "Unnamed";
    }

    public Model3D(String name) {
        this.vertices = new ArrayList<>();
        this.faces = new ArrayList<>();
        this.name = name;
    }

    public void addVertex(Point3D vertex) {
        vertices.add(vertex);
    }

    public void addFace(Face3D face) {
        faces.add(face);
    }

    public List<Point3D> getVertices() {
        return vertices;
    }

    public List<Face3D> getFaces() {
        return faces;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Point3D getVertex(int index) {
        if (index >= 0 && index < vertices.size()) {
            return vertices.get(index);
        }
        throw new IndexOutOfBoundsException("Vertex index out of bounds: " + index);
    }

    public int getVertexCount() {
        return vertices.size();
    }

    public int getFaceCount() {
        return faces.size();
    }

    public void computeBoundingBox() {
        if (vertices.isEmpty()) {
            centerX = centerY = centerZ = 0;
            normalizedScale = 1;
            return;
        }

        double minX = Double.MAX_VALUE, maxX = -Double.MAX_VALUE;
        double minY = Double.MAX_VALUE, maxY = -Double.MAX_VALUE;
        double minZ = Double.MAX_VALUE, maxZ = -Double.MAX_VALUE;

        for (Point3D v : vertices) {
            if (v.getX() < minX) minX = v.getX();
            if (v.getX() > maxX) maxX = v.getX();
            if (v.getY() < minY) minY = v.getY();
            if (v.getY() > maxY) maxY = v.getY();
            if (v.getZ() < minZ) minZ = v.getZ();
            if (v.getZ() > maxZ) maxZ = v.getZ();
        }

        centerX = (minX + maxX) / 2.0;
        centerY = (minY + maxY) / 2.0;
        centerZ = (minZ + maxZ) / 2.0;

        double extentX = (maxX - minX) / 2.0;
        double extentY = (maxY - minY) / 2.0;
        double extentZ = (maxZ - minZ) / 2.0;
        double maxExtent = Math.max(extentX, Math.max(extentY, extentZ));

        normalizedScale = (maxExtent > 1e-10) ? 0.5 / maxExtent : 1.0;
    }

    public double getCenterX() { return centerX; }
    public double getCenterY() { return centerY; }
    public double getCenterZ() { return centerZ; }
    public double getNormalizedScale() { return normalizedScale; }
}
