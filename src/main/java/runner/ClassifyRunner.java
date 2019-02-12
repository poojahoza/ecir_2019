package main.java.runner;

import main.java.BayesCounter;
import main.java.commandparser.CommandParser;
import main.java.commandparser.RegisterCommands;
import main.java.commandparser.ValidateCommands;


public class ClassifyRunner implements ProgramRunner {

    private RegisterCommands.CommandIndex indexParser = null;
    private ValidateCommands.ValidateIndexCommands validate = null;

    public ClassifyRunner(CommandParser parser)
    {
        indexParser = parser.getIndexCommand();
        validate = new ValidateCommands.ValidateIndexCommands(indexParser);
    }

    @Override
    public void run()
    {
        BayesCounter bc = new BayesCounter();
        // Here's where I train the data using the BuildHashMap methods
        // Potentially create a wrapper function called doTrain

    }
}
