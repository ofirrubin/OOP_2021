package impl;

import api.DirectedWeightedGraph;
import api.DirectedWeightedGraphAlgorithms;
import api.EdgeData;
import api.NodeData;


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
    public boolean isConnected() {   // DFS based is connected. We will check if a graph is connected by going through the graph from a selected node and using a checklist
        // we will mark each node as if visited, Then we will go through visited list of each node and check if any node haven't been visited,
        // If so, the graph is not connected. Because this graph is directed - we have to check if we can go through the opposite way -> from each node to the selected node.
        if (graph == null) return false;
        HashMap<Integer, Boolean> visited = new HashMap<>();
        Iterator<NodeData> nIter = graph.nodeIter();
        DirectedWeightedGraph trnsp = new DWGraph();
        NodeData n = nIter.next(); // Select a starting node

        putInverseEdges(graph, trnsp, n); // Add node if not in graph and it's edges in reverse (src->dest => dest->src)
        visited.put(n.getKey(), false);

        // Add all nodes to visited as false and set transpose graph.
        while (nIter.hasNext()) {
            NodeData nD = nIter.next();
            putInverseEdges(graph, trnsp, nD);
            visited.put(nD.getKey(), false);
        }

        DFS(graph, n, visited);
        for (int k : visited.keySet())
            if (!visited.get(k)) return false;

        visited.replaceAll((k, v) -> false);
        DFS(trnsp, n, visited);
        for (int k : visited.keySet())
            if (!visited.get(k)) return false;

        return true;
    }

    private static void putInverseEdges(DirectedWeightedGraph src, DirectedWeightedGraph dest, NodeData d) {
        for (Iterator<EdgeData> it = src.edgeIter(d.getKey()); it.hasNext(); ) {
            EdgeData e = it.next();
            if (dest.getNode(e.getDest()) == null)
                dest.addNode(src.getNode(e.getDest()));
            dest.connect(e.getDest(), e.getSrc(), e.getWeight());
        }
    }

    private static void DFS(DirectedWeightedGraph graph, NodeData startingAt, HashMap<Integer, Boolean> visited) {
        visited.put(startingAt.getKey(), true);
        for (Iterator<EdgeData> it = graph.edgeIter(startingAt.getKey()); it.hasNext(); ) {
            int key = it.next().getDest();
            if (!visited.get(key))
                DFS(graph, graph.getNode(key), visited);
        }
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
        return pathDistance(shortestPath(src, dest));
    }

    private double pathDistance(List<NodeData> nodes){
        // Allows me to calculate path distance while saving the result of shortestPath for tsp
        if (nodes.isEmpty()) return -1;
        double size = nodes.get(0).getWeight();
        for(int i=0; i < nodes.size() - 1; i ++){
            size += graph.getEdge(nodes.get(i).getKey(), nodes.get(i + 1).getKey()).getWeight() +
                    nodes.get(i+1).getWeight();
        }
        return size;
    }

    /**
     * Computes the the shortest path between src to dest - as an ordered List of nodes:
     * src--> n1-->n2-->...dest
     * see: https://en.wikipedia.org/wiki/Shortest_path_problem
     * Note if no such path --> returns null;
     * <p>
     * <p>
     * ALGORITHM: The algorithm is Uniform-Cost-Serach which is optimized version of Dijkstra's algorithm,
     * as described here: https://en.wikipedia.org/wiki/Dijkstra%27s_algorithm#Pseudocode
     * The psudo code described there seem to be partial, thus I implemented the algorithm according
     * to the psudo code described in this website:
     * https://www.baeldung.com/cs/uniform-cost-search-vs-dijkstras
     * <p>
     * The algorithm is better than Dijsktra's as it doesn't load all nodes but only the ones in use which allows
     * working on a very large graph.
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
    private HashMap<Integer, Integer> shortestPathPointer(int src, int dest) {
        NodeData[] nData = {graph.getNode(src)}; // Using Wrapper to use in forEach.
        // I need to save keys, weights & previous as I don't want to edit the graph.
        HashMap<Integer, Double> weights = new HashMap<>();
        HashMap<Integer, Integer> previous = new HashMap<>();
        HashSet<Integer> explored = new HashSet<>();
        PriorityQueue<NodeData> frontier = new PriorityQueue<>((o1, o2) -> {
            if (o1.getWeight() == o2.getWeight()) return 0;
            return o1.getWeight() > o2.getWeight() ? 1 : -1;
        });

        weights.put(src, 0.0); // weight of nData[0]
        frontier.add(nData[0]);
        while (!frontier.isEmpty()) {
            nData[0] = frontier.poll();
            if (nData[0].getKey() == dest)
                return previous;

            explored.add(nData[0].getKey());
           graph.edgeIter(nData[0].getKey()).forEachRemaining(edgs ->{
                NodeData n = graph.getNode(edgs.getDest());
                int nDataKey = nData[0].getKey();
                double weight = weights.get(nDataKey) + edgs.getWeight(); // nData is in weights as we always adding it while adding to frontier.
                if (!explored.contains(n.getKey())) {
                    if (weight < n.getWeight()) {
                        weights.put(n.getKey(), weight);
                        previous.put(n.getKey(), nDataKey);
                        frontier.add(n);
                    } else {
                        weights.put(n.getKey(), weight);
                        frontier.add(n);
                        previous.put(n.getKey(), nDataKey);
                    }
                }
            });
        }
        return null;
    }

    /**
     * Helper function for shortestPath, returns a List<NodeData> from the HashMap<Integer, Integer = Key, PreviousKey>
     * The function goes through the dest call to the src call and adding it to a list of Nodes which it gets from the graph.
     *
     * @param src  The stop node, coming from dest node
     * @param dest The start node
     * @param prev HashMap that describes the nodes relation
     * @return List<NodeData> as required by shortestPath function.
     */
    private List<NodeData> buildPath(int src, int dest, HashMap<Integer, Integer> prev) {
        ArrayList<NodeData> nodes = new ArrayList<>();
        int v = dest;
        while (v != src) {
            nodes.add(graph.getNode(v));
            v = prev.get(v);
        }
        nodes.add(graph.getNode(v));
        Collections.reverse(nodes);
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
        // ASSUMING THE GRAPH IS CONNECTED

        HashMap<Integer, Double> nDistances = new HashMap<>();  // Here we will save the distance from each node to any other node.

        // Validate that the graph is not empty.
        Iterator<NodeData> nIter = graph.nodeIter();
        if (!nIter.hasNext())
            return null;
        int minimum = nIter.next().getKey(); // Setting the minimum to a

        // For every node in the graph - set this distances to 0 and iterate any other node, calculate the time to him and add it to the distances.
        graph.nodeIter().forEachRemaining(current -> {
            nDistances.put(current.getKey(), 0.0);
            graph.nodeIter().forEachRemaining(node -> {
                if (node.getKey() != current.getKey()) {
                    int key = node.getKey();
                    double shortestPathDistance = shortestPathDist(current.getKey(), node.getKey());
                    if (!nDistances.containsKey(key))
                        nDistances.put(key, 0.0);
                    nDistances.put(key, nDistances.get(key) + shortestPathDistance);
                }
            });
        });
        // After we calculated the distances find the minimum one, I couldn't get it inside the lambda expression,
        // Seems like this is disallowed in java, could save some iterations...
        for (int k : nDistances.keySet()) {
            if (nDistances.get(k) < nDistances.get(minimum))
                minimum = k;
        }
        return graph.getNode(minimum);
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
        // Implementing TSP by Nearest Neighbor algorithm, which is O(n^2).
        // I chose this algorithm because it is the best among algorithms compared at:
        // http://160592857366.free.fr/joe/ebooks/ShareData/Heuristics%20for%20the%20Traveling%20Salesman%20Problem%20By%20Christian%20Nillson.pdf

        if (cities.size() == 0) return null;

        HashMap<Integer, Double> distancesToPoint; // <Key: Distance>
        HashMap<Integer, List<NodeData>> pathToPoint; // <Key: Path>
        List<NodeData> nodePath; // temporary node paths
        ArrayList<NodeData> path = new ArrayList<>(); // final Path

        NodeData node = cities.get(randomNum(0, cities.size() - 1));
        cities.remove(node); // mark as visited.

        while (cities.size() != 0) {
            distancesToPoint = new HashMap<>(); // <Key: Distance>
            pathToPoint = new HashMap<>(); // <Key: Path>
            for (NodeData next : cities) {
                nodePath = shortestPath(node.getKey(), next.getKey());
                if (cities.size() == 1)
                    cities.remove(node); // If we are in the last node it doesn't mather if we found a way or not, we have to remove it
                if (nodePath != null) {
                    distancesToPoint.put(next.getKey(), pathDistance(nodePath));
                    pathToPoint.put(next.getKey(), nodePath);
                }
            }
            int key = getMinKey(distancesToPoint, node);
            if (key == node.getKey() && pathToPoint.isEmpty()) // There is no other way to go.
                cities.clear(); // This will exit the loop and return path
            else {
                path.addAll(pathToPoint.get(key));
                cities.removeAll(pathToPoint.get(key)); // We remove all cities we've visited from path.
            }
            node = graph.getNode(key);
        }
        return path;

    }
    private int getMinKey(HashMap<Integer, Double> distancesToPoint, NodeData node){
        // Return minimum key or default if not valid
        double minimum = Double.MAX_VALUE;
        int key = node.getKey();
        for(int k: distancesToPoint.keySet()){
            if (distancesToPoint.get(k) <= minimum){
                minimum = distancesToPoint.get(k);
                key = k;
            }
        }
        return key;
    }
    private int randomNum(int minimum, int maximum){
        Random rand = new Random();
        return minimum + rand.nextInt((maximum - minimum) + 1);
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
        try {
            if ((f.exists() || f.createNewFile()) && f.canWrite()) {
                GraphJsonParser.save(new FileWriter(f), graph);
                return true;
            } else
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
                DirectedWeightedGraph g = GraphJsonParser.load(new FileReader(f));
                if (g == null) return false;
                this.graph = g;
                return true;
            }
            return false;
        } catch (IOException e) {
            return false;
        }
    }
}