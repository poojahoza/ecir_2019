package main.java.mrf;

import main.java.containers.Container;
import main.java.rerankerv2.concepts.EmbeddingStrategy;
import main.java.utils.PreProcessor;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;

import static main.java.utils.RunWriter.checkDir;
import static main.java.utils.RunWriter.createFile;

public class MrfHelper
{
    public static INDArray getVector(String query, EmbeddingStrategy embedding,Integer Dimension)
    {
        INDArray res = Nd4j.create(Dimension).add(0.000001);
        int _number_of_terms=1;
        try {
            for(String val:PreProcessor.processTermsUsingLucene(query))
            {
                    if(embedding.getEmbeddingVector(val)!=null)
                    {
                        _number_of_terms++;
                        INDArray temp = embedding.getEmbeddingVector(val);
                        res = res.add(temp);
                    }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return res.div(_number_of_terms);
    }

    public static void writeRunFile(Map<String, Map<String, Container>> res,String mname)
    {
        String dirname="result";
        boolean success = checkDir(dirname);

        if (!success) {
            System.out.println("Unable to create the directory");
            System.exit(-1);
        }

        FileWriter ptr = createFile(dirname,mname);
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
                //String score = String.valueOf(inner.getValue().getScore());
                StringBuilder sb =new StringBuilder();
                for(Double d:inner.getValue().getScoresList())
                {
                    sb.append(d);
                    sb.append(" ");
                }
                runFileLine = queryID+" Q0 "+pID+" "+ranking+" "+sb.toString()+" "+mname+newLine;
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
