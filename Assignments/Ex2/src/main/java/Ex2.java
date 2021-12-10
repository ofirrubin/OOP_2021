import Ex2GUI.Ex2UI;
import api.DirectedWeightedGraph;
import api.DirectedWeightedGraphAlgorithms;
import impl.DWGraphAlgo;

import javax.swing.*;
import java.io.File;

/**
 * This class is the main class for Ex2 - your implementation will be tested using this class.
 */
public class Ex2 {
    /**
     * This static function will be used to test your implementation
     * @param json_file - a json file (e.g., G1.json - G3.gson)
     * @return
     */
    public static DirectedWeightedGraph getGrapg(String json_file) {
        DirectedWeightedGraph ans = null;
        // ****** Add your code here ******
        ans = getGrapgAlgo(json_file).getGraph();
        // ********************************
        return ans;
    }
    /**
     * This static function will be used to test your implementation
     * @param json_file - a json file (e.g., G1.json - G3.gson)
     * @return
     */
    public static DirectedWeightedGraphAlgorithms getGrapgAlgo(String json_file) {
        DirectedWeightedGraphAlgorithms ans = null;
        // ****** Add your code here ******
        ans = new DWGraphAlgo();
        ans.load(json_file);
        // ********************************
        return ans;
    }
    /**
     * This static function will run your GUI using the json fime.
     * @param json_file - a json file (e.g., G1.json - G3.gson)
     *
     */
    public static void runGUI(String json_file) {
        DirectedWeightedGraphAlgorithms alg = getGrapgAlgo(json_file);
        // ****** Add your code here ******
        JFrame gui = new Ex2UI("Ex2 - Ofir Rubin", alg);
        gui.setLocationRelativeTo(null);
        gui.setVisible(true);
        gui.pack();
        // ********************************
    }

    public static void main(String[] args){
        String path = String.join(" ", args);
        File f = new File(path);
        if (!f.exists())
            System.out.println("File not found");
        else
            runGUI(f.toString());
    }
}