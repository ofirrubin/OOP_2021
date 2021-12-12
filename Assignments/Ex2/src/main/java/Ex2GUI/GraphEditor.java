package Ex2GUI;

import api.DirectedWeightedGraph;
import api.DirectedWeightedGraphAlgorithms;
import impl.Geo;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class GraphEditor extends JFrame {
    private JPanel mainPanel;
    private JButton exportImageBtn;
    private JButton saveBtn;
    private JPanel graphPanel;
    private JButton button1;
    private JButton button2;
    private JButton button3;

    private DirectedWeightedGraphAlgorithms algo;
    private GraphUI gUI;

    public GraphEditor(DirectedWeightedGraphAlgorithms algo, GraphUI graphPanel){
        this.setContentPane(mainPanel);
        gUI = graphPanel;
        this.graphPanel.add(gUI);
        this.graphPanel.addMouseListener(graphPanel);
        this.exportImageBtn.addActionListener(e -> exportImage());
    }

    private void createUIComponents() {
        // TODO: place custom component creation code here
        //graphPanel = gUI;
    }

    private void exportImage(){
        BufferedImage image = gUI.getScreenshot();
        try {
            ImageIO.write(image, "png", new File("Image.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
