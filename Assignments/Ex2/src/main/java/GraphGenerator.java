import impl.DWGraph;
import impl.DWGraphAlgo;
import impl.Geo;
import impl.Node;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class GraphGenerator {
    public static void main(String[] args){
        loadTilException();
    }

    public static void loadTilException(){
        int nodeSize = 100000;
        int edgeSize = 150000; // edgeSize <= 2 * nodeSize
        int width = 500;
        int height = 500;
        boolean exception = false;
        int iter = 0;
        while (!exception && iter < 1) {
            int id = -1 ;
            iter ++;
            DWGraphAlgo algo = new DWGraphAlgo();
            DWGraph g = new DWGraph();
            while (g.nodeSize() < nodeSize)
                g.addNode(generateNode(++id, width, height));
            while (g.edgeSize() < edgeSize)
                g.connect(randint(0, id), randint(0, id), randDouble());

            algo.init(g);
            try {
                algo.center();
                //algo.shortestPath(id, 0);
                //algo.shortestPathDist(id, 0);
            } catch (Exception e) {
                exception = true;
                algo.save("excepted.json");
            }
        }
        System.out.println("No errors");
    }

    public static int randint(int min, int max){
        return ThreadLocalRandom.current().nextInt(min, max + 1);
    }
    public static double randDouble(double min, double max){
        return ThreadLocalRandom.current().nextDouble(min, max+ 1);
    }
    public static double randDouble() {
        return ThreadLocalRandom.current().nextDouble();
    }

    public static Node generateNode(int id, int min, int max){
        return new Node(id, new Geo(randint(min, max), randint(min, max), 0));
    }
}
