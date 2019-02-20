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

    @Parameters(separators = "=",commandDescription = "Command to create ham-spam index")
    public static class IndexHamSpam
    {
        @Parameter(names = {"-i","--corpus-file"},description = "Index",required=true)
        private String IndexPath;

        @Parameter(names = {"-q","--new-qrels-file"},description = "qrels file")
        private String QrelPath=System.getProperty("user.dir") + System.getProperty("file.separator") + "indexed_file";

        @Parameter(names = {"-s","--dest-spamtrain"},description = "Location to save the spam training data")
        private String spamDestpath=System.getProperty("user.dir") + System.getProperty("file.separator") + "indexed_file";

        @Parameter(names = {"-h","--dest-hamtrain"},description = "Location to save the ham training data")
        private String hamDestpath=System.getProperty("user.dir") + System.getProperty("file.separator") + "indexed_file";

        @Parameter(names = {"-d","--dest-test"},description = "Location to save the test data")
        private String hamSpamDestpath=System.getProperty("user.dir") + System.getProperty("file.separator") + "indexed_file";


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

        public String getQrelPath()
        {
            return QrelPath;
        }

        public String getSpamDestPath()
        {
            return spamDestpath;
        }

        public String getHamDestPath()
        {
            return hamDestpath;
        }

        public String getHamSpamDestPath()
        {
            return hamSpamDestpath;
        }

    }

    @Parameters(separators = "=",commandDescription = "Use the ham and spam data sets to detect and filter spam from search results")
    public static class CommandFilter
    {
        @Parameter(names = {"-i","--index"},description = "Location of the main index", required=true)
        private String indexPath;

        @Parameter(names = {"-s","--spam-index"},description = "Location of the spam training data", required=true)
        private String spamIndexPath;

        @Parameter(names = {"-h","--ham-index"},description = "Location of the ham training data", required=true)
        private String hamIndexPath;

        @Parameter(names = {"-t","--test-index"},description = "Location of the test data", required=true)
        private String hamSpamIndexPath;

        @Parameter(names = "--help", help = true)
        private boolean help;

        boolean isHelp() {
            return help;
        }

        public String getIndexPath(){return indexPath;}

        public String getSpamIndexPath(){return spamIndexPath;}

        public String getHamIndexPath(){return hamIndexPath;}

        public String getTestIndexPath(){return hamSpamIndexPath;}

    }

     @Parameters(separators = "=",commandDescription = "Help Information")
     public static class CommandHelp
     {

     }
}
