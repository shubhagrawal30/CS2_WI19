package edu.caltech.cs2.lab01;

import edu.caltech.cs2.lab01.libraries.Wikipedia;

public class WikipediaPage {
    private String title;
    private String text;
    private int index;

    public WikipediaPage(String title, boolean followRedirects) {
        this.title = title;
        this.text = Wikipedia.getPageText(title);
        if (followRedirects) {
            while (this.isRedirect()) {
                this.text = Wikipedia.getPageText(Wikipedia.parseMarkup(this.getText()));
            }
        }
        this.index = 0;
    }

    public WikipediaPage(String title) {
        this(title, true);
    }

    public String getTitle() {
        return convertToTitleCase(this.title.replace('_', ' '));
    }

    private static String convertToTitleCase(String s) {
        String out = "";
        for (String word : s.split(" ")) {
            out += " " + Character.toUpperCase(word.charAt(0))
                    + (word.length() > 1 ? word.substring(1).toLowerCase() : "");
        }
        return out.substring(1);
    }

    public String getText() {
        return text;
    }

    public boolean isRedirect() {
        return this.isValid() && text.toLowerCase().startsWith("#redirect");
    }

    public boolean isValid() {
        return text != null;
    }

    public boolean isGalaxy() {
        return this.isValid() && text.toLowerCase().contains("infobox galaxy");
    }

    public boolean hasNextLink() {

        if (this.text == null) {
            return false;
        }
        return (this.text.indexOf("[[", this.index) != -1);

    }

    public String nextLink() {

        String link;
        do {
            link = Wikipedia.parseMarkup(
                    this.text.substring(this.text.indexOf("[[", this.index) + 2,
                            this.text.indexOf("]]", this.index)));
            this.index = this.text.indexOf("]]") + 2;
        } while (Wikipedia.isSpecialLink(link));
        return link;

    }
    public String getTitle2() {

        String title = convertToTitleCase(this.title.replace('_', ' '));
        int start = 0, end;
        while (title.indexOf('(', start) != -1) {
            end = title.indexOf(')', start);
            if (end == -1)
                break;
            title = title.substring(0, start) + title.substring(start, end).toLowerCase() +
                    title.substring(end);
            start = end;
        }
        String newTitle = "";

        for (String word : title.split(" ")) {
            boolean roman = true;
            for (int i = 0; i < word.length(); i++) {
                char c = word.toLowerCase().charAt(i);
                if (c != 'i' || c != 'x' || c != 'v') {
                    roman = false;
                    break;
                }
            }
            if (roman)
                word = word.toUpperCase();
            newTitle += " " + word;
        }

        return newTitle.substring(1);
    }
}