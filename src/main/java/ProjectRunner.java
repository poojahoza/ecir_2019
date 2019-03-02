package main.java;


import main.java.commandparser.CommandParser;
import main.java.runner.IndexRunner;
import main.java.runner.ProgramRunner;
import main.java.runner.SearchRunner;
import main.java.runner.IndexHamSpamRunner;
//import main.java.runner.FilterRunner;


public class ProjectRunner
{
    public static void main(String[] args)
    {
        /*
          Parses the command line and creates the parser
        */
        CommandParser parser = new CommandParser(args);
        ProgramRunner runner = null;
        if(args.length < 1)
        {
            parser.getParser().usage();
        }
        else {
            if (parser.getParser().getParsedCommand().equals("index")) {
                runner = new IndexRunner(parser);
                runner.run();
            } else if (parser.getParser().getParsedCommand().equals("search")) {
                runner = new SearchRunner(parser);
                runner.run();
            } else if (parser.getParser().getParsedCommand().equals("indexHamSpam")) {
                runner = new IndexHamSpamRunner(parser);
                runner.run();
            /*} else if (parser.getParser().getParsedCommand().equals("filter")) {
                runner = new FilterRunner(parser);
                runner.run();*/
            } else {
                parser.getParser().usage();
            }
        }
    }
}