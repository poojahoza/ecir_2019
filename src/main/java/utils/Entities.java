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

}
