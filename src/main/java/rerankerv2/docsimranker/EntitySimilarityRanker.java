package main.java.rerankerv2.docsimranker;

import main.java.commandparser.RegisterCommands;
import main.java.containers.Container;
import main.java.containers.EntityContainer;
import main.java.searcher.EntityAbstractSearcher;
import main.java.utils.RunWriter;
import org.nd4j.linalg.api.ndarray.INDArray;

import java.util.*;

public class EntitySimilarityRanker  extends SimilarityRankerBase
{
    public EntitySimilarityRanker(RegisterCommands.CommandSearch SearchCommand, Map<String,String> query)
    {
        super(SearchCommand,query);
    }

    private String[] getEntities(Container input)
    {
            EntityContainer e = input.getEntity();
            if(e.getEntityVal()== null)
            {

            }
            return e.getEntityVal().split("[\r\n]+");
    }

    @Override
    INDArray getVector(Container docID)
    {
        String[] entitiesID = getEntities(docID);
        return null;
    }

    public void doEntityReRank()
    {
        Map<String,Map<String,Container>> res = rerank();
        RunWriter.writeRunFile("Entity_similarity_ranking",res);
    }
}
