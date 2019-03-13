package main.java.mrf;

import main.java.containers.Container;
import main.java.rerankerv2.concepts.EmbeddingStrategy;
import main.java.searcher.BaseBM25;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.ops.transforms.Transforms;


import java.io.IOException;
import java.util.ArrayList;

import java.util.Map;



public class Evidences
{
    /*
        evidence1 is to return the BM25 score of query-document
    */
    public static ArrayList<Double> evidence1(Map<String, Container> list)
    {
        ArrayList<Double> res = new ArrayList<Double>();

        for(Map.Entry<String,Container> val: list.entrySet())
        {
            res.add(val.getValue().getScore());
        }
        return res;
    }

    public static ArrayList<Double> evidence2(Map<String, Container> list, EmbeddingStrategy embedding,Integer Dimension,String Query,String
                                              indexLoc,Integer kval)
    {
        ArrayList<Double> res = new ArrayList<Double>();
        BaseBM25 bm = null;
        try {
           bm = new BaseBM25(kval,indexLoc);
        } catch (IOException e) {
            e.printStackTrace();
        }

        INDArray queryVector = MrfHelper.getVector(Query,embedding,Dimension);

        for(Map.Entry<String,Container> val: list.entrySet())
        {

            int DocID = val.getValue().getDocID();
            INDArray doc = MrfHelper.getVector(bm.getDocument(DocID),embedding,Dimension);
            res.add(Transforms.cosineDistance(queryVector,doc));
        }
        return res;
    }

    public static void updatescores(Map<String,Container> res,ArrayList<Double> fet)
    {
        int index =0;
        if(res.size()!= fet.size())
        {
            System.out.println("Mismatch in the size, something wrong");
            System.exit(-1);
        }

        for(Map.Entry<String,Container> val: res.entrySet())
        {
//            if(val.getValue().getScore() == fet.get(0))
//            {
//                val.getValue().addScores(fet.get(index));
//            }
//            else
//            {
//                System.out.println("Problem with parallelism");
//                System.exit(-1);
//            }
            val.getValue().addScores(fet.get(index));
            index++;
        }
    }
}
