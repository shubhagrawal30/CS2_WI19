package edu.caltech.cs2.project04;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public abstract class AbstractMovieAutoCompleter {
    protected static Map<String, String> titleMap;
    protected static Map<String, String> idMap;

    static {
        try {
            titleMap = new HashMap<>();
            idMap = new HashMap<>();

            Scanner titleData = new Scanner(new File("data/movies.basics.tsv"));
            titleData.nextLine();

            while (titleData.hasNextLine()) {
                String[] line = titleData.nextLine().split("\t");
                titleMap.put(line[0], line[2]);
                idMap.put(line[2], line[0]);
            }
        } catch (FileNotFoundException e) {
        }
    }

    public static Map<String, String> getIDMap() {
        return idMap;
    }
}