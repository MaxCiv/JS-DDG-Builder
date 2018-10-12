package com.maxciv.jsparse;

import com.maxciv.jsparse.cfg.*;
import com.maxciv.jsparse.graph.AttrProvider;
import com.maxciv.jsparse.graph.Shape;
import com.maxciv.jsparse.graph.VertexJSp;
import jdk.nashorn.api.tree.CompilationUnitTree;
import jdk.nashorn.api.tree.Parser;
import jdk.nashorn.api.tree.Tree;
import jdk.nashorn.api.tree.VariableTree;
import org.apache.commons.lang3.RandomStringUtils;
import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.DirectedPseudograph;
import org.jgrapht.io.DOTExporter;
import org.jgrapht.io.StringComponentNameProvider;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;

public class MainDOT extends JFrame {

    private static final long serialVersionUID = -2707712944901661771L;

    public static void main(String[] args) throws Exception {
        Parser parser = Parser.create();
        File sourceFile = new File(args[0]);

        CompilationUnitTree cut = parser.parse(sourceFile, System.out::println);
        if (cut == null) return;

        Visitor visitor = new Visitor();
        cut.accept(visitor, null);

        CFG cfg = new CFG(visitor.functionBlockCFG);
        cfg.buildDDG();

        new MainDOT(cfg);
    }

    public MainDOT(CFG cfg) throws HeadlessException {

        Graph<VertexJSp, DefaultEdge> graph = new DirectedPseudograph<>(DefaultEdge.class);

        buildGraph(cfg, graph);

        toDOT(new File("/Users/maxim.oleynik/dot.gv"), graph);
    }

    private void buildGraph(CFG cfg, Graph<VertexJSp, DefaultEdge> graph) {
        HashMap<BlockCFG, Object> hashMap = new HashMap<>();

        drawVertex(cfg.getFirstBlock(), hashMap, graph);
        drawEdges(cfg.getFirstBlock(), hashMap, graph, new HashSet<>());
    }

    private void drawVertex(BlockCFG blockCFG, HashMap<BlockCFG, Object> hashMap, Graph<VertexJSp, DefaultEdge> graph) {
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
            Object vertex = new VertexJSp(blockCFG.getStringRepresentation(), Shape.ELLIPSE);
            graph.addVertex((VertexJSp) vertex);
            hashMap.put(blockCFG, vertex);
            drawVertex(blockCFG.getChild(), hashMap, graph);
        } else if (blockCFG instanceof ConditionBlockCFG) {
            Object vertex = new VertexJSp(blockCFG.getStringRepresentation(), Shape.RHOMBUS);
            graph.addVertex((VertexJSp) vertex);
            hashMap.put(blockCFG, vertex);
            drawVertex(blockCFG.getThenChild(), hashMap, graph);
            drawVertex(blockCFG.getElseChild(), hashMap, graph);
        } else if (blockCFG instanceof ActionBlockCFG) {
            Object vertex = new VertexJSp(blockCFG.getStringRepresentation(), Shape.BOX);
            graph.addVertex((VertexJSp) vertex);
            hashMap.put(blockCFG, vertex);
            drawVertex(blockCFG.getChild(), hashMap, graph);
        }
    }

    private void drawEdges(BlockCFG blockCFG, HashMap<BlockCFG, Object> hashMap, Graph<VertexJSp, DefaultEdge> graph, HashSet<BlockCFG> hashSet) {
        if (blockCFG == null)
            return;

        if (hashSet.contains(blockCFG))
            return;

        blockCFG.getDependentBlocks().forEach(blockEdgeTo ->
                graph.addEdge((VertexJSp) hashMap.get(blockCFG), (VertexJSp) hashMap.get(blockEdgeTo)));

        hashSet.add(blockCFG);
        if (blockCFG instanceof ConditionBlockCFG) {
            drawEdges(blockCFG.getThenChild(), hashMap, graph, hashSet);
            drawEdges(blockCFG.getElseChild(), hashMap, graph, hashSet);
        } else {
            drawEdges(blockCFG.getChild(), hashMap, graph, hashSet);
        }
    }

    private static void toDOT(File file, Graph<VertexJSp, DefaultEdge> graph)
    {
        DOTExporter<VertexJSp, DefaultEdge> exporter = new DOTExporter<>(
                component -> RandomStringUtils.randomAlphabetic(20),
                new StringComponentNameProvider<>(),
                null,
                new AttrProvider(),
                null);
        try (FileWriter fw = new FileWriter(file))
        {
            exporter.exportGraph(graph, fw);
            fw.flush();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}
