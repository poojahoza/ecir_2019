package main.java.predictors;

import main.java.BayesCounter;
import main.java.utils.SearchUtils;
import org.apache.lucene.search.IndexSearcher;

import java.io.IOException;
import java.util.List;

public class NaiveBayesPredictor extends LabelPredictor {
    private BayesCounter bc = new BayesCounter();

    public NaiveBayesPredictor(IndexSearcher s) {
        super(s);

        // Train classifier on ham emails
        for(List<String> tokens : retrieveHamEmailTokens()) {
            bc.buildHashMap("ham", tokens);
        }

        // Train classifier on spam emails
        for(List<String> tokens : retrieveSpamEmailTokens()) {
            bc.buildHashMap("spam", tokens);
        }
    }

    @Override
    public String predict(List<String> tokens) {
        return bc.classify(tokens);
    }

}
