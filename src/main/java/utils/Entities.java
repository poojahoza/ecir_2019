package main.java.utils;

import main.java.containers.Container;
import main.java.containers.EntityContainer;
import main.java.utils.SortUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
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

    public Map<String, Map<String, String>> getSortedEntitiesPerQuery(Map<String, Map<String, Container>> input){
        Map<String, Map<String, String>> query_entity_list = new LinkedHashMap<>();
        for(Map.Entry<String, Map<String, Container>> m: input.entrySet()){
            Map<String, String> entity_list = new LinkedHashMap<>();

            for(Map.Entry<String, Container> n: m.getValue().entrySet())
            {
                EntityContainer e = n.getValue().getEntity();
                String [] entity_ids = e.getEntityId().split("[\r\n]+");
                for(int s = 0; s  < entity_ids.length; s++) {
                    if(entity_list.containsKey(entity_ids[s])){
                        int freq = Integer.valueOf(entity_list.get(entity_ids[s]));
                        freq++;
                        entity_list.put(entity_ids[s], String.valueOf(freq));
                    }else{
                        entity_list.put(entity_ids[s], "1");
                    }

                }
            }
            entity_list = SortUtils.sortByValue(entity_list);
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
            Map<String, Double> ranked_entities_extract = ranked_entities.get(m.getKey());
            for(Map.Entry<String, Container> n:m.getValue().entrySet())
            {
                EntityContainer e = n.getValue().getEntity();
                String [] entity_ids = e.getEntityId().split("[\r\n]+");

                Double score = 0.0;
                for(int s = 0; s  < entity_ids.length; s++) {

                    if(ranked_entities_extract.containsKey(entity_ids[s])){
                        score += ranked_entities_extract.get(entity_ids[s]);
                    }
                }

                if(ranked_para.containsKey(m.getKey())){
                    Map<String, Double> ranked_para_extract = ranked_para.get(m.getKey());
                    ranked_para_extract.put(n.getKey(), n.getValue().getScore()+score);
                }else {
                    Map<String, Double> para_rank = new LinkedHashMap<>();
                    para_rank.put(n.getKey(), n.getValue().getScore()+score);
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

    public String[][] getEntityArray(Map<String, String> entities_list){
        String[][] edge_array = new String[entities_list.size()][2];
        int counter = 0;
        for(Map.Entry<String, String> m: entities_list.entrySet())
        {
            edge_array[counter][0] = m.getKey();
            edge_array[counter][1] = m.getValue();
            counter++;
        }
        return edge_array;
    }

    public static boolean searchArrayElement(String[] search_array, String target){
        int count = Arrays.binarySearch(search_array, target);
        if(count > 0){
            return true;
        }
        return false;
    }

    public Map<String, Map<String, String>> readEntityQrelFile(String filename){

        Map<String, Map<String, String>> mp = new LinkedHashMap<>();

        File fp = new File(filename);
        FileReader fr;
        BufferedReader br = null;


        try {
            fr = new FileReader(fp);
            br = new BufferedReader(fr);

        } catch (IOException e) {
            System.out.println(e.getMessage());
        }

        while (true) {
            try {
                String line = br.readLine();

                if (line == null) {
                    break;
                }

                String[] words = line.split(" ");
                String outKey = words[0];

                if (mp.containsKey(outKey)) {
                    Map<String, String> extract = mp.get(outKey);
                    extract.put(words[2], words[3]);
                } else {

                    Map<String, String> temp = new LinkedHashMap<>();
                    temp.put(words[2], words[3]);
                    mp.put(outKey, temp);
                }
            } catch (NullPointerException n) {
                System.out.println(n.getMessage());
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }

        }
        return mp;
    }


    public Map<String, Map<String, Double[]>> readEntityFeatureVectorFile(String filename){

        Map<String, Map<String, Double[]>> mp = new LinkedHashMap<>();

        File fp = new File(filename);
        FileReader fr;
        BufferedReader br = null;


        try {
            fr = new FileReader(fp);
            br = new BufferedReader(fr);

        } catch (IOException e) {
            System.out.println(e.getMessage());
        }

        while (true) {
            try {
                String line = br.readLine();

                if (line == null) {
                    break;
                }

                String[] words = line.split(" ");
                String outKey = words[0];

                if (mp.containsKey(outKey)) {
                    Map<String, Double[]> extract = mp.get(outKey);
                    Double[] features = new Double[] {Double.parseDouble(words[3]), Double.parseDouble(words[4])};
                    extract.put(words[1], features);
                } else {

                    Map<String, Double[]> temp = new LinkedHashMap<>();
                    Double[] features = new Double[] {Double.parseDouble(words[3]), Double.parseDouble(words[4])};
                    temp.put(words[1], features);
                    mp.put(outKey, temp);
                }
            } catch (NullPointerException n) {
                System.out.println(n.getMessage());
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }

        }
        return mp;
    }

    public Double[] readRankLibModelFile(String filename){

        Double[] mp = new Double[2];

        File fp = new File(filename);
        FileReader fr;
        BufferedReader br = null;


        try {
            fr = new FileReader(fp);
            br = new BufferedReader(fr);

        } catch (IOException e) {
            System.out.println(e.getMessage());
        }

        while (true) {
            try {
                String line = br.readLine();

                if (line == null) {
                    break;
                }
                if(line.startsWith("##")){
                    continue;
                }

                String[] words = line.split(" ");
                for(int s = 0; s<words.length; s++){
                    mp[s] = Double.parseDouble(words[s].split(":")[1]);
                }
            } catch (NullPointerException n) {
                System.out.println(n.getMessage());
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }

        }
        return mp;

    }

}
