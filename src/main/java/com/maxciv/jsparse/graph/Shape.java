package com.maxciv.jsparse.graph;

public enum Shape {

    ELLIPSE("ellipse"),
    RHOMBUS("diamond"),
    BOX("box");

    private String shape;

    Shape(String shape) {
        this.shape = shape;
    }

    @Override
    public String toString() {
        return shape;
    }
}
