package main.java.runner;

import main.java.commandparser.CommandParser;
import main.java.commandparser.RegisterCommands;
import main.java.commandparser.ValidateCommands;
import main.java.indexer.PageIndexBuilder;
import main.java.indexer.EntityIndexBuilder;

import java.io.IOException;

/*
The indexParser object will hold all the information that is passed as the command line argument.
There are helper methods to get the data.
 */
public class PageRunner implements ProgramRunner
{
    private RegisterCommands.CommandPageIndex pageIndexParser = null;
    private ValidateCommands.ValidatePageIndexCommands validate = null;

    public PageRunner(CommandParser parser)
    {
        pageIndexParser = parser.getPageIndexCommand();
        validate = new ValidateCommands.ValidatePageIndexCommands(pageIndexParser);
    }

    @Override
    public void run()
    {
        /*PageIndexBuilder pb = null;
        try {
            pb = new PageIndexBuilder(pageIndexParser.getPagedestpath());
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            pb.performPageIndex(pageIndexParser.PageIndexPath());
        } catch (IOException e) {
            e.printStackTrace();
        }*/

        EntityIndexBuilder pb = null;
        try {
            pb = new EntityIndexBuilder(pageIndexParser.getPagedestpath());
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            pb.performPageIndex(pageIndexParser.PageIndexPath());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
