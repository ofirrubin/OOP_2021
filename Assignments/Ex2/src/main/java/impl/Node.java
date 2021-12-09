package impl;

import api.GeoLocation;
import api.NodeData;

class Node implements NodeData{

    int key;
    double weight;
    String info;
    int tag;
    GeoLocation g;

    public Node(int key, GeoLocation g){
        this.key = key;
        this.g = g;

        this.weight = 0;
        this.info = this.key + " at {" + this.g.x() + ", " + this.g.y() + ", " + this.g.z() + "} weights: " + this.weight;
        this.tag = 0;
    }
    public Node(int key, double weight, String info, int tag, GeoLocation g){
        this.key = key;
        this.weight = weight;
        this.info = info;
        this.tag = tag;
        this.g = g;

    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Node node = (Node) o;
        return key == node.key;
    }

    /**
     * Returns the key (id) associated with this node.
     *
     * @return
     */
    @Override
    public int getKey() {
        return key;
    }

    /**
     * Returns the location of this node, if none return null.
     *
     * @return
     */
    @Override
    public GeoLocation getLocation() {
        return g;
    }

    /**
     * Allows changing this node's location.
     *
     * @param p - new new location  (position) of this node.
     */
    @Override
    public void setLocation(GeoLocation p) {
        this.g = p;
    }

    /**
     * Returns the weight associated with this node.
     *
     * @return
     */
    @Override
    public double getWeight() {
        return weight;
    }

    /**
     * Allows changing this node's weight.
     *
     * @param w - the new weight
     */
    @Override
    public void setWeight(double w) {
        weight = w;
    }

    /**
     * Returns the remark (meta data) associated with this node.
     *
     * @return
     */
    @Override
    public String getInfo() {
        return info;
    }

    /**
     * Allows changing the remark (meta data) associated with this node.
     *
     * @param s
     */
    @Override
    public void setInfo(String s) {
        info = s;
    }

    /**
     * Temporal data (aka color: e,g, white, gray, black)
     * which can be used be algorithms
     *
     * @return
     */
    @Override
    public int getTag() {
        return tag;
    }

    /**
     * Allows setting the "tag" value for temporal marking an node - common
     * practice for marking by algorithms.
     *
     * @param t - the new value of the tag
     */
    @Override
    public void setTag(int t) {
        tag = t;
    }
}