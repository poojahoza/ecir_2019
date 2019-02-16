package main.java.runner;

import main.java.commandparser.CommandParser;
import main.java.commandparser.RegisterCommands;
import main.java.commandparser.ValidateCommands;
import main.java.predictors.LabelPredictor;
import main.java.predictors.NaiveBayesPredictor;
import main.java.utils.SearchUtils;
import org.apache.lucene.search.IndexSearcher;


public class FilterRunner implements ProgramRunner {

    private RegisterCommands.CommandFilter filterParser = null;
    private ValidateCommands.ValidateFilterCommands validate = null;

    public FilterRunner(CommandParser parser)
    {
        filterParser = parser.getFilterCommand();
        validate = new ValidateCommands.ValidateFilterCommands(filterParser);
    }

    @Override
    public void run()
    {
        IndexSearcher searcher = SearchUtils.createIndexSearcher(filterParser.getIndexPath());
        LabelPredictor predictor = new NaiveBayesPredictor(searcher);
        // predictor.evaluate();
    }
}
