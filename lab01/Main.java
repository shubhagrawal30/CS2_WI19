package edu.caltech.cs2.lab01;

import edu.caltech.cs2.lab01.libraries.Wikipedia;

public class Main {
    private static String cleanLink(String link) {
        link = link.substring(2, link.length() - 2);
        if (link.contains("|")) {
            link = link.substring(link.indexOf("|") + 1);
        }
        return link;
    }

    private static String cleanLinks(String text) {
        text = text.replace("light-year", "ly").replace("]]s", "]]");
        String result = "";
        while (text.contains("]]")) {
            int start = text.indexOf("[[");
            int end = text.indexOf("]]", start) + 2;
            result += text.substring(0, start);
            result += cleanLink(text.substring(start, end));
            text = text.substring(end);
        }
        return result + text;
    }

    private static String getDistance(WikipediaPage galaxy) {
        for (String line : galaxy.getText().split("\n\\|", -1)) {
            String tline = line.trim();
            if (tline.contains("dist_ly")) {
                int angle = tline.indexOf("<");
                int paren = tline.indexOf("(");
                int end;
                if (angle == -1 && paren == -1) {
                    end = tline.length();
                }
                else if (angle == -1 || paren == -1) {
                    end = Math.max(angle, paren);
                }
                else {
                    end = Math.min(angle, paren);
                }
                String x = cleanLinks(tline.substring(tline.indexOf("=") + 1, end).trim());

                return Wikipedia.parseMarkup(x);
            }
        }
        return null;
    }

    public static void main(String[] args) {
        WikipediaPage list_of_galaxies = new WikipediaPage(args[0]);
        while (list_of_galaxies.hasNextLink()) {
            String link = list_of_galaxies.nextLink();
            WikipediaPage galaxy = new WikipediaPage(link);
            if (galaxy.isValid() && galaxy.isGalaxy()) {
                String dist = getDistance(galaxy);
                if (dist != null) {
                    System.out.println(galaxy.getTitle() + " = " + dist);
                }
            }
        }
    }
}
