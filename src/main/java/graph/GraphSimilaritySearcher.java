package main.java.graph;

import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.DefaultUndirectedWeightedGraph;

import main.java.reranker.WordEmbedding;

import java.util.Map;

public class GraphSimilaritySearcher extends GraphDegreeSearcher {

    WordEmbedding we = null;

    public Graph generateGraph(Map<String, String> entity_list,
                               Integer dim,
                               String embedding_file){
        entity_graph = new DefaultUndirectedWeightedGraph<>(DefaultEdge.class);
        we = new WordEmbedding(dim, embedding_file);
        createVertexList(entity_list);
        this.createEdgeList(entity_list);
        return entity_graph;
    }

    void createEdgeList(Map<String, String> entity_list){
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
