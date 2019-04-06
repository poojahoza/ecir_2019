package main.java.queryexp;

import main.java.commandparser.RegisterCommands;
import java.util.Map;


/*
        Base class
*/
class ExpandQueryBase {

    private RegisterCommands.CommandSearch SearchCommand = null;
    private Map<String,String> query=null;

    ExpandQueryBase(RegisterCommands.CommandSearch searchCommand,Map<String,String> query)
    {
        this.SearchCommand=searchCommand;
        this.query=query;
    }





}

