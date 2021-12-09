package Ex2GUI;

import api.DirectedWeightedGraphAlgorithms;
import api.NodeData;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

public class Ex2UI extends JFrame implements ActionListener {
    private JPanel panel;
    private JLabel welcomeLabel;
    private JButton loadGraphButton;

    private JButton isConnectedButton;
    private JButton centerButton;
    private JButton tspButton;
    private JButton shortestPathButton;

    private JButton showGraphButton;

    private DirectedWeightedGraphAlgorithms algo;
    private JButton[] actionButtons;

    public Ex2UI(String title, DirectedWeightedGraphAlgorithms algo){
        super(title);
        this.algo = algo;
        this.actionButtons = new JButton[]{isConnectedButton, centerButton, tspButton, showGraphButton, shortestPathButton};
        setButtonsVisibility();
        addButtonListeners();
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
    }

    private void addButtonListeners(){
        loadGraphButton.addActionListener(this);
        for(JButton b: actionButtons)
            b.addActionListener(this);
    }

    /**
     * Invoked when an action occurs.
     *
     * @param e the event to be processed
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == loadGraphButton){

            JOptionPane.showMessageDialog(null, "Graph loaded");
            setButtonsVisibility();
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
                            "Distance: " + algo.shortestPathDist(srcKey, destKey));
                }
            }
            catch (NumberFormatException nfE){
                JOptionPane.showMessageDialog(null, "The key must be an integer >= 0");
            }
        }
    }
}
