package main.java;

import main.java.commandparser.CommandParser;
import main.java.searcher.BaseBM25;
import main.java.utils.Constants;
import main.java.indexer.IndexBuilder;
import main.java.searcher.BaseSearcher;

import java.io.IOException;
import java.util.Map;

import main.java.containers.Container;
import main.java.utils.SearchUtils;

public class ProjectRunner
{
    private static void  generalhelp()
    {
        System.out.println("usage: <options> <file_paths>");
        System.out.println("Options: index, search, help");
        System.exit(-1);
    }

    private static void  indexUsage()
    {
        System.out.println("usage: index <corpus_file> - Index will be created in the current work directory  \n");
        System.out.println("File missing \n");
        System.exit(-1);
    }

    private static void  searchUsage()
    {
        System.out.println("usage: search <index_location> <outline_file> options \n");
        System.out.println("option: \"--section\" - Concatenates the section path \n");
        System.out.println("File missing \n");
        System.exit(-1);
    }

    private static void  help()
    {
        System.out.println("Team1: Implementation");
        System.out.println("\nusage: <options> <file_paths> \n");
        System.out.println("The following are all implemented sub-commands: \n ");
        System.out.println("index <Paragraph_corpus> \t \t \t \t -  Creates Index in the current work directory");
        System.out.println("search <index_location> <outline_file> \t -  performs the search given the outline file and the index location");
        System.out.println("help   \t \t \t \t \t \t \t \t \t -  Display general help");
        System.exit(-1);
    }


    public static void main(String[] args) throws IOException
    {
//        if (args.length < 1) {
//            generalhelp();
//        }
//
//        System.setProperty("file.encoding", "UTF-8");
//        String option = args[0];
//        String dest;
//
//        if(option.equals("index"))
//        {
//            if(args.length < 2 )
//            {
//                indexUsage();
//            }
//            Constants.setIndexFileName(args[1]);
//            dest = System.getProperty("user.dir") + System.getProperty("file.separator") + "indexed_file";
//            Constants.setDirectoryName(dest);
//
//            //Get the path of the index
//            String indexDir = Constants.DIRECTORY_NAME;
//            IndexBuilder ib = new IndexBuilder(indexDir);
//            ib.performIndex(Constants.FILE_NAME);
//
//        }
//        else if (option.equals("search")) {
//
//            if (args.length < 3) {
//                searchUsage();
//            }
//            dest = args[1];
//            Constants.setDirectoryName(dest);
//            Map<String,String> p = SearchUtils.readOutline(args[2]);
//            BaseSearcher bs = new BaseSearcher();
//            bs.writeRankings(p, "output_BM25_ranking.txt");
//            BaseBM25 bm= new BaseBM25(1000);
//            Map<String,Map<String, Container>> out = bm.getRanking(p);
//
//
//            int count=0;
//            for(Map.Entry<String,Map<String,Container>> outer:out.entrySet())
//            {
//                count++;
//                for(Map.Entry<String,Container> inner:outer.getValue().entrySet()) {
//
//                    System.out.println(outer.getKey() + "," + inner.getKey() + "," + inner.getValue().getDocID() + "," + inner.getValue().getScore() + "," + inner.getValue().getRanking());
//                    System.out.println(inner.getValue().getEntity());
//
//                }
//                if(count==1) break;
//
//            }
//            if(args.length >= 4)
//            {
//                if(args[3].equals("--section"))
//                {
//                    Map<String,String> sp = SearchUtils.readOutlineSectionPath(args[2]);
//                    bs.writeRankings(sp, "output_BM25_section_ranking.txt");
//                }
//            }
//        }
//        else
//        {
//            help();
//        }

        CommandParser com = new CommandParser(args);
        String commandReceived = com.getParser().getParsedCommand();



        if(commandReceived.equals("index"))
        {
            System.out.println("Passed in Index"+ com.getIndexCommand());
        }else if(commandReceived.equals("--help"))
        {
            com.getParser().usage();
        }
        else
        {

        }


    }
}