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

          @Parameter(names = {"-i","--corpus-file"},description = "Corpus file to index. In case of Entity Abstract, please specify Entity Index location",required=true)
          private String IndexPath;

          @Parameter(names = {"-d","--dest-location"},description = "Location to save the index file")
          private String destpath = System.getProperty("user.dir") + System.getProperty("file.separator") + "indexed_file";

         @Parameter(names = {"--para-index"}, description = "Perform Paragraph Index")
         private Boolean isParaIndex = false;

         @Parameter(names = {"--entity-index"}, description = "Perform Entity Index")
         private Boolean isEntity = false;

         @Parameter(names = {"--abstract-index"}, description = "Perform Entity Abstract Index")
         private Boolean isEntityAbstract = false;


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

         public Boolean getIsEntityAbstract() {return isEntityAbstract;}


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

         @Parameter(names = {"--leven-sim"},description ="Rerank the document based on the NormalizedLevenshtein similarity between two strings")
         private boolean isLevenSim = false;

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

         @Parameter(names = "--entity-relation",description ="Rerank the initial retrieved document using entity relationship")
         private boolean isEntityRelationEnabled =false;

         @Parameter(names = "article",description ="Article level retrieval")
         private boolean isArticleEnabled =false;

         @Parameter(names = "section",description ="Section level retrieval")
         private boolean isSectionEnabled =false;

         @Parameter(names = "--entity-doc-sim",description ="Rerank the initial retrieved document using entity abstract similarity")
         private boolean isEntityDocSimEnabled = false;

         @Parameter(names = "--spam-filter",description ="Uses the spam filter before performing the re-rank")
         private static boolean isSpamFilterEnabled = false;


         @Parameter(names = {"--spam-loc"}, description = "Directory to spam train file")
         private static String spamLocation = null;


         @Parameter(names = {"--ham-loc"}, description = "Directory to ham train file")
         private static String hamLocation = null;


         @Parameter(names = "--parallel",description ="Uses the parallel stream for the reranker methods")
         private boolean isParallelEnabled = false;

         @Parameter(names = "--mrf",description ="Uses the parallel stream for the reranker methods")
         private boolean isMrfEnabled = false;

         @Parameter(names = "--cluster",description ="Cluster Ranking")
         private boolean isClusterRankerEnabled = false;

         @Parameter(names = "--qrel-path",description ="Pass the absolute path of the Qrel")
         private String qrelPath = null;

         @Parameter(names = "--rank-lib",description ="Provide path to the Ranklib")
         private String ranklibpath = null;



         public String getRanklibPath()
         {
             return ranklibpath;
         }

         public boolean isClusterRankerEnabled()
         {
             return isClusterRankerEnabled;
         }
         public boolean isLevenSimEnabled() {return isLevenSim;}
         public String getQrelPath()
         {
             return qrelPath;
         }
         public boolean isMrfEnabled() {return  isMrfEnabled;}
         public boolean isParallelEnabled(){return isParallelEnabled;}
         public static boolean isSpamFilterEnabled()
         {
             return isSpamFilterEnabled;
         }
         public static String SpamLocation()
         {
             return spamLocation;
         }
         public static String hamLocation()
         {
             return hamLocation;
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

         public boolean isEntityRelationEnabled()
         {
             return isEntityRelationEnabled;
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

    @Parameters(separators = "=",commandDescription = "Command to create training and test data for the spam classifier")
    public static class IndexHamSpam
    {

        @Parameter(names = {"-p","--paragraphs-file"},description = "paragraph corpus directory")
        private String paragraphPath=System.getProperty("user.dir") + System.getProperty("file.separator") + "indexed_file";

        @Parameter(names = {"-q","--qrels-file"},description = "qrels file")
        private String qrelPath=System.getProperty("user.dir") + System.getProperty("file.separator") + "indexed_file";

        @Parameter(names = {"-spamTrain"},description = "Location to save the spam training data")
        private String spamTrainPath=System.getProperty("user.dir") + System.getProperty("file.separator") + "indexed_file";

        @Parameter(names = {"-hamTrain"},description = "Location to save the ham training data")
        private String hamTrainPath=System.getProperty("user.dir") + System.getProperty("file.separator") + "indexed_file";

        @Parameter(names = {"-hamSpamTest"},description = "Location to save the ham and spam test data")
        private String hamSpamTestpath=System.getProperty("user.dir") + System.getProperty("file.separator") + "indexed_file";

        @Parameter(names = {"-hamTest"},description = "Location to save the ham test data")
        private String hamTestpath=System.getProperty("user.dir") + System.getProperty("file.separator") + "indexed_file";

        @Parameter(names = {"-spamTest"},description = "Location to save the spam test data")
        private String spamTestpath=System.getProperty("user.dir") + System.getProperty("file.separator") + "indexed_file";


        @Parameter(names = "--help", help = true)
        private boolean help;

        boolean isHelp()
        {
            return help;
        }

        public String getParagraphPath()
        {
            return paragraphPath;
        }

        public String getQrelPath()
        {
            return qrelPath;
        }

        public String getSpamTrainPath()
        {
            return spamTrainPath;
        }

        public String getHamTrainPath()
        {
            return hamTrainPath;
        }

        public String getHamSpamTestPath()
        {
            return hamSpamTestpath;
        }

        public String getHamTestPath()
        {
            return hamTestpath;
        }

        public String getSpamTestPath()
        {
            return spamTestpath;
        }

    }

     @Parameters(separators = "=",commandDescription = "Help Information")
     public static class CommandHelp
     {

     }

    @Parameters(separators = "=",commandDescription = "Ranker")
    public static class Ranker
    {
        @Parameter(names = {"--model-file"},description = "Location of the model file",required=true)
        private String modelFile=null;

        @Parameter(names = {"--run-file"},description = "Location of the run file",required=true)
        private String runfile=null;

        @Parameter(names = {"--mname"},description = "Method name suffix")
        private String mname="mrfupdated";

        public String getModelFile(){return modelFile;}
        public String getRunfile() {return  runfile;}
        public String getMname(){return mname;}

    }

}
