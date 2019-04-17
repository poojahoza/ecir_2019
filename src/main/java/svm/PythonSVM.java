package main.java.svm;

import au.com.bytecode.opencsv.CSVWriter;
import org.apache.lucene.analysis.en.EnglishAnalyzer;

import java.io.*;
import java.util.*;

import static main.java.utils.SearchUtils.createTokenList;

public class PythonSVM extends SVM {
    ;
    private HashMap<String, HashMap<String, Integer>> freqMap = new LinkedHashMap<>();
    private ArrayList<String> words = new ArrayList<>();

    public PythonSVM(HashMap<String, HashMap<String, Integer>> freqMap, ArrayList<String> words) {
        super(freqMap, words);
    }

    public ArrayList<HashMap<Integer, Double>> readIndex(String path) {

        BufferedReader reader = null;
        HashMap<String, String> lines = new HashMap<>();
        File f = new File(path);

        String line = null;

        try {
            reader = new BufferedReader(new FileReader(f));
            while ((line = reader.readLine()) != null) {
                String[] curLine = line.split("\\t+");
                String pid = curLine[0].trim();
                String test = curLine[1].trim();
                lines.put(pid, test);
            }
        } catch (IOException | NullPointerException e) {
            e.printStackTrace();
        }

        return parse(lines);
    }


    /**
     * Desc: Put the data into a form that the SVM can read and use.
     *
     * @param corpus the training or test set of spam or ham documents.
     */
    public ArrayList<HashMap<Integer, Double>> parse(HashMap<String, String>corpus) {

        ArrayList <HashMap<String, String>> data = new ArrayList<>();

        // Put the data into a hash map, where the key is the term, and the value is the term frequency.
        for (String key : corpus.keySet()) {
            String text = corpus.get(key);
            ArrayList<String> tokens = createTokenList(text, new EnglishAnalyzer());
            buildHashMap(key, tokens);
        }

        // Make an item in a list for each pid from the freqMap. Each item is a hashmap of the feature vectors of that pid.
        // where the key is the term's unique id, and the value is the term frequency.
        ArrayList<HashMap<Integer, Double>> featureVectorList = new ArrayList<>();
        for (String key: freqMap.keySet()) {
            HashMap<Integer, Double> featureVector = createFeatureVectors(key);
            featureVectorList.add(featureVector);
        }

        return featureVectorList;
    }

