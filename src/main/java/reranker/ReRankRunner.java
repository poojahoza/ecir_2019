package main.java.reranker;

import main.java.searcher.BaseBM25;
import main.java.utils.StopWord;
import org.nd4j.linalg.api.ndarray.INDArray;

import java.util.List;



class ReRankRunner
{
    private BaseBM25 bm25 = null;
    private String embeddingFile =null;
    private Integer Dimension;
    private List<String> STOP_WORDS = null;
    private WordEmbedding word = null;

    ReRankRunner(BaseBM25 bm25, String embeddingFile,Integer Dimension)
    {
        this.bm25 = bm25;
        this.embeddingFile=embeddingFile;
        this.Dimension =Dimension;
        this.STOP_WORDS = StopWord.getStopWords();
        word = new WordEmbedding(this.Dimension,this.embeddingFile);
    }



    void performDocumentSimilarity()
    {

        INDArray in1 = word.getWordEmbeddingVector("the");
        INDArray in2 = word.getWordEmbeddingVector("is");

        if(in1 != null && in2 != null)
        {
            System.out.println(in1);
            System.out.println(in2);
            System.out.println(in1.add(in2));
        }

    }

}
