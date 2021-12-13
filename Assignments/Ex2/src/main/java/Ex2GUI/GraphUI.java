package Ex2GUI;

import api.DirectedWeightedGraph;
import api.GeoLocation;
import api.NodeData;
import impl.Geo;
import impl.Node;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class GraphUI extends JPanel implements MouseListener {

    public DirectedWeightedGraph graph;
    int width;
    int height;

    int rightPadding;
    int widthPadding;

    int topPadding;
    int heightPadding;

    int radius;
    double minX, minY;
    double maxX, maxY;
    double xD, yD;
    double xS, yS;
    int highestID;

    final Color nodeColor = Color.BLACK;
    final Color pColor = Color.RED;
    final Color sColor = Color.BLUE;
    ArrayList<NodeData> coloredNodes;
    ArrayList<NodeData> nodes;
    boolean changed = false;

    public enum MouseMode{Info, RemoveNode, AddNode};

    public MouseMode mouseMode;

    private JLabel locationLabel;

    public GraphUI(DirectedWeightedGraph g, int rightPadding, int topPadding,int leftPadding, int bottomPadding) {
        this.coloredNodes = new ArrayList<>();
        this.graph = g;
        this.widthPadding = leftPadding + rightPadding;
        this.rightPadding = rightPadding;
        this.topPadding = topPadding;
        this.heightPadding = bottomPadding + topPadding;
        this.nodes = new ArrayList<>();
        setScaleFactor(g.nodeIter());
        this.addMouseListener(this);
        this.highestID = getHighestID();
        mouseMode = MouseMode.Info;

        this.addMouseMotionListener(new MouseAdapter() {
            /**
             * {@inheritDoc}
             *
             * @param e
             * @since 1.6
             */
            @Override
            public void mouseMoved(MouseEvent e) {
                super.mouseMoved(e);
                GeoLocation g1 = getUnPositioned(e.getX(), e.getY());
                if (locationLabel != null){
                    locationLabel.setText("Scale: (" + xS + ", " + yS + ") Location on screen:" +
                            e.getX() + ", " + e.getY() + "\nLocation on graph: (" + g1.x() +", " + g1.y() +",0)");
                }
            }
        });
    }


    public static GraphUI initColored(DirectedWeightedGraph g, int rightPadding, int topPadding,int leftPadding,
                                      int bottomPadding, List<NodeData> coloredNodes){
        GraphUI gui = new GraphUI(g, rightPadding, topPadding, leftPadding, bottomPadding);
        gui.coloredNodes = (ArrayList<NodeData>) coloredNodes;
        return gui;
    }

    private void setScaleFactor(Iterator<NodeData> nIter) {
        if (!nIter.hasNext()) {
            setScaleAsWindow();
            return;
        }
        minX = Double.MAX_VALUE;
        minY = Double.MAX_VALUE; // min contains max value thus will always be changed (or equal to max).
        maxX = Double.MIN_VALUE;
        maxY = Double.MIN_VALUE; // max contains min value thus will always be changed (or equal to min).

        while (nIter.hasNext()) {
            GeoLocation n = nIter.next().getLocation();
            minX = Math.min(minX, n.x());
            maxX = Math.max(maxX, n.x());

            minY = Math.min(minY, n.y());
            maxY = Math.max(maxY, n.y());
        }

        if (maxX == minX && maxY == minY){
            if (maxX == 0)
                maxX = this.getSize().getWidth();
            if (maxY == 0)
                maxY = this.getSize().getHeight();
            minY = 0;
            minX = 0;
        }
        xD = Math.abs(maxX - minX);
        yD = Math.abs(maxY - minY);
        setScale();
    }

    private void setScaleAsWindow(){
        this.minX = 0;
        this.minY = 0;
        xD = this.getSize().width;
        yD = this.getSize().height;
        xS = yS = 1;
    }

    private void setScale(){

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

    public GeoLocation getUnPositioned(int x, int y){
        return new Geo(((x-rightPadding) / xS) + minX, ((y-topPadding) / yS) + minY, 0);
    }

    private void draw(Graphics g2d) {
        // Set screen size
        this.width = this.getSize().width - widthPadding;
        this.height = this.getSize().height - heightPadding;
        radius = Math.min(width, height) / 80; // Set new radius
        // Set scale
        if (graph.nodeSize() == 0)
            setScaleAsWindow();
        else
            setScaleFactor(graph.nodeIter());

        Graphics2D g = (Graphics2D) g2d;
        graph.nodeIter().forEachRemaining(n -> {
            GeoLocation srcGeo =  getPositioned(n.getLocation());
            graph.edgeIter(n.getKey()).forEachRemaining(e -> {
                NodeData destNode = graph.getNode(e.getDest());
                GeoLocation dstGeo = getPositioned(destNode.getLocation());
                g.setColor(coloredNodes.contains(n) && coloredNodes.contains(destNode) ? sColor: pColor);
                drawEdge(g, srcGeo, dstGeo);
                drawArrow(g, srcGeo, dstGeo);
            });
            g.setColor(coloredNodes.contains(n) ? sColor : nodeColor); // Drawing the point over the edges.
            drawFilledCircle(g, getPositioned(n.getLocation()), radius);
            nodes.add(new Node(n.getKey(), n.getWeight(), n.getInfo(), n.getTag(), srcGeo));
        });
    }

    private void drawFilledCircle(Graphics g, GeoLocation g1, int radius) {
        g.fillOval((int) g1.x() - radius, (int) g1.y() - radius, radius, radius);
    }

    private void drawArrow(Graphics g, GeoLocation src, GeoLocation dest){
        // Source: https://math.stackexchange.com/questions/1314006/drawing-an-arrow
        double lineLength = src.distance(dest);
        if (lineLength == 0)
            return;
        double arrowLength = lineLength / 16;
        double scale = arrowLength / lineLength;
        int arrowAngle = 65;
        double dX = src.x() - dest.x();
        double dY = src.y() - dest.y();
        double sinA = Math.sin(arrowAngle);
        double cosA = Math.cos(arrowAngle);

        Geo arrowHead = new Geo(src.x() + 0.8 * (dest.x() - src.x()), src.y() + 0.8 * (dest.y() - src.y()), 0);

        Geo g1 = new Geo(arrowHead.x() + scale * (dX*cosA + dY*sinA), arrowHead.y() + scale * (dY*cosA - dX*sinA),0);
        Geo g2 = new Geo(arrowHead.x() + scale * (dX*cosA - dY*sinA), arrowHead.y() + scale * (dY*cosA + dX*sinA),0);
        g.drawLine((int)arrowHead.x(), (int)arrowHead.y(), (int)g1.x(), (int)g1.y());
        g.drawLine((int)arrowHead.x(), (int)arrowHead.y(), (int)g2.x(), (int)g2.y());
    }

    private void drawEdge(Graphics g, GeoLocation g1, GeoLocation g2) {
        g.drawLine((int) g1.x(), (int) g1.y(), (int) g2.x(), (int) g2.y());
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
        NodeData n = getNodeInRadius(e);
        switch (mouseMode) {
            case Info -> {
                if (n != null)
                    showClickedInfo(n);
                break;
            }
            case AddNode -> {
                if (n == null) {
                    addNode(e);
                    changed = true;
                }
                break;
            }
            case RemoveNode -> {
                if (n != null) {
                    graph.removeNode(n.getKey());
                    changed = true;
                    this.updateUI();
                }
                break;
            }
        }
    }

    public void setLocationLabel(JLabel l){
        this.locationLabel = l;
    }

    public NodeData getNodeInRadius(MouseEvent e){
        for(NodeData n: nodes) {
            GeoLocation g = n.getLocation();
            if(inRadius(e, g))
                return n;
        }
        return null;
    }

    private boolean inRadius(MouseEvent e, GeoLocation g){
        return e.getX() >= g.x() - radius && e.getX() <= g.x() + radius
                &&  e.getY() >= g.y() - radius && e.getY() <= g.y() + radius;
    }

    private void addNode(MouseEvent e) {
        GeoLocation g1 = getUnPositioned(e.getX(), e.getY());
        Node n = new Node(++highestID, g1);
        graph.addNode(n);
        locationLabel.setText(n.getKey() + " > Info: " + n.getInfo() + "\n");
        this.updateUI();;
    }


    private int getHighestID(){
        int max = Integer.MIN_VALUE;
        Iterator<NodeData> nIter = graph.nodeIter();
        while(nIter.hasNext()){
            max = Math.max(max, nIter.next().getKey());
        }
        return max == Integer.MIN_VALUE ? 0 : max;
    }

    private void showClickedInfo(NodeData n){
        ArrayList<String> edges = new ArrayList<>();
        graph.edgeIter(n.getKey()).forEachRemaining(edg -> edges.add(edg.getSrc() + " -> " + edg.getDest() +
                "; Weight: " + edg.getWeight()));
        ListDialog dialog = new ListDialog(n.getKey() + " > Info: " + n.getInfo() + " -> Edges: ",
                new JList(edges.toArray()));
        //dialog.setOnOk(e -> System.out.println("Chosen item: " + dialog.getSelectedItem()));
        dialog.show();
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

    public BufferedImage getScreenshot(){
        BufferedImage image = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_RGB);
        Graphics2D g = image.createGraphics();
        printAll(g);
        g.dispose();
        return image;
    }
}
