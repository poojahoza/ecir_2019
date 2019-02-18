package main.java.runner;

import main.java.commandparser.CommandParser;
import main.java.commandparser.RegisterCommands;
import main.java.commandparser.ValidateCommands;
import main.java.containers.Container;
import main.java.reranker.ReRanker;
import main.java.searcher.BaseBM25;
import main.java.searcher.PageSearcher;
import main.java.utils.Entities;
import main.java.utils.SearchUtils;
import main.java.utils.WriteFile;

import java.io.IOException;
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
        //Read the outline file in to Map
        Map<String,String> queryCBOR = SearchUtils.readOutline(searchParser.getQueryfile());

        //parse based on th options
        if(searchParser.isReRankEnabled())
        {
            validate.ValidateReRank();
            ReRanker re = new ReRanker(searchParser,queryCBOR);
            re.ReRank();
        }

        if(searchParser.isEntityDegreeEnabled()){
            validate.ValidateEntityDegree();
            try {
                Map<String,String> querysecCBOR = SearchUtils.readOutlineSectionPath(searchParser.getQueryfile());

                BaseBM25 bm25 = new BaseBM25(100, searchParser.getIndexlocation());
                Map<String, Map<String, Container>> bm25_ranking = bm25.getRanking(querysecCBOR);

                WriteFile write_file = new WriteFile();
                write_file.generateBM25RunFile(bm25_ranking, "BM25");

                Entities e = new Entities();
                Map<String, Map<String, String>> query_ent_list = e.getEntitiesPerQuery(bm25_ranking);

                PageSearcher pgs = new PageSearcher(searchParser.getEntityIndLoc());
                Map<String, Map<String, Integer>> ranked_entities = pgs.getRanking(query_ent_list);

                ranked_entities = e.getParagraphsScore(bm25_ranking, ranked_entities);
                ranked_entities = e.getRerankedParas(ranked_entities);
                System.out.println(ranked_entities);


                write_file.generateEntityRunFile(ranked_entities, "entityDegree");

            }catch (IOException ioe){
                System.out.println(ioe.getMessage());
            }
        }


    }
}
