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



    public Map<String, Map<String, Double>> getParagraphsScore(Map<String, Map<String, Container>> bm25_ranking,
                                 Map<String, Map<String, Integer>> ranked_entities)
    {
        Map<String, Map<String, Double>> ranked_para = new LinkedHashMap<>();

        for(Map.Entry<String, Map<String, Container>> m: bm25_ranking.entrySet())
        {
            for(Map.Entry<String, Container> n:m.getValue().entrySet())
            {
                EntityContainer e = n.getValue().getEntity();
                int entity_counter = 1;
                String [] entity_ids = e.getEntityId().split("[\r\n]+");
                for(int s = 0; s  < entity_ids.length; s++) {
                    Map<String, Integer> query_entities_list = ranked_entities.get(m.getKey());

                    if(query_entities_list.containsKey(entity_ids[s]))
                    {
                        entity_counter += 1;
                    }
                    if(ranked_para.containsKey(m.getKey()))
                    {
                        Map<String, Double> query_extract = ranked_para.get(m.getKey());
                        if(query_extract.containsKey(entity_ids[s]))
                        {
                            query_extract.put(n.getKey(), (query_extract.get(entity_ids[s])+1)*n.getValue().getScore());
                        }
                        else{
                            query_extract.put(n.getKey(), entity_counter*n.getValue().getScore());
                        }

                    }

                }

                if(!ranked_para.containsKey(m.getKey()))
                {
                    Map<String, Double> para_rank = new LinkedHashMap<>();
                    para_rank.put(n.getKey(), entity_counter*n.getValue().getScore());
                    ranked_para.put(m.getKey(), para_rank);
                }

            }
        }
        return ranked_para;
    }

    public Map<String, Map<String, Double>> getParagraphsScoreDouble(Map<String, Map<String, Container>> bm25_ranking,
                                                               Map<String, Map<String, Double>> ranked_entities)
    {
        Map<String, Map<String, Double>> ranked_para = new LinkedHashMap<>();

        for(Map.Entry<String, Map<String, Container>> m: bm25_ranking.entrySet())
        {
            for(Map.Entry<String, Container> n:m.getValue().entrySet())
            {
                EntityContainer e = n.getValue().getEntity();
                //int entity_counter = 1;
                String [] entity_ids = e.getEntityId().split("[\r\n]+");
                for(int s = 0; s  < entity_ids.length; s++) {
                    Map<String, Double> query_entities_list = ranked_entities.get(m.getKey());

                    /*if(query_entities_list.containsKey(entity_ids[s]))
                    {
                        entity_counter += 1;
                    }*/
                    if(ranked_para.containsKey(m.getKey()))
                    {
                        Map<String, Double> query_extract = ranked_para.get(m.getKey());
                        if(query_extract.containsKey(entity_ids[s]))
                        {
                            query_extract.put(n.getKey(), (query_extract.get(entity_ids[s]))+n.getValue().getScore());
                        }
                        else{
                            query_extract.put(n.getKey(), n.getValue().getScore());
                        }

                    }

                }

                if(!ranked_para.containsKey(m.getKey()))
                {
                    Map<String, Double> para_rank = new LinkedHashMap<>();
                    para_rank.put(n.getKey(), n.getValue().getScore());
                    ranked_para.put(m.getKey(), para_rank);
                }

            }
        }
        return ranked_para;
    }


    public Map<String, Map<String, Double>> getRerankedParas(Map<String, Map<String, Double>> ranked_entities)
    {
        for(Map.Entry<String, Map<String, Double>> m: ranked_entities.entrySet())
        {
            ranked_entities.put(m.getKey(), SortUtils.sortByValue(m.getValue()));
        }

        return ranked_entities;
    }

    public Map<String, String> expandQuery(Map<String, String> queryCbor,
                                           Map<String, Map<String, Double>> ranked_entities)
    {
        Map<String, String> expanded_query = new LinkedHashMap<>();

        for(Map.Entry<String, String> m: queryCbor.entrySet())
        {
            if(ranked_entities.containsKey(m.getKey()))
            {
                expanded_query.put(m.getKey(), new StringBuilder().append(m.getValue()).append(ranked_entities.get(m.getKey()).keySet().iterator().next()).toString());
            }else{
                expanded_query.put(m.getKey(), m.getValue());
            }

        }
        return expanded_query;
    }

}
