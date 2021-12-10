package Ex2GUI;

import api.DirectedWeightedGraph;
import api.DirectedWeightedGraphAlgorithms;
import api.NodeData;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

public class Ex2UI extends JFrame implements ActionListener {
    private JPanel panel;
    private JFrame graphicFrame;
    private JLabel welcomeLabel;
    private JButton loadGraphButton;

    private JButton isConnectedButton;
    private JButton centerButton;
    private JButton tspButton;
    private JButton shortestPathButton;

    private JButton showGraphButton;

    private DirectedWeightedGraphAlgorithms algo;
    private JButton[] actionButtons;
    final int graphPadding = 10; // All sides padding
    final int graphBoxSize = 500; // Square sized graph

    public Ex2UI(String title, DirectedWeightedGraphAlgorithms algo) {
        super(title);
        this.algo = algo;
        graphicFrame = new JFrame();

        this.actionButtons = new JButton[]{isConnectedButton, centerButton, tspButton, showGraphButton, shortestPathButton};
        setButtonsVisibility();
        addButtonListeners();

        graphicFrame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setContentPane(panel);
        this.pack();

    }

    private void setButtonsVisibility() {
        boolean state = algo == null || algo.getGraph() == null;
        for (JButton b : actionButtons)
            b.setVisible(!state);
        if (!state) // Override visibility for tsp by isConnected
            tspButton.setVisible(algo.isConnected());
        this.pack();
    }

    private void addButtonListeners() {
        loadGraphButton.addActionListener(this);
        for (JButton b : actionButtons)
            b.addActionListener(this);
    }

    private void setGraphicFrame(JPanel g) {
        graphicFrame.getContentPane().removeAll(); // reset graphic frame
        graphicFrame.add(g);
        graphicFrame.setLocationRelativeTo(null);
        graphicFrame.pack();
        graphicFrame.setVisible(true);
    }

    /**
     * Invoked when an action occurs.
     *
     * @param e the event to be processed
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == loadGraphButton) {
            JFileChooser fC = new JFileChooser();

            //Allow Json file extension only
            fC.setFileFilter(new FileNameExtensionFilter("json", "json"));
            if (fC.showOpenDialog(this) == 0 && !algo.load(fC.getSelectedFile().toString())) // Graph not loaded (File not found / Couldn't parse etc.)
                JOptionPane.showMessageDialog(null,
                        "Couldn't load the graph, Nothing changed.");
            else {
                if (fC.getSelectedFile() == null) return; // If nothing updated return, otherwise update user & buttons.
                JOptionPane.showMessageDialog(null, "Loaded new graph");
                setButtonsVisibility();
                this.pack();
            }
        } else if (e.getSource() == isConnectedButton) {
            if (algo.isConnected())  // Show isConnected state to the user.
                JOptionPane.showMessageDialog(null, "This graph is a connected graph.");
            else
                JOptionPane.showMessageDialog(null, "This graph is not a connected graph.");
        } else if (e.getSource() == centerButton) { // Show the user the center point.
            JOptionPane.showMessageDialog(null, "Graph Center Node Info\n" + algo.center().getInfo());
        } else if (e.getSource() == showGraphButton) {
            GraphUI g = new GraphUI(algo.getGraph(), // Graph
                    graphPadding, graphPadding, graphPadding, graphPadding, // Padding <Right, Top, Left, Bottom>
                    graphBoxSize, graphBoxSize); // Width, Height
            setGraphicFrame(g);
        } else if (e.getSource() == tspButton) { // Get TSP cities and find it, show in graph
            // HashMap<String, NodeData> nodes = new HashMap<>();
            // algo.getGraph().nodeIter().forEachRemaining(n -> nodes.put(n.getInfo(), n));
            //ListDialog dialog = new ListDialog("Please select nodes:",
            //        new JList(nodes.keySet().toArray()));
            //dialog.setOnOk(d -> System.out.println("Chosen item: " + dialog.getSelectedItem()));
            String o = JOptionPane.showInputDialog("Please enter cities keys with ',' as dividers");
            String[] kS = o.split(","); // Split inputs
            ArrayList<NodeData> nodes = new ArrayList<>(); // Nodes will be saved here
            DirectedWeightedGraph g = algo.getGraph();
            NodeData node;

            try {
                for (String k : kS) { // Save parsed nodes
                    node = g.getNode(Integer.parseInt(k));
                    if (node == null)
                        throw new ClassNotFoundException(); // If input is invalid raise a message to the user, return.
                    else
                        nodes.add(node);
                }
            nodes = (ArrayList<NodeData>) algo.tsp(nodes); // Find tsp
            showColoredPathGraph(nodes); // Show the tsp to the user.
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(null, "Keys must be integers >= 0");
            } catch (ClassNotFoundException ex) {
                JOptionPane.showMessageDialog(null, "One key or more were missing");
            }
        } else if (e.getSource() == shortestPathButton) { // Get input from the user and find the shortest path -
            // If there is path calculate the distance and show the path by colored graph.
            String uInput = JOptionPane.showInputDialog("Please enter the source node key: ");
            try {
                if (uInput == null)
                    throw new NumberFormatException("User entered null");
                int srcKey = Integer.parseInt(uInput);
                if (srcKey < 0)
                    throw new NumberFormatException("Negative key is not allowed.");
                uInput = JOptionPane.showInputDialog("Please enter the dest node key: ");
                if (uInput == null)
                    throw new NumberFormatException("User entered null");
                int destKey = Integer.parseInt(uInput);
                List<NodeData> path = algo.shortestPath(srcKey, destKey);
                if (path == null)
                    JOptionPane.showMessageDialog(null, "No path from " + srcKey
                            + " to " + destKey);
                else {
                    showColoredPathGraph(path);
                    JOptionPane.showMessageDialog(null, "A path fround!\n" +
                            "Distance: " + algo.shortestPathDist(srcKey, destKey) + "\n" +
                            "Close this message to load the graph, It might be hidden behind the main window");

                }
            } catch (NumberFormatException nfE) {
                JOptionPane.showMessageDialog(null, "The key must be an integer >= 0");
            }
        }
    }

    private void showColoredPathGraph(List<NodeData> path) {
        GraphUI gui = GraphUI.initColored(algo.getGraph(),
                graphPadding, graphPadding, graphPadding, graphPadding,
                graphBoxSize, graphBoxSize, path);
        setGraphicFrame(gui);
    }

}
