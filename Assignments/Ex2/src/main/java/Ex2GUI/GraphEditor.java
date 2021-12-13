package Ex2GUI;

import api.DirectedWeightedGraph;
import api.DirectedWeightedGraphAlgorithms;
import api.NodeData;
import api.EdgeData;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class GraphEditor extends JFrame {
    private JPanel mainPanel;
    private JButton exportImageBtn;
    private JButton saveBtn;
    private JPanel graphPanel;
    private JButton infoSelectorBtn;
    private JButton removeSelectorBtn;
    private JButton addNodeBtn;
    private JButton removeEdgeBtn;
    private JButton addEdgeButton;
    private JLabel mouseGraphLocation;

    private final DirectedWeightedGraphAlgorithms algo;
    private final GraphUI gUI;

    public GraphEditor(DirectedWeightedGraphAlgorithms algo, GraphUI graphPanel) {
        this.setContentPane(mainPanel);
        this.algo = algo;
        gUI = graphPanel;
        gUI.setLocationLabel(mouseGraphLocation);
        this.graphPanel.add(gUI);
        this.graphPanel.addMouseListener(graphPanel);
        this.exportImageBtn.addActionListener(e -> exportImage());

        this.removeEdgeBtn.addActionListener(this::onRemoveEdge);
        this.addEdgeButton.addActionListener(this::onAddEdge);
        this.removeSelectorBtn.addActionListener(e -> gUI.mouseMode = GraphUI.MouseMode.RemoveNode);
        this.addNodeBtn.addActionListener(e -> gUI.mouseMode = GraphUI.MouseMode.AddNode);
        this.infoSelectorBtn.addActionListener(e -> gUI.mouseMode = GraphUI.MouseMode.Info);
        this.saveBtn.addActionListener(this::saveFile);
        this.mouseGraphLocation.setText("Hover a Node to get ID and information");

    }

    private void createUIComponents() {
        // TODO: place custom component creation code here
    }

    private void onRemoveEdge(ActionEvent actionEvent) {
        String o = JOptionPane.showInputDialog("Please enter Edge to remove: src and dest with ',' as divider" +
                " (no empty space)");
        ArrayList<NodeData> nodes = userInputParser(o);
        DirectedWeightedGraph g = gUI.graph;
        if (nodes == null) return; // nodes==null => Error occurred; displayed message to user in userInputeParser.
        else if (nodes.size() == 0)
            JOptionPane.showMessageDialog(null, "One key or more were missing.");
        else {
            EdgeData e = g.getEdge(nodes.get(0).getKey(), nodes.get(1).getKey());
            if (e == null)
                JOptionPane.showMessageDialog(null, "The edge your are trying to remove doesn't exist.");
            else {
                g.removeEdge(e.getSrc(), e.getDest());
                graphPanel.updateUI();
            }
        }
    }

    private void onAddEdge(ActionEvent actionEvent) {
        String o = JOptionPane.showInputDialog("Please enter Edge to add: src and dest with ',' as divider" +
                " (no empty space)");
        DirectedWeightedGraph g = gUI.graph;
        ArrayList<NodeData> nodes = userInputParser(o);
        if (nodes == null) return;
        if (nodes.size() == 0)
            JOptionPane.showMessageDialog(null, "One or more keys not found.");
        else if (g.getEdge(nodes.get(0).getKey(), nodes.get(1).getKey()) != null)
            JOptionPane.showMessageDialog(null, "Edge already exists");
        else {
            try {
                o = JOptionPane.showInputDialog("Please enter edge weight: <Integer>");
                double n = Double.parseDouble(o);
                if (n < 0)
                    throw new NumberFormatException("n must be >= 0.");
                g.connect(nodes.get(0).getKey(), nodes.get(1).getKey(), n);

            } catch (NumberFormatException nE) {
                JOptionPane.showMessageDialog(null, "Invalid weight. Weight must be a double greater or equal to 0.");
                return;
            }
            graphPanel.updateUI();

        }
    }

    private ArrayList<NodeData> userInputParser(String userInput) {
        if (userInput == null) return null;
        String[] kS = userInput.split(","); // Split inputs
        if (kS.length != 2) {
            JOptionPane.showMessageDialog(null, "Your input is not valid. You must enter <src,dest> i.e '3,2'");
            return null;
        }
        ArrayList<NodeData> nodes = new ArrayList<>(); // Nodes will be saved here
        NodeData node;
        DirectedWeightedGraph g = gUI.graph;
        try {
            for (String k : kS) { // Save parsed nodes
                node = g.getNode(Integer.parseInt(k));
                if (node == null)
                    return new ArrayList<>();
                else
                    nodes.add(node);
            }
            return nodes;
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(null, "Keys must be integers >= 0");
        }
        return null;
    }

    private void exportImage() {
        BufferedImage image = gUI.getScreenshot();
        try {
            String p = getSavePath(new FileNameExtensionFilter("PNG", "png"), "Select where to save the image");
            if (p != null)
                ImageIO.write(image, "jpeg", new File(p));
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Can't write the image");
        }

    }

    private void saveFile(ActionEvent actionEvent) {
        String p = getSavePath(new FileNameExtensionFilter("JSON", "json"), "Select where to save the graph");
        if (p != null)
            algo.save(p);
    }

    private String getSavePath(FileFilter f, String title) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle(title);
        fileChooser.setFileFilter(f);

        int userSelection = fileChooser.showSaveDialog(this);
        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File fileToSave = fileChooser.getSelectedFile();
            return fileToSave.getAbsolutePath();
        } else
            return null;
    }
}
