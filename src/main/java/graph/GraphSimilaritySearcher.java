package main.java.graph;

import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.DefaultUndirectedWeightedGraph;

import main.java.reranker.WordEmbedding;

import java.util.HashMap;
import java.util.Map;

import main.java.graph.GraphDegreeSearcher;

public class GraphSimilaritySearcher extends GraphDegreeSearcher {

    WordEmbedding we = null;

    public Graph generateGraph(HashMap<String, String> entity_list){
        entity_graph = new DefaultUndirectedWeightedGraph<>(DefaultEdge.class);
        createVertexList(entity_list);
        this.createEdgeList(entity_list);
        we = new WordEmbedding(300, "/home/poojaoza/Downloads/glove.6B/glove.6B.300d.txt");
        return entity_graph;
    }

    void createEdgeList(HashMap<String, String> entity_list){
        for(Map.Entry<String, String> m: entity_list.entrySet())
        {
            for(Map.Entry<String, String> n: entity_list.entrySet())
            {
                if(!m.getKey().equals(n.getKey()))
                {
                    Double cos_sim = we.getSimilarity(m.getValue(), n.getValue());
                    if(cos_sim >= 0.5)
                    {
                        entity_graph.addEdge(m.getKey(), n.getKey());
                    }
                }
            }
        }
    }
}
