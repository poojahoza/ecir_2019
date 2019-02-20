package main.java.runner;

import main.java.commandparser.CommandParser;
import main.java.commandparser.RegisterCommands;
import main.java.commandparser.ValidateCommands;
import main.java.predictors.LabelPredictor;
import main.java.predictors.NaiveBayesPredictor;
import main.java.utils.SearchUtils;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.store.FSDirectory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Array;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static main.java.utils.SearchUtils.createTokenList;


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
        ArrayList<Document> test = null;

        try {
            spamTrain = readIndex(filterParser.getSpamIndexPath());
            hamTrain= readIndex(filterParser.getHamIndexPath());
        } catch (IOException e) {
            e.printStackTrace();
        }

        LabelPredictor predictor = train(searcher, spamTrain, hamTrain);

        try {
            test = readIndex(filterParser.getTestIndexPath());
        } catch (IOException e) {
            e.printStackTrace();
        }

        predict(predictor, test);
    }

    private ArrayList<Document> readIndex(String path) throws IOException {

        //
        BufferedReader reader = null;
        HashMap<String, String> lines = new HashMap<>();
        File f = new File(path);

        String line = null;

        try {
            reader = new BufferedReader(new FileReader(f));
            while ((line = reader.readLine()) != null) {
                String[] curLine = line.split("\\s+");
                String curLineIndexed = curLine[0];
                String[] curHash = curLineIndexed.split(":");
                String curHashIndexed = curHash[1];
                curHashIndexed = curHashIndexed.substring(0, curHashIndexed.length() -1);
                lines.put(curHashIndexed, "");
            }
        } catch (IOException | NullPointerException e) {
            e.printStackTrace();
        }

        ArrayList<Document> train = getCorpus(lines);
        return train;
    }

    private ArrayList<Document> getCorpus(HashMap<String, String> train) throws IOException {

        IndexReader reader = DirectoryReader.open(FSDirectory.open(Paths.get(filterParser.getIndexPath())));
        ArrayList<Document> corpus = new ArrayList<>();

        for (int i = 0; i < reader.maxDoc(); i++) {
            Document doc = reader.document(i);
            String docId = doc.get("id");

            if (train.containsKey(docId)) {
                //System.out.println(docId);
                corpus.add(doc);
            }
        }
        return corpus;
    }


     private LabelPredictor train(IndexSearcher searcher, ArrayList<Document> spamCorpus, ArrayList<Document> hamCorpus) {

        LabelPredictor nbp = new NaiveBayesPredictor(searcher);

        for (Document item : spamCorpus) {
            String text = item.get("text");
            List<String> tokens = createTokenList(text, new EnglishAnalyzer());
            nbp.trainSpamTokens(tokens);
        }

        for (Document item : hamCorpus) {
            String text = item.get("text");
            List<String> tokens = createTokenList(text, new EnglishAnalyzer());
            nbp.trainHamTokens(tokens);
        }

        return nbp;
    }

    private void predict(LabelPredictor predictor, ArrayList<Document> test) {

        for (Document item : test) {
            String text = item.get("text");
            List<String> tokens = createTokenList(text, new EnglishAnalyzer());
            String result = predictor.predict(tokens);
            System.out.println(result);
        }
    }

}
