package com.bsuir.giis.editor.model;

public class Point {
    private int x;
    private int y;
    public Point(final int x, final int y) {
        if(x >= 0 && y >= 0) {
            this.x = x;
            this.y = y;
        }else
            throw new IllegalArgumentException("Point is invalid");

    }
    public int getX() {
        return x;
    }
    public void setX(final int x) {
        this.x = x;
    }
    public int getY() {
        return y;
    }
    public void setY(final int y) {
        this.y = y;
    }
    public boolean isValid() {
        return x >= 0 && y >= 0;
    }
    @Override
    public String toString() {
        return "(" + x + ", " + y + ")";
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Point point = (Point) o;
        return x == point.x && y == point.y;
    }

    @Override
    public int hashCode() {
        // Используем простое и эффективное хеширование для координат
        int result = x;
        result = 31 * result + y;
        return result;
    }
}

