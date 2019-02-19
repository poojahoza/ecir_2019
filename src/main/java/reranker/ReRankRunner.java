package main.java.reranker;

import main.java.containers.Container;
import main.java.searcher.BaseBM25;
import main.java.utils.PreProcessor;

import main.java.utils.SortUtils;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.ops.transforms.Transforms;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

/*
@author: Amith
Helper class for ReRanker
*/

class ReRankRunner
{
    protected BaseBM25 bm25 = null ;
    protected String embeddingFile = null;
    protected Integer Dimension;
    protected WordEmbedding word = null;


    ReRankRunner(BaseBM25 bm25, String embeddingFile,Integer Dimension)
    {
        this.bm25 = bm25;
        this.embeddingFile=embeddingFile;
        this.Dimension =Dimension;
        word = new WordEmbedding(this.Dimension,this.embeddingFile);
    }

    protected ArrayList<String> getTopK(ArrayList<String> sorted)
    {
        ArrayList<String> res = new ArrayList<>();
        int k =sorted.size()/2;
        int count=0;
        for(String ss: sorted)
        {
            count++;
            res.add(ss);
            if(k==count) break;
        }
        return res;
    }


    protected INDArray buildVector(ArrayList<String> processed)
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


    protected INDArray getVector(Integer docID)
    {
        ArrayList<String> processed = PreProcessor.processDocument(bm25.getDocument(docID));
        return  buildVector(processed);
    }



    /*
    This list takes the unranked list and perform the re ranking based on the document similarity
    This method make assumption that first method is relevant , compute the cosine similarity with other documents.
    */
    protected Map<String,Container> getReRank(Map<String, Container> unranked)
    {
        if(unranked.size()<3) return unranked;

        boolean _isFirst = false;
        String _relevant_para = null;
        Container _relevant_Container = null;
        INDArray _relevant_Vector = null;
        double _relevant_doc_score = 0;

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
            }
        }
        //Adding the First para back to the map
        _relevant_Container.setScoreVal(_relevant_doc_score);
        unsorted.put(_relevant_para,_relevant_Container);
        return  SortUtils.sortByValue(unsorted);
    }

}
