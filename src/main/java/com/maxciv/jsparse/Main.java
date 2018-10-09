package com.maxciv.jsparse;

import com.maxciv.jsparse.cfg.*;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.view.mxGraph;
import jdk.nashorn.api.tree.CompilationUnitTree;
import jdk.nashorn.api.tree.Parser;
import jdk.nashorn.api.tree.Tree;
import jdk.nashorn.api.tree.VariableTree;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.HashMap;
import java.util.HashSet;

public class Main extends JFrame {

    private static final long serialVersionUID = -2707712944901661771L;

    private static final double WIDTH = 120;
    private static final double HEIGHT = 40;
    private static final double X_OFFSET = WIDTH + 20;
    private static final double Y_OFFSET = HEIGHT + 20;
    private static double X = 20;
    private static double Y = 20;

    public static void main(String[] args) throws Exception {
        Parser parser = Parser.create();
        File sourceFile = new File(args[0]);

        CompilationUnitTree cut = parser.parse(sourceFile, System.out::println);
        if (cut == null) return;

        Visitor visitor = new Visitor();
        cut.accept(visitor, null);

        CFG cfg = new CFG(visitor.functionBlockCFG);
        cfg.buildDDG();

        Main frame = new Main(cfg);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1600, 1000);
        frame.setVisible(true);
    }

    public Main(CFG cfg) throws HeadlessException {
        super("DDG");

        mxGraph graph = new mxGraph();
        graph.getModel().beginUpdate();
        try {
            buildGraph(cfg, graph);
        } finally {
            graph.getModel().endUpdate();
        }

        mxGraphComponent graphComponent = new mxGraphComponent(graph);
        getContentPane().add(graphComponent);
    }

    private void buildGraph(CFG cfg, mxGraph graph) {
        HashMap<BlockCFG, Object> hashMap = new HashMap<>();

        drawVertex(cfg.getFirstBlock(), hashMap, graph);
        drawEdges(cfg.getFirstBlock(), hashMap, graph, new HashSet<>());
    }

    private void drawVertex(BlockCFG blockCFG, HashMap<BlockCFG, Object> hashMap, mxGraph graph) {
        if (blockCFG == null)
            return;

        if (hashMap.containsKey(blockCFG))
            return;

        if (blockCFG.getStringRepresentation().equals("[empty block]")) {
            drawVertex(blockCFG.getChild(), hashMap, graph);
            return;
        }

        if (blockCFG instanceof ActionBlockCFG
                && blockCFG.getExpression() != null
                && blockCFG.getExpression().getKind() == Tree.Kind.VARIABLE
                && ((VariableTree) blockCFG.getExpression()).getInitializer() == null
                && blockCFG.getDependentBlocks().isEmpty()) {
            drawVertex(blockCFG.getChild(), hashMap, graph);
            return;
        }

        if (blockCFG instanceof FunctionBlockCFG) {
            Object vertex = graph.insertVertex(graph.getDefaultParent(), null, blockCFG.getStringRepresentation(), X, Y, WIDTH, HEIGHT, "shape=ellipse");
            X += X_OFFSET;
            Y += Y_OFFSET;
            hashMap.put(blockCFG, vertex);
            drawVertex(blockCFG.getChild(), hashMap, graph);
        } else if (blockCFG instanceof ConditionBlockCFG) {
            Object vertex = graph.insertVertex(graph.getDefaultParent(), null, blockCFG.getStringRepresentation(), X, Y, WIDTH, HEIGHT, "shape=rhombus");
            X += X_OFFSET;
            Y += Y_OFFSET;
            hashMap.put(blockCFG, vertex);
            drawVertex(blockCFG.getThenChild(), hashMap, graph);
            drawVertex(blockCFG.getElseChild(), hashMap, graph);
        } else if (blockCFG instanceof ActionBlockCFG) {
            Object vertex = graph.insertVertex(graph.getDefaultParent(), null, blockCFG.getStringRepresentation(), X, Y, WIDTH, HEIGHT);
            X += X_OFFSET;
            Y += Y_OFFSET;
            hashMap.put(blockCFG, vertex);
            drawVertex(blockCFG.getChild(), hashMap, graph);
        }
    }

    private void drawEdges(BlockCFG blockCFG, HashMap<BlockCFG, Object> hashMap, mxGraph graph, HashSet<BlockCFG> hashSet) {
        if (blockCFG == null)
            return;

        if (hashSet.contains(blockCFG))
            return;

        blockCFG.getDependentBlocks().forEach(blockEdgeTo -> {
            if (blockCFG == blockEdgeTo) {
                graph.insertEdge(graph.getDefaultParent(), null, "", hashMap.get(blockCFG), hashMap.get(blockEdgeTo), "edgeStyle=loopEdgeStyle");
            } else {
                graph.insertEdge(graph.getDefaultParent(), null, "", hashMap.get(blockCFG), hashMap.get(blockEdgeTo), "edgeStyle=segmentEdgeStyle");
            }
        });
        hashSet.add(blockCFG);
        if (blockCFG instanceof ConditionBlockCFG) {
            drawEdges(blockCFG.getThenChild(), hashMap, graph, hashSet);
            drawEdges(blockCFG.getElseChild(), hashMap, graph, hashSet);
        } else {
            drawEdges(blockCFG.getChild(), hashMap, graph, hashSet);
        }
    }
}
