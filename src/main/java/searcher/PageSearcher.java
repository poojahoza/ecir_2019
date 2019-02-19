package main.java.searcher;

import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.FSDirectory;

import org.jgrapht.Graph;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import main.java.graph.GraphDegreeSearcher;
import main.java.utils.SortUtils;

public class PageSearcher extends BaseSearcher {

    HashMap<String, String> entity_outlinks = new LinkedHashMap<>();
    GraphDegreeSearcher graph = null;

    public PageSearcher(String indexLoc) throws IOException {

        super(indexLoc);
        searcher = new IndexSearcher(DirectoryReader.open(FSDirectory.open(Paths.get(indexLoc))));
        parser = new QueryParser("Title", new EnglishAnalyzer());
        graph = new GraphDegreeSearcher();
    }

    private void createEntityOutlinksPair(String entity_id, String entity_val){
        entity_outlinks.put(entity_id, entity_val);
    }

    private void parseScoreDocs(ScoreDoc[] scoreDocs, String entity_id) throws IOException
    {
        int ranking=1;
        for(ScoreDoc s:scoreDocs)
        {

            Document rankedDoc = searcher.doc(s.doc);
            String entityId = rankedDoc.getField("Id").stringValue();
            if(entity_id.equals(entityId)) {

                String outlinkIds = rankedDoc.getField("OutlinkIds").stringValue();

                this.createEntityOutlinksPair(entityId, outlinkIds);
            }
            ranking++;
        }
    }


    private Map<String, Map<String, Integer>> runRanking(Map<String, Map<String, String>> out)
    {
        Map<String, Map<String, Integer>> query_entity_degree = new LinkedHashMap<>();
        for(Map.Entry<String, Map<String, String>> m:out.entrySet())
        {
            entity_outlinks.clear();
            for(Map.Entry<String, String> n:m.getValue().entrySet()) {
                try {
                    TopDocs topDocuments = null;
                    try {
                        if(!n.getValue().equals("")) {
                            topDocuments = this.performSearch(n.getValue(), 100);
                        }
                    } catch (org.apache.lucene.queryparser.classic.ParseException e) {
                        System.out.println("The query was not parsed");
                        //e.printStackTrace();
                    }
                    try {
                        ScoreDoc[] scoringDocuments = topDocuments.scoreDocs;
                        this.parseScoreDocs(scoringDocuments, n.getKey());
                    }catch (NullPointerException npe){
                        System.out.println("No matching documents found "+n.getKey());
                        //npe.printStackTrace();
                    }
                } catch (IOException io) {
                    System.out.println(io.getMessage());
                }
            }
            Graph g = graph.generateGraph(entity_outlinks);
            Map<String, Integer> degree_list = graph.getNodeDegree(g);
            degree_list = SortUtils.sortByValue(degree_list);

            query_entity_degree.put(m.getKey(), degree_list);
        }
        return query_entity_degree;
    }

    public Map<String, Map<String, Integer>> getRanking(Map<String, Map<String, String>> out)
    {
        Map<String, Map<String, Integer>> ranked_entities = this.runRanking(out);
        return ranked_entities;
    }
}
