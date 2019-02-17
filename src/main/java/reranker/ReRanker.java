package main.java.reranker;

import main.java.commandparser.RegisterCommands;
import main.java.containers.Container;
import main.java.searcher.BaseBM25;
import main.java.utils.PrintUtils;
import main.java.utils.RunWriter;

import java.io.IOException;
import java.util.LinkedHashMap;
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
        Map<String,Map<String,Container >> result = new LinkedHashMap<String,Map<String,Container>>();
        for(Map.Entry<String,String> q: query.entrySet())
        {
            String Query = q.getValue();
            Map<String, Container> BM25Val = bm25.getRanking(Query);
            Map<String, Container> reOrdered = runnerReRank.getReRank(BM25Val);
            result.put(q.getKey(),reOrdered);
        }

        RunWriter.writeRunFile("doc_sim_reranking",result);

        if(SearchCommand.getisVerbose())
        {
            PrintUtils.displayMap(result);
        }

    }

}
