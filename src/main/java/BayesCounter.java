package main.java;

import main.java.evaluators.F1Evaluator;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import weka.core.stopwords.StopwordsHandler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static main.java.utils.SearchUtils.createTokenList;
import static weka.core.Stopwords.isStopword;

public class BayesCounter {

    /**
     * Makes a new BayesCounter with empty hash map.
     */
    public final HashMap<String, HashMap<String, Integer>> bayesMap;
    public final HashMap<String, ArrayList<Double>> stopWordMap;

    public BayesCounter() {
        bayesMap = new HashMap<>();
        stopWordMap = new HashMap<>();
    }

    public void evaluateStopCoverPredictor(HashMap<String, String> spamTest, HashMap<String, String> hamTest, HashMap<String, String> docs) {

        HashMap <String, String> calledLabels = new HashMap<>();
        HashMap <String, String> trueLabels = new HashMap<>();

        // For each document, call the predict method. Store the pid with its prediction in the calledLabels map
        for (String key: docs.keySet()) {
            String text = docs.get(key);
            List<String> tokens = createTokenList(text, new EnglishAnalyzer());
            String label = this.classifyWithStopCover(tokens);
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


    public void buildStopWordHashMap(String docClass, List<String> tokens) {

        if (stopWordMap.get(docClass) == null) {
            ArrayList<Double> classList = new ArrayList<>();
            stopWordMap.put(docClass, classList);
        }

        ArrayList<Double> curList = stopWordMap.get(docClass);
        // Get the stop coverage for each document in the ham and spam sets.
        double stop = 0;
        double total = 0;

        for (String token : tokens) {
            if (isStopword(token)) {
                stop++;
            }
            total++;
        }

        double result = (stop / total) * 100;
        curList.add(result);
    }

    /**
     * Desc: Parse the tokens of the document passed as a parameter and get the number of stopwords and non stopwords.
     *       This function is for evaluation and should therefore be called on the evaluation set of emails.
     *
     * @param tokens List of tokens in the document.
     * @return The document class with the larger count.
     */
    public String classifyWithStopCover(List<String> tokens) {

        ArrayList<Double> spamDist = stopWordMap.get("spam");
        ArrayList<Double> hamDist = stopWordMap.get("ham");

        // Get the average number of stopwords for spam and ham training
        double sum = 0;
        for (int i = 0; i < spamDist.size(); i++) {
            sum += spamDist.get(i);
        }
        double spamAverage = sum/spamDist.size();

        sum = 0;
        for (int i = 0; i < hamDist.size(); i++) {
            sum += hamDist.get(i);
        }
        double hamAverage = sum/hamDist.size();

        double stop = 0;
        double total = 0;

        for (String token : tokens) {
            if (isStopword(token)) {
                stop++;
            }
            total++;
        }

        double result = (stop / total) * 100;

        if (Math.abs(spamAverage - result) < Math.abs(hamAverage - result)) {
            return "ham";
        }
        else {
            return "spam";
        }
    }

    public ArrayList<Double> getStopCoverScores(List<String> tokens) {

        HashMap<String, Integer> spamDist = bayesMap.get("spam");
        HashMap<String, Integer> hamDist = bayesMap.get("ham");
        ArrayList<Double> scores = new ArrayList<>();

        double hamScore = 0;
        double spamScore = 0;

        for (String token : tokens) {
            //String quadgram = tokens.get(i) + tokens.get(i + 1) + tokens.get(i + 2) + tokens.get(i + 3);
            spamScore += Math.log(spamDist.getOrDefault(token, 1));
            hamScore += Math.log(hamDist.getOrDefault(token, 1));
        }

        scores.add(hamScore);
        scores.add(spamScore);
        return scores;
    }
}
