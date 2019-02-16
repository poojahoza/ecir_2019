package main.java.reranker;

import main.java.commandparser.RegisterCommands;
import main.java.searcher.BaseBM25;

import java.io.IOException;
import java.util.Map;

/*
@author: Amith
This class performs the Re-Ranking based on the document similarity
*/
public class ReRanker
{
    private BaseBM25 bm25 = null;

    private RegisterCommands.CommandSearch SearchCommand= null;
    private Map<String,String> query =null;
    private  ReRankRunner runnerReRank = null;

    public ReRanker(RegisterCommands.CommandSearch SearchCommand, Map<String,String> query)
    {
        this.SearchCommand = SearchCommand;
        this.query = query;
        try {
            this.bm25 = new BaseBM25(SearchCommand.getkVAL(),SearchCommand.getIndexlocation());
        } catch (IOException e) {
            e.printStackTrace();
        }
        runnerReRank = new ReRankRunner(bm25,SearchCommand.getWordEmbeddingFile(),SearchCommand.getDimension());
    }


    public void ReRank()
    {
        for(Map.Entry<String,String> q: query.entrySet())
        {
            System.out.println(q.getValue());
            Map<String,Double> v= runnerReRank.getReRank(bm25.getRanking(q.getValue()));
            if(v!=null)
            {
                    System.out.println(v);
            }
            break;
        }
    }




}
