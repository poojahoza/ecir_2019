package main.java.runner;

import main.java.commandparser.CommandParser;
import main.java.commandparser.RegisterCommands;
import main.java.indexer.IndexBuilder;

import java.io.IOException;

public class IndexRunner implements ProgramRunner
{
    private RegisterCommands.CommandIndex indexParser = null;

    public IndexRunner(CommandParser parser)
    {
        indexParser = parser.getIndexCommand();
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
