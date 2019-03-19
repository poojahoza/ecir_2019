package main.java.rerankerv2.docsimranker;

import main.java.commandparser.RegisterCommands;
import main.java.containers.Container;
import main.java.containers.EntityContainer;
import main.java.searcher.EntityAbstractSearcher;
import org.nd4j.linalg.api.ndarray.INDArray;

import java.util.*;

public class EntitySimilarityRanker  extends SimilarityRankerBase
{
    private EntityAbstractSearcher ebs =null;
    public EntitySimilarityRanker(RegisterCommands.CommandSearch SearchCommand, Map<String,String> query)
    {
        super(SearchCommand,query);
        ebs = new EntityAbstractSearcher(SearchCommand.getEntityIndLoc());
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
        //getEntities()
        return null;
    }

    public void lead(List<String> ll) {
        for (String str : ll) {
            System.out.println(ebs.getAbstract(str));
        }

    }

    public void doEntityReRank()
    {
        for(Map.Entry<String,String> q: query.entrySet())
        {
            Map<String,Container> re = bm25.getRanking(q.getValue());
            for(Map.Entry<String,Container> val: re.entrySet())
            {
                //lead(getEntities(val.getValue()));
                System.out.println(ebs.getAbstract(q.getValue()));
            }
        }

    }
}
