package main.java.predictors;

import edu.unh.cs.treccar_v2.Data;
import edu.unh.cs.treccar_v2.read_data.DeserializeData;

import java.io.*;
import java.util.HashMap;
import java.util.Iterator;

public class ClassifyUtils {

    public final HashMap<String, String> pidMap;
    public final HashMap<String, String> dump;

    public ClassifyUtils()
    {
        pidMap = new HashMap<>();
        dump = new HashMap<>();
    }


    private void extractText(Data.Paragraph p) throws IOException {

        /*for (String key: pidMap.keySet()) {
            System.out.println(key);
        }*/

        final String content = p.getTextOnly();
        final String paraId = p.getParaId();

        if (pidMap.get(paraId) != null) {
            System.out.println(paraId + '\t' + content);
            dump.put(paraId, content);
        }
    }


    /**
     * Desc: Iterate through the qrels file and map pids to ham/spam labels according to their annotations.
     * A pid of 2 indicates that a document is ham, whereas a pid of -1, -2, or -0 indicates that a
     * document is spam.
     *
     */
    public void parseRunfile() {

        BufferedReader reader = null;
        File runFile = new File("/home/rachel/grad_courses/data_science/extra_data");
        String line = null;

        try {
            reader = new BufferedReader(new FileReader(runFile));

            while ((line = reader.readLine()) != null) {

                String[] curLine = line.split("\\s+");
                if (curLine[2] != null) {
                    pidMap.put(curLine[2], "");
                    //System.out.println(curLine[2]);
                }
            }

        } catch (IOException | NullPointerException | ArrayIndexOutOfBoundsException e) {
            e.printStackTrace();
        }

    }


    /**
     * Desc: Iterate through the Lucene index and store all ids that are also found in the qrels.
     *
     */
    public void buildTrainTestSets() throws IOException {

        final FileInputStream fileInputStream2 = new FileInputStream(new File("/home/rachel/grad_courses/data_science/paragraphCorpus/dedup.articles-paragraphs.cbor"));
        final Iterator<Data.Paragraph> paragraphIterator = DeserializeData.iterParagraphs(fileInputStream2);
        for (int i = 1; paragraphIterator.hasNext(); i++) {
            extractText(paragraphIterator.next());
        }
        System.out.println("Finished iterating");
        write();
    }


    private void write() {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter("/home/rachel/grad_courses/data_science/dump", true));
            for (String key: dump.keySet()) {
                writer.write(key + '\t' + dump.get(key));
                writer.newLine();
            }
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

