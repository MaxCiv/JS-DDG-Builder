package com.maxciv.jsparse;

import com.maxciv.jsparse.graph.AttrProvider;
import com.maxciv.jsparse.graph.Shape;
import com.maxciv.jsparse.graph.VertexJSp;
import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleDirectedGraph;
import org.jgrapht.io.DOTExporter;
import org.jgrapht.io.StringComponentNameProvider;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class JGraphTRunner {

    public static void main(String[] args) {
        toDOT(new File("/Users/maxim.oleynik/dot.gv"), createStringGraph());
    }

    private static Graph<VertexJSp, DefaultEdge> createStringGraph()
    {
        Graph<VertexJSp, DefaultEdge> g = new SimpleDirectedGraph<>(DefaultEdge.class);

        VertexJSp v1 = new VertexJSp("ds1", Shape.BOX);
        VertexJSp v2 = new VertexJSp("ds2", Shape.ELLIPSE);
        VertexJSp v3 = new VertexJSp("ds3", Shape.RHOMBUS);
        VertexJSp v4 = new VertexJSp("ds4", Shape.BOX);

        // add the vertices
        g.addVertex(v1);
        g.addVertex(v2);
        g.addVertex(v3);
        g.addVertex(v4);

        // add edges to create a circuit
        g.addEdge(v1, v2);
        g.addEdge(v2, v3);
        g.addEdge(v3, v4);
        g.addEdge(v4, v1);

        return g;
    }

    private static void toDOT(File file, Graph<VertexJSp, DefaultEdge> graph)
    {
        DOTExporter<VertexJSp, DefaultEdge> exporter = new DOTExporter<>(
                new StringComponentNameProvider<>(),
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
