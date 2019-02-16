package main.java.reranker;

import main.java.containers.Container;
import main.java.searcher.BaseBM25;
import main.java.utils.PreProcessor;

import main.java.utils.SortUtils;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.ops.transforms.Transforms;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

/*
@author: Amith
Helper class for ReRanker
*/

class ReRankRunner
{
    private BaseBM25 bm25 = null ;
    private String embeddingFile = null;
    private Integer Dimension;
    private WordEmbedding word = null;

    ReRankRunner(BaseBM25 bm25, String embeddingFile,Integer Dimension)
    {
        this.bm25 = bm25;
        this.embeddingFile=embeddingFile;
        this.Dimension =Dimension;
        word = new WordEmbedding(this.Dimension,this.embeddingFile);
    }


    private INDArray buildVector(ArrayList<String> processed)
    {
        int _number_of_terms=0;
        INDArray res = Nd4j.create(Dimension); //Create the Dimension vector
        for(String str:processed)
        {
            if(word.getWordEmbeddingVector(str)!= null)
            {
                _number_of_terms++;
                INDArray temp = word.getWordEmbeddingVector(str);
                res = res.add(temp);
            }
        }
        return res.div(_number_of_terms); //Taking the mean of the vector
    }


    private INDArray getVector(Integer docID)
    {
        ArrayList<String> processed = PreProcessor.processDocument(bm25.getDocument(docID));
        return  buildVector(processed);
    }

    private INDArray getVector(String text)
    {
        ArrayList<String> processed = PreProcessor.processDocument(text);
        return  buildVector(processed);
    }

    private Map<String,Double> sortMAP(Map<String,Double> q)
    {

        Map<String, Double> sorted = new LinkedHashMap<>();
        q.entrySet().stream()
                .sorted(Map.Entry.<String, Double>comparingByValue().reversed())
                .forEachOrdered(x -> sorted.put(x.getKey(), x.getValue()));
        return sorted;

    }


    /*
    This list takes the unranked list and perform the re ranking based on the document similarity
    This method make assumption that first method is relevant , compute the cosine similarity with other documents.
    */
    Map<String,Double> getReRank(Map<String, Container> unranked)
    {
        if(unranked.size()<3) return null;

        boolean _isFirst = false;
        String _relevant_para = null;
        Container _relevant_Container = null;
        INDArray _relevant_Vector = null;
        double _relevant_doc_score = 0;

       // Map<String,Double> unsorted = new LinkedHashMap<>(unranked.size());
        Map<String,Container> unsorted = new LinkedHashMap<String,Container>();

        for(Map.Entry<String,Container> val: unranked.entrySet())
        {
            int docID = val.getValue().getDocID();

            if(!_isFirst)
            {
                _relevant_Container = val.getValue();
                _relevant_para = val.getKey();
                _relevant_Vector = getVector(docID);
                _isFirst = true;
            }
            else
            {
                INDArray _other_doc = getVector(docID);

                double cosineScore = Transforms.cosineSim(_relevant_Vector,_other_doc);
                double newScore = ((val.getValue().getScore() * cosineScore) + _relevant_Container.getScore());
                _relevant_doc_score += val.getValue().getScore();

                Container temp = new Container(newScore,val.getValue().getDocID());
                unsorted.put(val.getKey(),temp);
                //System.out.println(val.getKey()+ "  " +val.getValue().getScore() + "  "+ ((val.getValue().getScore() * cosineScore) + _relevant_Container.getScore())+ " "+ cosineScore);
                //unsorted.put(val.getKey(),newScore);
            }
        }
        _relevant_Container.setScoreVal(_relevant_doc_score);
        unsorted.put(_relevant_para,_relevant_Container);





        Map<String, Container> sorted = SortUtils.sortByValue(unsorted);

        for(Map.Entry<String,Container> v: sorted.entrySet())
        {
            System.out.println(v.getKey()+"  "+ v.getValue().getScore()+" "+ v.getValue().getDocID());
        }

        return null;
    }

}
