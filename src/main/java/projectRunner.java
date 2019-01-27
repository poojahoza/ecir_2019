package main.java;

import main.java.util.constants;
import main.java.indexer.indexBuilder;

import java.io.IOException;

public class projectRunner
{
    private static void  usage() {
        System.out.println("args[0] --> Paragraph corpus");
        System.exit(-1);
    }

    public static void main(String[] args) {
        String dest;
        if (args.length < 1) {
            usage();
        } else
            {
            dest = System.getProperty("user.dir") + System.getProperty("file.separator") + "indexed_file";
            constants.setDirectoryName(dest);
            constants.setIndexFileName(args[0]);

            indexBuilder ib= new indexBuilder();

                try {
                    ib.performIndex();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
    }

}
