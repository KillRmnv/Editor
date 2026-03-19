package com.bsuir.giis.editor.model.shapes;


import java.util.ArrayList;
import java.util.List;

public class CompoundShape {

    private final List<Shape<?>> shapes;

    public CompoundShape() {
        this.shapes = new ArrayList<>();
    }

    public CompoundShape(List<Shape<?>> shapes) {
        this.shapes = new ArrayList<>(shapes);
    }

    public void add(Shape<?> shape) {
        shapes.add(shape);
    }

    public void remove(Shape<?> shape) {
        shapes.remove(shape);
    }

    public List<Shape<?>> getShapes() {
        return new ArrayList<>(shapes);
    }

    public int size() {
        return shapes.size();
    }

    public boolean isEmpty() {
        return shapes.isEmpty();
    }
}
