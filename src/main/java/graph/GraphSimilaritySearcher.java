package main.java.graph;

import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.DefaultUndirectedWeightedGraph;

import java.util.HashMap;

public class GraphSimilaritySearcher implements GraphConstructor {

    Graph<String, DefaultEdge> entity_graph = null;

    @Override
    public Graph generateGraph(HashMap<String, String> entity_list){
        entity_graph = new DefaultUndirectedWeightedGraph<>(DefaultEdge.class);
        createVertexList(entity_list);
        createEdgeList(entity_list);
        return entity_graph;
    }

    @Override
    public void createVertexList(HashMap<String, String> entity_list){
        for(String v:entity_list.keySet()){
            entity_graph.addVertex(v);
        }
    }

    @Override
    public void createEdgeList(HashMap<String, String> entity_list){

    }
}
