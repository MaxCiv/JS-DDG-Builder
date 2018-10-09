package com.maxciv.jsparse;

import com.maxciv.jsparse.cfg.CFG;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.view.mxGraph;
import jdk.nashorn.api.tree.CompilationUnitTree;
import jdk.nashorn.api.tree.Parser;

import javax.swing.*;
import java.awt.*;
import java.io.File;

public class Main extends JFrame {

    private static final long serialVersionUID = -2707712944901661771L;

    private static final double WIDTH = 120;
    private static final double HEIGHT = 40;

    public Main() throws HeadlessException {
        super("DDG");

        mxGraph graph = new mxGraph();
        Object parent = graph.getDefaultParent();

        graph.getModel().beginUpdate();
        try {
            Object v1 = graph.insertVertex(parent, null, "Hello", 20, 20, WIDTH, HEIGHT);
            Object v2 = graph.insertVertex(parent, null, "World!", 240, 150, WIDTH, HEIGHT, "shape=ellipse");
            Object v3 = graph.insertVertex(parent, null, "World!", 440, 300, WIDTH, HEIGHT, "shape=rhombus");
            graph.insertEdge(parent, null, "Edge", v1, v2, "edgeStyle=segmentEdgeStyle");
            graph.insertEdge(parent, null, "Edge", v1, v1, "edgeStyle=loopEdgeStyle");
        } finally {
            graph.getModel().endUpdate();
        }

        mxGraphComponent graphComponent = new mxGraphComponent(graph);
        getContentPane().add(graphComponent);
    }

    public static void main(String[] args) throws Exception {
        Parser parser = Parser.create();
        File sourceFile = new File(args[0]);

        CompilationUnitTree cut = parser.parse(sourceFile, System.out::println);
        if (cut == null) return;

        Visitor visitor = new Visitor();
        cut.accept(visitor, null);

        CFG cfg = new CFG(visitor.functionBlockCFG);
        cfg.buildDDG();
        System.out.println(" ");

        Main frame = new Main();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1600, 1000);
        frame.setVisible(true);
    }
}
