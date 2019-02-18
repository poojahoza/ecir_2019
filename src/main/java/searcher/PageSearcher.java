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

import main.java.graph.GraphGenerator;
import main.java.utils.SortUtils;

public class PageSearcher extends BaseSearcher {

    HashMap<String, String> entity_outlinks = new LinkedHashMap<>();
    GraphGenerator graph = null;

    public PageSearcher(String indexLoc) throws IOException {

        super(indexLoc);
        searcher = new IndexSearcher(DirectoryReader.open(FSDirectory.open(Paths.get(indexLoc))));
        parser = new QueryParser("Title", new EnglishAnalyzer());
        graph = new GraphGenerator();
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
                String inlinkIds = rankedDoc.getField("InlinkIds").stringValue();
                String leadText = rankedDoc.getField("LeadText").stringValue();
             //   System.out.println("------------------------");
                //System.out.println(entityId + " " + outlinkIds + " " + inlinkIds);

            /*String paraId = rankedDoc.getField("id").stringValue();
            String entity = rankedDoc.getField("entities").stringValue();*/


                //Container that holds all the information
                //Container c = new Container(s.score,ranking,s.doc);
                //c.addEntityContainer(new EntityContainer(entity));

                //createRankingQueryDocPair(queryId, paraId,c);
                this.createEntityOutlinksPair(entityId, outlinkIds);
            }
            ranking++;
        }
    }


    private void runRanking(Map<String, Map<String, String>> out)
    {
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
                        e.printStackTrace();
                    }
                    try {
                        ScoreDoc[] scoringDocuments = topDocuments.scoreDocs;
                        this.parseScoreDocs(scoringDocuments, n.getKey());
                    }catch (NullPointerException npe){
                        npe.printStackTrace();
                    }
                } catch (IOException io) {
                    System.out.println(io.getMessage());
                }
            }
            Graph g = graph.generateGraph(entity_outlinks);
            Map<String, Integer> degree_list = graph.getNodeDegree(g);
            degree_list = SortUtils.sortByValue(degree_list);

        }
    }

    public void getRanking(Map<String, Map<String, String>> out)
    {
        this.runRanking(out);
        /*if(ranks == null)
        {
            this.runRanking(out);
        }
        else {
            ranks.clear();
            this.runRanking(out);
        }
        return ranks;*/
    }
}
