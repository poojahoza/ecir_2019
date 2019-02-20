package main.java.graph;

import main.java.utils.SortUtils;
import org.jgrapht.Graph;

import java.util.LinkedHashMap;
import java.util.Map;

public class GraphSimConstructor {

    public Map<String, Map<String, Integer>> getGraphDegree(Map<String, Map<String, String>> input,
                                                            Integer dim,
                                                            String embedding_file_loc)
    {
        GraphSimilaritySearcher graph = new GraphSimilaritySearcher();
        Map<String, Map<String, Integer>> query_entity_degree = new LinkedHashMap<>();

        for(Map.Entry<String, Map<String, String>> m:input.entrySet()) {
            Graph g = graph.generateGraph(m.getValue(), dim, embedding_file_loc);
            Map<String, Integer> degree_list = graph.getNodeDegree(g);
            degree_list = SortUtils.sortByValue(degree_list);

            query_entity_degree.put(m.getKey(), degree_list);
        }
        return query_entity_degree;
    }

}
