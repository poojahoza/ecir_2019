package main.java.predictors;

import main.java.predictors.BayesCounter;
import weka.core.stopwords.Null;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class FracStopPredictor extends StopWordLabelPredictor{

    private BayesCounter bc = new BayesCounter();

    public FracStopPredictor() {
        super();
    }

    /**
     * Desc: Train classifier on ham emails.
     *
     * @param tokens List of tokens in the document
     */
    public void trainHamTokens(List<String> tokens, String pid) {
        try {
            bc.buildFracStopHashMap("ham", tokens, pid);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * Desc: Train classifier on spam emails.
     *
     * @param tokens List of tokens in the document
     */
    public void trainSpamTokens(List<String> tokens, String pid) {
        try {
            bc.buildFracStopHashMap("spam", tokens, pid);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }


    /**
     * Desc: Predict whether a document is a ham or spam.
     *
     * @param tokens List of tokens in the document
     * @return String The label ("spam" or "ham") that is predicted given the document  tokens
     */
    public String predict(List<String> tokens) {
        String label = null;
        try {
            label =  bc.classifyWithFracStops(tokens);
        } catch (FileNotFoundException | NullPointerException e) {
            e.printStackTrace();
        }
        return label;
    }

    /**
     * Desc: Get the ham and spam scores for the test data.
     *
     * @param pid of the document
     * @return ArrayList The ham and spam scores of the given document tokens
     */
    public ArrayList<Double> score(String pid) throws NullPointerException {

        return bc.getFracStopScores(pid);
    }

    /**
     * Desc: Get the F1 score of the Naive Bayes classifiers.
     *
     * @param spam, a hash map of the ham test data by itself.
     * @param ham, a hash map of the spam test data by itself.
     * @param docs of mixed ham and spam documents mapping their pids to their text.
     */
    public void evaluate(HashMap<String, String> spam, HashMap<String, String> ham, HashMap<String, String> docs) {
        try {
            bc.evaluateFracStopPredictor(spam, ham, docs);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}
