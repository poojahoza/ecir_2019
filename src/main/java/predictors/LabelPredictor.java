package main.java.predictors;

import main.java.utils.SearchUtils;
//import main.kotlin.evaluation.KotlinEvaluator;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Abstract class meant to predict labels. Your prediction methods must extend this class!
 * This class also comes with some useful methods to retrieve corpus stats
 *
 * The IndexSearcher (searcher) is used to access a Lucene index containing training emails (spam and ham)
 */
abstract public class LabelPredictor {
    public IndexSearcher searcher;


    public LabelPredictor(IndexSearcher s) {
        searcher = s;
    }

    /**
     * Desc: Train classifier on ham emails.
     *
     * @param tokens List of tokens in the document
     */
    public void trainHamTokens(List<String> tokens) {}

    /**
     * Desc: Train classifier on ham emails.
     *
     * @param tokens List of tokens in the document
     */
    public void trainSpamTokens(List<String> tokens) {}

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
     * @param tokens List of tokens in the document
     * @return ArrayList The ham and spam scores of the given document tokens
     */
    abstract public ArrayList<Double> score(List<String> tokens);


    /**
     * Desc: Given a token, returns the number of documents in the training corpus that contains this token.
     *       i.e. this is Document Frequency given a term (your token)
     *
     * @param token A token from an email (this is turned into a Term)
     * @return long (number of emails that contain this token)
     */
    public long getDocFrequency(String token) {
        Term t = new Term("text", token);
        try {
            return searcher.getIndexReader().docFreq(t);
        } catch (IOException e) {
            return 0L;
        }
    }

    /**
     * Desc: Given a token, returns the total number of times the token appears in the training corpus
     *
     * @param token A token from an email (this is turned into a Term)
     * @return long (number of times the token appears in the corpus)
     */
    public long getTermFrequency(String token) {
        Term t = new Term("text", token);
        try {
            return searcher.getIndexReader().totalTermFreq(t);
        } catch (IOException e) {
            return 0L;
        }
    }


    /**
     * Desc: Returns the total number of tokens in all documents
     *
     * @return long (total number of tokens in corpus)
     */
    public double getTotalTermFrequency() {
        try {
            return searcher.getIndexReader().getSumTotalTermFreq("text");
        } catch (IOException e) {
            return 0.0;
        }
    }


    /**
     * Desc: Sums over the doc frequency of each term in the corpus
     *
     * @return long (total doc frequency)
     */
    public double getTotalDocFrequency() {
        try {
            return searcher.getIndexReader().getSumDocFreq("text");
        } catch (IOException e) {
            return 0.0;
        }
    }

    /**
     * Desc: Gets total number of docs in corpus.
     *
     * @return long (total number of docs in corpus)
     */
    public double getNumberOfDocsInCorpus() {
        return searcher.getIndexReader().numDocs();
    }


    /**
     * Desc: When called, the LabelPredictor will be handed unlabelled emails to classify (using predict method)
     * The labels will be used to compute the F1 Score of your label prediction method.
     *
     */
    //public void evaluate() { KotlinEvaluator.Companion.evaluate(this); }

    /**
     * Desc: For each spam email in training corpus, retrieves its tokens.
     * @return A list where each element is a list of tokens from a particular spam email
     */
    public ArrayList<ArrayList<String>> retrieveSpamEmailTokens() {
        return _retrieveEmailTokens("spam");
    }


    /**
     * Desc: For each ham email in training corpus, retrieves its tokens.
     * @return A list where each element is a list of tokens from a particular ham email
     */
    public ArrayList<ArrayList<String>> retrieveHamEmailTokens() {
        return _retrieveEmailTokens("ham");
    }


    // I think the problem is somewhere in here, because there are no ham or spam labels in this corpus.
    private ArrayList<ArrayList<String>> _retrieveEmailTokens(String label) {
        Query q = SearchUtils.createStandardBooleanQuery(label, "label");
        ArrayList<ArrayList<String>> emails = new ArrayList<>();
        try {
            for (ScoreDoc sd :  searcher.search(q, 100000).scoreDocs) {
                ArrayList<String> tokens =
                        new ArrayList<>(Arrays.asList(searcher.doc(sd.doc).get("text").split(" ")));
                emails.add(tokens);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return emails;
    }

}

