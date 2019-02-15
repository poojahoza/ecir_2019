package main.java.runner;

import main.java.commandparser.CommandParser;
import main.java.commandparser.RegisterCommands;
import main.java.commandparser.ValidateCommands;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.store.FSDirectory;

import java.io.*;
import java.nio.file.Paths;
import java.util.*;


/**
 * Responsible for creating the ham and spam corpus to train on with the
 * improved BayesCounter.
 */
public class ClassifyRunner implements ProgramRunner {

    private RegisterCommands.CommandClassify classifyParser = null;
    private ValidateCommands.ValidateClassifyCommands validate = null;

    public ClassifyRunner(CommandParser parser)
    {
        classifyParser = parser.getClassifyCommand();
        validate = new ValidateCommands.ValidateClassifyCommands(classifyParser);
    }

    @Override
    public void run() {
        // Instantiate the empty hash maps.
        HashMap<String, HashMap<String, String>> classifyMap = initializeMaps();

        // Open the qrels file and save all lines marked with a -2 or 2
        classifyMap = parseQrels(classifyMap);

        // Use the ids stored in the maps to get the documents from the Lucene index.
        HashMap<String, HashMap<String, String>> corpus = new HashMap<>();
        try {
            corpus = getDocIds(classifyMap);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Use these ids to look up the correct documents to feed to the BayesCounter
        writeMaps(corpus);
    }

    private HashMap<String, HashMap<String, String>> initializeMaps() {

        // Initialize the data structure
        HashMap<String, HashMap<String, String>> classifyMap = new HashMap<>();
        HashMap<String, String> spamMap = new HashMap<>();
        HashMap<String, String> hamMap = new HashMap<>();

        classifyMap.put("ham", hamMap);
        classifyMap.put("spam", spamMap);

        return classifyMap;
    }

    private HashMap<String, HashMap<String, String>> parseQrels(HashMap<String, HashMap<String, String>> classifyMap) {

        // Open the qrels file and iterate through each line
        BufferedReader reader = null;
        File qrelsFile = new File(classifyParser.getQrelPath());
        String line = null;

        try {

            reader = new BufferedReader(new FileReader(qrelsFile));

            while ((line = reader.readLine()) != null) {

                String[] curLine = line.split("\\s+");
                HashMap<String, String> curMap;

                if (curLine[3].equals("-2")) {
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

        return classifyMap;
    }

    private HashMap<String, HashMap<String, String>> getDocIds (HashMap<String, HashMap<String, String>> classifyMap) throws IOException {

        // Use the searcher to traverse the lucene index, and check each page id against those in the maps.
        // If there is a match, overwrite the page id in the map with the document id.
        IndexReader reader = DirectoryReader.open(FSDirectory.open(Paths.get(classifyParser.getIndexPath())));

        HashMap<String, String> spamMap = classifyMap.get("spam");
        HashMap<String, String> hamMap = classifyMap.get("ham");

        HashMap<String, String> spamTokens = new HashMap<>();
        HashMap<String, String> hamTokens = new HashMap<>();
        HashMap<String, HashMap<String, String>> corpus = new HashMap<>();

        for (int i = 0; i < reader.maxDoc(); i++) {
            Document doc = reader.document(i);
            String docId = doc.get("id");
            String text = doc.get("text");

            if (spamMap.containsKey(docId)) {
                spamTokens.put(docId, text);
                // There are no spam ids in the corpus...
            }
            else if (hamMap.containsKey(docId)) {
                hamTokens.put(docId, text);
            }
        }

        corpus.put("ham", hamMap);
        corpus.put("spam", spamMap);
        return corpus;
    }

    private void writeMaps(HashMap<String, HashMap<String, String>> corpus) {

        HashMap<String, String> spamTokens = corpus.get("spam");
        HashMap<String, String> hamTokens = corpus.get("ham");

        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(classifyParser.getSpamPath(), true));
            for (String key : spamTokens.keySet()) {
                writer.write(key + ": " + spamTokens.get(key) + '\n');
            }
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(classifyParser.getHamPath(), true));
            for (String key : hamTokens.keySet()) {
                writer.write(key + ": " + hamTokens.get(key) + '\n');
            }
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
