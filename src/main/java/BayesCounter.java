package main.java;

import main.java.evaluators.F1Evaluator;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.document.Document;

import javax.print.Doc;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.annotation.Documented;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static main.java.utils.SearchUtils.createTokenList;

public class BayesCounter {

    /**
     * Makes a new BayesCounter with empty hash map.
     */
    public final HashMap<String, HashMap<String, Integer>> bayesMap;

    public BayesCounter() {
        bayesMap = new HashMap<>();
    }

    public void evaluateUnigramPredictor(HashMap<String, String> spamTest, HashMap<String, String> hamTest, HashMap<String, String> docs) {

        HashMap <String, String> calledLabels = new HashMap<>();
        HashMap <String, String> trueLabels = new HashMap<>();

        // For each document, call the predict method. Store the pid with its prediction in the calledLabels map
        for (String key: docs.keySet()) {
            String text = docs.get(key);
            List<String> tokens = createTokenList(text, new EnglishAnalyzer());
            String label = this.classify(tokens);
            calledLabels.put(key, label);
        }

        // For each document, get the real label. Store the pid with its real label in the trueLabels map
        for (String key: docs.keySet()) {
            if (hamTest.get(key) != null) {
                trueLabels.put(key, "ham");
            }
            else if (spamTest.get(key) != null) {
                trueLabels.put(key, "spam");
            }
        }

        F1Evaluator f1 = new F1Evaluator(trueLabels);
        double f1Score = f1.evaluateCalledLabels(calledLabels);
        System.out.println("F1 score: " + f1Score);
    }

    public void evaluateBigramPredictor(HashMap<String, String> spamTest, HashMap<String, String> hamTest, HashMap<String, String> docs) {

        HashMap <String, String> calledLabels = new HashMap<>();
        HashMap <String, String> trueLabels = new HashMap<>();

        // For each document, call the predict method. Store the pid with its prediction in the calledLabels map
        for (String key: docs.keySet()) {
            String text = docs.get(key);
            List<String> tokens = createTokenList(text, new EnglishAnalyzer());
            String label = this.classifyWithBigrams(tokens);
            calledLabels.put(key, label);
        }

        // For each document, get the real label. Store the pid with its real label in the trueLabels map
        for (String key: docs.keySet()) {
            if (hamTest.get(key) != null) {
                trueLabels.put(key, "ham");
            }
            else if (spamTest.get(key) != null) {
                trueLabels.put(key, "spam");
            }
        }

        F1Evaluator f1 = new F1Evaluator(trueLabels);
        double f1Score = f1.evaluateCalledLabels(calledLabels);
        System.out.println("F1 score: " + f1Score);
    }

    public void evaluateTrgramPredictor(HashMap<String, String> spamTest, HashMap<String, String> hamTest, HashMap<String, String> docs) {

        HashMap <String, String> calledLabels = new HashMap<>();
        HashMap <String, String> trueLabels = new HashMap<>();

        // For each document, call the predict method. Store the pid with its prediction in the calledLabels map
        for (String key: docs.keySet()) {
            String text = docs.get(key);
            List<String> tokens = createTokenList(text, new EnglishAnalyzer());
            String label = this.classifyWithTrigrams(tokens);
            calledLabels.put(key, label);
        }

        // For each document, get the real label. Store the pid with its real label in the trueLabels map
        for (String key: docs.keySet()) {
            if (hamTest.get(key) != null) {
                trueLabels.put(key, "ham");
            }
            else if (spamTest.get(key) != null) {
                trueLabels.put(key, "spam");
            }
        }

        F1Evaluator f1 = new F1Evaluator(trueLabels);
        double f1Score = f1.evaluateCalledLabels(calledLabels);
        System.out.println("F1 score: " + f1Score);
    }

