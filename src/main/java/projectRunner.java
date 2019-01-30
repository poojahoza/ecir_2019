package main.java;

import main.java.util.constants;
import main.java.indexer.indexBuilder;
import main.java.searcher.baseSearcher;

import java.io.IOException;
import java.util.Map;
import main.java.util.Util;

public class projectRunner
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
        if (args.length < 1) {
            generalhelp();
        }

        System.setProperty("file.encoding", "UTF-8");
        String option = args[0];
        String dest;

        if(option.equals("index"))
        {
            if(args.length < 2 )
            {
                indexUsage();
            }
            constants.setIndexFileName(args[1]);
            dest = System.getProperty("user.dir") + System.getProperty("file.separator") + "indexed_file";
            constants.setDirectoryName(dest);

            //Get the path of the index
            String indexDir = constants.DIRECTORY_NAME;
            indexBuilder ib = new indexBuilder(indexDir);

            try {
                ib.performIndex(constants.FILE_NAME);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else if (option.equals("search")) {

            if (args.length < 3) {
                searchUsage();
            }
            dest = args[1];
            constants.setDirectoryName(dest);
            Map<String,String> p = Util.readOutline(args[2]);
            baseSearcher bs = new baseSearcher();
            bs.writeRankings(p, "output_BM25_ranking.txt");

            if(args.length >= 4)
            {
                if(args[3].equals("--section"))
                {
                    Map<String,String> sp = Util.readOutlineSectionPath(args[2]);
                    bs.writeRankings(sp, "output_BM25_section_ranking.txt");
                }
            }

        }
        else
        {
            help();
        }
    }
}