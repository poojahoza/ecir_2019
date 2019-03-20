package main.java.queryexpansion;

import main.java.commandparser.RegisterCommands;
import main.java.containers.Container;
import main.java.searcher.BaseBM25;
import main.java.containers.EntityContainer;
import main.java.utils.PrintUtils;
import main.java.utils.RunWriter;
import main.java.utils.SortUtils;
import java.io.IOException;

import java.util.*;
import org.apache.jena.query.*;


public class QueryExpansion {


    private BaseBM25 bm25 = null;
    private RegisterCommands.CommandSearch searchcommand = null;
    private Map<String, String> query = null;

    private HashMap<String, EntityContainer> myEntityMap = null;
    private HashMap<String, HashMap<String, EntityContainer>> myGlobalMap = null;


    public QueryExpansion(RegisterCommands.CommandSearch searchcommand, Map<String, String> query) {
        this.searchcommand = searchcommand;
        this.query = query;

        try {
            bm25 = new BaseBM25(searchcommand.getkVAL(), searchcommand.getIndexlocation());
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    public int returnDBPediaEntityCount(String s) {
        int count = 0;
        if (s !="") {
            String sparqlQueryString1 =
               /* "PREFIX dbont: <http://dbpedia.org/ontology/> " +
                        "PREFIX dbp: <http://dbpedia.org/property/>" +
                        "prefix rdf:<http://xmlns.com/foaf/0.1/>" +
                        "prefix rdfs: <http://www.w3.org/2000/01/rdf-schema#>" +
                        "prefix owl: <http://www.w3.org/2002/07/owl#>" +
                        "prefix xsd: <http://www.w3.org/2001/XMLSchema#>" +
                        "prefix dc: <http://purl.org/dc/elements/1.1/>" +
                        "prefix foaf: <http://xmlns.com/foaf/0.1/>" +
                        "prefix obo: <  http://purl.obolibrary.org/obo/>" +
                        "   SELECT  ?x ?name ?type  " +
                        "   WHERE { ?x dbont:name ?name . " +
                        "  ?x dbp:type ?type.    " +*/
                    " PREFIX dbont: <http://dbpedia.org/ontology/> " +
                            " PREFIX dbp: <http://dbpedia.org/property/>" +
                            " PREFIX dbo: <http://dbpedia.org/resource/classes#>" +
                            " prefix rdf:<http://xmlns.com/foaf/0.1/>" +
                            " prefix rdfs: <http://www.w3.org/2000/01/rdf-schema#>" +
                            " prefix owl: <http://www.w3.org/2002/07/owl#>" +
                            " prefix xsd: <http://www.w3.org/2001/XMLSchema#>" +
                            " prefix dc: <http://purl.org/dc/elements/1.1/>" +
                            " prefix foaf: <http://xmlns.com/foaf/0.1/>" +
                            " prefix obo: <http://purl.obolibrary.org/obo/>" +
                            " SELECT  DISTINCT ?x ?name ?abstract ?comment " +
                            " WHERE {    ?x dbont:name ?name ." +
                            " ?x dbo:abstract ?abstract." +
                            " ?x rdfs:comment ?comment." +
                            " FILTER (langMatches(lang(?abstract),'en')). FILTER (langMatches(lang(?comment),'en'))." +
                            " FILTER ( contains(?abstract ,'" + s + "')).   " +
                            " FILTER ( contains(?comment,'" + s + "')).  }";

            Query query = QueryFactory.create(sparqlQueryString1, Syntax.syntaxSPARQL_11);
            QueryExecution qexec = QueryExecutionFactory.sparqlService("http://dbpedia.org/sparql", query);
            ResultSet results = qexec.execSelect();
//            ResultSetFormatter.out(System.out, results, query);
//            System.out.println(s);
             count = results.getRowNumber();
            if (count > 0) {
                int a = 0;
            }
            qexec.close();
        }
        return count;
    }

    public Map<String,Map<String,Container >> doQueryExpansion() {
        //PrintUtils.displayMap(bm25.getRanking(query));


        myEntityMap = new HashMap<>();
        myGlobalMap = new HashMap<>();

        String qID = "";
        EntityContainer entity = null;
        int i = 0;
        for (Map.Entry<String, String> qMain : query.entrySet()) {

            if (searchcommand.getisVerbose()) {
                System.out.println(i + " of " + query.entrySet().size());
            }

            i++;
            String Query = qMain.getValue();

            Map<String, Container> temp = bm25.getRanking(Query);
            myEntityMap = new HashMap<>();

            for (Map.Entry<String, Container> q3 : temp.entrySet()) {

                EntityContainer e = q3.getValue().getEntity();
                if (qMain.getKey() != qID) {
                    // new Query
                    if (e.getEntityId() != " ") {
                        entity = e;
                        entity.setCount(1);
                        myEntityMap.put(e.getEntityId(), entity);
                        myGlobalMap.put(qMain.getKey(), myEntityMap);
                    }
                } else {
                    // old Query
                    if (e.getEntityId() != " ") {
                        entity = myEntityMap.get(e.getEntityId());
                        if (entity == null) {
                            entity = e;
                            entity.setCount(1);
                            myEntityMap.put(e.getEntityId(), entity);
                        } else {
                            entity.setCount(entity.getCount() + 1);
                            myEntityMap.replace(e.getEntityId(), entity);
                        }
                        myGlobalMap.replace(qMain.getKey(), myEntityMap);
                    }
                }
                qID = qMain.getKey();
            }
        }
        int i2 = 1;

        Map<String, String> expandedQuery = query;
        for (Map.Entry<String, HashMap<String, EntityContainer>> globalQMap : myGlobalMap.entrySet()) {


            int numberOfReturnedEntity = searchcommand.getNumberOfReturnedEntity();

            // return max entity (the top entity)
            List<EntityContainer> candidateEntityList = getMaxEntityCount(globalQMap.getValue(), numberOfReturnedEntity + 1);

            int iq = 0;
            String additionalQuery = "";
            for (EntityContainer candidateEntity : candidateEntityList) {
                if (iq != 0) { // do not take the first entity which is ""

                    additionalQuery = additionalQuery + " " + candidateEntity.getEntityVal();

                    if (searchcommand.getisVerbose()) {
                        System.out.println("\n" + i2 + " query " + globalQMap.getKey() + " the top Entity " + candidateEntity.getEntityId() + " --- Value ---- " + candidateEntity.getEntityVal() + " with count " + candidateEntity.getCount());
                    }
                }
                iq++;
            }
            i2++;

            expandedQuery.replace(globalQMap.getKey(), expandedQuery.get(globalQMap.getKey()) + " " + additionalQuery);
        }
        return ReRank(expandedQuery);
    }

    public Map<String,Map<String,Container >> doQueryExpansionWithDBpedia() {
        //PrintUtils.displayMap(bm25.getRanking(query));

        myEntityMap = new HashMap<>();
        myGlobalMap = new HashMap<>();

        String qID = "";
        EntityContainer entity = null;
        int i = 0;
        for (Map.Entry<String, String> qMain : query.entrySet()) {

            if (searchcommand.getisVerbose()) {
                System.out.println(i + " of " + query.entrySet().size());
            }

            i++;
            String Query = qMain.getValue();

            Map<String, Container> temp = bm25.getRanking(Query);
            myEntityMap = new HashMap<>();

            for (Map.Entry<String, Container> q3 : temp.entrySet()) {

                EntityContainer e = q3.getValue().getEntity();
                if (qMain.getKey() != qID) {
                    // new Query
                    if (e.getEntityId() != " ") {
                        entity = e;
                        int a = 0;
                        for (String s : e.getEntityVal().replaceAll("'s", "").replaceAll("\\'","").replaceAll("'","").split("\n")) {
                            if (e.getEntityVal() != "" && s != "") {
                                a += returnDBPediaEntityCount(Query.replaceAll("'s", "").replaceAll("\\'","").replaceAll("'","") + " " + s);
                            }
                        }
                        entity.setCount(a);
                        myEntityMap.put(e.getEntityId(), entity);
                        myGlobalMap.put(qMain.getKey(), myEntityMap);
                    }
                } else {
                    // old Query
                    if (e.getEntityId() != " ") {
                        entity = myEntityMap.get(e.getEntityId());
                        if (entity == null) {
                            entity = e;
                            int a = 0;
                            if (e.getEntityVal()!="") {
                                for (String s : e.getEntityVal().replaceAll("'s","").replaceAll("\\'","").replaceAll("'","").split("\n")) {
                                    a += returnDBPediaEntityCount(Query.replaceAll("'s","").replaceAll("\\'","").replaceAll("'","") + " " + s);
                                }
                            }
                            entity.setCount(a);
                            myEntityMap.put(e.getEntityId(), entity);
                        } else {
                            int a = 0;
                            if (e.getEntityVal()!="") {
                                for (String s : e.getEntityVal().replaceAll("'s","").replaceAll("\\'","").replaceAll("'","").split("\n")) {
                                    a += returnDBPediaEntityCount(Query.replaceAll("'s","").replaceAll("\\'","").replaceAll("'","") + " " + s);
                                }
                            }
                            entity.setCount(entity.getCount() + a);
                            myEntityMap.replace(e.getEntityId(), entity);
                        }
                        myGlobalMap.replace(qMain.getKey(), myEntityMap);
                    }
                }
                qID = qMain.getKey();
            }
        }
        int i2 = 1;

        Map<String, String> expandedQuery = query;
        for (Map.Entry<String, HashMap<String, EntityContainer>> globalQMap : myGlobalMap.entrySet()) {


            int numberOfReturnedEntity = searchcommand.getNumberOfReturnedEntity();

            // return max entity (the top entity)
            List<EntityContainer> candidateEntityList = getMaxEntityCount(globalQMap.getValue(), numberOfReturnedEntity + 1);

            int iq = 0;
            String additionalQuery = "";
            for (EntityContainer candidateEntity : candidateEntityList) {
                if (iq != 0) { // do not take the first entity which is ""

                    additionalQuery = additionalQuery + " " + candidateEntity.getEntityVal();

                    if (searchcommand.getisVerbose()) {
                        System.out.println("\n" + i2 + " query " + globalQMap.getKey() + " the top Entity " + candidateEntity.getEntityId() + " --- Value ---- " + candidateEntity.getEntityVal() + " with count " + candidateEntity.getCount());
                    }
                }
                iq++;
            }
            i2++;

            expandedQuery.replace(globalQMap.getKey(), expandedQuery.get(globalQMap.getKey()) + " " + additionalQuery);
        }

        return ReRank(expandedQuery);
    }

    private List<EntityContainer> getMaxEntityCount(Map<String, EntityContainer> entityMap, int numberOfEntityReturn) {
        // do not care of the entity that appear one time.
        int max = 1;
        List<EntityContainer> tempMaxEntity = new ArrayList<EntityContainer>();
        EntityContainer tempEntity = null;

        for (int i = 0; i < numberOfEntityReturn ; i++) {
            for (Map.Entry<String, EntityContainer> iEntity : entityMap.entrySet()) {
                if (tempMaxEntity.size() > 0) {
                    if (iEntity.getValue().getCount() > max && iEntity.getValue().getCount() < tempMaxEntity.get(tempMaxEntity.size() - 1).getCount()) {
                        if (iEntity.getValue().getEntityVal() != " " && iEntity.getValue().getCount() != 1) {
                            max = iEntity.getValue().getCount();
                            tempEntity = iEntity.getValue();
                        }
                    }
                }else {
                        if (iEntity.getValue().getCount() > max) {
                            if (iEntity.getValue().getEntityVal() != " " && iEntity.getValue().getCount() != 1) {
                                max = iEntity.getValue().getCount();
                                tempEntity = iEntity.getValue();
                            }
                        }
                    }
            }
            if (max > 1) {
                max = 1;
                tempMaxEntity.add(tempEntity);
                tempEntity = null;
            }
        }
        return tempMaxEntity;
    }

    public Map<String,Map<String,Container >> ReRank(Map<String,String> inputQuery)
    {

        Map<String,Map<String,Container >> result = new LinkedHashMap<String,Map<String,Container>>();
        for(Map.Entry<String,String> q: inputQuery.entrySet())
        {
            String Query = q.getValue();
            Map<String, Container> BM25Val = bm25.getRanking(Query);
            Map<String, Container> sortedMap = SortUtils.sortByValue(BM25Val);
            result.put(q.getKey(),sortedMap);
        }

        String mname = "doc_reranking_With_QueryExpansion"+"_k"+this.searchcommand.getkVAL()+"_top"+searchcommand.getNumberOfReturnedEntity();

        RunWriter.writeRunFile(mname,result);

        if(searchcommand.getisVerbose())
        {
            PrintUtils.displayMap(result);
        }
        return result;
    }
}
