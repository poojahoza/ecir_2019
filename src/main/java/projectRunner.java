package main.java;

import main.java.util.Util;
import main.java.util.constants;
import main.java.indexer.indexBuilder;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.Map;

public class projectRunner
{
    private static void  usage() {
        System.out.println("args[0] --> Options: index, search");
        System.out.println("args[1] --> Paragraph corpus");
        System.out.println("args[2] --> Outline CBOR");
        System.exit(-1);
    }

    public static void main(String[] args) throws IOException {
        System.setProperty("file.encoding", "UTF-8");
        String option = args[0];
        String dest;

        if (args.length < 2) {
            usage();
        }
        else if (option.equals("index")) {

            dest = System.getProperty("user.dir") + System.getProperty("file.separator") + "indexed_file";
            System.out.print(dest);
            constants.setDirectoryName(dest);
            constants.setIndexFileName(args[1]);

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

            // To be implemented

            /*Map<String,String> p = Util.readOutline(args[1]);
            for(Map.Entry<String,String> obj:p.entrySet())
            {
                System.out.println(obj.getKey()+"------------->"+obj.getValue());
            }*/
        }

    }

}
