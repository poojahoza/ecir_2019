package main.java.utils;

import org.tartarus.snowball.ext.PorterStemmer;

import java.util.ArrayList;
import java.util.List;

public class PreProcessor
{

    private final  List<String> STOP_WORDS = StopWord.getStopWords();

    private static String[] processQuery(String query)
    {
        return query.replaceAll("[^a-zA-Z ]", "").toLowerCase().split("\\s+");
    }


    public static ArrayList<String> processDocument(String sb)
    {
        PreProcessor p = new PreProcessor();

        String[] data = processQuery(sb);
        ArrayList<String> processedData= new ArrayList<>();
        for(String s:data)
        {
            if(!p.STOP_WORDS.contains(s))
            {
                if(!processedData.contains(s))

                    processedData.add(s);
            }
        }
        return processedData;
    }

    public static String stemTerm(String term)
    {
        PorterStemmer stem= new PorterStemmer();
        stem.setCurrent(term);
        stem.stem();
        return stem.getCurrent();
    }



}
