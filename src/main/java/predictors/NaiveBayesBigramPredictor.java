package main.java.predictors;

import main.java.utils.SearchUtils;
import main.java.BayesCounter;
import org.apache.lucene.search.IndexSearcher;

import java.io.IOException;
import java.util.List;

public class NaiveBayesBigramPredictor extends LabelPredictor {
    private BayesCounter bc = new BayesCounter();

    public NaiveBayesBigramPredictor(IndexSearcher s) {
        super(s);

        // Train classifier on ham emails
        for(List<String> tokens : retrieveHamEmailTokens()) {
            bc.buildBigramsHashMap("ham", tokens);
        }

        // Train classifier on spam emails
        for(List<String> tokens : retrieveSpamEmailTokens()) {
            bc.buildBigramsHashMap("spam", tokens);
        }
    }

    @Override
    public String predict(List<String> tokens) {
        return bc.classifyWithBigrams(tokens);
    }

    public static void main(String[] args) throws IOException {
        IndexSearcher searcher = SearchUtils.createIndexSearcher("index");
        LabelPredictor predictor = new NaiveBayesBigramPredictor(searcher);
        //predictor.evaluate();
    }

}