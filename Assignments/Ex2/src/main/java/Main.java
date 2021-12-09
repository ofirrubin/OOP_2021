import api.NodeData;
import impl.DWGraphAlgo;

import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args){
        DWGraphAlgo algo = new DWGraphAlgo();
        algo.load("/Users/ofirrubin/OOP_2021/Assignments/Ex2/data/G1.json");
        algo.save("/Users/ofirrubin/OOP_2021/Assignments/Ex2/data/G1-Saved.json");
        ArrayList<NodeData> visitAt = new ArrayList<>();
        visitAt.add(algo.getGraph().getNode(2));
        visitAt.add(algo.getGraph().getNode(7));
        System.out.println(algo.shortestPathDist(2, 7));
        List<NodeData> path = algo.shortestPath(2, 7);
        for(int i=0; i < path.size() - 1; i++){
            System.out.println("Going from: " + path.get(i).getInfo() + " to " + path.get(i+1).getInfo());
            System.out.println("Total weight of this part (including edge weight): " +
                    (algo.getGraph().getEdge(path.get(i).getKey(), path.get(i+1).getKey()).getWeight() +
                    path.get(i).getWeight() + path.get(i+1).getWeight()));
        }
        for(NodeData n: algo.tsp(visitAt))
            System.out.println(n.getInfo());
    }
}
