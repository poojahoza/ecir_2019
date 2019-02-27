package main.java.predictors;

import main.java.BayesCounter;
import org.apache.lucene.document.Document;
import org.apache.lucene.search.IndexSearcher;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class NaiveBayesQuadgramPredictor extends LabelPredictor {
    private BayesCounter bc = new BayesCounter();

    public NaiveBayesQuadgramPredictor(IndexSearcher s) {
        super(s);
    }

    /**
     * Desc: Train classifier on ham emails.
     *
     * @param tokens List of tokens in the document
     */
    public void trainHamTokens(List<String> tokens) {
        bc.buildHashMap("ham", tokens);
    }

    /**
     * Desc: Train classifier on spam emails.
     *
     * @param tokens List of tokens in the document
     */
    public void trainSpamTokens(List<String> tokens) {
        bc.buildHashMap("spam", tokens);
    }


    /**
     * Desc: Predict whether a document is a ham or spam.
     *
     * @param tokens List of tokens in the document
     * @return String The label ("spam" or "ham") that is predicted given the document  tokens
     */
    @Override
    public String predict(List<String> tokens) {
        return bc.classify(tokens);
    }

    /**
     * Desc: Get the ham and spam scores for the test data.
     *
     * @param tokens List of tokens in the document
     * @return ArrayList The ham and spam scores of the given document tokens
     */
    @Override
    public ArrayList<Double> score(List<String> tokens) {
        return bc.getScores(tokens);
    }

    /**
     * Desc: Get the F1 score of the Naive Bayes classifiers.
     *
     * @param hamTrain, a hash map of the ham test data by itself.
     * @param spamTrain, a hash map of the spam test data by itself.
     * @param corpus of mixed ham and spam documents mapping their pids to their qids.
     */
    @Override
    public void evaluate(HashMap<String, String> hamTrain, HashMap<String, String> spamTrain, ArrayList<Document> corpus) {
        bc.evaluate(hamTrain, spamTrain, corpus);
    }

}

