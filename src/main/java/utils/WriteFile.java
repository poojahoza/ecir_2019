package main.java.utils;

import main.java.containers.Container;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;

public class WriteFile {

    private void checkFileExistence(String output_file_name)
    {
        File e = new File(output_file_name);
        if(e.exists())
        {
            e.delete();
        }
    }

    public void generateEntityRunFile(Map<String, Map<String, Double>> results, String methodname)
    {
        String output_file = "output_ranking_"+methodname+".txt";
        List<String> rankings = new ArrayList<String>();
        Path file = Paths.get(System.getProperty("user.dir"), output_file);
        checkFileExistence(output_file);
        try {
            Files.createFile(file);
        }catch(IOException ioe)
        {
            System.out.println(ioe.getMessage());
        }
        for(Map.Entry<String, Map<String, Double>> m: results.entrySet())
        {
            int rank = 0;
            rankings.clear();
            for(Map.Entry<String, Double> n: m.getValue().entrySet())
            {
                rank += 1;
                rankings.add(m.getKey() + " Q0 " + n.getKey() +" "+String.valueOf(rank)+" "+String.valueOf(n.getValue())+ " team1 "+methodname);
            }
            try {
                Files.write(file, rankings, Charset.forName("UTF-8"), StandardOpenOption.APPEND);
            }
            catch (IOException io)
            {
                System.out.println("Error writing in file");
            }
        }
    }

    public void generateBM25RunFile(Map<String, Map<String, Container>> results, String methodname)
    {
        String output_file = "output_ranking_"+methodname+".txt";
        List<String> rankings = new ArrayList<String>();
        Path file = Paths.get(System.getProperty("user.dir"), output_file);
        checkFileExistence(output_file);
        try {
            Files.createFile(file);
        }catch(IOException ioe)
        {
            System.out.println(ioe.getMessage());
        }
        for(Map.Entry<String, Map<String, Container>> m: results.entrySet())
        {
            int rank = 0;
            rankings.clear();
            for(Map.Entry<String, Container> n: m.getValue().entrySet())
            {
                rank += 1;
                rankings.add(m.getKey() + " Q0 " + n.getKey() +" "+String.valueOf(rank)+" "+String.valueOf(n.getValue().getScore())+ " team1 "+methodname);
            }
            try {
                Files.write(file, rankings, Charset.forName("UTF-8"), StandardOpenOption.APPEND);
            }
            catch (IOException io)
            {
                System.out.println("Error writing in file");
            }
        }
    }

    public void generateFeatureVectorRunFile(Map<String, Map<String, Double[]>> results, String methodname)
    {
        String output_file = "output_ranking_"+methodname+".txt";
        List<String> rankings = new ArrayList<String>();
        Path file = Paths.get(System.getProperty("user.dir"), output_file);
        checkFileExistence(output_file);
        try {
            Files.createFile(file);
        }catch(IOException ioe)
        {
            System.out.println(ioe.getMessage());
        }
        for(Map.Entry<String, Map<String, Double[]>> m: results.entrySet())
        {
            //int rank = 0;
            rankings.clear();
            for(Map.Entry<String, Double[]> n: m.getValue().entrySet())
            {
                //rank += 1;
                rankings.add(m.getKey() + Arrays.toString(n.getValue()));

            }
            try {
                Files.write(file, rankings, Charset.forName("UTF-8"), StandardOpenOption.APPEND);
            }
            catch (IOException io)
            {
                System.out.println("Error writing in file");
            }
        }
    }

}
