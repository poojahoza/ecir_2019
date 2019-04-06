package main.java.predictors;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

abstract public class StopWordLabelPredictor {

    public StopWordLabelPredictor() {
    }

    /**
     * Desc: Train classifier on ham emails.
     *
     * @param tokens List of tokens in the document
     */
    public void trainHamTokens(List<String> tokens, String pid) {}

    /**
     * Desc: Train classifier on ham emails.
     *
     * @param tokens List of tokens in the document
     */
    public void trainSpamTokens(List<String> tokens, String pid) {}

    /**
     * Desc: Predict whether a document is a ham or spam.
     *
     * @param tokens List of tokens in the document
     * @return String The label ("spam" or "ham") that is predicted given the document  tokens
     */
    abstract public String predict(List<String> tokens);

    /**
     * Desc: Get the ham and spam scores for the test data.
     *
     * @param pid of the document
     * @return ArrayList The ham and spam scores of the given document tokens
     */
    abstract public ArrayList<Double> score(String pid);


    /**
     * Desc: When called, the LabelPredictor will be handed unlabelled documents to classify (using predict method)
     * The labels will be used to compute the F1 Score of your label prediction method.
     */
    abstract public void evaluate(HashMap<String, String> spam, HashMap<String, String> ham, HashMap<String, String> docs);
}
