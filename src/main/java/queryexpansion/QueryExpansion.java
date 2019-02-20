package main.java.queryexpansion;

import main.java.commandparser.RegisterCommands;
import main.java.searcher.BaseBM25;
import main.java.utils.PrintUtils;

import java.io.IOException;
import java.util.Map;

public class QueryExpansion
{
    private BaseBM25 bm25 = null;
    private RegisterCommands.CommandSearch searchcommand = null;
    private Map<String,String> query = null;


    public QueryExpansion(RegisterCommands.CommandSearch searchcommand, Map<String,String> query)
    {
        this.searchcommand =searchcommand;
        this.query = query;

        try {
            bm25 = new BaseBM25(searchcommand.getkVAL(),searchcommand.getIndexlocation());
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void doQueryExpansion()
    {
        PrintUtils.displayMap(bm25.getRanking(query));
    }

}
