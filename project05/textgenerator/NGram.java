package edu.caltech.cs2.textgenerator;

import edu.caltech.cs2.datastructures.*;
import edu.caltech.cs2.interfaces.IDeque;

import java.util.Iterator;

public class NGram implements Iterable<String>, Comparable<NGram> {
    public static final String NO_SPACE_BEFORE = ",?!.-,:'";
    public static final String NO_SPACE_AFTER = "-'><=";
    public static final String REGEX_TO_FILTER = "”|\"|“|\\(|\\)|\\*";
    public static final String DELIMITER = "\\s+|\\s*\\b\\s*";
    private IDeque<String> data;
    private int hash = 0;

    public static String normalize(String s) {
       return s.replaceAll(REGEX_TO_FILTER, "").strip();
    }

    public NGram(IDeque<String> x) {
        this.data = new LinkedDeque<>();
        for (int i = 0; i < x.size(); i++) {
            this.data.addBack(x.peekFront());
            x.addBack(x.removeFront());
        }
    }

    public NGram(String data) {
        this(normalize(data).split(DELIMITER));
    }

    public NGram(String[] data) {
        this.data = new LinkedDeque<>();
        for (String s : data) {
            s = normalize(s);
            if (!s.isEmpty()) {
                this.data.addBack(s);
            }
        }
    }

    public NGram next(String word) {
        String[] data = new String[this.data.size()];
        for (int i = 0; i < data.length - 1; i++) {
            this.data.addBack(this.data.removeFront());
            data[i] = this.data.peekFront();
        }
        this.data.addBack(this.data.removeFront());
        data[data.length - 1] = word;
        return new NGram(data);
     }

    public String toString() {
        String result = "";
        String prev = "";
        for (String s : this.data) {
            result += ((NO_SPACE_AFTER.contains(prev) || NO_SPACE_BEFORE.contains(s) || result.isEmpty()) ?  "" : " ") + s;
            prev = s;
        }
        return result.strip();
    }

    @Override
    public Iterator<String> iterator() {
        return this.data.iterator();
    }


    @Override
    public int compareTo(NGram other) {
        if (this.data.size() < other.data.size())
            return -1;
        else if (this.data.size() > other.data.size())
            return 1;

        Iterator<String> o_items = other.iterator();
        Iterator<String> this_items = iterator();
        while (o_items.hasNext() && this_items.hasNext()) {
            int compared = this_items.next().compareTo(o_items.next());
            if (compared != 0)
                return compared;
        }
        return 0;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof NGram)) {
            return false;
        }
        Iterator<String> o_items = ((NGram) o).iterator();
        Iterator<String> this_items = iterator();
        while (o_items.hasNext() && this_items.hasNext()) {
            if (!o_items.next().equals(this_items.next()))
                return false;
        }
        if (o_items.hasNext() || this_items.hasNext())
            return false;
        return true;
    }

    @Override
    public int hashCode() {
        if (hash == 0) {
            hash = 17;
            for (String s : data) {
                int h = 0;
                for (char c : new IterableString(s))
                    h = 31 * h + c;
                hash = 31 * hash + h;
            }
        }
        return hash;
    }
}