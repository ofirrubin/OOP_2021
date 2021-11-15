import class5.BST;
import class5.Point3D;
import class5.Point_Comp;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Iterator;

class BSTTest {
    private ArrayList<Point3D> _pp;
    private BST<Point3D> _ps;
    public static final Point_Comp COMP = new Point_Comp();

    @BeforeEach
    void setUp() {
        _pp = new ArrayList<Point3D>();
        for (int i = 0; i < 10; i = i + 1) {
            double x = Math.random() * 100;
            double y = Math.random() * 100;
            double z = Math.random() * 100;
            Point3D p = new Point3D(x, y, z);
            _pp.add(p);
        }
        _ps = new BST<Point3D>(COMP);
    }

    @Test
    void add() {
        for (int i = 0; i < _pp.size(); i++) {
            _ps.add(_pp.get(i));
        }
        int s = _ps.size();
        assertEquals(s, _pp.size());
    }

    private void assertEquals(int s, int size) {
    }

    @Test
    void interator() {
        for (int i = 0; i < _pp.size(); i++) {
            _ps.add(_pp.get(i));
        }
        double d1 = 0;
        Iterator<Point3D> iter = _ps.interator();
        int i = 0;
        while (iter.hasNext()) {
            Point3D p = iter.next();
            double d2 = p.distance3D();
            if (d1 > d2) {
                fail("ERR: the binary search tree is not sorted");
            }
            d1 = d2;
            System.out.println(i + ") " + p + "  dist: " + d2);
            i = i + 1;
        }

    }

    private void fail(String s) {
    }
}