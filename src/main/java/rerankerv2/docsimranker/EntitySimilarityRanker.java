package main.java.rerankerv2.docsimranker;

import main.java.commandparser.RegisterCommands;
import main.java.containers.Container;
import main.java.containers.EntityContainer;
import org.nd4j.linalg.api.ndarray.INDArray;

import java.util.*;

public class EntitySimilarityRanker  extends SimilarityRankerBase
{
    public EntitySimilarityRanker(RegisterCommands.CommandSearch SearchCommand, Map<String,String> query)
    {
        super(SearchCommand,query);
    }

    public List<String> getEntities(Container input)
    {
            List<String> emention = new ArrayList<>();
            EntityContainer e = input.getEntity();
            String [] entity_val = e.getEntityVal().split("[\r\n]+");
            emention = Arrays.asList(entity_val);
            return emention;
    }
    @Override
    INDArray getVector(Container docID) {
        return null;
    }


    public void doEntityReRank()
    {
        rerank();
    }
}
