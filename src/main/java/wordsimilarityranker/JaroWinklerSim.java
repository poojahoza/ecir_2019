package main.java.wordsimilarityranker;


import info.debatty.java.stringsimilarity.JaroWinkler;
import main.java.containers.Container;
import main.java.searcher.BaseBM25;
import main.java.utils.RunWriter;

import java.util.Map;

public class JaroWinklerSim extends SimilarityBase
{
    public JaroWinklerSim(BaseBM25 bm , Map<String,String> query)
    {
        super(bm,query);
    }

    @Override
    public double getScore(String str1, String str2) {
        JaroWinkler jaroWinkler = new JaroWinkler();
        return jaroWinkler.similarity(str1,str2);
    }

    public void doJaroWinkler()
    {
        Map<String,Map<String, Container>> result = performReRank();
        if( result != null)
        {
            RunWriter.writeRunFile("jarowinkler_similarity",result);
        }
    }

}
