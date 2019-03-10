package main.java.reranker.docsimranker;

import main.java.commandparser.RegisterCommands;
import main.java.containers.Container;
import main.java.containers.EntityContainer;
import main.java.searcher.BaseBM25;
import org.nd4j.linalg.api.ndarray.INDArray;

import java.io.IOException;
import java.util.Map;

abstract class SimilarityReRanker {

    private BaseBM25 bm25 = null;
    private RegisterCommands.CommandSearch SearchCommand= null;
    private Map<String,String> query = null;


    SimilarityReRanker(RegisterCommands.CommandSearch SearchCommand,Map<String,String> query)
    {
        this.SearchCommand = SearchCommand;
        this.query = query;
        try {
            this.bm25 = new BaseBM25(SearchCommand.getkVAL(),SearchCommand.getIndexlocation());
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /*
    All class needs to implement the below function which describes how to build vector
    Takes the Lucene document as input and returns the document vector representation
    */
    abstract INDArray getVector(int docID);


    protected void rerank()
    {

        for(Map.Entry<String,String> q: query.entrySet())
        {
            Map<String, Container> retDoc = bm25.getRanking(q.getValue());

            for(Map.Entry<String,Container> docs: retDoc.entrySet())
            {
                if(SearchCommand.isSpamFilterEnabled())
                {
                   System.out.println("Spam filter enabled for docID :"+ docs.getValue().getDocID());
                }
                //System.out.println("Doc ID: " + docs.getValue().getDocID() );
                EntityContainer ent = docs.getValue().getEntity();
                System.out.println(ent.getEntityVal());
            }
        }
    }
}
