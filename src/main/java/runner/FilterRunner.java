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
        IndexSearcher searcher = SearchUtils.createIndexSearcher(filterParser.getIndexPath());
        ArrayList<Document> spamTrain = null;
        ArrayList<Document> hamTrain = null;

        try {
            spamTrain = readIndex(filterParser.getSpamIndexPath());
            hamTrain= readIndex(filterParser.getHamIndexPath());
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }

        runUnigrams(searcher, spamTrain, hamTrain);
        runBigrams(searcher, spamTrain, hamTrain);
        runTrigrams(searcher, spamTrain, hamTrain);
        runQuadgrams(searcher, spamTrain, hamTrain);

    }


    /**
     * Desc: Read and store the training or test data.
     *
     * @param path to the training/test set.
     * @return train or test data stored as an ArrayList of Documents.
     */
    private ArrayList<Document> readIndex(String path) throws IOException, ParseException {

        BufferedReader reader = null;
        HashMap<String, String> lines = new HashMap<>();
        File f = new File(path);

        String line = null;

        try {
            reader = new BufferedReader(new FileReader(f));
            while ((line = reader.readLine()) != null) {
                String[] curLine = line.split("\\t+");
                String pid = curLine[0].trim();
                String qid = curLine[1].trim();
                lines.put(pid, qid);
            }
        } catch (IOException | NullPointerException e) {
            e.printStackTrace();
        }

        ArrayList<Document> data = null;
        try {
            data = getCorpus(lines);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return data;
    }


    /**
     * Desc: Build an ArrayList of Documents for the data set.
     *
     * @param  data, train or test in the form of a HashMap.
     * @return corpus, an ArrayList of documents.
     */
    private ArrayList<Document> getCorpus(HashMap<String, String> data) throws IOException, ParseException {

        IndexSearcher searcher = new IndexSearcher(DirectoryReader.open(FSDirectory.open(Paths.get(filterParser.getIndexPath()))));
        QueryParser parser = new QueryParser("Text", new StandardAnalyzer());
        ArrayList<Document> corpus = new ArrayList<>();

        for (String key : data.keySet()) {
            Query q = parser.parse(QueryParser.escape(data.get(key)));
            try {
                int docId = searcher.search(q, 1).scoreDocs[0].doc;
                System.out.println(docId);
                Document d = searcher.doc(docId);
                corpus.add(d);
            } catch (IndexOutOfBoundsException e) {
                //System.out.println(e.getStackTrace());
            }
        }
        return corpus;
    }


    /**
     * Desc: Train a NaiveBayesPredictor (unigrams).
     *
     * @param spamCorpus the training set of spam documents.
     * @param hamCorpus the training set of ham documents.
     */
     private void runUnigrams(IndexSearcher searcher, ArrayList<Document> spamCorpus, ArrayList<Document> hamCorpus) {

        LabelPredictor unigramsPredictor = new NaiveBayesPredictor(searcher);
        ArrayList<Document> test = null;

        for (Document item : spamCorpus) {
            String text = item.get("Text");
            List<String> tokens = createTokenList(text, new EnglishAnalyzer());
            unigramsPredictor.trainSpamTokens(tokens);
        }

        for (Document item : hamCorpus) {
            String text = item.get("Text");
            List<String> tokens = createTokenList(text, new EnglishAnalyzer());
            unigramsPredictor.trainHamTokens(tokens);
        }

         try {
             test = readIndex(filterParser.getTestIndexPath());
         } catch (IOException | ParseException e) {
             e.printStackTrace();
         }

         System.out.println("------------Unigrams------------");
         predict(unigramsPredictor, test);
    }

    /**
     * Desc: Train a NaiveBayesBigramPredictor.
     *
     * @param spamCorpus the training set of spam documents.
     * @param hamCorpus the training set of ham documents.
     */
    private void runBigrams(IndexSearcher searcher, ArrayList<Document> spamCorpus, ArrayList<Document> hamCorpus) {

        LabelPredictor bigramsPredictor = new NaiveBayesBigramPredictor(searcher);
        ArrayList<Document> test = null;

        for (Document item : spamCorpus) {
            String text = item.get("Text");
            List<String> tokens = createTokenList(text, new EnglishAnalyzer());
            bigramsPredictor.trainSpamTokens(tokens);
        }

        for (Document item : hamCorpus) {
            String text = item.get("Text");
            List<String> tokens = createTokenList(text, new EnglishAnalyzer());
            bigramsPredictor.trainHamTokens(tokens);
        }

        try {
            test = readIndex(filterParser.getTestIndexPath());
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }

        System.out.println("------------Bigrams------------");
        predict(bigramsPredictor, test);
    }


    /**
     * Desc: Train a NaiveBayesTrigramPredictor.
     *
     * @param spamCorpus the training set of spam documents.
     * @param hamCorpus the training set of ham documents.
     */
    private void runTrigrams(IndexSearcher searcher, ArrayList<Document> spamCorpus, ArrayList<Document> hamCorpus) {

        LabelPredictor trigramsPredictor = new NaiveBayesTrigramPredictor(searcher);
        ArrayList<Document> test = null;

        for (Document item : spamCorpus) {
            String text = item.get("Text");
            List<String> tokens = createTokenList(text, new EnglishAnalyzer());
            trigramsPredictor.trainSpamTokens(tokens);
        }

        for (Document item : hamCorpus) {
            String text = item.get("Text");
            List<String> tokens = createTokenList(text, new EnglishAnalyzer());
            trigramsPredictor.trainHamTokens(tokens);
        }

        try {
            test = readIndex(filterParser.getTestIndexPath());
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }

        System.out.println("------------Trigrams------------");
        predict(trigramsPredictor, test);
    }

    /**
     * Desc: Train a NaiveBayesQuadgramPredictor.
     *
     * @param spamCorpus the training set of spam documents.
     * @param hamCorpus the training set of ham documents.
     */
    private void runQuadgrams(IndexSearcher searcher, ArrayList<Document> spamCorpus, ArrayList<Document> hamCorpus) {

        LabelPredictor quadgramsPredictor = new NaiveBayesQuadgramPredictor(searcher);
        ArrayList<Document> test = null;

        for (Document item : spamCorpus) {
            String text = item.get("Text");
            List<String> tokens = createTokenList(text, new EnglishAnalyzer());
            quadgramsPredictor.trainSpamTokens(tokens);
        }

        for (Document item : hamCorpus) {
            String text = item.get("Text");
            List<String> tokens = createTokenList(text, new EnglishAnalyzer());
            quadgramsPredictor.trainHamTokens(tokens);
        }

        try {
            test = readIndex(filterParser.getTestIndexPath());
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }

        System.out.println("------------Quadgrams------------");
        predict(quadgramsPredictor, test);
    }


    /**
     * Desc: Test the predictors.
     *
     * @param predictor the predictor that was previously instantiated.
     * @param test the test data that was held out.
     */
    private void predict(LabelPredictor predictor, ArrayList<Document> test) {

        for (Document item : test) {
            String text = item.get("Text");
            List<String> tokens = createTokenList(text, new EnglishAnalyzer());
            ArrayList<Double> result = predictor.score(tokens);
            System.out.println(result);
        }
    }

}
