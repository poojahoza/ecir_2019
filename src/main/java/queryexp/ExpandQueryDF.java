package main.java.queryexp;

import main.java.commandparser.RegisterCommands;
import main.java.containers.Container;
import main.java.utils.RunWriter;

import java.util.ArrayList;
import java.util.Map;

/**
 * Expands the Query based on the highest Document Frequency for each query terms
 * Extends the ExpandQueryBase which has the most common implementation
 */
public class ExpandQueryDF extends ExpandQueryBase implements ExpandQuery {

    private RegisterCommands.CommandSearch SearchCommand = null;
    private Map<String, String> query = null;

    /**
     * Constructor calls super class to initialize the value
     * @param searchCommand
     * @param query
     */
    public ExpandQueryDF(RegisterCommands.CommandSearch searchCommand, Map<String, String> query) {
        super(searchCommand, query);
        this.SearchCommand = searchCommand;
        this.query = query;
    }


    /**
     * Performs the query expansion and write to the run file
     * This function will be called for those method who does not need result in the Hashmap
     */
    @Override
    public void doQueryExpansion() {
        Map<String, Map<String, Container>> res = performQueryExpansion();
        RunWriter.writeRunFile("test_exp", res);
    }

    /**
     * This method is wrapper for this class that it can be used outside.
     * Returns the expanded results in the hashmap
     * @return
     */
    @Override
    public Map<String, Map<String, Container>> getExpandedQuery() {
        return null;
    }

    /**
     * Returns the expanded query terms, computes K nearest words for each term in the query.
     * --prf-val-term option dictates the value of k
     * @param originalQuery
     * @param retrievedList
     * @return
     */

    @Override
    public String getExpandedTerms(String originalQuery, Map<String, Container> retrievedList) {
        ArrayList<String> candidates = getCandidateTerms(retrievedList);
        ArrayList<String> top =  getSemanticTerms(originalQuery, candidates);
        return ArrayListInToString(top);
    }
}
