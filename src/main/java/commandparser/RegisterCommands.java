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
     --rerank one of the method implementation requires word embeddings file, whatever data you need, you can accept it.

     There are some helper functions that needs to be implemented to validate the inputs for your method, if the user missed any
     data, the program should exit with a status message

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

          @Parameter(names = {"--we","--word-embedding"},description ="Pass the word embedding file GloVe/ Word2Vec")
          private String word_embedding_file = null;


          @Parameter(names = {"--dim","--word-dimension"},description ="Dimension of the Word embeddings")
          private Integer dimension=0;


          public String getIndexlocation()
          {
               return indexlocation;
          }

          public String getQueryfile()
          {
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

          public Integer getDimension()
          {
              return dimension;
          }

          boolean isHelp() {
               return help;
          }
     }

    @Parameters(separators = "=",commandDescription = "Command to create ham and spam datasets")
    public static class CommandClassify
    {
        @Parameter(names = {"-i", "--index-loc"}, description = "Indexed directory to search", required = true)
        private String indexlocation = null;

        @Parameter(names = {"-q", "--qrel-loc"}, description = "Qrels file to create the ham and spam datasets", required = true)
        private String qrellocation = null;

        @Parameter(names = {"-h","--ham-location"},description = "Location to save the ham corpus")
        private String hampath=System.getProperty("user.dir") + System.getProperty("file.separator") + "indexed_file";

        @Parameter(names = {"-s","--spam-location"},description = "Location to save the spam corpus")
        private String spampath=System.getProperty("user.dir") + System.getProperty("file.separator") + "indexed_file";

        @Parameter(names = "--help", help = true)
        private boolean help;

        boolean isHelp()
        {
            return help;
        }

        public String getIndexPath()
        {
            return indexlocation;
        }

        public String getQrelPath()
        {
            return qrellocation;
        }

        public String getHamPath()
        {
            return hampath;
        }

        public String getSpamPath()
        {
            return spampath;
        }
    }

    @Parameters(separators = "=",commandDescription = "Use the ham and spam data sets to detect and filter spam from search results")
    public static class CommandFilter
    {
        @Parameter(names = {"-i","--data-file"},description = "Location of the indexed spam and ham datasets",required=true)
        private String IndexPath;

        @Parameter(names = "--help", help = true)
        private boolean help;

        boolean isHelp() {
            return help;
        }

        public String getIndexPath()
        {
            return IndexPath;
        }

    }

     @Parameters(separators = "=",commandDescription = "Help Information")
     public static class CommandHelp
     {

     }
}
