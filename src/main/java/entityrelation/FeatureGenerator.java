package main.java.entityrelation;

import com.mongodb.MongoClient;

import org.apache.commons.lang3.ArrayUtils;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import main.java.containers.Container;
import main.java.utils.DBSettings;
import main.java.utils.Entities;
import main.java.utils.Stats;
import main.java.utils.SortUtils;
import main.java.database.databaseWrapper;

public class FeatureGenerator {

    public FeatureGenerator(){

        if(DBSettings.mongoClient == null){
            DBSettings.mongoClient = new MongoClient(DBSettings.HOST_NAME, DBSettings.PORT);
            DBSettings.mongoDB = DBSettings.mongoClient.getDB("test");
            DBSettings.mongoCollection = DBSettings.mongoDB.getCollection("entityIndex");
        }
    }

    /*
    first_entity: the main entity
    second_entity: against which the main entity is compared
     *//**/
    private Double get1HopRelation(String first_entity,
                                   String second_entity,
                                   Map<String, String[]> entities_details){
        if(entities_details.containsKey(second_entity)){
            String[] details = entities_details.get(second_entity);
            String[] outlinkIds = details[1].split("[\r\n]+");
            String[] inlinkIds = details[2].split("[\r\n]+");
            if(Arrays.stream(outlinkIds).anyMatch(first_entity::equals) || Arrays.stream(inlinkIds).anyMatch(first_entity::equals)){
                return 1.0;
            }
            /*if(Entities.searchArrayElement(outlinkIds, first_entity) || Entities.searchArrayElement(inlinkIds, first_entity)){
                return 1.0;
            }*/
        }
        return 0.0;
    }

    private Double[] getHopRelations(String first_entity,
                                   String second_entity,
                                   Map<String, String[]> entities_details,
                                     Double[] relations) {
        if(entities_details.containsKey(second_entity)){
            String[] details = entities_details.get(second_entity);
            String[] outlinkIds = details[1].split("[\r\n]+");
            String[] inlinkIds = details[2].split("[\r\n]+");

            if(Arrays.stream(outlinkIds).anyMatch(first_entity::equals)) {
                relations[0] = relations[0] + 1.0;
            }

            if(Arrays.stream(inlinkIds).anyMatch(first_entity::equals)){
                relations[0] = relations[0] + 1.0;
            }

            /*for(int out = 0; out < outlinkIds.length; out++){

                if(entities_details.containsKey(outlinkIds[out])) {
                    String[] out_entity_details = entities_details.get(outlinkIds[out]);
                    String[] out_entity_outlinkIds = out_entity_details[1].split("[\r\n]+");
                    String[] out_entity_inlinkIds = out_entity_details[2].split("[\r\n]+");

                    //System.out.println("outlink id : "+outlinkIds[out]);
                    if (Arrays.stream(out_entity_outlinkIds).anyMatch(first_entity::equals)) {
                        relations[1] = relations[1] + 1.0;
                    }
                    if(Arrays.stream(out_entity_inlinkIds).anyMatch(first_entity::equals)){
                        relations[1] = relations[1] + 1.0;
                    }
                }
            }
            for(int in = 0; in < inlinkIds.length; in++){
                if(entities_details.containsKey(inlinkIds[in])) {
                    String[] in_entity_details = entities_details.get(inlinkIds[in]);
                    String[] in_entity_outlinkIds = in_entity_details[1].split("[\r\n]+");
                    String[] in_entity_inlinkIds = in_entity_details[2].split("[\r\n]+");

                    if (Arrays.stream(in_entity_outlinkIds).anyMatch(first_entity::equals)) {
                        relations[1] = relations[1] + 1.0;
                    }
                    if(Arrays.stream(in_entity_inlinkIds).anyMatch(first_entity::equals)){
                        relations[1] = relations[1] + 1.0;
                    }
                }
            }*/
        }
        return relations;
    }

    private double entityCoMentions(String first_entity,
                                  String second_entity,
                                  Map<String, Container> bm25_ranking){
        double counter = 0.0;
        for(Map.Entry<String, Container> c:bm25_ranking.entrySet()){
            String[] para_entities = c.getValue().getEntity().getEntityId().split("[\r\n]+");
            if (Arrays.stream(para_entities).anyMatch(first_entity::equals) && Arrays.stream(para_entities).anyMatch(second_entity::equals)) {
                counter += (double)1 / c.getValue().getRank();
            }
        }
        return counter;
    }