    public void prepareData(ArrayList<HashMap<Integer, Double>> hamData, ArrayList<HashMap<Integer, Double>> spamData, ArrayList<HashMap<Integer, Double>> test, String trainPath, String testPath) {

        File file = new File(trainPath);


        try {
            FileWriter outputfile = new FileWriter(file);

            CSVWriter writer = new CSVWriter(outputfile);
            List<String[]> data = new ArrayList<>();
            String[] headers = new String[words.size() + 1];

            for (int i = 1; i < words.size(); i++) {
                headers[i] = Integer.toString(i);
            }
            headers[headers.length - 1] = "Class";
            data.add(headers);

            for (int i = 0; i < hamData.size(); i++) {
                HashMap<Integer, Double> curMap = hamData.get(i);
                TreeMap<Integer, Double> sorted = new TreeMap<>(curMap);

                String[] curArray = new String[words.size() + 1]; //66079 size array
                for (int j = 0; j < words.size(); j++) {
                    if (sorted.get(j) != null) {
                        curArray[j] = sorted.get(j).toString();
                    }
                    else {
                        curArray[j] = "0";
                    }

                }
                curArray[curArray.length - 1] = "1";
                data.add(curArray);
            }

            for (int i = 0; i < spamData.size(); i++) {
                HashMap<Integer, Double> curMap = spamData.get(i);
                TreeMap<Integer, Double> sorted = new TreeMap<>(curMap);

                String[] curArray = new String[words.size() + 1];
                for (int j = 0; j < words.size(); j++) {
                    if (sorted.get(j) != null) {
                        curArray[j] = sorted.get(j).toString();
                    }
                    else {
                        curArray[j] = "0";
                    }

                }
                curArray[curArray.length - 1] = "-1";
                data.add(curArray);
            }

            writer.writeAll(data);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        File file2 = new File(testPath);

        try {
            // Same idea as the train data, without the labels column at the end.
            FileWriter outputfile2 = new FileWriter(file2);

            CSVWriter writer = new CSVWriter(outputfile2);
            List<String[]> data = new ArrayList<>();
            String[] headers = new String[words.size()];

            for (int i = 1; i < words.size(); i++) {
                headers[i] = Integer.toString(i);
            }

            for (int i = 0; i < test.size(); i++) {
                HashMap<Integer, Double> curMap = test.get(i);
                TreeMap<Integer, Double> sorted = new TreeMap<>(curMap);

                String[] curArray = new String[words.size()];
                for (int j = 0; j < words.size(); j++) {
                    if (sorted.get(j) != null) {
                        curArray[j] = sorted.get(j).toString();
                    }
                    else {
                        curArray[j] = "0";
                    }
                }
                data.add(curArray);
            }
            writer.writeAll(data);
            writer.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


        public ArrayList<String> execLinearSVC() throws IOException {

        String s = null;
        ArrayList<String> labels = new ArrayList<>();

        try {
            Process p = Runtime.getRuntime().exec("python3 /home/rachel/grad_courses/data_science/cs953-team1/svm.py");

            BufferedReader stdInput = new BufferedReader(new InputStreamReader(p.getInputStream()));

            BufferedReader stdError = new BufferedReader(new InputStreamReader(p.getErrorStream()));

            while ((s = stdInput.readLine()) != null) {
                //System.out.println(s);
                labels.add(s);
            }

            while ((s = stdError.readLine()) != null) {
                System.out.println(s);
            }

        } catch (IOException e) {
            System.out.println("Exception: ");
            e.printStackTrace();
            System.exit(-1);
        }

        return labels;
    }

    public ArrayList<String> execRbfSVC() throws IOException {

        String s = null;
        ArrayList<String> labels = new ArrayList<>();

        try {
            Process p = Runtime.getRuntime().exec("python3 /home/rachel/grad_courses/data_science/cs953-team1/rbf_svm.py");

            BufferedReader stdInput = new BufferedReader(new InputStreamReader(p.getInputStream()));

            BufferedReader stdError = new BufferedReader(new InputStreamReader(p.getErrorStream()));

            while ((s = stdInput.readLine()) != null) {
                //System.out.println(s);
                labels.add(s);
            }

            while ((s = stdError.readLine()) != null) {
                System.out.println(s);
            }

        } catch (IOException e) {
            System.out.println("Exception: ");
            e.printStackTrace();
            System.exit(-1);
        }

        return labels;
    }


    public ArrayList<String> execPolynomialSVC() throws IOException {

        String s = null;
        ArrayList<String> labels = new ArrayList<>();

        try {
            Process p = Runtime.getRuntime().exec("python3 /home/rachel/grad_courses/data_science/cs953-team1/polynomial_svm.py");

            BufferedReader stdInput = new BufferedReader(new InputStreamReader(p.getInputStream()));

            BufferedReader stdError = new BufferedReader(new InputStreamReader(p.getErrorStream()));

            while ((s = stdInput.readLine()) != null) {
                //System.out.println(s);
                labels.add(s);
            }

            while ((s = stdError.readLine()) != null) {
                System.out.println(s);
            }

        } catch (IOException e) {
            System.out.println("Exception: ");
            e.printStackTrace();
            System.exit(-1);
        }

        return labels;
    }


    public ArrayList<String> execSigmoidSVC() throws IOException {

        String s = null;
        ArrayList<String> labels = new ArrayList<>();

        try {
            Process p = Runtime.getRuntime().exec("python3 /home/rachel/grad_courses/data_science/cs953-team1/sigmoid_svm.py");

            BufferedReader stdInput = new BufferedReader(new InputStreamReader(p.getInputStream()));

            BufferedReader stdError = new BufferedReader(new InputStreamReader(p.getErrorStream()));

            while ((s = stdInput.readLine()) != null) {
                //System.out.println(s);
                labels.add(s);
            }

            while ((s = stdError.readLine()) != null) {
                System.out.println(s);
            }

        } catch (IOException e) {
            System.out.println("Exception: ");
            e.printStackTrace();
            System.exit(-1);
        }

        return labels;
    }



    /**
     *  For each paragraph, builds a hash map of tokens to their term frequency.
     */
    public void buildHashMap(String pid, ArrayList<String> tokens) {

        HashMap<String, Integer> curMap = new HashMap<>();

        for (String token : tokens) {
            if (!curMap.containsKey(token)) {
                curMap.put(token, 1);
                words.add(token);
            }
            else {
                int curCount = curMap.get(token);
                curMap.put(token, curCount + 1);
            }
        }
        freqMap.put(pid, curMap);
    }


    /**
     *  Combine information from the list and the hashmap to get a hash map of feature vectors with id => count
     */
    public HashMap<Integer, Double> createFeatureVectors(String pid) {

        HashMap<Integer, Double> featureVectors = new HashMap<>();
        HashMap<String, Integer> curMap = freqMap.get(pid);
        for (int i = 0; i < words.size(); i++) {
            if (curMap.get(words.get(i)) != null) {
                featureVectors.put(i, (curMap.get(words.get(i)).doubleValue()));
            }
        }

        return featureVectors;
    }

}
