package Ex2GUI;

import api.DirectedWeightedGraphAlgorithms;
import api.NodeData;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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
    final int graphPadding = 10;
    final int graphBoxSize = 500;

    public Ex2UI(String title, DirectedWeightedGraphAlgorithms algo){
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
    private void setButtonsVisibility(){
        boolean state = algo == null || algo.getGraph() == null;
        for(JButton b: actionButtons)
            b.setVisible(!state);
        if (!state) // Override visibility for tsp by isConnected
            tspButton.setVisible(algo.isConnected());
        this.pack();
    }

    private void addButtonListeners(){
        loadGraphButton.addActionListener(this);
        for(JButton b: actionButtons)
            b.addActionListener(this);
    }

    private void setGraphicFrame(JPanel g){
        graphicFrame.getContentPane().removeAll(); // reset graphic frame
        graphicFrame.add(g);
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
        GraphUI gui = GraphUI.initColored(algo.getGraph(),
                graphPadding, graphPadding, graphPadding, graphPadding,
                graphBoxSize, graphBoxSize, algo.shortestPath(0, 6));
        setGraphicFrame(gui);

        if (e.getSource() == loadGraphButton){
            JFileChooser fC = new JFileChooser();

            //fC.setAcceptAllFileFilterUsed(false);
            fC.setFileFilter(new FileNameExtensionFilter("json", "json"));
            if (fC.showOpenDialog(this) == 0 && !algo.load(fC.getSelectedFile().toString()))
                JOptionPane.showMessageDialog(null,
                        "Couldn't load the graph, Nothing changed.");
            else {
                if (fC.getSelectedFile() == null) return;
                JOptionPane.showMessageDialog(null, "Loaded new graph");
                setButtonsVisibility();
                this.pack();
            }
        }
        else if (e.getSource() == isConnectedButton) {
            if (algo.isConnected())
                JOptionPane.showMessageDialog(null, "This graph is a connected graph.");
            else
                JOptionPane.showMessageDialog(null, "This graph is not a connected graph.");
        }
        else if (e.getSource() == centerButton) {
            JOptionPane.showMessageDialog(null, "Graph Center Node Info\n" + algo.center().getInfo());
        }
        else if (e.getSource() == showGraphButton) {
            GraphUI g = new GraphUI(algo.getGraph(), // Graph
                    graphPadding, graphPadding, graphPadding, graphPadding, // Padding <Right, Top, Left, Bottom>
                    graphBoxSize, graphBoxSize); // Width, Height
            setGraphicFrame(g);
        }
        else if (e.getSource() == tspButton) {
        }
        else if (e.getSource() == shortestPathButton) {
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
                    JOptionPane.showMessageDialog(null, "A path fround!\n" +
                            "Distance: " + algo.shortestPathDist(srcKey, destKey) +"\n" +
                            "Close this message to load the graph");

                }
            }
            catch (NumberFormatException nfE){
                JOptionPane.showMessageDialog(null, "The key must be an integer >= 0");
            }
        }
    }
}
