package main.java.mrf;

import main.java.commandparser.RegisterCommands;
import main.java.containers.Container;
import main.java.reranker.WordEmbedding;
import main.java.rerankerv2.concepts.EmbeddingStrategy;
import main.java.searcher.BaseBM25;
import main.java.utils.PrintUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.StreamSupport;

public class MarkovRandomField
{
    private RegisterCommands.CommandSearch SearchCommand = null;
    private Map<String,String> query = null;
    private EmbeddingStrategy embedding = null;

    public MarkovRandomField(RegisterCommands.CommandSearch SearchCommand, Map<String,String> query)
    {
        this.SearchCommand =SearchCommand;
        this.query = query;
        embedding = new WordEmbedding(SearchCommand.getDimension(),SearchCommand.getWordEmbeddingFile());
    }

    private Map<String,Container> collectEvidences(Map<String, Container> unranked,String queryVal)
    {

        if(queryVal.equals("Aerosol spray History"))
        {
            System.out.println("Stop");
        }
        Map<String,Container> result = new LinkedHashMap<String,Container>();
        result.putAll(unranked);

        ArrayList<Double> ans = Evidences.evidence1(unranked);
        Evidences.updatescores(result,ans);

        ans = Evidences.evidence2(unranked,embedding,SearchCommand.getDimension(),queryVal,SearchCommand.getIndexlocation(),
                    SearchCommand.getkVAL());
        Evidences.updatescores(result,ans);


        PrintUtils.displayMapContainerList(result);

       return result;
    }

    public void doMarkovRandomField()
    {
        Map<String,Map<String,Container>> res = new LinkedHashMap<String,Map<String,Container>>();
        long start= System.currentTimeMillis();

        StreamSupport.stream(query.entrySet().spliterator(),SearchCommand.isParallelEnabled())
                .forEach(q -> {
                    try {
                        System.out.println("Query "+ q.getValue());
                        //int count=0;
                        BaseBM25 bm = new BaseBM25(SearchCommand.getkVAL(),SearchCommand.getIndexlocation());
                        Map<String, Container> retDoc = bm.getRanking(q.getValue());
                        Map<String,Container> reranked = collectEvidences(retDoc,q.getValue());
                        res.put(q.getKey(),reranked);
                        //count++;
                        //if(count==1) System.exit(-1);
                        System.out.print(".");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
        long end = System.currentTimeMillis();
        long timeElapsed = end-start;
        System.out.println("Time took :"+ (double)timeElapsed/1000 +" sec "+ ((double)timeElapsed/1000)/60 +" min");

        MrfHelper.writeRunFile(res,"mrf");
    }
}
