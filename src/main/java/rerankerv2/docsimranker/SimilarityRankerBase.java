package main.java.rerankerv2.docsimranker;

import main.java.commandparser.RegisterCommands;
import main.java.containers.Container;

import main.java.containers.EntityContainer;
import main.java.rerankerv2.concepts.EmbeddingStrategy;
import main.java.reranker.WordEmbedding;
import main.java.searcher.BaseBM25;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;

import java.io.IOException;
import java.util.*;


public abstract class SimilarityRankerBase {


    private RegisterCommands.CommandSearch SearchCommand=null;
    private BaseBM25 bm25 = null;
    private Map<String,String> query = null;
    private EmbeddingStrategy embedding = null;

    SimilarityRankerBase(RegisterCommands.CommandSearch SearchCommand,Map<String,String> query)
    {
        this.SearchCommand = SearchCommand;
        this.query = query;
        try {
            this.bm25 = new BaseBM25(SearchCommand.getkVAL(),SearchCommand.getIndexlocation());
        } catch (IOException e) {
            e.printStackTrace();
        }
        embedding = new WordEmbedding(SearchCommand.getDimension(),SearchCommand.getWordEmbeddingFile());

    }


    protected boolean checkForSpam(Container c)
    {
//        String content = bm25.getDocument(c.getDocID());
//        // System.out.println(content);
//        ArrayList<String> res= null;
//        try {
//            res = PreProcessor.processTermsUsingLucene(content);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//        for(String s: res)
//        {
//            System.out.println(s+"  "+ embedding.getEmbeddingVector(s));
//        }

//        List<String> ent = this.getEntities(c);
//        for(String str: ent)
//        {
//            System.out.println(str);
//        }

        return false;


    }

    /*
    All class needs to implement the below function which describes how to build vector (Document rep)
    Takes the Lucene document as input and returns the document vector representation
    */
    abstract INDArray getVector(int docID);

    public List<String> getEntities(Container input){

        List<String> emention = new ArrayList<>();
        EntityContainer e = input.getEntity();
        String [] entity_val = e.getEntityVal().split("[\r\n]+");

        emention = Arrays.asList(entity_val);
        return emention;
    }

    private Map<String,Container> getReRank(Map<String,Container> unranked)
    {
        if (unranked.size() < 3) return unranked;

        /*
        Values from the SearCommand
        */
        Integer Dimension = SearchCommand.getDimension();
        Integer biasFactor = SearchCommand.getBiasFactor();
        INDArray biased_vector = null;
        INDArray res = Nd4j.create(Dimension);


        for(Map.Entry<String,Container> docs: unranked.entrySet())
        {
            if(SearchCommand.isSpamFilterEnabled())
            {
                System.out.println("Spam filter enabled for docID :"+ docs.getValue().getDocID());
                checkForSpam(docs.getValue());
            }


        }

        return null;
    }

    protected void rerank()
    {
        Map<String,Map<String,Container>> res = new LinkedHashMap<String,Map<String,Container>>();
        for(Map.Entry<String,String> q: query.entrySet())
        {
            Map<String, Container> retDoc = bm25.getRanking(q.getValue());
            res.put(q.getKey(),getReRank(retDoc));
        }


    }

    protected void rerank(Map<String,Map<String,Container>> expandedlist)
    {

    }
}
