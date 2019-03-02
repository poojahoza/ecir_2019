package main.java.runner;

import main.java.commandparser.CommandParser;
import main.java.commandparser.RegisterCommands;
import main.java.commandparser.ValidateCommands;
import main.java.predictors.*;
import main.java.searcher.BaseSearcher;
import main.java.utils.SearchUtils;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.FSDirectory;

import java.awt.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static main.java.utils.SearchUtils.createTokenList;

/**
 * Uses the training and test sets created by IndexHamSpamRunner to train and classify documents as
 * either ham or spam.
 *
 *   My Vision:
 *                 Amith and Pooja can create a new spam classifier in their class. They will have to pass in the documents they want in the form of pid => text.
 *                 The spam classifier will return a vector of labels in order of the documents passed, and the documents corresponding to the spam labels can be kicked out.
 *
 *                 This means that my classifier will not be called from the command line most likely. The class will instead have a series of methods that the user can
 *                 call. For instance, if they want to classify with bigrams, they would call "spamClassifier.runBigrams()". This means I will remove filter runner from
 *                 runners, and just make it a class called SpamClassifier inside of the predictors folder.
 *
 *                 For now, though, I'm keeping this file around for testing purposes.
 *
 */
public class FilterRunner implements ProgramRunner {

    private RegisterCommands.CommandFilter filterParser = null;
    private ValidateCommands.ValidateFilterCommands validate = null;

    public FilterRunner(CommandParser parser)
    {
        filterParser = parser.getFilterCommand();
        validate = new ValidateCommands.ValidateFilterCommands(filterParser);
    }


    @Override
    public void run()
    {
        HashMap<String, String> spamTrain = null;
        HashMap<String, String> hamTrain = null;

        try {
            spamTrain = readIndex(filterParser.getSpamTrainPath());
            hamTrain= readIndex(filterParser.getHamTrainPath());
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }

        runUnigrams(spamTrain, hamTrain);
        runBigrams(spamTrain, hamTrain);
        runTrigrams(spamTrain, hamTrain);
        runQuadgrams(spamTrain, hamTrain);
    }


