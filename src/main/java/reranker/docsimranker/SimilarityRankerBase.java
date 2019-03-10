package main.java.reranker.docsimranker;

import main.java.commandparser.RegisterCommands;
import main.java.containers.Container;

import org.nd4j.linalg.api.ndarray.INDArray;



public abstract class SimilarityRankerBase {


    private RegisterCommands.CommandSearch SearchCommand= null;


    SimilarityRankerBase(RegisterCommands.CommandSearch SearchCommand)
    {
        this.SearchCommand = SearchCommand;

    }


    protected void checkForSpam(Container c)
    {

    }

    /*
    All class needs to implement the below function which describes how to build vector
    Takes the Lucene document as input and returns the document vector representation
    */
    abstract INDArray getVector(int docID);

    /*
        All the class must
    */
    abstract void rerank();


}