    public void evaluateQuadgramPredictor(HashMap<String, String> spamTest, HashMap<String, String> hamTest, HashMap<String, String> docs) {

        HashMap <String, String> calledLabels = new HashMap<>();
        HashMap <String, String> trueLabels = new HashMap<>();

        // For each document, call the predict method. Store the pid with its prediction in the calledLabels map
        for (String key: docs.keySet()) {
            String text = docs.get(key);
            List<String> tokens = createTokenList(text, new EnglishAnalyzer());
            String label = this.classifyWithQuadgrams(tokens);
            calledLabels.put(key, label);
        }

        // For each document, get the real label. Store the pid with its real label in the trueLabels map
        for (String key: docs.keySet()) {
            if (hamTest.get(key) != null) {
                trueLabels.put(key, "ham");
            }
            else if (spamTest.get(key) != null) {
                trueLabels.put(key, "spam");
            }
        }

        F1Evaluator f1 = new F1Evaluator(trueLabels);
        double f1Score = f1.evaluateCalledLabels(calledLabels);
        System.out.println("F1 score: " + f1Score);
    }

    /**
     * Desc: Parse the document tokens and make a hash map of hash maps for the class passed as a parameter.
     *      Example state of the map after this function: {Spam => ["Viagra", 1000], ["great", 987]}
     *      This function is for training and should therefore be called on the training set.
     *
     * @param docClass the document label of "ham" or "spam"
     * @param tokens List of tokens in the document
     */
    public void buildHashMap(String docClass, List<String> tokens) {

        if (bayesMap.get(docClass) == null) {
            HashMap<String, Integer> classMap = new HashMap<>();
            bayesMap.put(docClass, classMap);
        }

        HashMap<String, Integer> curMap = bayesMap.get(docClass);

        for (String token : tokens) {
            if (!curMap.containsKey(token)) {
                curMap.put(token, 0);
            }

            int curCount = curMap.get(token);
            curMap.put(token, curCount + 1);
        }
    }


    /**
     * Desc: Parse the tokens of the document passed as a parameter and sum the counts of each word.
     *       This function is for evaluation and should therefore be called on the evaluation set of emails.
     *
     * @param tokens List of tokens in the document.
     * @return The document class with the larger count.
     */
    public String classify(List<String> tokens) {

        HashMap<String, Integer> spamDist = bayesMap.get("spam");
        HashMap<String, Integer> hamDist = bayesMap.get("ham");


        // The following block tells me that I'm hardly getting any ham. Spam tokens take up 99% of the document.
        /*try {
            BufferedWriter writer = new BufferedWriter(new FileWriter("/home/rachel/grad_courses/data_science/dump/", true));
            writer.write("++++++++ SPAM COUNTS ++++++++\n\n");
            for (String key: spamDist.keySet()) {
                writer.write(key + '\t' + spamDist.get(key) + '\n');
            }
            writer.write("++++++++ HAM COUNTS ++++++++\n\n");
            for (String key: hamDist.keySet()) {
                writer.write(key + '\t' + hamDist.get(key) + '\n');
            }
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }*/



        double hamScore = 0;
        double spamScore = 0;

        for (String token : tokens) {
            spamScore += Math.log(spamDist.getOrDefault(token, 1));
            hamScore += Math.log(hamDist.getOrDefault(token, 1));
        }

        if (hamScore > spamScore) {
            return "ham";
        }
        else {
            return "spam";
        }
    }


    /**
     * Desc: Parse the tokens of the email passed as a parameter and sum the counts of each word.
     *
     * @param tokens List of tokens in the document.
     * @return scores a list containing the hamScore, followed by the spamScore.
     */
    public ArrayList<Double> getScores(List<String> tokens) {

        HashMap<String, Integer> spamDist = bayesMap.get("spam");
        HashMap<String, Integer> hamDist = bayesMap.get("ham");
        ArrayList<Double> scores = new ArrayList<>();

        double hamScore = 0;
        double spamScore = 0;

        for (String token : tokens) {
            spamScore += Math.log(spamDist.getOrDefault(token, 1));
            hamScore += Math.log(hamDist.getOrDefault(token, 1));
        }

        scores.add(hamScore);
        scores.add(spamScore);
        return scores;
    }


