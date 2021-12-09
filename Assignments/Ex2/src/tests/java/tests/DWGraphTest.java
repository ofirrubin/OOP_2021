package tests;

import impl.DWGraph;
import impl.Edge;
import impl.Geo;
import impl.Node;

import static org.junit.jupiter.api.Assertions.*;

class DWGraphTest {

    @org.junit.jupiter.api.Test
    void getNode() {
        DWGraph graph = new DWGraph();
        graph.addNode(new Node(1, new Geo(1, 2, 3)));
        graph.addNode(new Node(2, new Geo(2, 3, 4)));
        assertEquals(graph.getNode(1), new Node(1, new Geo(1, 2, 3)));
    }

    @org.junit.jupiter.api.Test
    void getEdge() {
        DWGraph graph = new DWGraph();
        graph.addNode(new Node(1, new Geo(1, 2, 3)));
        graph.addNode(new Node(2, new Geo(2, 3, 4)));
        assertEquals(graph.getNode(1), new Node(1, new Geo(1, 2, 3)));
    }

    @org.junit.jupiter.api.Test
    void addNode() {
        DWGraph graph = new DWGraph();
        graph.addNode(new Node(1, new Geo(1, 2, 3)));
        assertEquals(new Node(1, new Geo(1, 2, 3)), graph.getNode(1));
    }

    @org.junit.jupiter.api.Test
    void connect() {
        DWGraph graph = new DWGraph();
        graph.addNode(new Node(1, new Geo(1, 2, 3)));
        graph.addNode(new Node(2, new Geo(2, 3, 4)));
        graph.connect(1, 2, 1);
        Edge e = new Edge(1, 2, 1);
        assertEquals(e, graph.getEdge(1, 2));
    }

    @org.junit.jupiter.api.Test
    void removeNode() {
        DWGraph graph = new DWGraph();
        graph.addNode(new Node(1, new Geo(1, 2, 3)));
        graph.removeNode(1);
        graph.removeNode(1);
        assertEquals(0, graph.nodeSize());
    }

    @org.junit.jupiter.api.Test
    void removeEdge() {
        DWGraph graph = new DWGraph();
        graph.addNode(new Node(1, new Geo(1, 2, 3)));
        graph.addNode(new Node(2, new Geo(2, 3, 4)));
        graph.connect(1, 2, 1);
        graph.removeEdge(1, 2);
        assertEquals(0, graph.edgeSize());
    }

    @org.junit.jupiter.api.Test
    void nodeSize() {
        DWGraph graph = new DWGraph();
        graph.addNode(new Node(1, new Geo(1, 2, 3)));
        assertEquals(1, graph.nodeSize());
    }

    @org.junit.jupiter.api.Test
    void edgeSize() {
        DWGraph graph = new DWGraph();
        graph.addNode(new Node(1, new Geo(1, 2, 3)));
        graph.addNode(new Node(2, new Geo(2, 3, 4)));
        graph.connect(1, 2, 1);
        assertEquals(1, graph.edgeSize());
    }

    @org.junit.jupiter.api.Test
    void getMC() {
        DWGraph graph = new DWGraph();
        graph.addNode(new Node(1, new Geo(1, 2, 3)));
        graph.addNode(new Node(2, new Geo(2, 3, 4)));
        graph.connect(1, 2, 1);
        assertEquals(3, graph.getMC());
    }
}