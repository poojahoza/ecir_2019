package main.java.runner;


import main.java.clustering.ClusteringRanker;
import main.java.commandparser.CommandParser;
import main.java.commandparser.RegisterCommands;
import main.java.commandparser.ValidateCommands;
import main.java.containers.Container;
import main.java.mrf.MarkovRandomField;
import main.java.queryexp.ExpandQuery;
import main.java.queryexp.ExpandQueryAbstract;
import main.java.queryexp.ExpandQueryDF;
import main.java.queryexp.ExpandQueryIDF;
import main.java.reranker.ReRanker;
import main.java.rerankerv2.docsimranker.DocumentFrequencySimilarity;
import main.java.rerankerv2.docsimranker.EntitySimilarityRanker;
import main.java.searcher.BaseBM25;
import main.java.utils.*;
import main.java.searcher.PageSearcher;
import main.java.searcher.LeadtextSearcher;
import main.java.graph.GraphSimConstructor;
import main.java.graph.GraphDegreeConstructor;
import main.java.wordsimilarityranker.*;
import main.java.queryexpansion.QueryExpansion;
import main.java.entityrelation.FeatureGenerator;
import main.java.wrapper.QueryExpansionReRanking;


import java.io.IOException;
import java.util.Map;

/*
The searchParser object will hold all the information that is passed as the command line argument.
There are helper methods to get the data.
*/
public class SearchRunner implements ProgramRunner
{
    private RegisterCommands.CommandSearch searchParser = null;
    private ValidateCommands.ValidateSearchCommands validate = null;

    public SearchRunner(CommandParser parser)
    {
        searchParser = parser.getSearchCommand();
        validate = new ValidateCommands.ValidateSearchCommands(searchParser);
    }

