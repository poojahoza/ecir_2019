package main.java.reranker;

import main.java.utils.PreProcessor;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.ops.transforms.Transforms;


import java.io.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

/*
Class that Holds the Word Embeddings vectors.
*/

public class WordEmbedding
{
    private Map<String, INDArray> word = null;
    private Integer dimension=0;
    private String embeddingFile=null;

    public WordEmbedding(Integer dimension,String embeddingFile)
    {
        this.dimension =dimension;
        this.embeddingFile=embeddingFile;
        word = readWordVectors();
    }


    private Map<String, INDArray> readWordVectors()
    {
        if(word==null)
        {
            word = new LinkedHashMap<String,INDArray>();
        }

        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(new File(embeddingFile)));
        } catch (FileNotFoundException e) {
            System.out.println("What??");
            System.out.println(e.getMessage());
        }


        String line=null;
        try {
            while ((line = br.readLine()) != null) {
                String[] vec = line.split(" ");

                //Allocate the memory to convert the read value in to IND array.
                float[] temp=new float[dimension];
                for(int i=0;i < dimension ;i++)
                {
                        temp[i] = Float.parseFloat(vec[i+1]);
                }
                word.put(vec[0], Nd4j.create(temp));
            }
        } catch (NullPointerException | IOException n) {
            System.out.println(n.getMessage());
        }

        return word;
    }


   public void DisplayWordEmbeddings(int countval)
    {
        int count=0;
        for(Map.Entry<String,INDArray> outer:word.entrySet())
        {
            count++;
            System.out.print(outer.getKey() +" ");
            for(int i=0;i< dimension;i++)
            {
                INDArray ind = outer.getValue();
                System.out.print(ind.getFloat(i));
                System.out.print(" ");
            }
            System.out.println(" ");
            if(count == countval ) break;
        }
    }

    private INDArray buildVector(ArrayList<String> processed)
    {
        int _number_of_terms=0;
        INDArray res = Nd4j.create(dimension); //Create the Dimension vector
        for(String str:processed)
        {
            if(getWordEmbeddingVector(str)!= null)
            {
                _number_of_terms++;
                INDArray temp = getWordEmbeddingVector(str);
                res = res.add(temp);
            }
        }
        return res.div(_number_of_terms); //Taking the mean of the vector
    }

    /*
        Given two strings, it gives the Similarity based on the word vectors.
    */
    public Double getSimilarity(String para1, String para2)
    {
        ArrayList<String> para1List = PreProcessor.processDocument(para1);
        ArrayList<String> para2List = PreProcessor.processDocument(para2);
        return Transforms.cosineSim(buildVector(para1List),buildVector(para2List));
    }


    public INDArray getWordEmbeddingVector(String wordEmbedding)
    {
        if(word.containsKey(wordEmbedding.toLowerCase()))
        {
            return word.get(wordEmbedding);
        }
        return null;
    }

}