    /**
     * Desc: Read and store the training or test data.
     *
     * @param path to the training/test set.
     * @return train or test data stored as an ArrayList of Documents.
     */
    private HashMap<String, String> readIndex(String path) throws IOException, ParseException {

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
     * Desc: Train a NaiveBayesPredictor (unigrams).
     *
     * @param spamCorpus the training set of spam documents.
     * @param hamCorpus the training set of ham documents.
     */
     private void runUnigrams(HashMap<String, String> spamCorpus, HashMap<String, String> hamCorpus) {

        LabelPredictor unigramsPredictor = new NaiveBayesPredictor();
        HashMap<String, String> test = null;

        for (String key : spamCorpus.keySet()) {
            String text = spamCorpus.get(key);
            List<String> tokens = createTokenList(text, new EnglishAnalyzer());
            unigramsPredictor.trainSpamTokens(tokens);
        }

        for (String key: hamCorpus.keySet()) {
            String text = spamCorpus.get(key);
            List<String> tokens = createTokenList(text, new EnglishAnalyzer());
            unigramsPredictor.trainHamTokens(tokens);
        }

         try {
             test = readIndex(filterParser.getHamSpamTestPath()); // This is where the rerank documents will be passed in.
         } catch (IOException | ParseException e) {
             e.printStackTrace();
         }

         System.out.println("------------Unigrams------------");
         predict(unigramsPredictor, test);

         try {
             evaluateClassifier(unigramsPredictor);
         } catch (IOException | ParseException e) {
             e.printStackTrace();
         }
    }

    /**
     * Desc: Train a NaiveBayesBigramPredictor.
     *
     * @param spamCorpus the training set of spam documents.
     * @param hamCorpus the training set of ham documents.
     */
    private void runBigrams(HashMap<String, String> spamCorpus, HashMap<String, String> hamCorpus) {

        LabelPredictor bigramsPredictor = new NaiveBayesBigramPredictor();
        HashMap<String, String> test = null;

        for (String key : spamCorpus.keySet()) {
            String text = spamCorpus.get(key);
            List<String> tokens = createTokenList(text, new EnglishAnalyzer());
            bigramsPredictor.trainSpamTokens(tokens);
        }

        for (String key: hamCorpus.keySet()) {
            String text = spamCorpus.get(key);
            List<String> tokens = createTokenList(text, new EnglishAnalyzer());
            bigramsPredictor.trainHamTokens(tokens);
        }

        try {
            test = readIndex(filterParser.getHamSpamTestPath());
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }

        System.out.println("------------Bigrams------------");
        predict(bigramsPredictor, test);

        try {
            evaluateClassifier(bigramsPredictor);
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
    }


    /**
     * Desc: Train a NaiveBayesTrigramPredictor.
     *
     * @param spamCorpus the training set of spam documents.
     * @param hamCorpus the training set of ham documents.
     */
    private void runTrigrams(HashMap<String, String> spamCorpus, HashMap<String, String> hamCorpus) {

        LabelPredictor trigramsPredictor = new NaiveBayesTrigramPredictor();
        HashMap<String, String> test = null;

        for (String key : spamCorpus.keySet()) {
            String text = spamCorpus.get(key);
            List<String> tokens = createTokenList(text, new EnglishAnalyzer());
            trigramsPredictor.trainSpamTokens(tokens);
        }

        for (String key: hamCorpus.keySet()) {
            String text = spamCorpus.get(key);
            List<String> tokens = createTokenList(text, new EnglishAnalyzer());
            trigramsPredictor.trainHamTokens(tokens);
        }

        try {
            test = readIndex(filterParser.getHamSpamTestPath());
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }

        System.out.println("------------Trigrams------------");
        predict(trigramsPredictor, test);

        try {
            evaluateClassifier(trigramsPredictor);
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
    }

    /**
     * Desc: Train a NaiveBayesQuadgramPredictor.
     *
     * @param spamCorpus the training set of spam documents.
     * @param hamCorpus the training set of ham documents.
     */
    private void runQuadgrams(HashMap<String, String> spamCorpus, HashMap<String, String> hamCorpus) {

        LabelPredictor quadgramsPredictor = new NaiveBayesQuadgramPredictor();
        HashMap<String, String> test = null;

        for (String key : spamCorpus.keySet()) {
            String text = spamCorpus.get(key);
            List<String> tokens = createTokenList(text, new EnglishAnalyzer());
            quadgramsPredictor.trainSpamTokens(tokens);
        }

        for (String key: hamCorpus.keySet()) {
            String text = spamCorpus.get(key);
            List<String> tokens = createTokenList(text, new EnglishAnalyzer());
            quadgramsPredictor.trainHamTokens(tokens);
        }

        try {
            test = readIndex(filterParser.getHamSpamTestPath());
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }

        System.out.println("------------Quadgrams------------");
        predict(quadgramsPredictor, test);

        try {
            evaluateClassifier(quadgramsPredictor);
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
    }


    /**
     * Desc: Test the predictors.
     *
     * @param predictor the predictor that was previously instantiated.
     * @param test the test data that was held out.
     */
    private void predict(LabelPredictor predictor, HashMap<String, String> test) {

        for (String key : test.keySet()) {
            String text = test.get(key);
            List<String> tokens = createTokenList(text, new EnglishAnalyzer());
            ArrayList<Double> result = predictor.score(tokens);
            // String prediction = predictor.predict(RerankTokens);
            //
            System.out.println(result);
        }
    }

    private void evaluateClassifier(LabelPredictor predictor) throws IOException, ParseException {

        HashMap<String, String> testDocs = readIndex(filterParser.getHamSpamTestPath());
        HashMap<String, String> hamTest = readIndex(filterParser.getHamTestPath());
        HashMap<String, String> spamTest = readIndex(filterParser.getSpamTestPath());
        //predictor.evaluate(hamTest, spamTest, testDocs);

    }

    /* Skeleton code for Rerank */


}