    private Map<String, Double[]> generateFeatureVectors(Map<String, Integer> entities_list,
                                                         String query_id,
                                                         Map<String, Map<String, Container>> bm25_ranking){
        Map<String, Map<String, Double[]>> entities_features = new LinkedHashMap<>();
        Map<String, Double[]> entities_normalized_features = new LinkedHashMap<>();
        Entities entities_utils = new Entities();
        List<String> entities_array = entities_utils.getEntityIdsList(entities_list);

        String[] entities_ids = new String[entities_array.size()];

        for(int i = 0; i < entities_array.size(); i++){
            entities_ids[i] = entities_array.get(i);
        }

        databaseWrapper dbwrapper = new databaseWrapper();
        Map<String, String[]> entities_details = dbwrapper.getRecordDetails(entities_ids);

        //System.out.println(cursor.length());
        int entity_length = entities_ids.length;
        //System.out.println(entity_length);
        Double hop_relations[] = new Double[] {0.0, 0.0};

        //Map<String, Double[]> other_entities = new LinkedHashMap<>();
        for(int c = 0; c < entity_length; c++){
            //System.out.println(entities_features);
            Map<String, Double[]> other_entities = new LinkedHashMap<>();
            double get1hoprelation_calc = 0.0;
            double get2hoprelation_calc = 0.0;
            double comention_calc = 0.0;

            for(int e = 0; e < entity_length; e++){
                //System.out.println("Inside e "+entities_features.containsKey(entities_array.get(e))+ " "+c+" "+e+" "+entities_array.get(c));
                if(e == c){
                    continue;
                }
                double[] features_list = new double[5];

                if(entities_features.containsKey(entities_array.get(e))){

                    Map<String, Double[]> other_entities_val = entities_features.get(entities_array.get(e));

                    if(other_entities_val.containsKey(entities_array.get(c))){

                        Double[] val = other_entities_val.get(entities_array.get(c));
                        get1hoprelation_calc += val[0].doubleValue();
                        get2hoprelation_calc += val[1].doubleValue();
                        comention_calc += val[2].doubleValue();
                        features_list[0] = val[0].doubleValue();
                        features_list[1] = val[1].doubleValue();
                        features_list[2] = val[2].doubleValue();

                        //System.out.println("=="+entities_array.get(e)+" "+entities_array.get(c)+" "+features_list[0]+" "+features_list[1]+" "+get1hoprelation_calc+" "+get2hoprelation_calc+" "+comention_calc);
                    }
                    other_entities.put(entities_array.get(e), ArrayUtils.toObject(features_list));
                }
                else{
                    hop_relations[0] = 0.0;
                    hop_relations[1] = 0.0;

                    hop_relations = getHopRelations(entities_array.get(c), entities_array.get(e), entities_details, hop_relations);
                    features_list[2] = entityCoMentions(entities_array.get(c), entities_array.get(e), bm25_ranking.get(query_id));
                    features_list[0] = hop_relations[0];
                    features_list[1] = hop_relations[1];

                    get1hoprelation_calc += features_list[0];
                    get2hoprelation_calc += features_list[1];
                    comention_calc += features_list[2];
                    //System.out.println(entities_array.get(c)+" "+entities_array.get(e)+" "+features_list[0]+" "+features_list[1]+" "+get1hoprelation_calc+" "+get2hoprelation_calc+" "+comention_calc);
                    other_entities.put(entities_array.get(e), ArrayUtils.toObject(features_list));

                }

            }
            entities_features.put(entities_array.get(c), other_entities);
            /*entities_normalized_features.put(entities_array.get(c), new Double[] {get1hoprelation_calc/entity_length,
                    get2hoprelation_calc/entity_length,
                    comention_calc/entity_length});*/

            entities_normalized_features.put(entities_array.get(c), new Double[] {get1hoprelation_calc/entity_length,
                    //get2hoprelation_calc/entity_length,
                    comention_calc/entity_length});
            System.out.println("Features : "+query_id+" "+c+" "+entity_length+" "+get1hoprelation_calc/entity_length+" "+get2hoprelation_calc/entity_length+" "+comention_calc/entity_length);
        }
        return entities_normalized_features;
    }


    public Map<String, Map<String, Double[]>> getFeatureVectors(Map<String, Map<String, Integer>> query_entity_list,
                                                                 Map<String, Map<String, Container>> bm25_ranking){
        System.out.println(query_entity_list.size());
        int p = 0;
        Map<String, Map<String, Double[]>> query_entity_feature_vec = new ConcurrentHashMap<>();

        query_entity_list.entrySet().parallelStream().forEach(m ->
        { query_entity_feature_vec.put(m.getKey(), generateFeatureVectors(m.getValue(), m.getKey(), bm25_ranking));});

        /*query_entity_list.entrySet().parallelStream().forEach(m ->
        {
            if(m.getKey().equals("enwiki:Ice%20bath/Techniques/Ice%20baths%20versus%20cold%20baths")){
                query_entity_feature_vec.put(m.getKey(), generateFeatureVectors(m.getValue(), m.getKey(), bm25_ranking));
            } });*/

       /*Map.Entry<String, Map<String, Integer>> m = query_entity_list.entrySet().iterator().next();
            p++;
            System.out.println(p);
            System.out.println(m.getKey());
            query_entity_feature_vec.put(m.getKey(), generateFeatureVectors(m.getValue(), m.getKey(), bm25_ranking));*/

        return query_entity_feature_vec;
    }

