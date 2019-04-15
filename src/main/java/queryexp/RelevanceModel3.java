package main.java.queryexp;

import main.java.commandparser.RegisterCommands;
import main.java.containers.Container;

import java.util.Map;

public class RelevanceModel3 extends ExpandQueryBase implements ExpandQuery {


    private RegisterCommands.CommandSearch SearchCommand = null;
    private Map<String, String> query = null;

    public RelevanceModel3(RegisterCommands.CommandSearch searchCommand, Map<String, String> query) {
        super(searchCommand, query);
        this.SearchCommand = searchCommand;
        this.query = query;
    }

    @Override
    public String getExpandedTerms(String originalQuery, Map<String, Container> retrievedList) {
        return null;
    }


    @Override
    public void doQueryExpansion() {

    }


    @Override
    public Map<String, Map<String, Container>> getExpandedQuery() {
        return null;
    }


}
