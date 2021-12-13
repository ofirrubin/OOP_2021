package api;

/**
 * This interface represents the set of operations applicable on a 
 * node (vertex) in a (directional) weighted graph.
 * @author boaz.benmoshe
 */
public interface NodeData {
	/**
	 * Returns the key (id) associated with this node.
	 * @return
	 *
	 */
    int getKey();
	/** Returns the location of this node, if none return null.
	 * @return
	 */
    GeoLocation getLocation();
	/** Allows changing this node's location.
	 * @param p - new new location  (position) of this node.
	 */
    void setLocation(GeoLocation p);
	/**
	 * Returns the weight associated with this node.
	 * @return
	 */
    double getWeight();
	/**
	 * Allows changing this node's weight.
	 * @param w - the new weight
	 */
    void setWeight(double w);
	/**
	 * Returns the remark (meta data) associated with this node.
	 * @return
	 */
    String getInfo();
	/**
	 * Allows changing the remark (meta data) associated with this node.
	 * @param s
	 */
    void setInfo(String s);
	/**
	 * Temporal data (aka color: e,g, white, gray, black) 
	 * which can be used be algorithms 
	 * @return
	 */
    int getTag();
	/** 
	 * Allows setting the "tag" value for temporal marking an node - common
	 * practice for marking by algorithms.
	 * @param t - the new value of the tag
	 */
    void setTag(int t);
}
