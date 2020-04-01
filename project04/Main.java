package edu.caltech.cs2.project04;

import java.io.*;
import java.net.InetSocketAddress;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import edu.caltech.cs2.interfaces.IDeque;

import java.awt.Desktop;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Stream;

public class Main {
    public static final int PORT = 8000;
    private static final int NUMBER_OF_OPTIONS = 15;
    public static Map<String, Double> RATINGS;

    /**
     * TODO: CHANGE THE TYPE OF THIS VARIABLE TO SWITCH
     *       IMPLEMENTATIONS OF AUTOCOMPLETE!
     **/
    private static HashMovieAutoCompleter COMPLETER;

    public static void main(String[] args) throws Exception {
        System.out.println("Loading Completer...");
        COMPLETER.populateTitles();
        System.out.println("Completer loaded!");
        System.out.println("Parsing Ratings...");
        RATINGS = parseRatings();
        System.out.println("Ratings parsed!");
        HttpServer server = HttpServer.create(new InetSocketAddress(PORT), 0);
        server.createContext("/", new LocalFile("movies.html"));
        server.createContext("/css/autocomplete.css", new LocalFile("css/autocomplete.css"));
        server.createContext("/js/autocomplete.js", new LocalFile("js/autocomplete.js"));
        server.createContext("/js/typeahead.js", new LocalFile("js/typeahead.js"));
        server.createContext("/autocomplete", new Autocomplete());
        server.setExecutor(null); // creates a default executor
        server.start();
        System.out.println("Server started!");

        if (Desktop.isDesktopSupported()) {
            Desktop.getDesktop().browse(new URI("http://localhost:" + PORT + "/"));
        }
    }

    private static Map<String, Double> parseRatings() {
        Map<String, Double> ratings = new HashMap<>();
        try {
            Scanner s = new Scanner(new File("data/title.ratings.tsv"));
            s.nextLine();
            while (s.hasNextLine()) {
                String[] line = s.nextLine().split("\\s+");
                if (Integer.parseInt(line[2]) > 1000) {
                    ratings.put(line[0], Double.parseDouble(line[1]));
                }
            }
        } catch (FileNotFoundException e) {
            return ratings;
        }
        return ratings;
    }

    static class Autocomplete implements HttpHandler {
        @Override
        public void handle(HttpExchange t) throws IOException {
            Optional<String> query = Stream.of(t.getRequestURI().getQuery().split("\\&")).filter(x -> x.startsWith("query=")).map(x -> x.split("=")[1]).findAny();
            String response = "[]";
            if (query.isPresent()) {
                long before = System.currentTimeMillis();
                IDeque<String> options = COMPLETER.complete(query.get());
                String[] opts = new String[options.size()];
                for (int i = 0; i < opts.length; i++) {
                    opts[i] = options.removeFront();
                }
                Arrays.sort(opts, (x, y) -> {
                    Double a = RATINGS.get(COMPLETER.getIDMap().get(x));
                    Double b = RATINGS.get(COMPLETER.getIDMap().get(y));
                    if (a == null) {
                        a = 0.0;
                    }

                    if (b == null) {
                        b = 0.0;
                    }
                    return -Double.compare(a, b);
                });
                long after = System.currentTimeMillis();
                StringBuilder result = new StringBuilder();
                for (int i = 0; i < opts.length && i < NUMBER_OF_OPTIONS; i++) {
                    result.append("{\"value\": \"" + opts[i].replace("\"", "\\\"") + "\"}, ");
                }
                System.out.println("\"" + query.get() + "\" took " + (after - before) + " milliseconds!");
                if (result.length() > 0) {
                    response = result.toString();
                    response = "[" + response.substring(0, response.length() - 2) + "]";
                }

            }
            t.sendResponseHeaders(200, 0);
            OutputStream os = t.getResponseBody();
            os.write(response.getBytes());
            os.close();
        }
    }

    static class LocalFile implements HttpHandler {
        private String name;

        public LocalFile(String name) {
            this.name = name;
        }

        @Override
        public void handle(HttpExchange t) throws IOException {
            String response = new String(Files.readAllBytes(Paths.get(this.name)), StandardCharsets.UTF_8);
            t.sendResponseHeaders(200, 0);
            OutputStream os = t.getResponseBody();
            os.write(response.getBytes());
            os.close();
        }
    }
}