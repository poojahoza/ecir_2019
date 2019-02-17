package main.java.runner;

import main.java.commandparser.CommandParser;
import main.java.commandparser.RegisterCommands;
import main.java.commandparser.ValidateCommands;
import main.java.containers.Container;
import main.java.reranker.ReRanker;
import main.java.searcher.BaseBM25;
import main.java.utils.RunWriter;
import main.java.utils.SearchUtils;
import org.nd4j.shade.jackson.databind.ser.Serializers;

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
    public void run()  {
        //Read the outline file in to Map
        Map<String,String> queryCBOR = SearchUtils.readOutline(searchParser.getQueryfile());

        //parse based on th options
        if(searchParser.isReRankEnabled())
        {
            validate.ValidateReRank();
            ReRanker re = new ReRanker(searchParser,queryCBOR);
            re.ReRank();
        }

        if(searchParser.isBM25Enabled())
        {
            BaseBM25 bm = null;
            try {
                 bm = new BaseBM25(searchParser.getkVAL(),searchParser.getIndexlocation());
            } catch (IOException e) {
                e.printStackTrace();
            }
            Map<String,Map<String, Container>> res = bm.getRanking(queryCBOR);
            RunWriter.writeRunFile("BM_25",res);

        }

    }
}
