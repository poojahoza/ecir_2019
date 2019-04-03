package main.java.svm;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class DemoPythonSVM {

    public static void main (String[] args) throws IOException {

        HashMap<String, HashMap<String, Integer>> freqMap = new HashMap<>();
        ArrayList<String> words = new ArrayList<>();
        PythonSVM pythonSVM = new PythonSVM(freqMap, words);


        /* Data preprocessing stage. The train set is already created on the server, so all
            you'll have to do is call ReadIndex on your test set. Data mush be in the form
            pid -> text.
         */
        ArrayList<HashMap<Integer, Double>> spamTrain = pythonSVM.readIndex("/home/rachel/grad_courses/data_science/spamTrain");
        ArrayList<HashMap<Integer, Double>> hamTrain = pythonSVM.readIndex("/home/rachel/grad_courses/data_science/hamTrain");
        ArrayList<HashMap<Integer, Double>> test = pythonSVM.readIndex("/home/rachel/grad_courses/data_science/hamSpamTest");

        String trainPath = "/home/rachel/grad_courses/data_science/train_data.csv";
        String testPath = "/home/rachel/grad_courses/data_science/test_data.csv";
        pythonSVM.prepareData(spamTrain, hamTrain, test, trainPath, testPath);

        /* After prepreoceesing the data, you can call the following to train your model and return
            class labels. 1 means the document is a ham, and -1 means it is a spam.
         */
        ArrayList<String> labels = pythonSVM.execLinearSVC();
        System.out.println("Finished classifying, printing labels");
        //System.out.println(labels.size());
        for (int i = 0; i < labels.size(); i++) {
            System.out.println(labels.get(i));
        }

    }

}
