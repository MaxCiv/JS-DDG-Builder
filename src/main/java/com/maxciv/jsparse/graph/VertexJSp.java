package com.maxciv.jsparse.graph;

public class VertexJSp {

    private String title;
    private Shape shape;

    public VertexJSp(String title, Shape shape) {
        this.title = title;
        this.shape = shape;
    }

    @Override
    public String toString() {
        return title;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Shape getShape() {
        return shape;
    }

    public void setShape(Shape shape) {
        this.shape = shape;
    }
}
