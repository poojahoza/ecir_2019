package main.java.reranker;

import main.java.containers.Container;
import main.java.searcher.BaseBM25;
import main.java.utils.PreProcessor;

import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.ops.transforms.Transforms;

import java.util.ArrayList;
import java.util.Map;


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
        INDArray res = Nd4j.create(Dimension);
        for(String str:processed)
        {
            if(word.getWordEmbeddingVector(str)!= null)
            {
                _number_of_terms++;
                INDArray temp = word.getWordEmbeddingVector(str);
                res = res.add(temp);
            }
        }
        return res.div(_number_of_terms);
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

    /*
    This list takes the unranked list and perform the re ranking based on the document similarity
    This method make assumption that first method is relevant , compute the cosine similarity with other documents.
    */
    void getReRank(Map<String, Container> unranked)
    {
        if(unranked.size()<3) return;

        boolean _isFirst =false;
        String _relPara = null;
        Container _relContainer = null;
        INDArray _relVector = null;

        for(Map.Entry<String,Container> val: unranked.entrySet())
        {
            if(!_isFirst)
            {
                _relContainer = val.getValue();
                _relPara = val.getKey();
                _isFirst = true;
                _relVector = getVector(val.getValue().getDocID());
            }
            else
            {
                INDArray _other_doc = getVector(bm25.getDocument(val.getValue().getDocID()));
                double value = Transforms.cosineSim(_relVector,_other_doc);
                System.out.println(val.getKey()+ "  " +val.getValue().getScore() + "  "+ ((val.getValue().getScore() * value) + _relContainer.getScore())+ " "+ value);
                //System.out.println(val.getKey()+"  "+ Transforms.cosineSim(_relVector,_other_doc));
            }

        }

    }

}
