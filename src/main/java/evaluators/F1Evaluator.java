package main.java.evaluators;

import it.unimi.dsi.fastutil.Hash;

import java.util.HashMap;

public class F1Evaluator {

    private HashMap<String, String> correctLabels;

    public F1Evaluator(HashMap<String, String> trueLabels) {
        correctLabels = trueLabels;
    }

    public double evaluateCalledLabels(HashMap<String, String> calledLabels) {

        int tn = 0;
        int tp = 0;
        int fn = 0;
        int fp = 0;

        /* map must contain id => label */
        for (String id : calledLabels.keySet()) {
            String calledLabel = calledLabels.get(id);
            String correctLabel = correctLabels.get(id); // Will be "Ham" or "Spam"
            System.out.println("correct label: " + correctLabel);
            boolean isSpam = false;

            if (correctLabel.equals("Spam")) {
                isSpam = true;
            }

            if (isSpam && id.equals("Spam")) {
                tp++;
            }
            else if (isSpam && !calledLabel.equals("Spam")) {
                fn++;
            }
            else if (!isSpam && !calledLabel.equals("Spam")) {
                tn++;
            }
            else {
                fp++;
            }
        }

        double precision = (double)tp / (tp + fp);
        double recall = (double)tp / (tp + fn);
        double f1 = 2 * (precision * recall) / (precision + recall);
        double precision2 = (double)tn / (tn + fn);
        double recall2 = (double)tn / (tn + fp);
        double f2 = 2 * (precision2 * recall2) / (precision2 + recall2);
        return (f1 + f2)/2;
    }

}
