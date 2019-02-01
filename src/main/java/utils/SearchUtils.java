package main.java.utils;
import edu.unh.cs.treccar_v2.read_data.DeserializeData;
import edu.unh.cs.treccar_v2.Data;
import java.io.*;
import java.util.*;

public class SearchUtils
{
    static public Map<String, String> readOutline(String filename) {
        Map<String, String> data = new LinkedHashMap<String, String>();

        FileInputStream qrelStream = null;
        try {
            qrelStream = new FileInputStream(new File(filename));
        } catch (FileNotFoundException fnf) {
            System.out.println(fnf.getMessage());
        }
        for (Data.Page page : DeserializeData.iterableAnnotations(qrelStream))
        {
            data.put(page.getPageId(), page.getPageName());
        }
        return data;
    }

    static public Map<String, String> readOutlineSectionPath(String filename) {
        Map<String, String> data = new LinkedHashMap<String, String>();

        FileInputStream qrelStream = null;
        try {
            qrelStream = new FileInputStream(new File(filename));
        } catch (FileNotFoundException fnf) {
            System.out.println(fnf.getMessage());

        }
        for (Data.Page page : DeserializeData.iterableAnnotations(qrelStream))
        {
            StringBuilder queryBuilder = new StringBuilder();

            for (List<Data.Section> sectionPath : page.flatSectionPaths())
            {
                queryBuilder.append(" ");
                queryBuilder.append(page.getPageName());
                for(Data.Section sec:sectionPath)
                {
                    queryBuilder.append(" ");
                    queryBuilder.append(sec.getHeading().replaceAll("[^\\w\\s]",""));
                }
                //queryBuilder.append(String.join(" ", Data.sectionPathHeadings(sectionPath)).replaceAll("[^\\w\\s]",""));
            }
            //System.out.println(queryBuilder.toString());
            data.put(page.getPageId(), queryBuilder.toString());
        }
        return data;
    }
}
