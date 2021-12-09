package impl;

import api.DirectedWeightedGraph;
import api.EdgeData;
import api.NodeData;

import java.util.*;

public class DWGraph implements DirectedWeightedGraph{

    HashMap<Integer, HashMap<String, EdgeData>> adjs; // Each Node presented by its ID contains HashMap of <EdgeData.src +";" + EdgeData.dest>, EdgeData that gets from the node.
    HashMap<Integer, NodeData> nodes;
    HashMap<Integer, TreeSet<Integer>> links;
    int nAdjs = 0, changes = 0;

    public DWGraph(){
        adjs = new HashMap<>();
        nodes = new HashMap<>();
        nAdjs = 0;
    }

    public DWGraph(DWGraph g){
        this.adjs = new HashMap<>(g.adjs);
        this.nodes = new HashMap<>(g.nodes);
        this.links = new HashMap<>(g.links);
        this.nAdjs = g.nAdjs;
        this.changes = g.changes;
    }
    /**
     * returns the node_data by the node_id,
     *
     * @param key - the node_id
     * @return the node_data by the node_id, null if none.
     */
    @Override
    public NodeData getNode(int key) {
        return nodes.get(key);
    }

    /**
     * returns the data of the edge (src,dest), null if none.
     * Note: this method should run in O(1) time.
     *
     * @param src
     * @param dest
     * @return
     */
    @Override
    public EdgeData getEdge(int src, int dest) {
        return adjs.get(src).get(src + ";" + dest);
    }

    /**
     * adds a new node to the graph with the given node_data.
     * Note: this method should run in O(1) time.
     *
     * @param n
     */
    @Override
    public void addNode(NodeData n) {
        if (nodes.put(n.getKey(), n) != null) {
            links.put(n.getKey(), new TreeSet<>()); // If this is a new node, there are no links to it.
            adjs.put(n.getKey(), new HashMap<>()); // New node is not connected to anything
            changes++;
        }
    }

    /**
     * Connects an edge with weight w between node src to node dest.
     * * Note: this method should run in O(1) time.
     *
     * @param src  - the source of the edge.
     * @param dest - the destination of the edge.
     * @param w    - positive weight representing the cost (aka time, price, etc) between src-->dest.
     */
    @Override
    public void connect(int src, int dest, double w) {
        // Assuming src and dest is in the graph.
        if (!adjs.containsKey(src))
            adjs.put(src, new HashMap<>());
        if (adjs.get(src).put(src + ";" + dest, new Edge(src, dest, 0, w, src + " -> " + dest)) != null) {
            links.get(dest).add(src); // Add link to the dest node.
            nAdjs++;
            changes ++;
        }
    }

    /**
     * This method returns an Iterator for the
     * collection representing all the nodes in the graph.
     * Note: if the graph was changed since the iterator was constructed - a RuntimeException should be thrown.
     *
     * @return Iterator<node_data>
     */
    @Override
    public Iterator<NodeData> nodeIter() {
        return nodes.values().iterator();
    }

    /**
     * This method returns an Iterator for all the edges in this graph.
     * Note: if any of the edges going out of this node were changed since the iterator was constructed - a RuntimeException should be thrown.
     *
     * @return Iterator<EdgeData>
     */
    @Override
    public Iterator<EdgeData> edgeIter() {
        ArrayList<EdgeData> allEdges = new ArrayList<>();
        for (HashMap<String, EdgeData> edges: adjs.values()){
            allEdges.addAll(edges.values());
        }
        return allEdges.iterator();
    }

    /**
     * This method returns an Iterator for edges getting out of the given node (all the edges starting (source) at the given node).
     * Note: if the graph was changed since the iterator was constructed - a RuntimeException should be thrown.
     *
     * @param node_id
     * @return Iterator<EdgeData>
     */
    @Override
    public Iterator<EdgeData> edgeIter(int node_id) {
        return adjs.get(node_id).values().iterator();
    }

    /**
     * Deletes the node (with the given ID) from the graph -
     * and removes all edges which starts or ends at this node.
     * This method should run in O(k), V.degree=k, as all the edges should be removed.
     *
     * @param key
     * @return the data of the removed node (null if none).
     */
    @Override
    public NodeData removeNode(int key) {
        if (!nodes.containsKey(key)) return null;
        if (adjs.containsKey(key)) {
            for (Integer id: links.get(key)) {
                if (adjs.get(id).remove(id + ";" + key) != null)
                    changes ++;
            }
            nAdjs -= links.get(key).size();
            adjs.remove(key);
            links.remove(key);
        }
        changes ++;
        return nodes.remove(key);
    }

    /**
     * Deletes the edge from the graph,
     * Note: this method should run in O(1) time.
     *
     * @param src
     * @param dest
     * @return the data of the removed edge (null if none).
     */
    @Override
    public EdgeData removeEdge(int src, int dest) {
        EdgeData d = adjs.get(src).remove(src + ";" + dest);
        if (d != null)
            changes ++;
        return d;
    }

    /**
     * Returns the number of vertices (nodes) in the graph.
     * Note: this method should run in O(1) time.
     *
     * @return
     */
    @Override
    public int nodeSize() {
        return nodes.size();
    }

    /**
     * Returns the number of edges (assume directional graph).
     * Note: this method should run in O(1) time.
     *
     * @return
     */
    @Override
    public int edgeSize() {
        return nAdjs;
    }

    /**
     * Returns the Mode Count - for testing changes in the graph.
     *
     * @return
     */
    @Override
    public int getMC() {
        return changes;
    }
}