package Ex2GUI;

import api.DirectedWeightedGraph;
import api.DirectedWeightedGraphAlgorithms;
import api.NodeData;
import impl.DWGraph;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
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
    private JButton newGraphButton;

    private DirectedWeightedGraphAlgorithms algo;
    private final JButton[] actionButtons;
    final int graphPadding = 10; // All sides padding
    final int graphBoxSize = 500; // Square sized graph

    public Ex2UI(String title, DirectedWeightedGraphAlgorithms algo) {
        super(title);
        this.algo = algo;
        graphicFrame = new JFrame();

        this.actionButtons = new JButton[]{isConnectedButton, centerButton, tspButton, showGraphButton, shortestPathButton};
        setButtonsVisibility();
        addButtonListeners();
        buttonListenerMapper();

        graphicFrame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setContentPane(panel);
        this.pack();

    }

    private void setButtonsVisibility() {
        boolean state = algo == null || algo.getGraph() == null;
        if (state)
            newGraphButton.setText("Create new graph");
        else
            newGraphButton.setText("Reset current graph");
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

    private void setGraphicFrame() {
        graphicFrame.setLocationRelativeTo(null);
        graphicFrame.setMinimumSize(new Dimension(graphBoxSize + 2 * graphPadding,
                graphBoxSize + 2 * graphPadding));
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
    }

    private void showColoredPathGraph(List<NodeData> path) {
        GraphUI gui = GraphUI.initColored(algo.getGraph(),
                graphPadding, graphPadding, graphPadding, graphPadding, path);
        graphicFrame = new GraphEditor(algo, gui);
        setGraphicFrame();
    }

    private void buttonListenerMapper(){
        this.loadGraphButton.addActionListener( e -> onLoadClicked());
        this.showGraphButton.addActionListener( e -> onShowGraphClicked());
        this.isConnectedButton.addActionListener( e -> onIsConnectedClicked());
        this.tspButton.addActionListener( e -> onTspClicked());
        this.centerButton.addActionListener( e -> onCenterClicked());
        this.shortestPathButton.addActionListener( e -> onShortestPathClicked());
        this.newGraphButton.addActionListener(e -> setNewGraph());
    }

    private void setNewGraph() {
        this.algo.init(new DWGraph());
        setButtonsVisibility();
    }


    private void onLoadClicked(){
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
    }

    private void onIsConnectedClicked(){
        if (algo.isConnected())  // Show isConnected state to the user.
            JOptionPane.showMessageDialog(null, "This graph is a connected graph.");
        else
            JOptionPane.showMessageDialog(null, "This graph is not a connected graph.");
    }

    private void onCenterClicked(){
        algo.center().getInfo();
        JOptionPane.showMessageDialog(null, "Graph Center Node Info\n" + algo.center().getInfo());
    }

    private void onShowGraphClicked(){
        GraphUI gUI = new GraphUI(algo.getGraph(), // Graph
                graphPadding, graphPadding, graphPadding, graphPadding); // Padding <Right, Top, Left, Bottom>
        graphicFrame = new GraphEditor(algo, gUI);
        setGraphicFrame();
    }

    private void onTspClicked(){
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
    }

    private void onShortestPathClicked(){
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
