package main.java.runner;

import main.java.commandparser.CommandParser;
import main.java.commandparser.RegisterCommands;
import main.java.commandparser.ValidateCommands;
//import main.java.reranker.ReRanker;
import main.java.containers.Container;
import main.java.searcher.BaseBM25;
import main.java.searcher.PageSearcher;
import main.java.utils.SearchUtils;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

/*
The searchParser object will hold all the information that is passed as the command line argument.
There are helper methods to get the data.
 */
public class SearchRunner implements ProgramRunner
{
    private RegisterCommands.CommandSearch searchParser = null;
    private ValidateCommands.ValidateSearchCommands validate = null;

    public SearchRunner(CommandParser parser)
    {
        searchParser = parser.getSearchCommand();
        validate = new ValidateCommands.ValidateSearchCommands(searchParser);
    }

    @Override
    public void run()
    {
        Map<String,String> queryCBOR = SearchUtils.readOutline(searchParser.getQueryfile());

        /*if(searchParser.isReRankEnabled())
        {
            validate.ValidateReRank();
            //ReRanker re = new ReRanker(searchParser,queryCBOR);
            //re.ReRank();
        }*/
        try {
            BaseBM25 bm25 = new BaseBM25(100, searchParser.getIndexlocation());
            //System.out.println("inside search");
            Map<String, Map<String, Container>> initial_ranking = bm25.getRanking(queryCBOR);
            //System.out.println(initial_ranking);
            /*for(Map.Entry<String,Map<String, Container>> m:initial_ranking.entrySet()){
                for(Map.Entry<String, Container> para: m.getValue().entrySet()){
                    System.out.println(m.getKey()+" "+para.getKey()+" "+para.getValue().getDocID()+" "+para.getValue().getEntity());
                }
            }*/
            Map<String, String> trial = new LinkedHashMap<>();
            trial.put("3408d2060be42e03b41acda0351fac1e22a7d284", "New York City");
            PageSearcher ps = new PageSearcher("/home/poojaoza/Documents/projects/cs953-team1/entity_indexed_file/");
            ps.getRanking(trial);
        }catch (IOException ioe){
            System.out.println(ioe.getMessage());
        }
    }
}
