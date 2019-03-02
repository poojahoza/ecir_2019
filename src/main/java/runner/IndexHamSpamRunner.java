package main.java.runner;

import main.java.commandparser.CommandParser;
import main.java.commandparser.RegisterCommands;
import main.java.commandparser.ValidateCommands;

import edu.unh.cs.treccar_v2.Data;
import edu.unh.cs.treccar_v2.read_data.DeserializeData;

import java.awt.*;
import java.io.*;
import java.util.*;


/**
 * Creates training and test data for ham and spam documents from the manual qrels file.
 */
public class IndexHamSpamRunner implements ProgramRunner {

    private RegisterCommands.IndexHamSpam indexHamSpamParser = null;
    private ValidateCommands.ValidateIndexHamSpamCommands validate = null;
    public final HashMap<String, HashMap<String, String>> classifyMap;
    public final HashMap<String, HashMap<String, String>> corpus;

    public IndexHamSpamRunner(CommandParser parser)
    {
        indexHamSpamParser = parser.getIndexHamSpamCommand();
        validate = new ValidateCommands.ValidateIndexHamSpamCommands(indexHamSpamParser);
        classifyMap = new HashMap<>();
        corpus = new HashMap<>();
    }


    @Override
    public void run() {

        parseQrels();
        try {
            buildTrainTestSets();
        } catch (IOException e) {
            e.printStackTrace();
        }
        write(corpus);
    }

    private void extractText(Data.Paragraph p) {

        HashMap<String, String> spamMap = classifyMap.get("spam");
        HashMap<String, String> hamMap = classifyMap.get("ham");

        final String content = p.getTextOnly();
        final String paraId = p.getParaId();

        if (spamMap.get(paraId) != null) {
            HashMap curMap = corpus.get("spam");
            curMap.put(paraId, content);
        }
        else if (hamMap.get(paraId) != null) {
            HashMap curMap = corpus.get("spam");
            curMap.put(paraId, content);
        }
    }


    /**
     * Desc: Iterate through the qrels file and map pids to ham/spam labels according to their annotations.
     * A pid of 2 indicates that a document is ham, whereas a pid of -1, -2, or -0 indicates that a
     * document is spam.
     *
     */
    private void parseQrels() {

        HashMap<String, String> spamMap = new HashMap<>();
        HashMap<String, String> hamMap = new HashMap<>();
        classifyMap.put("ham", hamMap);
        classifyMap.put("spam", spamMap);

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
                    curMap.put(curLine[2], curLine[0]);
                }
                else if (curLine[3].equals("2")) {
                    curMap = classifyMap.get("ham");
                    curMap.put(curLine[2], curLine[0]);
                }
            }

        } catch (IOException | NullPointerException e) {
            e.printStackTrace();
        }

    }


    /**
     * Desc: Iterate through the Lucene index and store all ids that are also found in the qrels.
     *
     */
    private void buildTrainTestSets () throws IOException {

        HashMap<String, String> spamMap = classifyMap.get("spam");
        HashMap<String, String> hamMap = classifyMap.get("ham");

        corpus.put("ham", new HashMap<>());
        corpus.put("spam", new HashMap<>());

        // Get the doc id and check if it is in the spam/ham map. If it is, then add it to the corpus.
        final FileInputStream fileInputStream2 = new FileInputStream(new File(indexHamSpamParser.getParagraphPath()));
        final Iterator<Data.Paragraph> paragraphIterator = DeserializeData.iterParagraphs(fileInputStream2);

        for (int i = 1; paragraphIterator.hasNext(); i++) {
            extractText(paragraphIterator.next());
        }
    }


    /**
     * Desc: Create separate training and test sets.
     *
     * @param corpus of ham and spam documents created in the previous step.
     */
    private void write(HashMap<String, HashMap<String, String>> corpus) {

        HashMap<String, String> spamCorpus = corpus.get("spam");
        HashMap<String, String> hamCorpus = corpus.get("ham");

        // Divide the ham and spam maps in half.
        HashMap<String, String> spamTrain = new HashMap<>();
        HashMap<String, String> spamTest =  new HashMap<>();

        HashMap<String, String> hamTrain = new HashMap<>();
        HashMap<String, String> hamTest =  new HashMap<>();

        Random random = new Random(50);
        for (String key : spamCorpus.keySet()) {
            if (random.nextInt() > 25) {
                spamTest.put(key, spamCorpus.get(key));
            }
            else {
                spamTrain.put(key, spamCorpus.get(key));
            }
        }
        for (String key : hamCorpus.keySet()) {
            if (random.nextInt() > 25) {
                hamTest.put(key, hamCorpus.get(key));
            }
            else {
                hamTrain.put(key, hamCorpus.get(key));
            }
        }

        // Write out to separate files
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(indexHamSpamParser.getSpamTrainPath(), true));
            for (String key: spamTrain.keySet()) {
                writer.write(key + '\t' + spamTrain.get(key));
                writer.newLine();
            }
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(indexHamSpamParser.getHamTrainPath(), true));
            for (String key: hamTrain.keySet()) {
                writer.write(key + '\t' + hamTrain.get(key));
                writer.newLine();
            }
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Write out to a third file a combination of the remaining halves for testing.
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(indexHamSpamParser.getHamSpamTestPath(), true));
            for (String key: spamTest.keySet()) {
                writer.write(key + '\t' + spamTest.get(key));
                writer.newLine();
            }
            for (String key: hamTest.keySet()) {
                writer.write(key + '\t' + hamTest.get(key));
                writer.newLine();
            }
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }



        // Write the test data sets into their own files too, so we can evaluate the performance of the classifiers on test data.
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(indexHamSpamParser.getSpamTestPath(), true));
            for (String key: spamTest.keySet()) {
                writer.write(key + '\t' + spamTest.get(key));
                writer.newLine();
            }
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(indexHamSpamParser.getHamTestPath(), true));
            for (String key: hamTest.keySet()) {
                writer.write(key + '\t' + hamTest.get(key));
                writer.newLine();
            }
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
