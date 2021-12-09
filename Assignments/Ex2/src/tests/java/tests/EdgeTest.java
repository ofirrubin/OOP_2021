package tests;

import impl.Edge;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EdgeTest {

    @Test
    void getSrc() {
        Edge e = new Edge(0, 1, 2);
        assertEquals(0, e.getSrc());
    }

    @Test
    void getDest() {
        Edge e = new Edge(8, 5, Math.E);
        assertEquals(5, e.getDest());
    }

    @Test
    void getWeight() {
        Edge e = new Edge(8, 5, Math.E);
        assertEquals(Math.E, e.getWeight());
    }

    @Test
    void getInfo() {
        Edge e = new Edge(8, 5, 0, Math.E, "Custom Info");
        assertEquals("Custom Info", e.getInfo());
    }

    @Test
    void setInfo() {
        Edge e = new Edge(2, 3, Math.E);
        e.setInfo("Custom Info");
        assertEquals("Custom Info", e.getInfo());
    }

    @Test
    void getTag() {
        Edge e = new Edge(8, 5, 123456, Math.E, "Custom Info");
        assertEquals(123456, e.getTag());
    }

    @Test
    void setTag() {
        Edge e = new Edge(2, 3, Math.E);
        e.setTag(2);
        assertEquals(2, e.getTag());
    }

    @Test
    void equals(){
        Edge e1 = new Edge(2, 3, Math.E);
        Edge e2 = new Edge(2, 3, Math.E);
        assertEquals(e1, e2);
    }
}