package main.java.predictors;

import main.java.predictors.BayesCounter;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SpecialCharPredictor extends FracStopPredictor {

    private BayesCounter bc = new BayesCounter();

    public SpecialCharPredictor() {
        super();
    }

    /**
     * Desc: Train classifier on ham emails.
     *
     * @param tokens List of tokens in the document
     */
    public void trainHamTokens(List<String> tokens, String pid) { bc.buildSpecialCharHashMap("ham", tokens, pid); }

    /**
     * Desc: Train classifier on spam emails.
     *
     * @param tokens List of tokens in the document
     */
    public void trainSpamTokens(List<String> tokens, String pid) {
        bc.buildSpecialCharHashMap("spam", tokens, pid);
    }


    /**
     * Desc: Predict whether a document is a ham or spam.
     *
     * @param tokens List of tokens in the document
     * @return String The label ("spam" or "ham") that is predicted given the document  tokens
     */
    public String predict(List<String> tokens) {
        return bc.classifyWithSpecialChars(tokens);
    }

    /**
     * Desc: Get the ham and spam scores for the test data.
     *
     * @param tokens in the document
     * @return ArrayList The scores of the given document tokens
     */
    public ArrayList<Double> score(List<String> tokens) {
        return bc.getSpecialCharScores(tokens);
    }

    /**
     * Desc: Get the F1 score of the Naive Bayes classifiers.
     *
     * @param spam, a hash map of the ham test data by itself.
     * @param ham, a hash map of the spam test data by itself.
     * @param docs of mixed ham and spam documents mapping their pids to their text.
     */
    public void evaluate(HashMap<String, String> spam, HashMap<String, String> ham, HashMap<String, String> docs) {
        bc.evaluateSpecialCharPredictor(spam, ham, docs);
    }
}
