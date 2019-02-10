package main.java;

import com.beust.jcommander.JCommander;
import main.java.commandparser.CommandParser;
import main.java.commandparser.RegisterCommands;
import main.java.runner.IndexRunner;
import main.java.runner.ProgramRunner;
import main.java.runner.SearchRunner;
import main.java.searcher.BaseBM25;
import main.java.utils.Constants;
import main.java.indexer.IndexBuilder;
import main.java.searcher.BaseSearcher;

import java.io.IOException;

import java.util.Map;

import main.java.containers.Container;
import main.java.utils.SearchUtils;


public class ProjectRunner
{
    public static void main(String[] args) {
        CommandParser parser = new CommandParser(args);
        ProgramRunner runner = null;
        if(args.length < 1)
        {
            parser.getParser().usage();
        }
        else
        {
           if(parser.getParser().getParsedCommand().equals("index"))
            {
                runner = new IndexRunner(parser);
                runner.run();
            }
           else if(parser.getParser().getParsedCommand().equals("search"))
           {
               runner = new SearchRunner(parser);
               runner.run();
           }
           else
           {
               parser.getParser().usage();
           }
        }
    }
}