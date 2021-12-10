package Ex2GUI;

import api.DirectedWeightedGraph;
import api.GeoLocation;
import api.NodeData;
import impl.Geo;
import impl.Node;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class GraphUI extends JPanel implements MouseListener {

    DirectedWeightedGraph graph;
    int width;
    int height;
    int rightPadding;
    int topPadding;

    int radius;
    double minX, minY;
    double xS, yS;

    final Color nodeColor = Color.BLACK;
    final Color pColor = Color.RED;
    final Color sColor = Color.BLUE;
    ArrayList<NodeData> coloredNodes;
    ArrayList<NodeData> nodes;

    public GraphUI(DirectedWeightedGraph g,
                   int rightPadding, int topPadding,int leftPadding, int bottomPadding,
                   int width, int height) {
        this.coloredNodes = new ArrayList<>();
        this.graph = g;
        this.width = width - leftPadding - rightPadding;
        this.rightPadding = rightPadding;
        this.topPadding = topPadding;
        this.height = height - bottomPadding - topPadding;
        this.nodes = new ArrayList<>();
        setScale(g.nodeIter());
        this.addMouseListener(this);
    }

    public static GraphUI initColored(DirectedWeightedGraph g,
                                      int rightPadding, int topPadding,int leftPadding, int bottomPadding,
                                      int width, int height,
                                      List<NodeData> coloredNodes){
        GraphUI gui = new GraphUI(g, rightPadding, topPadding, leftPadding, bottomPadding, width, height);
        gui.coloredNodes = (ArrayList<NodeData>) coloredNodes;
        return gui;
    }

    private void setScale(Iterator<NodeData> nIter) {
        double minX = Double.MAX_VALUE, minY = Double.MAX_VALUE; // min contains max value thus will always be changed.
        double maxX = Double.MIN_VALUE, maxY = Double.MIN_VALUE; // max contains min value thus will always be changed.
        while (nIter.hasNext()) {
            GeoLocation n = nIter.next().getLocation();
            minX = Math.min(minX, n.x());
            maxX = Math.max(maxX, n.x());

            minY = Math.min(minY, n.y());
            maxY = Math.max(maxY, n.y());
        }
        this.minX = minX;
        this.minY = minY;
        double xD = Math.abs(maxX - minX);
        double yD = Math.abs(maxY - minY);
        if (xD < width) // We want it to be wider
            xS = width / xD;
        else // We want it to be narrower
            xS = xD / width;
        if (yD < height) // We want it to be higher
            yS = height / yD;
        else // We want it to be shorter
            yS = yD / height;
    }

    private GeoLocation getPositioned(GeoLocation p1){
        return new Geo(rightPadding + (p1.x()  - minX) * xS , topPadding + (p1.y() - minY) * yS, p1.z());
    }

    private void draw(Graphics g2d) {
        Graphics2D g = (Graphics2D) g2d;
        radius = Math.min(width, height) / 80;
        graph.nodeIter().forEachRemaining(n -> {
            g.setColor(pColor);
            GeoLocation geo =  getPositioned(n.getLocation());
            graph.edgeIter(n.getKey()).forEachRemaining(e -> {
                drawEdge(g, geo, getPositioned(graph.getNode(e.getDest()).getLocation()));
            });
            g.setColor(coloredNodes.contains(n) ? sColor : nodeColor); // Drawing the point over the edges.
            drawFilledCircle(g, getPositioned(n.getLocation()), radius);
            nodes.add(new Node(n.getKey(), n.getWeight(), n.getInfo(), n.getTag(), geo));
        });
    }

    private void drawFilledCircle(Graphics g, GeoLocation g1, int radius) {
        g.fillOval((int) g1.x() - radius, (int) g1.y() - radius, radius, radius);
    }
    private void drawCircle(Graphics g, GeoLocation g1, int radius) {
        g.drawOval((int) g1.x() - radius, (int) g1.y() - radius, radius, radius);
    }

    private void drawEdge(Graphics g, GeoLocation g1, GeoLocation g2) {
        int moveSize = radius / 6;
        g.drawLine((int) g1.x() - moveSize, (int) g1.y() + moveSize,
                (int) g2.x() + moveSize, (int) g2.y() - moveSize);
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        draw(g);
    }

    /**
     * Invoked when the mouse button has been clicked (pressed
     * and released) on a component.
     *
     * @param e the event to be processed
     */
    @Override
    public void mouseClicked(MouseEvent e) {
        for(NodeData n: nodes){
            GeoLocation g = n.getLocation();
            if (e.getX() >= g.x() - radius && e.getX() <= g.x() + radius
                    &&  e.getY() >= g.y() - radius && e.getY() <= g.y() + radius) {
                ArrayList<String> edges = new ArrayList<>();
                graph.edgeIter(n.getKey()).forEachRemaining(edg -> edges.add(edg.getSrc() + " -> " + edg.getDest() +
                        "; Weight: " + edg.getWeight()));
                ListDialog dialog = new ListDialog(n.getKey() + " > Info: " + n.getInfo() + " -> Edges: ",
                        new JList(edges.toArray()));
                //dialog.setOnOk(e -> System.out.println("Chosen item: " + dialog.getSelectedItem()));
                dialog.show();
                return;
            }
        }
    }

    /**
     * Invoked when a mouse button has been pressed on a component.
     *
     * @param e the event to be processed
     */
    @Override
    public void mousePressed(MouseEvent e) {

    }

    /**
     * Invoked when a mouse button has been released on a component.
     *
     * @param e the event to be processed
     */
    @Override
    public void mouseReleased(MouseEvent e) {

    }

    /**
     * Invoked when the mouse enters a component.
     *
     * @param e the event to be processed
     */
    @Override
    public void mouseEntered(MouseEvent e) {
    }

    /**
     * Invoked when the mouse exits a component.
     *
     * @param e the event to be processed
     */
    @Override
    public void mouseExited(MouseEvent e) {

    }
}
