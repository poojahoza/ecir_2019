package main.java.searcher;

import main.java.containers.Container;
import main.java.containers.EntityContainer;
import org.apache.lucene.document.Document;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

public class BaseBM25 extends BaseSearcher
{
    private Map<String, Map<String, Container>> ranks=null;
    private int k;

     BaseBM25() throws IOException
    {
        super();
        k=100;
        if(ranks==null) this.ranks= new LinkedHashMap<String, Map<String, Container>>();
    }

    public BaseBM25(int k) throws IOException
    {
        this();
        this.k=k;
    }


    private void createRankingQueryDocPair(String outer_key, String inner_key, Container rank)
    {
        if(ranks.containsKey(outer_key))
        {
            Map<String, Container> extract = ranks.get(outer_key);
            extract.put(inner_key, rank);
        }
        else
        {
            Map<String,Container> temp = new LinkedHashMap<>();
            temp.put(inner_key, rank);
            ranks.put(outer_key,temp);
        }
    }

    private void parseScoreDocs(ScoreDoc[] scoreDocs, String queryId) throws IOException
    {
        int ranking=1;
        for(ScoreDoc s:scoreDocs)
        {
            Document rankedDoc = searcher.doc(s.doc);
            String paraId = rankedDoc.getField("id").stringValue();
            String entity = rankedDoc.getField("entities").stringValue();

            //Container that holds all the information
            Container c = new Container(s.score,ranking,s.doc);
            c.addEntityContainer(new EntityContainer(entity));

            createRankingQueryDocPair(queryId, paraId,c);
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
                    topDocuments = this.performSearch(m.getValue(),this.k);
                } catch (org.apache.lucene.queryparser.classic.ParseException e) {
                    e.printStackTrace();
                }
                ScoreDoc[] scoringDocuments = topDocuments.scoreDocs;
                this.parseScoreDocs(scoringDocuments, m.getKey());
            }
            catch (IOException io)
            {
                System.out.println(io.getMessage());
            }

        }
    }

    public Map<String, Map<String, Container>> getRanking(Map<String,String> out)
    {
        if(ranks == null)
        {
            this.runRanking(out);
        }
        else {
            ranks.clear();
            this.runRanking(out);
        }
        return ranks;
    }

    public String getDocument(int docID)
    {
        String docString=null;
        try
        {
            Document rankedDoc = searcher.doc(docID);
            docString = rankedDoc.getField("text").stringValue();
        }
        catch (IOException io)
        {
            System.out.println(io.getMessage());
        }
        return docString;
    }



}
