package main.java.utils;

import main.java.containers.Container;
import main.java.containers.EntityContainer;

import java.util.LinkedHashMap;
import java.util.Map;

public class Entities {

    /*Method takes the BM25 output as input and gives a list of entites per query
      Parameter: input - Map of queryId, paraId, container object
      Result: Map of queryId, EntityId, EntityText
    */
    public Map<String, Map<String, String>> getEntitiesPerQuery(Map<String, Map<String, Container>> input){
        Map<String, Map<String, String>> query_entity_list = new LinkedHashMap<>();
        for(Map.Entry<String, Map<String, Container>> m: input.entrySet()){
            Map<String, String> entity_list = new LinkedHashMap<>();

            for(Map.Entry<String, Container> n: m.getValue().entrySet())
            {
                EntityContainer e = n.getValue().getEntity();
                String [] entity_ids = e.getEntityId().split("[\r\n]+");
                String [] entity_val = e.getEntityVal().split("[\r\n]+");
                for(int s = 0; s  < entity_ids.length; s++) {
                    entity_list.put(entity_ids[s], entity_val[s]);
                }
            }

            query_entity_list.put(m.getKey(), entity_list);
        }
        return query_entity_list;
    }

    public Map<String, Map<String, Integer>> getParagraphsScore(Map<String, Map<String, Container>> bm25_ranking,
                                 Map<String, Map<String, Integer>> ranked_entities)
    {
        Map<String, Map<String, Integer>> ranked_para = new LinkedHashMap<>();

        for(Map.Entry<String, Map<String, Container>> m: bm25_ranking.entrySet())
        {
            for(Map.Entry<String, Container> n:m.getValue().entrySet())
            {
                EntityContainer e = n.getValue().getEntity();
                int entity_counter = 0;
                String [] entity_ids = e.getEntityId().split("[\r\n]+");
                for(int s = 0; s  < entity_ids.length; s++) {
                    Map<String, Integer> query_entities_list = ranked_entities.get(m.getKey());

                    if(query_entities_list.containsKey(entity_ids[s]))
                    {
                        entity_counter += 1;
                    }
                    if(ranked_para.containsKey(m.getKey()))
                    {
                        Map<String, Integer> query_extract = ranked_para.get(m.getKey());
                        if(query_extract.containsKey(entity_ids[s]))
                        {
                            query_extract.put(n.getKey(), query_extract.get(entity_ids[s])+1);
                        }
                        else{
                            query_extract.put(n.getKey(), entity_counter);
                        }

                    }

                }

                if(!ranked_para.containsKey(m.getKey()))
                {
                    Map<String, Integer> para_rank = new LinkedHashMap<>();
                    para_rank.put(n.getKey(), entity_counter);
                    ranked_para.put(m.getKey(), para_rank);
                }

            }
        }
        return ranked_para;
    }

    public Map<String, Map<String, Integer>> getRerankedParas(Map<String, Map<String, Integer>> ranked_entities)
    {
        for(Map.Entry<String, Map<String, Integer>> m: ranked_entities.entrySet())
        {
            ranked_entities.put(m.getKey(), SortUtils.sortByValue(m.getValue()));
        }

        return ranked_entities;
    }

}
