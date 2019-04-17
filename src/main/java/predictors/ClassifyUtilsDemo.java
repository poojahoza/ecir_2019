package main.java.predictors;

import java.io.IOException;
import java.util.HashMap;

public class ClassifyUtilsDemo {

    public static void main(String [] args) throws IOException {
        ClassifyUtils classifyUtils = new ClassifyUtils();
        classifyUtils.parseRunfile();
        classifyUtils.buildTrainTestSets();

    }
}
