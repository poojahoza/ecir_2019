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
        String[][] edge_array = new String[entity_list.size()][2];
        int counter = 0;
        for(Map.Entry<String, String> m: entity_list.entrySet())
        {
            edge_array[counter][0] = m.getKey();
            edge_array[counter][1] = m.getValue();
            counter++;
        }
        for(int i = 0; i < edge_array.length; i++)
        {
            for(int j = i+1; j < edge_array.length; j++)
            {
                Double cos_sim = we.getSimilarity(edge_array[i][1], edge_array[j][1]);
                if(cos_sim >= 0.5)
                {
                    entity_graph.addEdge(edge_array[i][0], edge_array[j][0]);
                    entity_graph.setEdgeWeight(edge_array[i][0], edge_array[j][0], cos_sim);
                }
            }
        }
    }
}
