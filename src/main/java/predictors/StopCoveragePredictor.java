package main.java.predictors;

import main.java.BayesCounter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class StopCoveragePredictor extends LabelPredictor{

    private BayesCounter bc = new BayesCounter();

    public StopCoveragePredictor() {
        super();
    }

    /**
     * Desc: Train classifier on ham emails.
     *
     * @param tokens List of tokens in the document
     */
    public void trainHamTokens(List<String> tokens) {
        bc.buildStopWordHashMap("ham", tokens);
    }

    /**
     * Desc: Train classifier on spam emails.
     *
     * @param tokens List of tokens in the document
     */
    public void trainSpamTokens(List<String> tokens) {
        bc.buildStopWordHashMap("spam", tokens);
    }


    /**
     * Desc: Predict whether a document is a ham or spam.
     *
     * @param tokens List of tokens in the document
     * @return String The label ("spam" or "ham") that is predicted given the document  tokens
     */
    public String predict(List<String> tokens) {
        return bc.classifyWithStopCover(tokens);
    }

    /**
     * Desc: Get the ham and spam scores for the test data.
     *
     * @param tokens List of tokens in the document
     * @return ArrayList The ham and spam scores of the given document tokens
     */
    public ArrayList<Double> score(List<String> tokens) {
        return bc.getStopCoverScores(tokens);
    }

    /**
     * Desc: Get the F1 score of the Naive Bayes classifiers.
     *
     * @param spam, a hash map of the ham test data by itself.
     * @param ham, a hash map of the spam test data by itself.
     * @param docs of mixed ham and spam documents mapping their pids to their text.
     */
    public void evaluate(HashMap<String, String> spam, HashMap<String, String> ham, HashMap<String, String> docs) {
        bc.evaluateStopCoverPredictor(spam, ham, docs);
    }
}

