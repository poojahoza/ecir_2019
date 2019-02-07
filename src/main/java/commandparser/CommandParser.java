package main.java.commandparser;

import com.beust.jcommander.JCommander;

public class CommandParser
{
    private  JCommander  parse = null;
    private RegisterCommands.CommandIndex index = null;
    private RegisterCommands.CommandSearch search=null;
    private RegisterCommands.CommandHelp helpc=null;
    private String[] argslist=null;

    public CommandParser(String  ... args)
    {
        index = new RegisterCommands.CommandIndex();
        search =new RegisterCommands.CommandSearch();
        helpc = new RegisterCommands.CommandHelp();
        argslist=args;
    }

    private void createParser()
    {
        if(parse == null)
        {
            parse = JCommander.newBuilder().addCommand("index",index).addCommand("search",search).addCommand("--help",helpc).build();
            parse.parse(argslist);
        }

    }

    public JCommander getParser()
    {
        if(parse==null) createParser();
        return parse;
    }

    public RegisterCommands.CommandIndex getIndexCommand()
    {
        return index;
    }

    public RegisterCommands.CommandSearch getSearchCommand()
    {
        return search;
    }

}