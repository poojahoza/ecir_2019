package main.java.wordsimilarityranker;

import main.java.containers.Container;
import main.java.searcher.BaseBM25;
import main.java.utils.PreProcessor;
import main.java.utils.SortUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
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

    private Map<String, Container> performRankerOnCandidate(String Query, Map<String, Container> candidate) {

        //if the map has only one value, return as-is
        if(candidate.size()<2) return candidate;

        //Preprocessed query terms
        ArrayList<String> querylist = null;
        try {
            querylist = PreProcessor.processTermsUsingLucene(Query);
        } catch (IOException e) {
            e.printStackTrace();
        }

        Map<String,Container> result = new LinkedHashMap<>();

        for(Map.Entry<String,Container> candid : candidate.entrySet())
        {
            double score = 0.0;
            int docID = candid.getValue().getDocID();
            ArrayList<String> canidateterms = null;
            try {
                canidateterms = PreProcessor.processTermsUsingLucene(bm.getDocument(docID));
            } catch (IOException e) {
                e.printStackTrace();
            }

            for(String qterm: querylist)
            {
                for(String cterm: canidateterms)
                {
                    score+= getScore(qterm,cterm);
                }
            }
            Container temp = new Container(score,candid.getValue().getDocID());
            result.put(candid.getKey(),temp);
        }
        return SortUtils.sortByValue(result);
    }


    /*
        For each query, it retrieves the initial BM25 candidate sets and calls performRankerOnCandidate to rerank
        based on the String similarity.
    */
    protected Map<String,Map<String,Container>>  performReRank()
    {
        Map<String,Container> candidate = null;
        Map<String,Container> candidateRanked = null;

        Map<String,Map<String,Container>> result = new LinkedHashMap<>();

        for(Map.Entry<String,String> Q: query.entrySet())
        {
            String query = Q.getValue();
            candidate = bm.getRanking(query);
            candidateRanked = performRankerOnCandidate(query,candidate);
            result.put(Q.getKey(),candidateRanked);
        }
        return result;
    }
}
