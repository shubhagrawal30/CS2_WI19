package edu.caltech.cs2.project07;

import edu.caltech.cs2.datastructures.Location;
import edu.caltech.cs2.datastructures.LinkedDeque;
import edu.caltech.cs2.datastructures.TrieMap;
import edu.caltech.cs2.interfaces.IDeque;
import edu.caltech.cs2.interfaces.ISet;
import edu.caltech.cs2.interfaces.ITrieMap;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class MapsAutoCompleter {
    private static ITrieMap<String, IDeque<String>, IDeque<Location>> locs = new TrieMap<>((IDeque<String> s) -> s);

    public static void populateLocations(ISet<Location> locations) {
        locs.clear();
        for (Location l : locations) {
            if (l.name == null) {
                continue;
            }

            String[] words = l.name.toLowerCase().split(" ");
            char[] charArr = l.name.toLowerCase().toCharArray();
            String[] strCharArr = new String[charArr.length];

            for (int i = 0; i < charArr.length; i++) {
                strCharArr[i] = Character.toString(charArr[i]);
            }

            for (int i = 1; i <= l.name.length(); i++) {
                String[] subarr = Arrays.copyOfRange(strCharArr, 0, i);
                IDeque<String> iterableSubarr = listFromArray(subarr);

                if (!locs.containsKey(iterableSubarr)) {
                    locs.put(iterableSubarr, new LinkedDeque<>());
                }

                locs.get(iterableSubarr).add(l);
            }

            for (int i = 0; i < words.length; i++) {
                String[] subarr = Arrays.copyOfRange(words, i, words.length);
                IDeque<String> iterableSubarr = listFromArray(subarr);

                if (!locs.containsKey(iterableSubarr)) {
                    locs.put(iterableSubarr, new LinkedDeque<>());
                }

                locs.get(iterableSubarr).add(l);
            }

            if (l.address == null) {
                continue;
            }

            words = l.address.toLowerCase().split(" ");
            for (int i = 0; i < words.length; i++) {
                String[] subarr = Arrays.copyOfRange(words, i, words.length);
                IDeque<String> iterableSubarr = listFromArray(subarr);

                if (!locs.containsKey(iterableSubarr)) {
                    locs.put(iterableSubarr, new LinkedDeque<>());
                }

                locs.get(iterableSubarr).add(l);
            }
        }
    }

    private static IDeque<String> listFromArray(String[] arr) {
        IDeque<String> lst = new LinkedDeque<>();

        for (String s: arr) {
            lst.addBack(s);
        }

        return lst;
    }

    public static IDeque<String> charArrToStringIterable(char[] ca) {
        IDeque<String> d = new LinkedDeque<>();

        for (int i = 0; i < ca.length; i++) {
            d.addBack(Character.toString(ca[i]));
        }

        return d;
    }

    public static IDeque<Long> complete(String term) {
        String[] keyPath = term.strip().toLowerCase().split("\\s");
        IDeque<String> kpIterable = listFromArray(keyPath);

        IDeque<IDeque<Location>> options = locs.getCompletions(kpIterable);
        options.addAll(locs.getCompletions(charArrToStringIterable(term.toLowerCase().toCharArray())));

        Set<Long> opts = new HashSet<>();

        for (IDeque<Location> val : options) {
            for (Location o : val) {
                opts.add(o.id);
            }
        }

        IDeque<Long> completions = new LinkedDeque<>();
        for (Long o : opts) {
            completions.add(o);
        }

        return completions;
    }
}
