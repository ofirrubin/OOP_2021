package tests;

import api.DirectedWeightedGraph;
import impl.DWGraphAlgo;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DWGraphAlgoTest {
    DWGraphAlgo graph;
    final double EPS = 0.00001;
    private boolean loadGraph1(){
        if (graph == null)
             graph = new DWGraphAlgo();
        return graph.load("data/G1.json");
    }
    private boolean loadGraph2(){
        if (graph == null)
            graph = new DWGraphAlgo();
        return graph.load("data/G2.json");
    }
    private boolean loadGraph3(){
        if (graph == null)
            graph = new DWGraphAlgo();
        return graph.load("data/G3.json");
    }
    @Test
    void isConnected() {
        loadGraph1();
        assertTrue(graph.isConnected());
        loadGraph2();
        assertTrue(graph.isConnected());
        loadGraph3();
        assertTrue(graph.isConnected());
    }

    @Test
    void shortestPathDist() {
        loadGraph1();
        assertEquals(3.0336329076522373, graph.shortestPathDist(0, 2), EPS);
        assertEquals(7.3765928584015565, graph.shortestPathDist(3, 9), EPS);
    }

    @Test
    void shortestPath() {
        loadGraph1();
        // Direct
        var p = graph.shortestPath(0, 16);
        assertEquals(2, p.size());
        assertEquals(graph.getGraph().getNode(0),p.get(0));
        assertEquals(graph.getGraph().getNode(16),p.get(1));

        // Shortest path with another one in the way as connection
        p = graph.shortestPath(2, 7);
        assertEquals(3, p.size());
        assertEquals(graph.getGraph().getNode(2), p.get(0));
        assertEquals(graph.getGraph().getNode(6), p.get(1));
        assertEquals(graph.getGraph().getNode(7), p.get(2));
    }

    @Test
    void center() {
        loadGraph1();
        assertEquals(16, graph.center().getKey());
        loadGraph2();
        assertEquals(30, graph.center().getKey());
        loadGraph3();
        assertEquals(47, graph.center().getKey());
    }

    @Test
    void tsp() {

    }

    @Test
    void save() {
        loadGraph1();
        graph.save("G1-Saved.json");
        loadGraph2();
        graph.save("G2-Saved.json");
        loadGraph3();
        graph.save("G3-Saved.json");
    }

    @Test
    void load() {
        assertTrue(loadGraph1());
        assertTrue(loadGraph2());
        assertTrue(loadGraph3());
    }
}