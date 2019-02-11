package main.java.reranker;

import main.java.commandparser.RegisterCommands;
import main.java.containers.Container;
import main.java.searcher.BaseBM25;
import main.java.utils.StopWord;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/*
Document Re-ranker based on the document similarity. This is naive approach.
*/

public class ReRanker
{
    private BaseBM25 bm25 = null;

    private RegisterCommands.CommandSearch SearchCommand= null;
    private Map<String,String> query =null;
    private  ReRankUtils RankUtil = null;

    public ReRanker(RegisterCommands.CommandSearch SearchCommand, Map<String,String> query)
    {
        this.SearchCommand = SearchCommand;
        this.query = query;
        try {
            this.bm25 = new BaseBM25(SearchCommand.getkVAL(),SearchCommand.getIndexlocation());
        } catch (IOException e) {
            e.printStackTrace();
        }
        RankUtil = new ReRankUtils(bm25,SearchCommand.getWordEmbeddingFile(),SearchCommand.getDimension());
    }


    public void ReRank()
    {

//       Map<String,Map<String, Container>>  initialRet = bm25.getRanking(query);
//
//       int count=0;
//       for( Map.Entry<String,Map<String, Container>>  outer : initialRet.entrySet())
//       {
//           System.out.println(outer.getKey());
//           count++;
//           RankUtil.getReRank(outer.getValue());
//           if(count ==1) break;
//       }

        int count=0;
        for(Map.Entry<String,String> ini: query.entrySet())
        {
            count++;
            System.out.println("QID" + ini.getKey()+" "+ini.getValue());
            Map<String,Container> temp = bm25.getRanking(ini.getValue());

            for(Map.Entry<String,Container> val: temp.entrySet())
            {
                System.out.println(val.getKey()+" "+ val.getValue().getRanking()+" "+val.getValue().getDocID()+" "+val.getValue().getScore());
            }

            if(count==1) break;
        }



    }




}
