package main.java.wordsimilarityranker;

import main.java.containers.Container;
import main.java.searcher.BaseBM25;

import java.util.Map;

/*
A Base class which implements all functionality.
*/
abstract  public class SimilarityBase
{
    private  BaseBM25 bm = null;
    private Map<String,String> query = null;

    SimilarityBase(BaseBM25 bm, Map<String,String> query)
    {
        this.bm = bm;
        this.query = query;
    }

    /*
        Abstract methods needs to be implemented by other methods
    */
    abstract  public double getScore(String str1,String str2);

    /*
    This will perform the re-rank for each query candidate set.
    */
    private Map<String, Container> performRankerOnCandidate(Map<String, Container> forEachContainer)
    {

        if(forEachContainer.size()<2) return forEachContainer;

        return null;
    }


    /*
        For each query, it retrieves the initial BM25 candidate sets and calls performRankerOnCandidate to rerank
        based on the String similarity.
    */
    protected void  performReRank()
    {
        for(Map.Entry<String,String> Q: query.entrySet())
        {
            System.out.println("Key: "+ Q.getKey() + " value :"+Q.getValue());
        }

    }

}
