package main.java.runner;

import main.java.commandparser.CommandParser;
import main.java.commandparser.RegisterCommands;

/*
The searchParser object will hold all the information that is passed as the command line argument.
There are helper methods to get the data.
 */
public class SearchRunner implements ProgramRunner
{
    private RegisterCommands.CommandSearch searchParser = null;
    public SearchRunner(CommandParser parser)
    {
        searchParser = parser.getSearchCommand();
    }

    @Override
    public void run()
    {
        System.out.println("Path "+ searchParser.getIndexlocation());
        System.out.println("Path "+ searchParser.getQueryfile());
    }
}
