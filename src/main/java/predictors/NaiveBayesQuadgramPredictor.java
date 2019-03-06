package main.java.predictors;

import main.java.BayesCounter;
import org.apache.lucene.document.Document;
import org.apache.lucene.search.IndexSearcher;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class NaiveBayesQuadgramPredictor extends LabelPredictor {
    private BayesCounter bc = new BayesCounter();

    public NaiveBayesQuadgramPredictor() {
        super();
    }

    /**
     * Desc: Train classifier on ham emails.
     *
     * @param tokens List of tokens in the document
     */
    public void trainHamTokens(List<String> tokens) {
        bc.buildQuadgramsHashMap("ham", tokens);
    }

    /**
     * Desc: Train classifier on spam emails.
     *
     * @param tokens List of tokens in the document
     */
    public void trainSpamTokens(List<String> tokens) {
        bc.buildQuadgramsHashMap("spam", tokens);
    }


    /**
     * Desc: Predict whether a document is a ham or spam.
     *
     * @param tokens List of tokens in the document
     * @return String The label ("spam" or "ham") that is predicted given the document  tokens
     */
    @Override
    public String predict(List<String> tokens) {
        return bc.classifyWithQuadgrams(tokens);
    }

    /**
     * Desc: Get the ham and spam scores for the test data.
     *
     * @param tokens List of tokens in the document
     * @return ArrayList The ham and spam scores of the given document tokens
     */
    @Override
    public ArrayList<Double> score(List<String> tokens) {
        return bc.getQuadramScores(tokens);
    }

    /**
     * Desc: Get the F1 score of the Naive Bayes classifiers.
     *
     * @param spam, a hash map of the ham test data by itself.
     * @param ham, a hash map of the spam test data by itself.
     * @param docs of mixed ham and spam documents mapping their pids to their text.
     */
    @Override
    public void evaluate(HashMap<String, String> spam, HashMap<String, String> ham, HashMap<String, String> docs) {
        bc.evaluateQuadgramPredictor(spam, ham, docs);
    }

}

