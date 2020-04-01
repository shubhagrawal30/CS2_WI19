package edu.caltech.cs2.lab01.libraries;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Wikipedia {
    private static String SITE = "en.wikipedia.org";

    public static String getPageText(String title) {
        try {
            URLConnection u = new URL(
                    "https://" + SITE + "/w/api.php?action=parse&page=" + title.replace(" ", "_") + "&prop=wikitext&format=xml"
            ).openConnection();
            BufferedReader in = new BufferedReader(new InputStreamReader(u.getInputStream(), StandardCharsets.UTF_8));
            String xmlPage = in.lines().collect(Collectors.joining("\n"));
            int wikitext = xmlPage.indexOf("<wikitext");
            int pageStart = xmlPage.indexOf(">", wikitext);
            int pageEnd = xmlPage.lastIndexOf("</wikitext");
            if (pageStart == -1 || pageEnd == -1) {
                return null;
            }
            return decode(xmlPage.substring(pageStart + 1, pageEnd));
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static String decode(String in) {
        in = in.replace("&lt;", "<").replace("&gt;", ">"); // html tags
        in = in.replace("&quot;", "\"");
        in = in.replace("&#039;", "'");
        in = in.replace("&amp;", "&");
        in = in.replace("&nbsp;", " ");
        return in;
    }

    public static String parseMarkup(String markup) {
        if (markup == null) {
            return null;
        }

        markup = markup.replaceFirst("\\{\\{nowrap\\|$", "").strip();

        if (markup.contains("{{±")) {
            markup = markup.substring(0, markup.indexOf("{{")) +
                    " ± " +
                    markup.substring(markup.indexOf("|") + 1, markup.indexOf("|", markup.indexOf("|") + 1)) +
                    markup.substring(markup.indexOf("}}") + 2);
        }

        if (markup.contains("[[")) {
            markup += "]]";
            markup = markup.substring(markup.indexOf("[["), markup.indexOf("]]") + 2);
        } else if (markup.contains("{{")) {
            markup += "}}";
            markup = markup.substring(markup.indexOf("{{"), markup.indexOf("}}") + 2);
        }

        if (markup.startsWith("{{") && markup.endsWith("}}")) {
            if (markup.startsWith("{{val")) {
                markup = markup.substring(6, markup.indexOf("|", 6)) + " " + markup.substring(markup.lastIndexOf("=") + 1, markup.length() - 2);
            }
            else if (markup.contains("{{convert")) {
                int dividerIndex = markup.lastIndexOf("|", markup.lastIndexOf("|", markup.indexOf("=")) - 1);
                markup = markup.substring(markup.indexOf("{{convert") + 10, dividerIndex).replace("|", " ");
            }
            else if (markup.contains("{{cvt")) {
                int dividerIndex = markup.lastIndexOf("|", markup.lastIndexOf("|", markup.indexOf("=")) - 1);
                markup = markup.substring(markup.indexOf("{{cvt") + 6, dividerIndex).replace("|", " ");
            } else if (markup.contains("{{nowrap")) {
                markup = markup.substring(markup.lastIndexOf("|") + 1, markup.lastIndexOf("}}"));
            } else if (markup.contains("{{")){
                markup = markup.substring(0, markup.indexOf("{{"));
            }
        }
        else if (markup.startsWith("[[") && markup.endsWith("]]")){
            markup = markup.substring(2, markup.length() - 2);
            if (markup.contains("|")) {
                markup = markup.substring(0, markup.indexOf("|"));
            }
        }
        return markup;
    }

    public static boolean isSpecialLink(String link) {
        return link != null && Stream.of("Image:", "File:", ":File:", "Category:", "Wiktionary:", "wikt:", "#", "s:").anyMatch(
                (x) -> link.strip().startsWith(x)
        );
    }
}