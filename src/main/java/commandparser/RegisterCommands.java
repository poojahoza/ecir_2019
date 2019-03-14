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
          private String destpath = System.getProperty("user.dir") + System.getProperty("file.separator") + "indexed_file";

         @Parameter(names = {"--para-index"}, description = "Perform Paragraph Index")
         private Boolean isParaIndex = false;

         @Parameter(names = {"--entity-index"}, description = "Perform Entity Index")
         private Boolean isEntity = false;



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

          public Boolean getIsParaIndex() {return isParaIndex;}

         public Boolean getIsEntity() {return isEntity;}


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



         @Parameter(names = {"-we","--word-embedding"},description ="Pass the word embedding file GloVe/ Word2Vec")
         private String word_embedding_file = null;


         @Parameter(names = {"-dim","--word-dimension"},description ="Dimension of the Word embeddings")
         private Integer dimension=0;

         @Parameter(names = {"-bm25","--default-bm25"},description ="Rerank the initial retrieved cluster using document similarity")
         private boolean isBM25 =false;

         @Parameter(names = {"-V","--verbose"},description ="Print out  some of the results  into stdout")
         private boolean isVerbose =false;

         @Parameter(names = "--rerank",description ="Rerank the initial retrieved document using document similarity")
         private boolean isReRank =false;

         @Parameter(names = "--bias-fact",description ="Bias factor to get the document representation")
         private Integer biasFactor = 1;

         @Parameter(names = {"--rerank-idf"},description ="Rerank the document based on the IDF")
         private boolean isIDFReRank =false;

         @Parameter(names = {"--rerank-df"},description ="Rerank the document based on the DF")
         private boolean isDFReRank =false;

         @Parameter(names = {"--cosine-sim"},description ="Rerank the document based on the cosine similarity between two strings")
         private boolean isCosineSimilarity =false;

         @Parameter(names = {"--jaccard-sim"},description ="Rerank the document based on the Jaccard similarity between two strings")
         private boolean isJaccardSimilarity = false;

         @Parameter(names = {"--jaro-sim"},description ="Rerank the document based on the Jaro Winkler similarity between two strings")
         private boolean isJaroEnabled = false;

         @Parameter(names = {"--dice-sim"},description ="Rerank the document based on the Sorensen Dice coefficient similarity between two strings")
         private boolean isDiceEnabled = false;

         @Parameter(names = {"-qe","--query-expansion"},description ="Rerank the document using Query expansion")
         private boolean isQE =false;

         @Parameter(names = {"-top"},description ="specify the top number of selected entity to used in the Query expansion")
         private int numberOfReturnedEntity = 3;

         @Parameter(names = "--entity-degree",description ="Rerank the initial retrieved document using entity degree")
         private boolean isEntityDegree =false;

         @Parameter(names = "--entity-index",description ="Pass the index location of entity index")
         private String entityIndLoc = null;

         @Parameter(names = "--entity-sim",description ="Rerank the initial retrieved document using entity abstract similarity")
         private boolean isEntitySim =false;

         @Parameter(names = "--entity-expand",description ="Rerank the initial retrieved document using expanded query")
         private boolean isQueryExpand =false;

         @Parameter(names = "article",description ="Article level retrieval")
         private boolean isArticleEnabled =false;

         @Parameter(names = "section",description ="Section level retrieval")
         private boolean isSectionEnabled =false;

         @Parameter(names = "--entity-doc-sim",description ="Rerank the initial retrieved document using entity abstract similarity")
         private boolean isEntityDocSimEnabled = false;

         @Parameter(names = "--spam-filter",description ="Uses the spam filter before performing the re-rank")
         private boolean isSpamFilterEnabled = false;

         @Parameter(names = "--parallel",description ="Uses the parallel stream for the reranker methods")
         private boolean isParallelEnabled = false;

         @Parameter(names = "--mrf",description ="Uses the parallel stream for the reranker methods")
         private boolean isMrfEnabled = false;

         @Parameter(names = "--qrel-path",description ="Pass the absolute path of the Qrel")
         private String qrelPath = null;

         public String getQrelPath()
         {
             return qrelPath;
         }
         public boolean isMrfEnabled() {return  isMrfEnabled;}
         public boolean isParallelEnabled(){return isParallelEnabled;}
         public boolean isSpamFilterEnabled()
         {
             return isSpamFilterEnabled;
         }
         public boolean isArticleEnabled()
         {
             return isArticleEnabled;
         }
         public boolean isSectionEnabled()
         {
             return isSectionEnabled;
         }
         public boolean isQEEnabled(){return isQE;}
         public int getNumberOfReturnedEntity() {return numberOfReturnedEntity;}

        public Integer getBiasFactor() {return biasFactor;}
         public boolean isDiceEnabled() { return isDiceEnabled;}
         public boolean isJaroSimilarityEnabled(){return isJaroEnabled;}
         public boolean isJaccardSimilarityEnabled(){return isJaccardSimilarity;}

         public boolean isIDFReRankEnabled()
         {
             return isIDFReRank;
         }
         public boolean isDFReRankEnabled()
         {
             return isDFReRank;
         }

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

          public Boolean getisVerbose()
          {
              return isVerbose;
          }

          public boolean isBM25Enabled()
          {
              return isBM25;
          }

          public boolean isCosineSimilarityEnabled() {return isCosineSimilarity;}


            public boolean isEntityDocSimEnabled()
            {
                return isEntityDocSimEnabled;
            }

          public boolean isEntityDegreeEnabled()
         {
             return isEntityDegree;
         }

            public String getEntityIndLoc(){return entityIndLoc; }
         public boolean isEntitySimEnabled()
         {
             return isEntitySim;
         }

         public boolean isQueryExpand(){return isQueryExpand;}

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
