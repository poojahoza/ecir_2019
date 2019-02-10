package main.java.reranker;

import main.java.commandparser.RegisterCommands;
import main.java.searcher.BaseBM25;
import main.java.utils.StopWord;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/*
Document Re-ranker based on the document similarity. This is naive approach.
*/

public class ReRanker
{
    private BaseBM25 bm_25 = null;
    private List<String> STOP_WORDS = null;
    private RegisterCommands.CommandSearch SearchCommand= null;
    private Map<String,String> query =null;

    public ReRanker(RegisterCommands.CommandSearch SearchCommand, Map<String,String> query)
    {
        this.SearchCommand = SearchCommand;
        this.STOP_WORDS = StopWord.getStopWords();
        this.query = query;
        try {
            this.bm_25 = new BaseBM25(SearchCommand.getkVAL(),SearchCommand.getIndexlocation());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void ReRank()
    {
        for(Map.Entry<String,String> que:query.entrySet())
        {
            System.out.println(que.getKey() + "----------"+ que.getValue());
        }
    }




}
