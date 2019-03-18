package main.java.predictors;

import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.queryparser.classic.ParseException;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import static main.java.utils.SearchUtils.createTokenList;

/*
 * This class returns a vector of labels, classifying the documents input as either "ham" or "spam".
 *
 * Example usage:
 *      SpamClassifier sc = new SpamClassifier();
 *      HashMap<String, String> hamTrain = sc.readIndex(hamTrainPath);
 *      HashMap<String, String> spamTrain = sc.readIndex(spamTrainPath);
 *      HashMap<String, String> test = sc.readIndex(docsForRerank);
 *
 *      LabelPredictor lp = sc.classifyWithUnigrams(hamTrain, spamTrain);
 *      HashMap<String, String> labels = sc.predict(lp, test);
 *
 */
public class
SpamClassifier {

    private HashMap<String, String> labels;

    /* This constructor should be invoked if the user doesn't have seperate ham/spam test data and just wants to
     * classify their documents as ham or spam.
     */
    SpamClassifier() {
        labels = new HashMap<>();
    }

    /**
     * Desc: Read and store the training or test data. The user should call this before any of other methods to make
     * ham and spam train sets, and a test set.
     *
     * @param path to the training/test set.
     * @return train or test data stored as an ArrayList of Documents.
     */
    public HashMap<String, String> readIndex(String path) throws IOException, ParseException {

        BufferedReader reader = null;
        HashMap<String, String> lines = new HashMap<>();
        File f = new File(path);

        String line = null;

        try {
            reader = new BufferedReader(new FileReader(f));
            while ((line = reader.readLine()) != null) {
                String[] curLine = line.split("\\t+");
                String pid = curLine[0].trim();
                String test = curLine[1].trim();
                lines.put(pid, test);
            }
        } catch (IOException | NullPointerException e) {
            e.printStackTrace();
        }

        return lines;
    }


    /**
     * Desc: Train a NaiveBayesPredictor (unigrams) using the HashMaps made from the previous method.
     *
     * @param spamCorpus the training set of spam documents.
     * @param hamCorpus the training set of ham documents.
     */
    public LabelPredictor classifyWithUnigrams(HashMap<String, String>spamCorpus, HashMap<String, String> hamCorpus) {

        LabelPredictor unigramsPredictor = new NaiveBayesPredictor();

        for (String key : spamCorpus.keySet()) {
            String text = spamCorpus.get(key);
            List<String> tokens = createTokenList(text, new EnglishAnalyzer());
            unigramsPredictor.trainSpamTokens(tokens);
        }

        for (String key: hamCorpus.keySet()) {
            String text = hamCorpus.get(key);
            List<String> tokens = createTokenList(text, new EnglishAnalyzer());
            unigramsPredictor.trainHamTokens(tokens);
        }

        return unigramsPredictor;

    }

    /**
     * Desc: Train a NaiveBayesBigramPredictor.
     *
     * @param spamCorpus the training set of spam documents.
     * @param hamCorpus the training set of ham documents.
     */
    public LabelPredictor classifyWithBigrams(HashMap<String, String> spamCorpus, HashMap<String, String> hamCorpus) {

        LabelPredictor bigramsPredictor = new NaiveBayesBigramPredictor();

        for (String key : spamCorpus.keySet()) {
            String text = spamCorpus.get(key);
            List<String> tokens = createTokenList(text, new EnglishAnalyzer());
            bigramsPredictor.trainSpamTokens(tokens);
        }

        for (String key: hamCorpus.keySet()) {
            String text = hamCorpus.get(key);
            List<String> tokens = createTokenList(text, new EnglishAnalyzer());
            bigramsPredictor.trainHamTokens(tokens);
        }

        return bigramsPredictor;
    }


    /**
     * Desc: Train a NaiveBayesTrigramPredictor.
     *
     * @param spamCorpus the training set of spam documents.
     * @param hamCorpus the training set of ham documents.
     */
    public LabelPredictor classifyWithTrigrams(HashMap<String, String> spamCorpus, HashMap<String, String> hamCorpus) {

        LabelPredictor trigramsPredictor = new NaiveBayesTrigramPredictor();
        HashMap<String, String> test = null;

        for (String key : spamCorpus.keySet()) {
            String text = spamCorpus.get(key);
            List<String> tokens = createTokenList(text, new EnglishAnalyzer());
            trigramsPredictor.trainSpamTokens(tokens);
        }

        for (String key: hamCorpus.keySet()) {
            String text = hamCorpus.get(key);
            List<String> tokens = createTokenList(text, new EnglishAnalyzer());
            trigramsPredictor.trainHamTokens(tokens);
        }

        return trigramsPredictor;

    }

    /**
     * Desc: Train a NaiveBayesQuadgramPredictor.
     *
     * @param spamCorpus the training set of spam documents.
     * @param hamCorpus the training set of ham documents.
     */
    public LabelPredictor classifyWithQuadgrams(HashMap<String, String> spamCorpus, HashMap<String, String> hamCorpus) {

        LabelPredictor quadgramsPredictor = new NaiveBayesQuadgramPredictor();
        HashMap<String, String> test = null;

        for (String key : spamCorpus.keySet()) {
            String text = spamCorpus.get(key);
            List<String> tokens = createTokenList(text, new EnglishAnalyzer());
            quadgramsPredictor.trainSpamTokens(tokens);
        }

        for (String key: hamCorpus.keySet()) {
            String text = hamCorpus.get(key);
            List<String> tokens = createTokenList(text, new EnglishAnalyzer());
            quadgramsPredictor.trainHamTokens(tokens);
        }

        return quadgramsPredictor;

    }

    /**
     * Desc: Train a NaiveBayesQuadgramPredictor.
     *
     * @param spamCorpus the training set of spam documents.
     * @param hamCorpus the training set of ham documents.
     */
    public LabelPredictor classifyWithStopCover(HashMap<String, String> spamCorpus, HashMap<String, String> hamCorpus) {

        LabelPredictor stopCoverPredictor = new StopCoveragePredictor();
        HashMap<String, String> test = null;

        for (String key : spamCorpus.keySet()) {
            String text = spamCorpus.get(key);
            List<String> tokens = createTokenList(text, new EnglishAnalyzer());
            stopCoverPredictor.trainSpamTokens(tokens);
        }

        for (String key: hamCorpus.keySet()) {
            String text = hamCorpus.get(key);
            List<String> tokens = createTokenList(text, new EnglishAnalyzer());
            stopCoverPredictor.trainHamTokens(tokens);
        }

        return stopCoverPredictor;

    }


    /**
     * Desc: Test the trained predictor.
     *
     * @param predictor the predictor that was previously trained.
     * @param test the test data that was held out.
     */
    public HashMap<String, String> predict(LabelPredictor predictor, HashMap<String, String> test) {

        for (String key : test.keySet()) {
            String text = test.get(key);
            List<String> tokens = createTokenList(text, new EnglishAnalyzer());
            String prediction = predictor.predict(tokens);
            labels.put(key, prediction);
        }

        return labels;
    }

    /*
     * The user can evaluate their predictor if they have ham and spam test data.
     */
    public void evaluate(HashMap<String, String> spamTest, LabelPredictor predictor, HashMap<String, String> hamTest, HashMap<String, String> testDocs) throws IOException, ParseException {

        predictor.evaluate(spamTest, hamTest, testDocs);
    }
}

