package main.java.reranker;

import main.java.commandparser.RegisterCommands;
import main.java.searcher.BaseBM25;

import java.io.IOException;
import java.util.Map;

/*
Document Re-ranker based on the document similarity. This is naive approach.
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
            runnerReRank.getReRank(bm25.getRanking(q.getValue()));
            break;
        }
        //runnerReRank.performDocumentSimilarity();
    }




}
