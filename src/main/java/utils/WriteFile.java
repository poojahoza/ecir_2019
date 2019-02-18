package main.java.utils;

import java.io.File;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Map;

public class WriteFile {

    private void checkFileExistence(String output_file_name)
    {
        File e = new File(output_file_name);
        if(e.exists())
        {
            e.delete();
        }
    }

    public void generateRunFile(Map<String, Map<String, Integer>> results)
    {

    }
}
