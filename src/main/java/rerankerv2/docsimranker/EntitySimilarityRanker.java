package main.java.rerankerv2.docsimranker;

import main.java.commandparser.RegisterCommands;
import main.java.containers.Container;
import main.java.containers.EntityContainer;
import org.nd4j.linalg.api.ndarray.INDArray;

import java.util.*;

public class EntitySimilarityRanker  extends SimilarityRankerBase
{

    public List<String> getEntities(Container input){
            List<String> emention = new ArrayList<>();

            EntityContainer e = input.getEntity();
            //String [] entity_ids = e.getEntityId().split("[\r\n]+");
            String [] entity_val = e.getEntityVal().split("[\r\n]+");

            emention = Arrays.asList(entity_val);
//            for(int s = 0; s  < entity_ids.length; s++) {
//                emention.add(entity_val[s]);
//            }
            return emention;
    }

    public EntitySimilarityRanker(RegisterCommands.CommandSearch SearchCommand, Map<String,String> query)
    {
        super(SearchCommand,query);
    }

    @Override
    INDArray getVector(int docID) {
        return null;
    }

    public void doEntityReRank()
    {
        rerank();
    }

    public void doEntityReRank(Map<String,Map<String,Container>> expandedlist)
    {
        rerank(expandedlist);
    }

}
