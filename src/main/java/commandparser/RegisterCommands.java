package main.java.commandparser;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;

import java.util.List;

/*
All the necessary commands should be registered here first for your implementation.
*/
public class RegisterCommands
{
     /*
      All the commands as part of the index should be registered here
      */
     @Parameters(separators = "=",commandDescription = "Command to Index the Corpus")
     public static class CommandIndex
     {

          @Parameter(names = {"-i","--corpus-file"},description = "Corpus file to index",required=true)
          private String IndexPath;

          @Parameter(names = {"-d","--dest-location"},description = "Location to save the index file")
          private String destpath=System.getProperty("user.dir") + System.getProperty("file.separator") + "indexed_file";

          @Parameter(names = "--help", help = true)
          private boolean help;

          boolean isHelp()
          {
               return help;
          }

          public String getIndexPath()
          {
               return IndexPath;
          }

          public String getDestpath()
          {
               return destpath;
          }

     }

     /*
     All the commands as part of the search should be registered here
     The required parameter is set to true because its mandatory

     Example command for your search implementation.
     --rerank one of the method implementation requires word embeddings file, whatever data you need, you can accept it
     using the postional arguments for the --rerank.

     arity =1 because I am expecting if users use --rerank option, they will pass in the Glove file as argument.
     if arity=2 I can expect two other data the user needs to pass in.

     Note: for the options such as --rerank, all the data passed acts in as positional argument.
     */

     @Parameters(separators = "=",commandDescription = "Command to search")
     public static class CommandSearch
     {
          @Parameter(names = {"-i", "--index-loc"}, description = "Indexed directory to search", required = true)
          private String indexlocation = null;

          @Parameter(names = {"-q", "--query-cbor"}, description = "Query file (CBOR file)", required = true)
          private String queryfile = null;

          @Parameter(names = "--help", help = true)
          private boolean help;

          @Parameter(names = {"-k","--candidate-set-val"}, description = "How many candidate set to retrieve using BM25")
          private Integer kVAL=100;

          @Parameter(names = "--rerank",description ="Rerank the initial retrieved cluster using document similarity")
          private boolean isReRank =false;

         @Parameter(names = {"--we","--word-embedding"},description ="Rerank the initial retrieved cluster using document similarity")
         private String word_embedding_file = null;

          public String getIndexlocation() {
               return indexlocation;
          }

          public String getQueryfile() {
               return queryfile;
          }

          public boolean isReRankEnabled()
          {
               return isReRank;
          }

          public Integer getkVAL()
          {
               return kVAL;
          }

          public String getWordEmbeddingFile()
          {
              return word_embedding_file;
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
