package main.java.graph;

import org.jgrapht.Graph;

import java.util.HashMap;

public interface GraphConstructor {

    Graph generateGraph(HashMap<String, String> entity_list);

    void createVertexList(HashMap<String, String> entity_list);

    void createEdgeList(HashMap<String, String> entity_list);
}
