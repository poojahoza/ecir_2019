package main.java.searcher;

import edu.unh.cs.lucene.TrecCarLuceneConfig;
import edu.unh.cs.treccar_v2.Data;
import main.java.containers.Container;
import main.java.containers.EntityContainer;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.FSDirectory;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.Map;

public class PageSearcher extends BaseSearcher {

    public PageSearcher(String indexLoc) throws IOException {

        super(indexLoc);
        searcher = new IndexSearcher(DirectoryReader.open(FSDirectory.open(Paths.get(indexLoc))));
        parser = new QueryParser("Title", new EnglishAnalyzer());
    }

    private void parseScoreDocs(ScoreDoc[] scoreDocs, String queryId) throws IOException
    {
        int ranking=1;
        for(ScoreDoc s:scoreDocs)
        {

            Document rankedDoc = searcher.doc(s.doc);
            String entityId = rankedDoc.getField("Id").stringValue();
            String outlinkIds = rankedDoc.getField("OutlinkIds").stringValue();
            String inlinkIds = rankedDoc.getField("InlinkIds").stringValue();
            //String leadText = rankedDoc.getField("LeadText").stringValue();
            System.out.println(entityId+" "+outlinkIds+" "+inlinkIds);

            /*String paraId = rankedDoc.getField("id").stringValue();
            String entity = rankedDoc.getField("entities").stringValue();*/


            //Container that holds all the information
            //Container c = new Container(s.score,ranking,s.doc);
            //c.addEntityContainer(new EntityContainer(entity));

            //createRankingQueryDocPair(queryId, paraId,c);
            ranking++;
        }
    }


    private void runRanking(Map<String,String> out)
    {
        for(Map.Entry<String,String> m:out.entrySet())
        {
            try
            {
                TopDocs topDocuments = null;
                try {
                    topDocuments = this.performSearch(m.getValue(),100);
                } catch (org.apache.lucene.queryparser.classic.ParseException e) {
                    e.printStackTrace();
                }
                ScoreDoc[] scoringDocuments = topDocuments.scoreDocs;
                System.out.println(scoringDocuments.length);
                this.parseScoreDocs(scoringDocuments, m.getKey());
            }
            catch (IOException io)
            {
                System.out.println(io.getMessage());
            }

        }
    }

    public void getRanking(Map<String,String> out)
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
