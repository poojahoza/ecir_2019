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
    private  ReRankIDFRunner runnerIDFReRank = null;
    private  ReRankDFRunner runnerDFReRank = null;


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
        runnerIDFReRank= new ReRankIDFRunner(bm25,SearchCommand.getWordEmbeddingFile(),SearchCommand.getDimension(),SearchCommand.getIndexlocation());
        runnerDFReRank=new ReRankDFRunner(bm25,SearchCommand.getWordEmbeddingFile(),SearchCommand.getDimension(),SearchCommand.getIndexlocation());
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


    public void ReRankIDF()
    {

        Map<String,Map<String,Container >> result = new LinkedHashMap<String,Map<String,Container>>();
        for(Map.Entry<String,String> q: query.entrySet())
        {
            String Query = q.getValue();
            Map<String, Container> BM25Val = bm25.getRanking(Query);
            Map<String, Container> reOrdered = runnerIDFReRank.getReRank(BM25Val);
            result.put(q.getKey(),reOrdered);
        }

        RunWriter.writeRunFile("doc_sim_IDF_reranking",result);
        if(SearchCommand.getisVerbose())
        {
            PrintUtils.displayMap(result);
        }

    }

    public void ReRankDF()
    {
        Map<String,Map<String,Container >> result = new LinkedHashMap<String,Map<String,Container>>();
        for(Map.Entry<String,String> q: query.entrySet())
        {
            String Query = q.getValue();
            Map<String, Container> BM25Val = bm25.getRanking(Query);
            Map<String, Container> reOrdered = runnerDFReRank.getReRank(BM25Val);
            result.put(q.getKey(),reOrdered);
        }

        RunWriter.writeRunFile("doc_sim_DF_reranking",result);

        if(SearchCommand.getisVerbose())
        {
            PrintUtils.displayMap(result);
        }
    }
}
