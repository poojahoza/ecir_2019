package main.java.wordsimilarityranker;

//project imports
import main.java.searcher.BaseBM25;

//Dependency imports
import  info.debatty.java.stringsimilarity.*;

//Java imports
import java.util.Map;

/*
A class implements the abstract class SimilarityBase and returns the CosineSimilarity score
*/
public class CosineSimilarity extends SimilarityBase
{
    public CosineSimilarity(BaseBM25 bm , Map<String,String> query)
    {
        super(bm,query);
    }

    @Override
    public double getScore(String str1 , String str2) {
        Cosine cosine = new Cosine();
        return cosine.similarity(str1,str2);
    }

    public void doCosine()
    {
        performReRank();
    }
}
