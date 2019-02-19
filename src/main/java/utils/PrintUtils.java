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
}
