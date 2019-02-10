package main.java.runner;

import main.java.commandparser.CommandParser;
import main.java.commandparser.RegisterCommands;
import main.java.commandparser.ValidateCommands;
import main.java.indexer.IndexBuilder;

import java.io.IOException;

/*
The indexParser object will hold all the information that is passed as the command line argument.
There are helper methods to get the data.
 */
public class IndexRunner implements ProgramRunner
{
    private RegisterCommands.CommandIndex indexParser = null;
    private ValidateCommands.ValidateIndexCommands validate = null;

    public IndexRunner(CommandParser parser)
    {
        indexParser = parser.getIndexCommand();
        validate = new ValidateCommands.ValidateIndexCommands(indexParser);
    }

    @Override
    public void run()
    {
        IndexBuilder ib = null;
        try {
            ib = new IndexBuilder(indexParser.getDestpath());
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            ib.performIndex(indexParser.getIndexPath());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
