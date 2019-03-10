package main.java.rerankerv2.docsimranker;

import main.java.commandparser.RegisterCommands;
import main.java.containers.Container;
import main.java.utils.PreProcessor;
import main.java.utils.RunWriter;

import org.nd4j.linalg.api.ndarray.INDArray;


import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

public class DocumentFrequencySimilarity extends SimilarityRankerBase
{
    private RegisterCommands.CommandSearch SearchCommand = null;

    public DocumentFrequencySimilarity(RegisterCommands.CommandSearch SearchCommand, Map<String,String> query)
    {
        super(SearchCommand,query);
        this.SearchCommand =SearchCommand;
    }

    @Override
    INDArray getVector(Container c) {
        ArrayList<String> processed = null;
        try {
            processed = PreProcessor.processTermsUsingLucene(bm25.getDocument(c.getDocID()));
        } catch (IOException e) {
            e.printStackTrace();
        }
        ArrayList<String> highestDF = null;
        try {
            highestDF = cs.getDF(processed);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return  buildVector(getTopK(highestDF));
    }

    public void doDocumentFrequency()
    {
        Map<String,Map<String,Container>> res = rerank();
        RunWriter.writeRunFile("test",res);
    }

}
