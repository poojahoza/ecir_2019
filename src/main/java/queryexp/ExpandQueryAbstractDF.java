package main.java.queryexp;

import main.java.commandparser.RegisterCommands;
import main.java.containers.Container;
import main.java.utils.CorpusStats;
import main.java.utils.RunWriter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

public class ExpandQueryAbstractDF extends ExpandQueryBase implements ExpandQuery {

    private RegisterCommands.CommandSearch SearchCommand = null;
    private Map<String, String> query = null;

    /**
     * Constructor calls super class to initialize the value
     *
     * @param searchCommand
     * @param query
     */
    public ExpandQueryAbstractDF(RegisterCommands.CommandSearch searchCommand, Map<String, String> query) {
        super(searchCommand, query);
        this.SearchCommand = searchCommand;
        this.query = query;
    }

    @Override
    public void doQueryExpansion() {
        Map<String, Map<String, Container>> res = performQueryExpansion();
        String fname = getFileSuffix("Entity_Abstract_DF");
        RunWriter.writeRunFile(fname, res);

    }

    @Override
    public Map<String, Map<String, Container>> getExpandedQuery() {
        return performQueryExpansion();
    }

    @Override
    public String getExpandedTerms(String originalQuery, Map<String, Container> retrievedList) {
        ArrayList<String> candidates = getCandidateTermsWithEntitiesAbstract(retrievedList);
        ArrayList<String> top = getSemanticTerms(originalQuery, candidates);
        CorpusStats cs = new CorpusStats(SearchCommand.getIndexlocation());
        ArrayList<String> topDF = null;
        try {
            topDF = cs.getDFStandardAnalyzer(top);
        } catch (IOException e) {
            e.printStackTrace();
        }
        ArrayList<String> finalTerms = getTopK(topDF, originalQuery);
        return ArrayListInToString(finalTerms);
    }
}
