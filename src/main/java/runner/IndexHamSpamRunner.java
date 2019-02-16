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
public class IndexHamSpamRunner implements ProgramRunner {

    private RegisterCommands.IndexHamSpam indexHamSpamParser = null;
    private ValidateCommands.ValidateIndexHamSpamCommands validate = null;

    public IndexHamSpamRunner(CommandParser parser)
    {
        indexHamSpamParser = parser.getClassifyCommand();
        validate = new ValidateCommands.ValidateIndexHamSpamCommands(indexHamSpamParser);
    }

    @Override
    public void run() {
        // Instantiate the empty hash maps.
        HashMap<String, HashMap<String, String>> classifyMap = initializeMaps();

        // Open the qrels file and save all lines marked with a -2 or 2
        classifyMap = parseQrels(classifyMap);

        // Use the ids stored in the maps to get the documents from the Lucene index.
        ArrayList<Document> corpus = new ArrayList<>();
        try {
            corpus = getDocIds(classifyMap);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // This is not enough. I need to make an actual Lucene index. To do this, I need to write the data to a certain
        // format before invoking the indexbuilder.
        writeIndex(corpus);
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
        File qrelsFile = new File(indexHamSpamParser.getQrelPath());
        String line = null;

        try {

            reader = new BufferedReader(new FileReader(qrelsFile));

            while ((line = reader.readLine()) != null) {

                String[] curLine = line.split("\\s+");
                HashMap<String, String> curMap;

                if (curLine[3].equals("-2")) {
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

    private ArrayList<Document> getDocIds (HashMap<String, HashMap<String, String>> classifyMap) throws IOException {

        IndexReader reader = DirectoryReader.open(FSDirectory.open(Paths.get(indexHamSpamParser.getIndexPath())));

        HashMap<String, String> spamMap = classifyMap.get("spam");
        HashMap<String, String> hamMap = classifyMap.get("ham");

        ArrayList<Document> corpus = new ArrayList<>();

        for (int i = 0; i < reader.maxDoc(); i++) {
            Document doc = reader.document(i);
            String docId = doc.get("id");

            if (spamMap.containsKey(docId)) {
                corpus.add(doc);
            }
            else if (hamMap.containsKey(docId)) {
               corpus.add(doc);
            }
        }

        return corpus;
    }

    private void writeIndex(ArrayList<Document> corpus) {

        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(indexHamSpamParser.getPath(), true));
            for (Document item : corpus) {
                writer.write(item + "\n");
            }
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
