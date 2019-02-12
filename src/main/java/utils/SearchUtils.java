package main.java.utils;
import edu.unh.cs.treccar_v2.read_data.DeserializeData;
import edu.unh.cs.treccar_v2.Data;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.*;
import org.apache.lucene.store.FSDirectory;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class SearchUtils
{
    /**
     * Function: createIndexSearcher
     * Desc: Creates an IndexSearcher (responsible for querying a Lucene index directory).
     * @param indexLoc: Location of a Lucene index directory.
     * @return IndexSearcher
     */
    public static IndexSearcher createIndexSearcher(String indexLoc) {
        Path indexPath = Paths.get(indexLoc);
        IndexSearcher searcher = null;
        try {
            FSDirectory indexDir = FSDirectory.open(indexPath);
            DirectoryReader reader = DirectoryReader.open(indexDir);
            searcher = new IndexSearcher(reader);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return searcher;
    }


    /**
     * Function: createTokenList
     * Desc: Given a query string, chops it up into tokens and returns an array list of tokens.
     * @param queryString: String to be tokenized
     * @param analyzer: The analyzer responsible for parsing the string.
     * @return A list of tokens (Strings)
     */
    public static ArrayList<String> createTokenList(String queryString, Analyzer analyzer) {
        final ArrayList<String> tokens = new ArrayList<>();

        final StringReader stringReader = new StringReader(queryString);
        try {
            final TokenStream tokenStream = analyzer.tokenStream("text", stringReader);
            tokenStream.reset();
            while (tokenStream.incrementToken()) {
                tokens.add(tokenStream.getAttribute(CharTermAttribute.class).toString());
            }
            tokenStream.end();
            tokenStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return tokens;
    }

    /**
     * Function: createStandardBooleanQuery
     * Desc: Creates a boolean query (a bunch of terms joined with OR clauses) given a query string.
     *       Note: this is tokenized using the EnglishAnalyzer.
     *
     * @param queryString: Query string that will be tokenized into query terms.
     * @param termField: The document field that we will be searching against with our query terms.
     * @return
     */
    public static Query createStandardBooleanQuery(String queryString, String termField) {
        BooleanQuery.Builder builder = new BooleanQuery.Builder();
        ArrayList<String> tokens = createTokenList(queryString, new EnglishAnalyzer());

        for (String token : tokens) {
            Term term = new Term(termField, token);
            TermQuery termQuery = new TermQuery(term);
            builder.add(termQuery, BooleanClause.Occur.SHOULD);
        }

        return builder.build();
    }


    static public Map<String, String> readOutline(String filename) {
        Map<String, String> data = new LinkedHashMap<String, String>();

        FileInputStream qrelStream = null;
        try {
            qrelStream = new FileInputStream(new File(filename));
        } catch (FileNotFoundException fnf) {
            System.out.println(fnf.getMessage());
        }
        for (Data.Page page : DeserializeData.iterableAnnotations(qrelStream))
        {
            data.put(page.getPageId(), page.getPageName());
        }
        return data;
    }

    static public Map<String, String> readOutlineSectionPath(String filename) {
        Map<String, String> data = new LinkedHashMap<String, String>();

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
    }
}

