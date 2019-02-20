package main.java.runner;

import main.java.commandparser.CommandParser;
import main.java.commandparser.RegisterCommands;
import main.java.commandparser.ValidateCommands;
import main.java.indexer.IndexBuilder;
import main.java.predictors.LabelPredictor;
import main.java.predictors.NaiveBayesPredictor;
import main.java.utils.SearchUtils;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.store.FSDirectory;

import java.io.*;
import java.lang.reflect.Array;
import java.nio.file.Paths;
import java.util.*;

import static main.java.utils.SearchUtils.createIndexSearcher;
import static main.java.utils.SearchUtils.createTokenList;


/**
 * Creates training and test data for ham and spam documents from the manual qrels file.
 */
public class IndexHamSpamRunner implements ProgramRunner {

    private RegisterCommands.IndexHamSpam indexHamSpamParser = null;
    private ValidateCommands.ValidateIndexHamSpamCommands validate = null;

    public IndexHamSpamRunner(CommandParser parser)
    {
        indexHamSpamParser = parser.getIndexHamSpamCommand();
        validate = new ValidateCommands.ValidateIndexHamSpamCommands(indexHamSpamParser);
    }


    @Override
    public void run() {
        HashMap<String, HashMap<String, String>> classifyMap = initializeMaps();

        classifyMap = parseQrels(classifyMap);

        HashMap<String, ArrayList<Document>> corpus = new HashMap<>();
        try {
            corpus = getDocIds(classifyMap);
        } catch (IOException e) {
            e.printStackTrace();
        }
        write(corpus);
    }


    /**
     * Desc: Initialize the main data structure for this class.
     *
     * @return HashMap with ham and spam keys added.
     */
    private HashMap<String, HashMap<String, String>> initializeMaps() {

        HashMap<String, HashMap<String, String>> classifyMap = new HashMap<>();
        HashMap<String, String> spamMap = new HashMap<>();
        HashMap<String, String> hamMap = new HashMap<>();

        classifyMap.put("ham", hamMap);
        classifyMap.put("spam", spamMap);

        return classifyMap;
    }


    /**
     * Desc: Iterate through the qrels file and map pids to ham/spam labels according to their annotations.
     * A pid of 2 indicates that a document is ham, whereas a pid of -1, -2, or -0 indicates that a
     * document is spam.
     *
     * @param classifyMap that was initialized in the previous step.
     * @return classifyMap Fully populated HashMap that maps the ham/spam labels to pids.
     */
    private HashMap<String, HashMap<String, String>> parseQrels(HashMap<String, HashMap<String, String>> classifyMap) {

        BufferedReader reader = null;
        File qrelsFile = new File(indexHamSpamParser.getQrelPath());
        String line = null;

        try {
            reader = new BufferedReader(new FileReader(qrelsFile));

            while ((line = reader.readLine()) != null) {

                String[] curLine = line.split("\\s+");
                HashMap<String, String> curMap;

                if (curLine[3].equals("-2") || curLine[3].equals("-1") || curLine[3].equals("-0")) {
                    curMap = classifyMap.get("spam");
                    curMap.put(curLine[2], line);
                }
                else if (curLine[3].equals("2")) {
                    curMap = classifyMap.get("ham");
                    curMap.put(curLine[2], line);
                }
            }

        } catch (IOException | NullPointerException e) {
            e.printStackTrace();
        }

        return classifyMap;
    }


    /**
     * Desc: Iterate through the Lucene index and store all ids that are also found in the qrels.
     *
     * @param classifyMap that was populated in the previous step.
     * @return corpus A new HashMap only containing Documents that were found in both the qrels and index.
     */
    private HashMap<String, ArrayList<Document>> getDocIds (HashMap<String, HashMap<String, String>> classifyMap) throws IOException {

        IndexReader reader = DirectoryReader.open(FSDirectory.open(Paths.get(indexHamSpamParser.getIndexPath())));

        HashMap<String, String> spamMap = classifyMap.get("spam");
        HashMap<String, String> hamMap = classifyMap.get("ham");

        HashMap<String, ArrayList<Document>> corpus = new HashMap<>();
        corpus.put("ham", new ArrayList<>());
        corpus.put("spam", new ArrayList<>());

        ArrayList<Document> curMap;
        for (int i = 0; i < reader.maxDoc(); i++) {
            Document doc = reader.document(i);
            String docId = doc.get("id");

            if (spamMap.containsKey(docId)) {
                curMap = corpus.get("spam");
                curMap.add(doc);
            }
            else if (hamMap.containsKey(docId)) {
                curMap = corpus.get("ham");
                curMap.add(doc);
            }
        }
        return corpus;
    }


    /**
     * Desc: Create seperate training and test sets.
     *
     * @param corpus of ham and spam documents created in the previous step.
     */
    public void write(HashMap<String, ArrayList<Document>> corpus) {

        ArrayList<Document> spamCorpus = corpus.get("spam");
        ArrayList<Document> hamCorpus = corpus.get("ham");

        // Divide the ham and spam lists in half.
        int spamSize = spamCorpus.size();
        int hamSize = hamCorpus.size();

        ArrayList<Document> spamTrain = new ArrayList<>(spamCorpus.subList(0, (spamSize + 1)/2));
        ArrayList<Document> spamTest =  new ArrayList<>(spamCorpus.subList((spamSize + 1) / 2, spamSize));

        ArrayList<Document> hamTrain = new ArrayList<>(hamCorpus.subList(0, (hamSize + 1)/2));
        ArrayList<Document> hamTest =  new ArrayList<>(hamCorpus.subList((hamSize + 1) / 2, hamSize));

        // Write out to seperate files the first half of each
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(indexHamSpamParser.getSpamDestPath(), true));
            for (Document d: spamTrain) {
                String s = d.toString();
                writer.write(s);
                writer.newLine();
            }
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(indexHamSpamParser.getHamDestPath(), true));
            for (Document d: hamTrain) {
                String s = d.toString();
                writer.write(s);
                writer.newLine();
            }
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Write out to a third file a combination of the remaining halves for testing.
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(indexHamSpamParser.getHamSpamDestPath(), true));
            for (Document d: spamTest) {
                String s = d.toString();
                writer.write(s);
                writer.newLine();
            }
            for (Document d: hamTest) {
                String s = d.toString();
                writer.write(s);
                writer.newLine();
            }
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
