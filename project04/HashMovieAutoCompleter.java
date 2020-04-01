package edu.caltech.cs2.project04;

import edu.caltech.cs2.datastructures.LinkedDeque;
import edu.caltech.cs2.interfaces.IDeque;

import java.util.HashMap;
import java.util.Map;

public class HashMovieAutoCompleter extends AbstractMovieAutoCompleter {
    private static Map<String, IDeque<String>> titles = new HashMap<>();

    public static void populateTitles() {
        titles.clear();
        for (String title : idMap.keySet()) {
            String suffix = title.toLowerCase();
            IDeque<String> suffixes = new LinkedDeque<>();
            int index = 0;
            do {
                suffixes.add(suffix);
                index = suffix.indexOf(' ');
                suffix = suffix.substring(index + 1);
            } while (index >= 0);
            titles.put(title.toLowerCase(), suffixes);
        }
    }

    public static IDeque<String> complete(String term) {
        term = term.toLowerCase();
        IDeque<String> matches = new LinkedDeque<>();
        for (String title : idMap.keySet()) {
            for (String suffix : titles.get(title.toLowerCase())){
                if (checkPrefix(suffix, term) && !matches.contains(title))
                    matches.add(title);
                }
        }
        return matches;
    }

    private static boolean checkPrefix(String suffix, String prefix) {
        String pre_words[] = prefix.split(" ");
        String suf_words[] = suffix.split(" ");
        if (pre_words.length > suf_words.length)
            return false;

        for (int i = 0; i < pre_words.length; i++)
            if (!pre_words[i].equals(suf_words[i]))
                return false;

        return true;
    }
}