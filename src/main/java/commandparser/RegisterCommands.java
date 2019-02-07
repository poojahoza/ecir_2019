package main.java.commandparser;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;

public class RegisterCommands
{
     @Parameters(separators = "=",commandDescription = "Command to Index the Corpus")
     public static class CommandIndex
     {

          @Parameter(names = {"-i","--corpus-file"},description = "Corpus file to index",required=true)
          private String IndexPath=null;

          @Parameter(names = {"-d","--dest-location"},description = "Location to save the index file")
          private String destpath=System.getProperty("user.dir") + System.getProperty("file.separator") + "indexed_file";

          @Parameter(names = "--help", help = true)
          private boolean help;

          boolean isHelp()
          {
               return help;
          }

          String getIndexPath()
          {
               return IndexPath;
          }

          String getDestpath()
          {
               return destpath;
          }

     }

     @Parameters(separators = "=",commandDescription = "Command to search")
     public static class CommandSearch
     {
          @Parameter(names = {"-i", "--index-loc"}, description = "Indexed directory to search", required = true)
          private String indexlocation = null;

          @Parameter(names = "--help", help = true)
          private boolean help;

          String getIndexlocation() {
               return indexlocation;
          }

          boolean isHelp() {
               return help;
          }
     }

     @Parameters(separators = "=",commandDescription = "Help Information")
     public static class CommandHelp
     {

     }
}
