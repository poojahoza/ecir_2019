package main.java.predictors;

import org.apache.lucene.queryparser.classic.ParseException;

import java.io.IOException;
import java.util.HashMap;

public class SpamClassifierDemo {

    public static void main (String [] args) throws IOException, ParseException {

        SpamClassifier sc = new SpamClassifier();
        HashMap<String, String> hamTrain = sc.readIndex("/home/rachel/grad_courses/data_science/hamTrain");
        HashMap<String, String> spamTrain = sc.readIndex("/home/rachel/grad_courses/data_science/spamTrain");
        HashMap<String, String> test = sc.readIndex("/home/rachel/grad_courses/data_science/hamSpamTest");

        HashMap<String, String> spamTest = sc.readIndex("/home/rachel/grad_courses/data_science/spamTest");
        HashMap<String, String> hamTest = sc.readIndex("/home/rachel/grad_courses/data_science/hamTest");

        LabelPredictor unigramsPredictor = sc.classifyWithUnigrams(spamTrain, hamTrain);
        HashMap<String, String> labels = sc.predict(unigramsPredictor, test);

       for (String key : labels.keySet()) {
           System.out.println(key + '\t' + labels.get(key));
       }

        LabelPredictor bigramsPredictor = sc.classifyWithBigrams(spamTrain, hamTrain);
        LabelPredictor trigramsPredictor = sc.classifyWithTrigrams(spamTrain, hamTrain);
        LabelPredictor quadgramsPredictor = sc.classifyWithQuadgrams(spamTrain, hamTrain);
        LabelPredictor stopCoverPredictor = sc.classifyWithStopCover(spamTrain, hamTrain);
        LabelPredictor fracStopsPredictor = sc.classifyWithFracStops(spamTrain, hamTrain);

        System.out.println("\n++++++++ UNIGRAMS ++++++++");
        unigramsPredictor.evaluate(spamTest, hamTest, test);

        System.out.println("\n++++++++ BIGRAMS ++++++++");
        bigramsPredictor.evaluate(spamTest, hamTest, test);

        System.out.println("\n++++++++ TRIGRAMS ++++++++");
        trigramsPredictor.evaluate(spamTest, hamTest, test);

        System.out.println("\n++++++++ QUADGRAMS ++++++++");
        quadgramsPredictor.evaluate(spamTest, hamTest, test);

        System.out.println("\n++++++++ STOP COVERAGE ++++++++");
        stopCoverPredictor.evaluate(spamTest, hamTest, test);

        System.out.println("\n++++++++ FRAC STOPS ++++++++");
        fracStopsPredictor.evaluate(spamTest, hamTest, test);

    }
}
