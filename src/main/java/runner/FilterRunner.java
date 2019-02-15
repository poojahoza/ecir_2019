package main.java.runner;

import main.java.BayesCounter;
import main.java.commandparser.CommandParser;
import main.java.commandparser.RegisterCommands;
import main.java.commandparser.ValidateCommands;


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
        BayesCounter bc = new BayesCounter();
        // Here's where I train the data using the BuildHashMap methods
        // Potentially create a wrapper function called doTrain

    }
}
