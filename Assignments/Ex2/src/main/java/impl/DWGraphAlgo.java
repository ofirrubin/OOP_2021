package impl;

import api.DirectedWeightedGraph;
import api.DirectedWeightedGraphAlgorithms;
import api.EdgeData;
import api.NodeData;
import com.google.gson.Gson;

import java.io.*;
import java.util.*;

public class DWGraphAlgo implements DirectedWeightedGraphAlgorithms {

    /**
     * Inits the graph on which this set of algorithms operates on.
     *
     * @param g
     */
    DirectedWeightedGraph graph;

    @Override
    public void init(DirectedWeightedGraph g) {
        graph = g;
    }

    /**
     * Returns the underlying graph of which this class works.
     *
     * @return
     */
    @Override
    public DirectedWeightedGraph getGraph() {
        return graph;
    }

    /**
     * Computes a deep copy of this weighted graph.
     *
     * @return
     */
    @Override
    public DirectedWeightedGraph copy() {
        DirectedWeightedGraph newGraph = new DWGraph();
        for (Iterator<NodeData> it = graph.nodeIter(); it.hasNext(); ) {
            NodeData n = it.next();
            newGraph.addNode(n);
            for (Iterator<EdgeData> iter = graph.edgeIter(n.getKey()); iter.hasNext(); ) {
                EdgeData e = iter.next();
                newGraph.connect(e.getSrc(), e.getDest(), e.getWeight());
            }
        }
        return newGraph;
    }

    /**
     * Returns true if and only if (iff) there is a valid path from each node to each
     * other node. NOTE: assume directional graph (all n*(n-1) ordered pairs).
     *
     * @return
     */
    @Override
    public boolean isConnected() {
        // Choose a node (doesn't mather which) as a head, if the node contains connection to all nodes and all nodes has connections to it,
        if (graph == null) return false;
        Iterator<NodeData> nIter = graph.nodeIter();
        if (!nIter.hasNext()) return false;
        NodeData n = nIter.next();
        // It is a connected graph.

        // Using DFS find paths to each node,
        // If one not found we can stop
        // For every node, Find a path to the head.


        return false;
    }

    /**
     * Computes the length of the shortest path between src to dest
     * Note: if no such path --> returns -1
     *
     * @param src  - start node
     * @param dest - end (target) node
     * @return
     */
    @Override
    public double shortestPathDist(int src, int dest) {
        HashMap<Integer, Integer> prvs = shortestPathPointer(src, dest);
        if (prvs == null || prvs.size() == 0)
            return -1;
        double size = 0;
        for(Integer next: prvs.keySet())
            size += graph.getNode(next).getWeight() + graph.getEdge(prvs.get(next), next).getWeight();

        return size;
    }

    /**
     * Computes the the shortest path between src to dest - as an ordered List of nodes:
     * src--> n1-->n2-->...dest
     * see: https://en.wikipedia.org/wiki/Shortest_path_problem
     * Note if no such path --> returns null;
     *
     *
     *  ALGORITHM: The algorithm is Uniform-Cost-Serach which is optimized version of Dijkstra's algorithm,
     *  as described here: https://en.wikipedia.org/wiki/Dijkstra%27s_algorithm#Pseudocode
     *  The psudo code described there seem to be partial, thus I implemented the algorithm according
     *  to the psudo code described in this website:
     *  https://www.baeldung.com/cs/uniform-cost-search-vs-dijkstras
     *
     *  The algorithm is better than Dijsktra's as it doesn't load all nodes but only the ones in use which allows
     *  working on a very large graph.
     *
     * @param src  - start node
     * @param dest - end (target) node
     * @return
     */
    @Override
    public List<NodeData> shortestPath(int src, int dest) {
        HashMap<Integer, Integer> previousList = shortestPathPointer(src, dest);
        return previousList == null ? null : buildPath(src, dest, previousList);
    }