    /**
     * Desc: Parse the document tokens and make a hash map of hash maps for the class passed as a parameter.
     *      Example state of the map after this function: {Spam => ["Viagra", 1000], ["great", 987]}
     *      This function is for training and should therefore be called on the training set.
     *
     * @param docClass the document label of "ham" or "spam"
     * @param tokens List of tokens in the document
     */
    public void buildBigramsHashMap(String docClass, List<String> tokens) {

        if (bayesMap.get(docClass) == null) {
            HashMap<String, Integer> classMap = new HashMap();
            bayesMap.put(docClass, classMap);
        }

        HashMap<String, Integer> curMap = bayesMap.get(docClass);

        for (int i = 0; i < tokens.size() - 1; i++) {
            String bigram = tokens.get(i) + tokens.get(i + 1);
            if (!curMap.containsKey(bigram)) {
                curMap.put(bigram, 0);
            }

            int curCount = curMap.get(bigram);
            curMap.put(bigram, curCount + 1);

        }

    }


    /**
     * Desc: Parse the tokens of the document passed as a parameter and sum the counts of each word.
     *       This function is for evaluation and should therefore be called on the evaluation set of emails.
     *
     * @param tokens List of tokens in the document.
     * @return The document class with the larger count.
     */
    public String classifyWithBigrams(List<String> tokens) {

        HashMap<String, Integer> spamDist = bayesMap.get("spam");
        HashMap<String, Integer> hamDist = bayesMap.get("ham");

        double hamScore = 0;
        double spamScore = 0;

        for (int i = 0; i < tokens.size() - 1; i++) {
            String bigram = tokens.get(i) + tokens.get(i + 1);
            spamScore += Math.log(spamDist.getOrDefault(bigram, 1));
            hamScore += Math.log(hamDist.getOrDefault(bigram, 1));
        }

        if (hamScore > spamScore) {
            return "ham";
        }
        else {
            return "spam";
        }
    }


    /**
     * Desc: Parse the tokens of the email passed as a parameter and sum the counts of each word.
     *
     * @param tokens List of tokens in the document.
     * @return scores a list containing the hamScore, followed by the spamScore.
     */
    public ArrayList<Double> getBigramScores(List<String> tokens) {

        HashMap<String, Integer> spamDist = bayesMap.get("spam");
        HashMap<String, Integer> hamDist = bayesMap.get("ham");
        ArrayList<Double> scores = new ArrayList<>();

        double hamScore = 0;
        double spamScore = 0;

        for (int i = 0; i < tokens.size() - 1; i++) {
            String bigram = tokens.get(i) + tokens.get(i + 1);
            spamScore += Math.log(spamDist.getOrDefault(bigram, 1));
            hamScore += Math.log(hamDist.getOrDefault(bigram, 1));
        }

        scores.add(hamScore);
        scores.add(spamScore);
        return scores;
    }


    /**
     * Desc: Parse the document tokens and make a hash map of hash maps for the class passed as a parameter.
     *      Example state of the map after this function: {Spam => ["Viagra", 1000], ["great", 987]}
     *      This function is for training and should therefore be called on the training set.
     *
     * @param docClass the document label of "ham" or "spam"
     * @param tokens List of tokens in the document
     */
    public void buildTrigramsHashMap(String docClass, List<String> tokens) {

        if (bayesMap.get(docClass) == null) {
            HashMap<String, Integer> classMap = new HashMap();
            bayesMap.put(docClass, classMap);
        }

        HashMap<String, Integer> curMap = bayesMap.get(docClass);

        for (int i = 0; i < tokens.size() - 2; i++) {
            String trigram = tokens.get(i) + tokens.get(i + 1) + tokens.get(i + 2);
            if (!curMap.containsKey(trigram)) {
                curMap.put(trigram, 0);
            }

            int curCount = curMap.get(trigram);
            curMap.put(trigram, curCount + 1);
        }
    }

    /**
     * Desc: Parse the tokens of the document passed as a parameter and sum the counts of each word.
     *       This function is for evaluation and should therefore be called on the evaluation set of emails.
     *
     * @param tokens List of tokens in the document.
     * @return The document class with the larger count.
     */
    public String classifyWithTrigrams(List<String> tokens) {

        HashMap<String, Integer> spamDist = bayesMap.get("spam");
        HashMap<String, Integer> hamDist = bayesMap.get("ham");

        double hamScore = 0;
        double spamScore = 0;

        for (int i = 0; i < tokens.size() - 2; i++) {
            String trigram = tokens.get(i) + tokens.get(i + 1) + tokens.get(i + 2);
            spamScore += Math.log(spamDist.getOrDefault(trigram, 1));
            hamScore += Math.log(hamDist.getOrDefault(trigram, 1));
        }

        if (hamScore > spamScore) {
            return "ham";
        }
        else {
            return "spam";
        }
    }


