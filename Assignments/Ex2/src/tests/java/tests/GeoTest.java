package tests;

import impl.Geo;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GeoTest {
    final double EPS = 0.00001;
    @Test
    void x() {
        assertEquals(Math.PI, new Geo(Math.PI, 2, 3).x());
        assertEquals(Math.E, new Geo(Math.E, 2, 3).x());
    }

    @Test
    void equals(){
        assertEquals(new Geo(Math.PI, 2, 3), new Geo(Math.PI, 2, 3));
        assertNotEquals(new Geo(Math.PI + EPS, -1, 3),
                new Geo(Math.PI, -1, 3));
    }

    @Test
    void y() {
        assertEquals(Math.PI + 1, new Geo(3, Math.PI + 1, 3).y());
        assertEquals(Math.E - 1, new Geo(7, Math.E - 1, 3).y());
    }

    @Test
    void z() {
        assertEquals(Math.PI + Math.E, new Geo(3, -2, Math.PI + Math.E).z());
        assertEquals(Math.E * Math.E, new Geo(7,  - 1, Math.E * Math.E).z());
    }

    @Test
    void distance() {
        assertEquals(11.686578,
                new Geo(Math.PI * Math.E, -2, 3).distance(new Geo(17, 6, 2)),
                EPS);
        assertEquals(8.780757,
                new Geo(Math.PI * Math.E, Math.E, Math.PI).distance(
                        new Geo(Math.PI / Math.E, Math.E + Math.PI, Math.E - Math.PI)),
                EPS);
    }
}