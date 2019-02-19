package main.java.utils;

import main.java.containers.Container;

import java.util.Map;

public class PrintUtils
{
    public static void displayMap(Map<String,Map<String,Container>> result)
    {
        for(Map.Entry<String,Map<String, Container>> outer: result.entrySet())
        {
            System.out.println(outer.getKey());
            for(Map.Entry<String,Container> inner:outer.getValue().entrySet())
            {
                System.out.println(inner.getKey()+" "+inner.getValue().getScore()+" "+inner.getValue().getDocID());
            }
            System.out.println("***********************************************************************************************************************");

        }
    }

    public static void  displayMapContainer(Map<String,Container> result)
    {
        for(Map.Entry<String,Container> val: result.entrySet())
        {
            String paraID = val.getKey();
            double score = val.getValue().getScore();
            int docID= val.getValue().getDocID();
            System.out.println(paraID +" "+ score +" "+docID);
        }

    }
}
