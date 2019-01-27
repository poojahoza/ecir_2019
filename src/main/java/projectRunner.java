package main.java;

import main.java.util.Util;
import main.java.util.constants;
import main.java.indexer.indexBuilder;

import java.io.IOException;
import java.util.Map;

public class projectRunner
{
    private static void  usage() {
        System.out.println("args[0] --> Paragraph corpus");
        System.out.println("args[1] --> Outline CBOR");
        System.exit(-1);
    }

    public static void main(String[] args) {
        String dest;
        if (args.length < 2) {
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

                Map<String,String> p = Util.readOutline(args[1]);
                for(Map.Entry<String,String> obj:p.entrySet())
                {
                    System.out.println(obj.getKey()+"------------->"+obj.getValue());
                }

            }

    }

}
