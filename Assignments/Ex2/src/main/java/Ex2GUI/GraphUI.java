package Ex2GUI;

import api.DirectedWeightedGraph;
import api.EdgeData;
import api.GeoLocation;
import api.NodeData;
import impl.Edge;
import impl.Geo;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

public class GraphUI extends JPanel {

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

    public GraphUI(DirectedWeightedGraph g,
                   int rightPadding, int topPadding,int leftPadding, int bottomPadding,
                   int width, int height) {
        this.coloredNodes = new ArrayList<>();
        this.graph = g;
        this.width = width - leftPadding - rightPadding;
        this.rightPadding = rightPadding;
        this.topPadding = topPadding;
        this.height = height - bottomPadding - topPadding;

        setScale(g.nodeIter());
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
            graph.edgeIter(n.getKey()).forEachRemaining(e -> {
                drawEdge(g, getPositioned(n.getLocation()), getPositioned(graph.getNode(e.getDest()).getLocation()));
            });
            g.setColor(coloredNodes.contains(n) ? sColor : nodeColor); // Drawing the point over the edges.
            drawFilledCircle(g, getPositioned(n.getLocation()), radius);
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
}
