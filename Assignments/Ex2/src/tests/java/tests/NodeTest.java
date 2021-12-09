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
        assertEq
    }

    @Test
    void getWeight() {
    }

    @Test
    void setWeight() {
    }

    @Test
    void getInfo() {
    }

    @Test
    void setInfo() {
    }

    @Test
    void getTag() {
    }

    @Test
    void setTag() {
    }
}