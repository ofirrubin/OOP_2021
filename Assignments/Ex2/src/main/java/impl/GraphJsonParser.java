package impl;

import api.DirectedWeightedGraph;
import api.GeoLocation;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.internal.LinkedTreeMap;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class GraphJsonParser
{
    public static DirectedWeightedGraph load(FileReader f){
        Gson gson = new Gson();
        DWGraph graph = new DWGraph();
        Map<String, ArrayList<LinkedTreeMap<String, Object>>> g = new HashMap<>();
        try {
        g = gson.fromJson(f, g.getClass());
            g.get("Nodes").forEach(n -> graph.addNode(new Node(((Double) n.get("id")).intValue(),
                    getLocation((String) n.get("pos")))));
            g.get("Edges").forEach(e -> graph.connect(((Double) e.get("src")).intValue(),
                    ((Double) e.get("dest")).intValue(),
                    (Double) e.get("w")));

            return graph;
        }
        catch(Exception e){
            return null;
        }
    }

    public static void save(FileWriter f, DirectedWeightedGraph graph) throws IOException {
        HashMap<String, ArrayList<LinkedTreeMap<String, Object>>> g = new HashMap<>();
        g.put("Nodes", getNodes(graph));
        g.put("Edges", getEdges(graph));
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        gson.toJson(g, f);
        f.flush();
        f.close();
    }

    private static ArrayList<LinkedTreeMap<String, Object>> getNodes(DirectedWeightedGraph graph){
        ArrayList<LinkedTreeMap<String, Object>> allElements = new ArrayList<>();

        graph.nodeIter().forEachRemaining(n ->{
            LinkedTreeMap<String, Object> element = new LinkedTreeMap<>();
            element.put("pos", geoString(n.getLocation()));
            element.put("id", n.getKey());
            allElements.add(element);
        });
        return allElements;
    }
    private static ArrayList<LinkedTreeMap<String, Object>> getEdges(DirectedWeightedGraph graph){
        ArrayList<LinkedTreeMap<String, Object>> allElements = new ArrayList<>();
        //LinkedTreeMap<String, Object> element = new LinkedTreeMap<>();
        graph.edgeIter().forEachRemaining(e ->{
            LinkedTreeMap<String, Object> element = new LinkedTreeMap<>();
            element.put("src", e.getSrc());
            element.put("dest", e.getDest());
            element.put("w", e.getWeight());
            allElements.add(element);
        });

        return allElements;
    }


    private static Geo getLocation(String s){
        String[] geo = s.split(",", 3);
        return new Geo(Double.parseDouble(geo[0]),Double.parseDouble(geo[1]), Double.parseDouble(geo[2]));
    }

    private static String geoString(GeoLocation g){
        return g.x() + "," + g.y() + "," + g.z();
    }
}
