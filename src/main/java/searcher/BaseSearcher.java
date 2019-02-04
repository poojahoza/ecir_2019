package main.java.searcher;

import edu.unh.cs.treccar_v2.Data;
import main.java.utils.IndexUtils;
import main.java.utils.SearchUtils;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.document.Document;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Paths;
import main.java.utils.Constants;
import java.util.Map;
import java.util.LinkedHashMap;
import java.nio.file.Path;
import java.io.File;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.List;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;

public class BaseSearcher {
    public final IndexSearcher searcher;
    public final Iterable<Data.Page> pages;

    public BaseSearcher(String indexLoc, String queryCborLoc) throws IOException{
        searcher = SearchUtils.createIndexSearcher(indexLoc);
        pages = IndexUtils.createPageIterator(queryCborLoc);
    }

    // 5) Refactored performSearch. Renamed it to query, renamed variables to be more descriptive, and surrounded it in try catch.
    /**
     * Function: query
     * Desc: Queries Lucene paragraph corpus using a standard similarity function.
     *       Note that this uses the EnglishAnalyzer.
     * @param queryString: The query string that will be turned into a boolean query.
     * @param nResults: How many search results should be returned
     * @return TopDocs (ranked results matching query)
     */
    private TopDocs query(String queryString, Integer nResults) {
        Query q = SearchUtils.createStandardBooleanQuery(queryString, "text");
        try {
            return searcher.search(q, nResults);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Function: run
     * Desc: Creates the run file for the given cbor outline file.
     */
    public void run() throws IOException {
        FileWriter fstream = new FileWriter("standard_bm25.run", false);
        BufferedWriter out = new BufferedWriter(fstream);

        for (Data.Page page : pages) {

            // Id of the page, which is needed when the run file is printed.
            String pageId = page.getPageId();

            // Name of the page, also needed when the run file is printed.
            String query = page.getPageName();
            ArrayList<IdScore> idSc = performSearch(query);

            int counter = 1;
            for (IdScore item : idSc) {
                out.write(pageId + " Q0 " + item.i + " " + counter + " " + item.s + " team1-english\n");
                counter++;
            }
        }
        out.close();
    }

    /**
     * Function: run
     * Desc: Creates the run file for the given cbor outline file.
     */
    public void sectionOutlineRun() throws IOException {
        FileWriter fstream = new FileWriter("standard_bm25.run", false);
        BufferedWriter out = new BufferedWriter(fstream);

        for (Data.Page page : pages) {

            // Id of the page, which is needed when the run file is printed.
            String pageId = page.getPageId();

            // Name of the page, also needed when the run file is printed.
            String query = page.getPageName();
            ArrayList<IdScore> idSc = performSearch(query);

            int counter = 1;
            for (IdScore item : idSc) {
                out.write(pageId + " Q0 " + item.i + " " + counter + " " + item.s + " team1-english\n");
                counter++;
            }
        }
        out.close();

            /*Map<String, String> data = new LinkedHashMap<String, String>();

            FileInputStream qrelStream = null;
            try {
                qrelStream = new FileInputStream(new File(filename));
            } catch (FileNotFoundException fnf) {
                System.out.println(fnf.getMessage());

            }
            for (Data.Page page : DeserializeData.iterableAnnotations(qrelStream))
            {
                StringBuilder queryBuilder = new StringBuilder();

                for (List<Data.Section> sectionPath : page.flatSectionPaths())
                {
                    queryBuilder.append(" ");
                    queryBuilder.append(page.getPageName());
                    for(Data.Section sec:sectionPath)
                    {
                        queryBuilder.append(" ");
                        queryBuilder.append(sec.getHeading().replaceAll("[^\\w\\s]",""));
                    }
                    //queryBuilder.append(String.join(" ", Data.sectionPathHeadings(sectionPath)).replaceAll("[^\\w\\s]",""));
                }
                //System.out.println(queryBuilder.toString());
                data.put(page.getPageId(), queryBuilder.toString());
            }
            return data;
        }*/
    }

    // 6) Added a custom class to store Id-Score pairs.
    /**
     * Class: IdScore
     * Desc: Custom class for storing the retrieved data.
     */
    public class IdScore {
        String i;
        float s;

        IdScore(String id, float score) {
            i = id;
            s = score;
        }
    }

    /**
     * Function: parseTopDocs
     * Desc: Iterate through the search results from the TopDocs and return
     *       an ArrayList with the ids and scores of the top results of
     *       the query.
     * @param topDocs: The query string that will be turned into a boolean query.
     * @return ArrayList<IdScore> (ranked results matching query)
     */
    private ArrayList<IdScore> parseTopDocs(TopDocs topDocs) throws IOException {
        ArrayList<IdScore> al = new ArrayList<>();
        for (ScoreDoc sd : topDocs.scoreDocs) {
            Document doc = searcher.doc(sd.doc);
            String paraId = doc.get("id");
            float score = sd.score;
            IdScore cur = new IdScore(paraId, score);
            al.add(cur);
        }
        return al;
    }

    /**
     * Function: performSearch
     * Desc: Gets the scores of the top 100 documents for the given query.
     * @param query: The query string that will be turned into a boolean query.
     * @return ArrayList: The top 100 results for this query.
     */
    public ArrayList<IdScore> performSearch(String query) throws IOException {
        TopDocs topDocs = query(query, 100);
        assert topDocs != null;
        return parseTopDocs(topDocs);
    }

    /**
     * Function: performSearch
     * Desc: Overloaded version to take a Query as a parameter instead of a String.
     * @param q: The query object that will be turned into a boolean query.
     * @return ArrayList: The top 100 results for this query.
     */
    public ArrayList<IdScore> performSearch(Query q) throws IOException {
        TopDocs topDocs = searcher.search(q, 100);
        return parseTopDocs(topDocs);
    }

}
