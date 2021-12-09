package tests;

import impl.Geo;
import org.junit.jupiter.api.Test;
import impl.Node;
import static org.junit.jupiter.api.Assertions.*;

class NodeTest {

    @Test
    void testEquals() {
        Node n1 = new Node(1, new Geo(2, 3, 4));
        Node n2 = new Node(2, new Geo(2, 3, 4));
        assertNotEquals(n1, n2);
    }

    @Test
    void getKey() {
        Node n1 = new Node(1, new Geo(2, 3, 4));
        assertEquals(1, n1.getKey());
    }

    @Test
    void getLocation() {
        Node n1 = new Node(1, new Geo(2, 3, 4));
        assertEquals(new Geo(2, 3, 4), n1.getLocation());
    }

    @Test
    void setLocation() {
        Node n1 = new Node(1, new Geo(2, 3, 4));
        n1.setLocation(new Geo(3, 2, 1));
        assertEquals(new Geo(3, 2, 1), n1.getLocation());
    }

    @Test
    void getWeight() {
        Node n1 = new Node(1, Math.PI, "", 0, new Geo(1, 2, 3));
        assertEquals(Math.PI, n1.getWeight());
    }

    @Test
    void setWeight() {
        Node n1 = new Node(1, Math.PI, "", 0, new Geo(1, 2, 3));
        n1.setWeight(Math.E);
        assertEquals(Math.E, n1.getWeight());
    }

    @Test
    void getInfo() {
        Node n1 = new Node(1, Math.PI, "Custom Info", 0, new Geo(1, 1, 1));
        assertEquals("Custom Info", n1.getInfo());
    }

    @Test
    void setInfo() {
        Node n1 = new Node(1, new Geo(1, 1, 1));
        n1.setInfo("Custom Info");
        assertEquals("Custom Info", n1.getInfo());
    }

    @Test
    void getTag() {
        Node n1 = new Node(1, Math.PI, "Custom Info", 865, new Geo(1, 1, 1));
        assertEquals(865, n1.getTag());
    }

    @Test
    void setTag() {
        Node n1 = new Node(1, new Geo(1, 1, 1));
        n1.setTag(1553);
        assertEquals(1553, n1.getTag());
    }
}