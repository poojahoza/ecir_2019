package main.java.reranker.docsimranker;

import main.java.commandparser.RegisterCommands;
import main.java.containers.Container;
import main.java.containers.EntityContainer;
import main.java.reranker.EmbeddingStrategy;
import main.java.reranker.WordEmbedding;
import main.java.searcher.BaseBM25;
import main.java.utils.PreProcessor;
import org.nd4j.linalg.api.ndarray.INDArray;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

public class EntitySimilarityRanker  extends SimilarityRankerBase
{
    private BaseBM25 bm25 = null;
    private RegisterCommands.CommandSearch SearchCommand= null;
    private Map<String,String> query = null;
    private EmbeddingStrategy embedding = null;


    public EntitySimilarityRanker(RegisterCommands.CommandSearch SearchCommand, Map<String,String> query)
    {
        super(SearchCommand);
        this.SearchCommand = SearchCommand;
        this.query = query;
        try {
            this.bm25 = new BaseBM25(SearchCommand.getkVAL(),SearchCommand.getIndexlocation());
        } catch (IOException e) {
            e.printStackTrace();
        }
        embedding = new WordEmbedding(SearchCommand.getDimension(),SearchCommand.getWordEmbeddingFile());
    }


    @Override
    INDArray getVector(int docID) {
        return null;
    }

    protected void checkForSpam(Container c)
    {
        String content = bm25.getDocument(c.getDocID());
        // System.out.println(content);
        ArrayList<String> res= null;
        try {
            res = PreProcessor.processTermsUsingLucene(content);
        } catch (IOException e) {
            e.printStackTrace();
        }

        for(String s: res)
        {
            System.out.println(s+"  "+ embedding.getEmbeddingVector(s));
        }
    }

    @Override
    void rerank()
    {
        for(Map.Entry<String,String> q: query.entrySet())
        {
            Map<String, Container> retDoc = bm25.getRanking(q.getValue());

            for(Map.Entry<String,Container> docs: retDoc.entrySet())
            {
                if(SearchCommand.isSpamFilterEnabled())
                {
                    System.out.println("Spam filter enabled for docID :"+ docs.getValue().getDocID());
                    checkForSpam(docs.getValue());
                }
                //System.out.println("Doc ID: " + docs.getValue().getDocID() );
                EntityContainer ent = docs.getValue().getEntity();
                System.out.println(ent.getEntityVal());
            }
        }
    }

    public void doEntityReRank()
    {
        rerank();
    }

}