    @Override
    public void run()  {

        Map<String,String> queryCBOR = null;

        validate.ValidateRetrievalOptions();
        if(searchParser.isArticleEnabled())
        {
            queryCBOR = SearchUtils.readOutline(searchParser.getQueryfile());
        }else
        {
            queryCBOR = SearchUtils.readOutlineSectionPath(searchParser.getQueryfile());
        }

        if(searchParser.isBM25Enabled())
        {
            BaseBM25 bm = null;
            try {
                bm = new BaseBM25(searchParser.getkVAL(),searchParser.getIndexlocation());
            } catch (IOException e) {
                e.printStackTrace();
            }
            Map<String,Map<String, Container>> res = bm.getRanking(queryCBOR);

            String datafile ="";
            if(searchParser.getQueryfile().toLowerCase().contains("test".toLowerCase()))
            {
                datafile = "_test";
            }
            else if(searchParser.getQueryfile().toLowerCase().contains("train".toLowerCase()))
            {
                datafile = "_train";
            }

            String level = searchParser.isArticleEnabled()? "_article": "_section";
            String mname= "BM_25"+level+datafile;

            RunWriter.writeRunFile(mname,res);
        }

        if(searchParser.isReRankEnabled())
        {
            validate.ValidateReRank();
            ReRanker re = new ReRanker(searchParser,queryCBOR);
            re.ReRank();
        }

        if(searchParser.isIDFReRankEnabled())
        {
            validate.ValidateReRank();
            ReRanker re = new ReRanker(searchParser,queryCBOR);
            re.ReRankIDF();
        }

        if(searchParser.isDFReRankEnabled())
        {

            validate.ValidateReRank();
//            ReRanker re = new ReRanker(searchParser,queryCBOR);
//            re.ReRankDF();

            DocumentFrequencySimilarity df = new DocumentFrequencySimilarity(searchParser,queryCBOR);
            df.doDocumentFrequency();
        }

        if(searchParser.isCosineSimilarityEnabled())
        {
            CosineSimilarity cosineSimilarity = new CosineSimilarity(searchParser,queryCBOR);
            cosineSimilarity.doCosine();
        }

        if(searchParser.isJaccardSimilarityEnabled())
        {
            JaccardSimilarity jaccardSimilarity = new JaccardSimilarity(searchParser,queryCBOR);
            jaccardSimilarity.doJaccard();
        }

        if(searchParser.isJaroSimilarityEnabled())
        {
            JaroWinklerSim jaroWinkler = new JaroWinklerSim(searchParser,queryCBOR);
            jaroWinkler.doJaroWinkler();
        }

        if(searchParser.isDiceEnabled())
        {
            SorensenDiceCoefficient sorensenDiceCoefficient = new SorensenDiceCoefficient(searchParser,queryCBOR);
            sorensenDiceCoefficient.doSorsenCoff();
        }

        if(searchParser.isLevenSimEnabled())
        {
            NormalizedLevenshteinSimilarity normalizedLevenshteinSimilarity = new NormalizedLevenshteinSimilarity(searchParser,queryCBOR);
            normalizedLevenshteinSimilarity.doNormalizedLevenshtein();
        }


        if(searchParser.isEntityDegreeEnabled()){
            validate.ValidateEntityDegree();
            try {
                Map<String,String> querysecCBOR = SearchUtils.readOutlineSectionPath(searchParser.getQueryfile());

                BaseBM25 bm25 = new BaseBM25(searchParser.getkVAL(), searchParser.getIndexlocation());
                Map<String, Map<String, Container>> bm25_ranking = bm25.getRanking(querysecCBOR);

                Entities e = new Entities();
                Map<String, Map<String, String>> query_ent_list = e.getEntitiesPerQuery(bm25_ranking);

                PageSearcher pgs = new PageSearcher(searchParser.getEntityIndLoc());
                Map<String, Map<String, String>> query_entities = pgs.getRanking(query_ent_list);

                GraphDegreeConstructor gdc = new GraphDegreeConstructor();
                Map<String, Map<String, Integer>> ranked_entities = gdc.getGraphDegree(query_entities);

                Map<String, Map<String, Double>> ranked_entities_score = e.getParagraphsScore(bm25_ranking, ranked_entities);
                ranked_entities_score = e.getRerankedParas(ranked_entities_score);

                WriteFile write_file = new WriteFile();
                write_file.generateEntityRunFile(ranked_entities_score, "entityDegree");

            }catch (IOException ioe){
                System.out.println(ioe.getMessage());
            }
        }
        if(searchParser.isEntitySimEnabled()){
            validate.ValidateEntitySim();
            try {
                Map<String,String> querysecCBOR = SearchUtils.readOutlineSectionPath(searchParser.getQueryfile());

                BaseBM25 bm25 = new BaseBM25(searchParser.getkVAL(), searchParser.getIndexlocation());
                Map<String, Map<String, Container>> bm25_ranking = bm25.getRanking(querysecCBOR);

                Entities e = new Entities();
                Map<String, Map<String, String>> query_ent_list = e.getEntitiesPerQuery(bm25_ranking);

                LeadtextSearcher gs = new LeadtextSearcher(searchParser.getEntityIndLoc());
                Map<String, Map<String, String>> query_entities = gs.getRanking(query_ent_list);

                GraphSimConstructor gdc = new GraphSimConstructor();
                Map<String, Map<String, Double>> ranked_entities = gdc.getGraphDegree(query_entities,
                        searchParser.getDimension(),
                        searchParser.getWordEmbeddingFile());


                Map<String, Map<String, Double>> ranked_entities_score = e.getParagraphsScoreDouble(bm25_ranking, ranked_entities);
                ranked_entities_score = e.getRerankedParas(ranked_entities_score);

                WriteFile write_file = new WriteFile();
                write_file.generateEntityRunFile(ranked_entities_score, "entitySim");

            }catch (IOException ioe){
                System.out.println(ioe.getMessage());
            }


        }
        if(searchParser.isQueryExpand()){
            validate.ValidateEntityDegree();
            try {
                Map<String,String> querysecCBOR = SearchUtils.readOutlineSectionPath(searchParser.getQueryfile());

                BaseBM25 bm25 = new BaseBM25(searchParser.getkVAL(), searchParser.getIndexlocation());
                Map<String, Map<String, Container>> bm25_ranking = bm25.getRanking(querysecCBOR);

                Entities e = new Entities();
                Map<String, Map<String, String>> query_ent_list = e.getEntitiesPerQuery(bm25_ranking);

                PageSearcher pgs = new PageSearcher(searchParser.getEntityIndLoc());
                Map<String, Map<String, String>> query_entities = pgs.getRanking(query_ent_list);

                GraphDegreeConstructor gdc = new GraphDegreeConstructor();
                Map<String, Map<String, Integer>> ranked_entities = gdc.getGraphDegree(query_entities);

                Map<String, Map<String, Double>> ranked_entities_score = e.getParagraphsScore(bm25_ranking, ranked_entities);
                ranked_entities_score = e.getRerankedParas(ranked_entities_score);

                Map<String, String> expanded_query = e.expandQuery(querysecCBOR, ranked_entities_score);

                //BaseBM25 bm25 = new BaseBM25(100, searchParser.getIndexlocation());
                Map<String, Map<String, Container>> expanded_bm25_ranking = bm25.getRanking(expanded_query);

                WriteFile write_file = new WriteFile();
                write_file.generateBM25RunFile(expanded_bm25_ranking, "expandedBM25");
                //write_file.generateEntityRunFile(ranked_entities_score, "entityDegree");

            }catch (IOException ioe){
                System.out.println(ioe.getMessage());
            }
        }

        if(searchParser.isEntityRelationEnabled()){
            validate.ValidateEntityRelation();

            try {
                //Map<String,String> querysecCBOR = SearchUtils.readOutlineSectionPath(searchParser.getQueryfile());

                BaseBM25 bm25 = new BaseBM25(searchParser.getkVAL(), searchParser.getIndexlocation());
                Map<String, Map<String, Container>> bm25_ranking = bm25.getRanking(queryCBOR);

                Entities e = new Entities();
                Map<String, Map<String, String>> query_ent_list = e.getSortedEntitiesPerQuery(bm25_ranking);

                FeatureGenerator featuregenerator = new FeatureGenerator();
                Map<String, Map<String, Double[]>> featureVectors = featuregenerator.getNormalizedFeatureVectors(query_ent_list, bm25_ranking);

                WriteFile write_file = new WriteFile();
                String level = searchParser.isArticleEnabled()? "_article": "_section";
                String datafile ="";
                if(searchParser.getQueryfile().toLowerCase().contains("test".toLowerCase()))
                {
                    datafile = "_test";
                }
                else if(searchParser.getQueryfile().toLowerCase().contains("train".toLowerCase()))
                {
                    datafile = "_train";
                }
                write_file.generateFeatureVectorRunFile(featureVectors, "feature_vectors"+level+datafile);
                write_file.generateEntityRankLibRunFile(featureVectors, searchParser.getQrelfile(), "rank_lib"+level+datafile);

            }catch (Exception ioe){
                ioe.printStackTrace();
            }
        }
        if(searchParser.isEntityRanklibEnabled()){
            validate.ValidateEntityRankLib();

            try {
                FeatureGenerator featureGenerator = new FeatureGenerator();
                Map<String, Map<String, Double>> query_entity_scores = featureGenerator.generateDotProduct(searchParser.getFeaturevectorfile(),
                        searchParser.getRankLibModelFile());
                //System.out.println(query_entity_scores);
                String level = searchParser.isArticleEnabled()? "_article": "_section";
                String datafile ="";
                if(searchParser.getQueryfile().toLowerCase().contains("test".toLowerCase()))
                {
                    datafile = "_test";
                }
                else if(searchParser.getQueryfile().toLowerCase().contains("train".toLowerCase()))
                {
                    datafile = "_train";
                }
                WriteFile write_file = new WriteFile();
                write_file.generateEntityRunFile(query_entity_scores, "entity_ranklib"+level+datafile);

                BaseBM25 bm25 = new BaseBM25(searchParser.getkVAL(), searchParser.getIndexlocation());
                Map<String, Map<String, Container>> bm25_ranking = bm25.getRanking(queryCBOR);

                Entities e = new Entities();
                Map<String, Map<String, Double>> ranked_entities_score = e.getParagraphsScoreDouble(bm25_ranking, query_entity_scores);
                ranked_entities_score = e.getRerankedParas(ranked_entities_score);

                write_file.generateEntityRunFile(ranked_entities_score, "paragraph_ranklib"+level+datafile);

            }catch (IOException ioe){
                ioe.printStackTrace();
            }
        }
        if(searchParser.isEntityCentroidEnabled()){
            validate.ValidateEntityCentroid();

            try {
                FeatureGenerator featureGenerator = new FeatureGenerator();
                Map<String, Map<String, Double>> query_entity_scores = featureGenerator.generateAverageCentroidVector(searchParser.getFeaturevectorfile());
                //System.out.println(query_entity_scores);
                String level = searchParser.isArticleEnabled()? "_article": "_section";
                String datafile ="";
                if(searchParser.getQueryfile().toLowerCase().contains("test".toLowerCase()))
                {
                    datafile = "_test";
                }
                else if(searchParser.getQueryfile().toLowerCase().contains("train".toLowerCase()))
                {
                    datafile = "_train";
                }
                WriteFile write_file = new WriteFile();
                write_file.generateEntityRunFile(query_entity_scores, "entity_avg_centroid"+level+datafile);

                BaseBM25 bm25 = new BaseBM25(searchParser.getkVAL(), searchParser.getIndexlocation());
                Map<String, Map<String, Container>> bm25_ranking = bm25.getRanking(queryCBOR);

                Entities e = new Entities();
                Map<String, Map<String, Double>> ranked_entities_score = e.getParagraphsScoreDouble(bm25_ranking, query_entity_scores);
                ranked_entities_score = e.getRerankedParas(ranked_entities_score);

                write_file.generateEntityRunFile(ranked_entities_score, "paragraph_avg_centroid"+level+datafile);

            }catch (IOException ioe){
                ioe.printStackTrace();
            }
        }

        if(searchParser.isQEEnabled())
        {
            validate.ValidateQE();
            QueryExpansion qe = new QueryExpansion(searchParser,queryCBOR);
            qe.doQueryExpansion();

        }

        if(searchParser.isEntityDocSimEnabled())
        {

            EntitySimilarityRanker ent = new EntitySimilarityRanker(searchParser,queryCBOR);
            ent.doEntityReRank();
        }

        if(searchParser.isMrfEnabled())
        {
            MarkovRandomField mrf = new MarkovRandomField(searchParser,queryCBOR);
            mrf.doMarkovRandomField();
        }

        if(searchParser.isClusterRankerEnabled())
        {
            ClusteringRanker cr = new ClusteringRanker(searchParser,queryCBOR);
            cr.doCluster();
        }

        if(searchParser.getisVerbose())
        {
            PrintUtils.displayQuery(queryCBOR);
        }

        if(searchParser.is_qe_reranking())
        {
            validate.ValidateQE();
            validate.ValidateReRank();
            QueryExpansion qe = new QueryExpansion(searchParser,queryCBOR);
            Map<String,Map<String,Container >> res = qe.doQueryExpansion();
            QueryExpansionReRanking geReRank = new QueryExpansionReRanking(searchParser,queryCBOR,res);
            String mname= "qe_rerank_"+searchParser.getQEType().toString();
            geReRank.getDocumentFrequencyReRanking(mname);

        }

        if(searchParser.isQe_entity_degree_rerankingEnabled()) {
            validate.ValidateEntityDegree();
            validate.ValidateReRank();

            try {
                BaseBM25 bm25 = new BaseBM25(searchParser.getkVAL(), searchParser.getIndexlocation());
                Map<String, Map<String, Container>> bm25_ranking = bm25.getRanking(queryCBOR);

                Entities e = new Entities();
                Map<String, Map<String, String>> query_ent_list = e.getEntitiesPerQuery(bm25_ranking);

                PageSearcher pgs = new PageSearcher(searchParser.getEntityIndLoc());
                Map<String, Map<String, String>> query_entities = pgs.getRanking(query_ent_list);

                GraphDegreeConstructor gdc = new GraphDegreeConstructor();
                Map<String, Map<String, Integer>> ranked_entities = gdc.getGraphDegree(query_entities);

                Map<String, Map<String, Double>> ranked_entities_score = e.getParagraphsScore(bm25_ranking, ranked_entities);
                ranked_entities_score = e.getRerankedParas(ranked_entities_score);

                Map<String, String> expanded_query = e.expandQuery(queryCBOR, ranked_entities_score);

                //BaseBM25 bm25 = new BaseBM25(100, searchParser.getIndexlocation());
                Map<String, Map<String, Container>> expanded_bm25_ranking = bm25.getRanking(expanded_query);


                QueryExpansionReRanking geReRank = new QueryExpansionReRanking(searchParser, queryCBOR, expanded_bm25_ranking);
                geReRank.getDocumentFrequencyReRanking("QE_Entity_degree");

            } catch (IOException io) {
                        io.getStackTrace();
            }

         }

        if(searchParser.isQueryExDFEnabled())
        {
            ExpandQuery exp = new ExpandQueryDF(searchParser,queryCBOR);
            exp.doQueryExpansion();
        }

        if(searchParser.isQueryExDFEnabled())
        {
            ExpandQuery exp = new ExpandQueryIDF(searchParser,queryCBOR);
            exp.doQueryExpansion();
        }

        if(searchParser.isTestEnabled())
        {
//            ExpandQuery exp = new ExpandQueryIDF(searchParser,queryCBOR);
//            Map<String,Map<String,Container>> res = exp.getExpandedQuery();
//            RunWriter.writeRunFile("Test_IDF",res);

            ExpandQuery exp = new ExpandQueryAbstract(searchParser,queryCBOR);
            exp.doQueryExpansion();


        }

       }
    }

