package main.java.utils;

import main.java.containers.Container;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

public class RunWriter
{
    public static void writeRunFile(String mname, Map<String, Map<String, Container>> res){

        File dir = new File("result");
        if (!dir.exists()) {
            boolean isSuccess = false;
            try {
                isSuccess = dir.mkdir();
            } catch (SecurityException se) {
                System.out.println(se.getMessage());
            }

            if (!isSuccess) {
                System.out.println("Unable to create the directory");
                System.exit(-1);
            }
        }

        String result = "result" + System.getProperty("file.separator") + "output_" + mname + "_ranking.txt";

        if (result != null) {
            File e = new File(result);
            if (e.exists()) {
                e.delete();
            }


            FileWriter ptr = null;
            try {
                ptr = new FileWriter(result);
            } catch (IOException ee) {
                ee.printStackTrace();
            }

            String newLine = System.getProperty("line.separator");
            String runFileLine;

            for(Map.Entry<String,Map<String, Container>> outer: res.entrySet())
            {
                int ranking=0;
                for(Map.Entry<String,Container> inner:outer.getValue().entrySet())
                {
                    ranking++;
                    String queryID = outer.getKey();
                    String pID = inner.getKey();
                    String score = String.valueOf(inner.getValue().getScore());
                    runFileLine = queryID+" Q0 "+pID+" "+ranking+" "+score+" "+mname+newLine;
                    try {
                        ptr.write(runFileLine);
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                }
            }
            if(ptr !=null)
            {
                try {
                    ptr.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }

        }
    }

}
