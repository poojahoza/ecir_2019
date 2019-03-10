package main.java.reranker.docsimranker;

import main.java.commandparser.RegisterCommands;
import org.nd4j.linalg.api.ndarray.INDArray;

import java.util.Map;

public class EntitySimilarityReRanker  extends SimilarityReRanker
{

    public EntitySimilarityReRanker(RegisterCommands.CommandSearch SearchCommand, Map<String,String> query)
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

}
