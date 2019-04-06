package main.java.queryexp;

import main.java.containers.Container;

import java.util.Map;

public interface ExpandQuery {

    /*
        This is the public method can be called, this performs the query expansion
        and writes to the run files
    */
    public void doQueryExpansion();


    /*
        All the query expansion methods should implement this method
        to find new terms for the given query
    */
    public String getExpandedTerms();

    /*
        Method should run for all the queries and return the expanded results.
        This is the interface for anyone who wants to access it the results
    */

    public Map<String, Map<String , Container>> getExpandedQuery();

}
