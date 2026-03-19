package com.bsuir.giis.editor.model.dimensions;

import java.util.ArrayList;
import java.util.List;

public class Model3D {
    private final List<Point3D> vertices;
    private final List<Face3D> faces;
    private String name;

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
}