    public Map<String, Map<String, Double[]>> getNormalizedFeatureVectors(Map<String, Map<String, Integer>> query_entity_list,
                                                                       Map<String, Map<String, Container>> bm25_ranking){

        Map<String, Map<String, Double[]>> query_entity_normalized_vec;
        query_entity_normalized_vec = getFeatureVectors(query_entity_list, bm25_ranking);
        query_entity_normalized_vec = new Stats().normalizeData(query_entity_normalized_vec);
        return query_entity_normalized_vec;
    }

    public Map<String, Map<String, Double>> extractFeatures(Map<String, Map<String, Double[]>> feature_vec_list, int featureNum){
        Map<String, Map<String, Double>> features = new LinkedHashMap<>();

        for(Map.Entry<String, Map<String, Double[]>> mp : feature_vec_list.entrySet()){
            for(Map.Entry<String, Double[]> np: mp.getValue().entrySet()){
                if (features.containsKey(mp.getKey())) {
                    Map<String, Double> extract = features.get(mp.getKey());
                    //Double[] features = new Double[] {Double.parseDouble(words[3]), Double.parseDouble(words[4])};
                    extract.put(np.getKey(), np.getValue()[featureNum]);
                } else {

                    Map<String, Double> temp = new LinkedHashMap<>();
                    //Double[] features = new Double[] {Double.parseDouble(words[3]), Double.parseDouble(words[4])};
                    temp.put(np.getKey(), np.getValue()[featureNum]);
                    features.put(mp.getKey(), temp);
                }
            }
        }
        return features;
    }

    public Map<String, Map<String, Double>> sortFeatureVectors(Map<String, Map<String, Double>> feature_vec_list){
        Map<String, Map<String, Double>> sorted_entity_list = new LinkedHashMap<>();
        for(Map.Entry<String, Map<String, Double>> m : feature_vec_list.entrySet()) {
            sorted_entity_list.put(m.getKey(), Entities.sortByValueWithLimit(m.getValue()));
        }
        return sorted_entity_list;
    }

    public Map<String, Map<String, Double>> generateDotProduct(String feature_vectors, String ranklib_model ){

        Map<String, Map<String, Double>> query_entity_score = new LinkedHashMap<>();
        Entities e = new Entities();
        Map<String, Map<String, Double[]>> features = e.readEntityFeatureVectorFile(feature_vectors);
        Double[] weights = e.readRankLibModelFile(ranklib_model);

        Stats s = new Stats();

        for(Map.Entry<String, Map<String, Double[]>> m : features.entrySet()){
            Map<String, Double> score = new LinkedHashMap<>();
            for(Map.Entry<String, Double[]> n: m.getValue().entrySet()) {
                score.put(n.getKey(), s.getDotProduct(n.getValue(), weights));
            }
            query_entity_score.put(m.getKey(), SortUtils.sortByValue(score));
        }
        return query_entity_score;
    }

    public Map<String, Map<String, Double>> generateAverageCentroidVector(String feature_vectors){

        Map<String, Map<String, Double>> query_entity_score = new LinkedHashMap<>();
        Entities e = new Entities();
        Map<String, Map<String, Double[]>> features = e.readEntityFeatureVectorFile(feature_vectors);

        Stats s = new Stats();

        for(Map.Entry<String, Map<String, Double[]>> m : features.entrySet()){
            Map<String, Double> score = new LinkedHashMap<>();
            int entities_size = m.getValue().size();
            Double[] centroid = new Double[] {0.0, 0.0, 0.0, 0.0, 0.0};
            for(Map.Entry<String, Double[]> n: m.getValue().entrySet()) {
                try {
                    //System.out.println(n.getValue());
                    centroid[0] += n.getValue()[0];
                    centroid[1] += n.getValue()[1];
                    centroid[2] += n.getValue()[2];
                    centroid[3] += n.getValue()[3];
                    centroid[4] += n.getValue()[4];
                }catch (NullPointerException npe){
                    System.out.println(npe.getMessage());
                }
            }
            centroid[0] = centroid[0]/entities_size;
            centroid[1] = centroid[1]/entities_size;
            centroid[2] = centroid[2]/entities_size;
            centroid[3] = centroid[3]/entities_size;
            centroid[4] = centroid[4]/entities_size;


            for(Map.Entry<String, Double[]> n: m.getValue().entrySet()) {
                score.put(n.getKey(), s.getDotProduct(n.getValue(), centroid));
            }
            query_entity_score.put(m.getKey(), SortUtils.sortByValue(score));

        }
        return query_entity_score;

    }
}
