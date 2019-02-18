package main.java.graph;

import org.jgrapht.*;
import org.jgrapht.graph.*;
//import org.jgrapht.io.*;
import org.jgrapht.traverse.*;

import java.util.HashMap;
import java.util.LinkedHashMap;

public class GraphGenerator {

    Graph<String, DefaultEdge> entity_graph = null;

    public Graph generateGraph(HashMap<String, String> entity_list){
        entity_graph = new DefaultUndirectedWeightedGraph<>(DefaultEdge.class);
        createVertexList(entity_list);
        createEdgeList(entity_list);
        return entity_graph;
    }

    public HashMap<String, Integer> getNodeDegree(Graph g){

        HashMap<String, Integer> entity_degree = new LinkedHashMap<>();
        for(Object v:g.vertexSet()){
            entity_degree.put(v.toString(),entity_graph.degreeOf(v.toString()));
        }
        return entity_degree;
    }

    private void createVertexList(HashMap<String, String> entity_list)
    {
        for(String v:entity_list.keySet()){
            entity_graph.addVertex(v);
        }
    }

    private void createEdgeList(HashMap<String, String> entity_list)
    {
        for (HashMap.Entry<String, String> e: entity_list.entrySet()){
            String [] outlinks_list = e.getValue().split("[\r\n]+");
            for(int l = 0; l < outlinks_list.length; l++){
                for(String eid:entity_list.keySet()){
                    if(eid.equals(outlinks_list[l])){
                        entity_graph.addEdge(outlinks_list[l], eid);
                    }
                }
            }
            //System.out.println(e.getValue());
        }
    }
}