    /**
     * Desc: Parse the tokens of the email passed as a parameter and sum the counts of each word.
     *
     * @param tokens List of tokens in the document.
     * @return scores a list containing the hamScore, followed by the spamScore.
     */
    public ArrayList<Double> getTrigramScores(List<String> tokens) {

        HashMap<String, Integer> spamDist = bayesMap.get("spam");
        HashMap<String, Integer> hamDist = bayesMap.get("ham");
        ArrayList<Double> scores = new ArrayList<>();

        double hamScore = 0;
        double spamScore = 0;

        for (int i = 0; i < tokens.size() - 2; i++) {
            String trigram = tokens.get(i) + tokens.get(i + 1) + tokens.get(i + 2);
            spamScore += Math.log(spamDist.getOrDefault(trigram, 1));
            hamScore += Math.log(hamDist.getOrDefault(trigram, 1));
        }

        scores.add(hamScore);
        scores.add(spamScore);
        return scores;
    }

    /**
     * Desc: Parse the document tokens and make a hash map of hash maps for the class passed as a parameter.
     *      Example state of the map after this function: {Spam => ["Viagra", 1000], ["great", 987]}
     *      This function is for training and should therefore be called on the training set.
     *
     * @param docClass the document label of "ham" or "spam"
     * @param tokens List of tokens in the document
     */
    public void buildQuadgramsHashMap(String docClass, List<String> tokens) {

        if (bayesMap.get(docClass) == null) {
            HashMap<String, Integer> classMap = new HashMap();
            bayesMap.put(docClass, classMap);
        }

        HashMap<String, Integer> curMap = bayesMap.get(docClass);

        for (int i = 0; i < tokens.size() - 3; i++) {
            String quadgram = tokens.get(i) + tokens.get(i + 1) + tokens.get(i + 2) + tokens.get(i + 3);
            if (!curMap.containsKey(quadgram)) {
                curMap.put(quadgram, 0);
            }

            int curCount = curMap.get(quadgram);
            curMap.put(quadgram, curCount + 1);

        }

    }


    /**
     * Desc: Parse the tokens of the document passed as a parameter and sum the counts of each word.
     *       This function is for evaluation and should therefore be called on the evaluation set of emails.
     *
     * @param tokens List of tokens in the document.
     * @return The document class with the larger count.
     */
    public String classifyWithQuadgrams(List<String> tokens) {

        HashMap<String, Integer> spamDist = bayesMap.get("spam");
        HashMap<String, Integer> hamDist = bayesMap.get("ham");

        double hamScore = 0;
        double spamScore = 0;

        for (int i = 0; i < tokens.size() - 3; i++) {
            String quadgram = tokens.get(i) + tokens.get(i + 1) + tokens.get(i + 2) + tokens.get(i + 3);
            spamScore += Math.log(spamDist.getOrDefault(quadgram, 1));
            hamScore += Math.log(hamDist.getOrDefault(quadgram, 1));
        }

        if (hamScore > spamScore) {
            return "ham";
        }
        else {
            return "spam";
        }
    }


    /**
     * Desc: Parse the tokens of the email passed as a parameter and sum the counts of each word.
     *
     * @param tokens List of tokens in the document.
     * @return scores a list containing the hamScore, followed by the spamScore.
     */
    public ArrayList<Double> getQuadramScores(List<String> tokens) {

        HashMap<String, Integer> spamDist = bayesMap.get("spam");
        HashMap<String, Integer> hamDist = bayesMap.get("ham");
        ArrayList<Double> scores = new ArrayList<>();

        double hamScore = 0;
        double spamScore = 0;

        for (int i = 0; i < tokens.size() - 3; i++) {
            String quadgram = tokens.get(i) + tokens.get(i + 1) + tokens.get(i + 2) + tokens.get(i + 3);
            spamScore += Math.log(spamDist.getOrDefault(quadgram, 1));
            hamScore += Math.log(hamDist.getOrDefault(quadgram, 1));
        }

        scores.add(hamScore);
        scores.add(spamScore);
        return scores;
    }
}