    private HashMap<Integer, Integer> shortestPathPointer(int src, int dest){
        double weight;
        NodeData nData = graph.getNode(src);
        // I need to save keys, weights & previous as I don't want to edit the graph.
        HashMap<Integer, Double> weights = new HashMap<>();
        HashMap<Integer, Integer> previous = new HashMap<>();
        HashSet<Integer> explored = new HashSet<>();


        PriorityQueue<NodeData> frontier = new PriorityQueue<>((o1, o2) -> {
            if (o1.getWeight() == o2.getWeight()) return 0;
            return o1.getWeight() > o2.getWeight() ? 1 : -1;
        });

        weights.put(nData.getKey(), 0.0);
        frontier.add(nData);

        while(!frontier.isEmpty()){
            nData = frontier.poll();
            if (nData.getKey() == dest){
                return previous;
            }

            explored.add(nData.getKey());
            for (Iterator<EdgeData> it = graph.edgeIter(nData.getKey()); it.hasNext(); ) {
                EdgeData edgs = it.next();
                NodeData n = graph.getNode(edgs.getDest() == nData.getKey() ? edgs.getSrc(): edgs.getDest());
                weight = weights.get(nData.getKey()) + edgs.getWeight(); // nData is in weights as we always adding it while adding to frontier.
                if (explored.contains(n.getKey())){
                    if (weight < n.getWeight())
                    {
                        weights.put(n.getKey(), weight);
                        previous.put(n.getKey(), nData.getKey());
                        frontier.add(n);
                    }
                    else{
                        weights.put(n.getKey(), weight);
                        frontier.add(n);
                        previous.put(n.getKey(), nData.getKey());
                    }
                }
            }
        }
        return null;
    }

    /**
     * Helper function for shortestPath, returns a List<NodeData> from the HashMap<Integer, Integer = Key, PreviousKey>
     * The function goes through the dest call to the src call and adding it to a list of Nodes which it gets from the graph.
     * @param src The stop node, coming from dest node
     * @param dest The start node
     * @param prev HashMap that describes the nodes relation
     * @return List<NodeData> as required by shortestPath function.
     */
    private List<NodeData> buildPath(int src, int dest, HashMap<Integer, Integer> prev){
        ArrayList<NodeData> nodes = new ArrayList<>();
        int v = dest;
        while (v != src){
            nodes.add(graph.getNode(v));
            v = prev.get(v);
        }
        nodes.add(graph.getNode(v));
        return nodes;
    }


    /**
     * Finds the NodeData which minimizes the max distance to all the other nodes.
     * Assuming the graph isConnected, elese return null. See: https://en.wikipedia.org/wiki/Graph_center
     *
     * @return the Node data to which the max shortest path to all the other nodes is minimized.
     */
    @Override
    public NodeData center() {
        return null;
    }

    /**
     * Computes a list of consecutive nodes which go over all the nodes in cities.
     * the sum of the weights of all the consecutive (pairs) of nodes (directed) is the "cost" of the solution -
     * the lower the better.
     * See: https://en.wikipedia.org/wiki/Travelling_salesman_problem
     *
     * @param cities
     */
    @Override
    public List<NodeData> tsp(List<NodeData> cities) {
        return null;
    }

    /**
     * Saves this weighted (directed) graph to the given
     * file name - in JSON format
     *
     * @param file - the file name (may include a relative path).
     * @return true - iff the file was successfully saved
     */
    @Override
    public boolean save(String file) {
        File f = new File(file);
        try{
            if ((f.exists() || f.createNewFile()) && f.canWrite()){
                Gson gson = new Gson();
                gson.toJson(graph, new FileWriter(f));
                return true;
            }
            else
                return false;
        } catch (IOException e) {
            return false;
        }
    }

    /**
     * This method loads a graph to this graph algorithm.
     * if the file was successfully loaded - the underlying graph
     * of this class will be changed (to the loaded one), in case the
     * graph was not loaded the original graph should remain "as is".
     *
     * @param file - file name of JSON file
     * @return true - iff the graph was successfully loaded.
     */
    @Override
    public boolean load(String file) {
        File f = new File(file);
        try {
            if (f.exists()) {
                Gson gson = new Gson();
                this.graph = gson.fromJson(new FileReader(f), DirectedWeightedGraph.class);
                return true;
            }
            return false;
        } catch (IOException e) {
            return false;
        }
    }
}