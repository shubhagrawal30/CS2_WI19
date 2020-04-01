package edu.caltech.cs2.project07;


import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import edu.caltech.cs2.datastructures.BeaverMapsGraph;
import edu.caltech.cs2.datastructures.LinkedDeque;
import edu.caltech.cs2.datastructures.Location;
import edu.caltech.cs2.interfaces.IDeque;
import edu.caltech.cs2.interfaces.ISet;

import java.awt.*;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Stream;

public class BeaverMaps {
    public static final int PORT = 8001;
    public static final int NUMBER_OF_OPTIONS = 15;
    private static BeaverMapsGraph graph;
    private static MapsAutoCompleter COMPLETER;

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

    public static void main(String[] args) throws Exception {
        long start = System.currentTimeMillis();
        graph = new BeaverMapsGraph("data/pasadena.buildings", "data/pasadena.waypoints", "data/pasadena.roads");
        long end = System.currentTimeMillis();
        System.out.println("Reading data took " + (end - start) + " millis.");
        System.out.println("Populating autocomplete");
        COMPLETER.populateLocations(graph.getBuildings());
        System.out.println("Done populating autocomplete");

        HttpServer server = HttpServer.create(new InetSocketAddress(PORT), 0);
        server.createContext("/", new LocalFile("map.html"));
        server.createContext("/css/map.css", new LocalFile("css/map.css"));
        server.createContext("/js/map.js", new LocalFile("js/map.js"));
        server.createContext("/js/autocomplete.js", new LocalFile("js/autocomplete.js"));
        server.createContext("/js/typeahead.js", new LocalFile("js/typeahead.js"));
        server.createContext("/byname", new AllWithNameSearch());
        server.createContext("/nearby", new LocationSearch());
        server.createContext("/pathfinder", new PathFinder());
        server.createContext("/autocomplete", new Autocomplete());
        server.createContext("/nearest", new NearestSearch());
        server.setExecutor(null); // creates a default executor
        server.start();
        System.out.println("Server started!");

        if (Desktop.isDesktopSupported()) {
            Desktop.getDesktop().browse(new URI("http://localhost:" + PORT + "/"));
        }
    }

    static class PathFinder implements HttpHandler {
        @Override
        public void handle(HttpExchange t) throws IOException {
            Optional<String> start = Stream.of(t.getRequestURI().getQuery().split("\\&")).filter(x -> x.startsWith("start=")).map(x -> x.split("=")[1]).findAny();
            Optional<String> start_id = Stream.of(t.getRequestURI().getQuery().split("\\&")).filter(x -> x.startsWith("start-id=")).map(x -> x.split("=")[1]).findAny();
            Optional<String> end = Stream.of(t.getRequestURI().getQuery().split("\\&")).filter(x -> x.startsWith("end=")).map(x -> x.split("=")[1]).findAny();
            Optional<String> end_id = Stream.of(t.getRequestURI().getQuery().split("\\&")).filter(x -> x.startsWith("end-id=")).map(x -> x.split("=")[1]).findAny();
            String response = "[]";

            if (start.isPresent() && end.isPresent()) {
                String startL = start.get();
                String endL = end.get();
                String startID = start_id.orElse(null);
                String endID = end_id.orElse(null);
                Location startLocation = startID != null ?
                        graph.getLocationByID(Long.parseLong(startID)) :
                        graph.getLocationByName(startL).peek();
                Location endLocation = endID != null ?
                        graph.getLocationByID(Long.parseLong(endID)) :
                        graph.getLocationByName(endL).peek();

                if (startLocation != null && endLocation != null) {
                    IDeque<Location> locs = graph.dijkstra(startLocation, endLocation);
                    IDeque<String> path = new LinkedDeque<>();

                    int i = 0;
                    for (Location loc : locs) {
                        String locStr = loc.lat + "::" + loc.lon + "::" + ("" + i + ":" + (loc.name != null ? " " + loc.name : "") + "::" + loc.id);
                        path.add(locStr);
                        i++;
                    }

                    response = path.toString();
                }
            }

            t.sendResponseHeaders(200, 0);
            OutputStream os = t.getResponseBody();
            os.write(response.getBytes());
            os.close();
        }
    }

    static class NearestSearch implements HttpHandler {
        @Override
        public void handle(HttpExchange t) throws IOException {
            Optional<String> lat = Stream.of(t.getRequestURI().getQuery().split("\\&")).filter(x -> x.startsWith("lat=")).map(x -> x.split("=")[1]).findAny();
            Optional<String> lon = Stream.of(t.getRequestURI().getQuery().split("\\&")).filter(x -> x.startsWith("lon=")).map(x -> x.split("=")[1]).findAny();
            String response = "";

            if (lat.isPresent() && lon.isPresent()) {
                String latS = lat.get();
                String lonS = lon.get();
                Location l = graph.getClosestBuilding(Double.parseDouble(latS), Double.parseDouble(lonS));

                if (l != null) {
                    response = "{\"id\": " + l.id + ", \"name\": \"" + l.name + "\"" + ", \"lat\":" + l.lat + ", \"lon\": " + l.lon + "}";
                }
            }

            t.sendResponseHeaders(200, 0);
            OutputStream os = t.getResponseBody();
            os.write(response.getBytes());
            os.close();
        }
    }

    static class AllWithNameSearch implements HttpHandler {
        @Override
        public void handle(HttpExchange t) throws IOException {
            Optional<String> query = Stream.of(t.getRequestURI().getQuery().split("\\&")).filter(x -> x.startsWith("query=")).map(x -> x.split("=")[1]).findAny();
            String response = "[]";

            if (query.isPresent()) {
                String location = query.get();
                IDeque<Location> locs = graph.getLocationByName(location);

                if (!locs.isEmpty()) {
                    IDeque<String> locList = new LinkedDeque<>();

                    for (Location loc : locs) {
                        if (loc.type == Location.Type.BUILDING) {
                            String locStr = loc.lat + "::" + loc.lon + "::" + loc.name + "::" + loc.id;
                            locList.add(locStr);
                        }
                    }

                    response = locList.toString();
                }
            }

            t.sendResponseHeaders(200, 0);
            OutputStream os = t.getResponseBody();
            os.write(response.getBytes());
            os.close();
        }
    }

    static class LocationSearch implements HttpHandler {
        @Override
        public void handle(HttpExchange t) throws IOException {
            Optional<String> name = Stream.of(t.getRequestURI().getQuery().split("\\&")).filter(x -> x.startsWith("name=")).map(x -> x.split("=")[1]).findAny();
            Optional<String> id = Stream.of(t.getRequestURI().getQuery().split("\\&")).filter(x -> x.startsWith("id=")).map(x -> x.split("=")[1]).findAny();
            Optional<String> distance = Stream.of(t.getRequestURI().getQuery().split("\\&")).filter(x -> x.startsWith("distance=")).map(x -> x.split("=")[1]).findAny();
            String response = "[]";

            if (name.isPresent()) {
                String nameS = name.get();
                String idS = id.orElse(null);
                Location l = idS != null ?
                        graph.getLocationByID(Long.parseLong(idS)) :
                        graph.getLocationByName(nameS).peek();

                if (l != null) {
                    IDeque<String> locList = new LinkedDeque<>();
                    double dist = Double.parseDouble(distance.orElse("200"));
                    ISet<Location> closeLocs = graph.dfs(l, dist * 2);
                    closeLocs.remove(l);

                    for (Location loc : closeLocs) {
                        if (l.getDistance(loc) < dist && loc.type == Location.Type.BUILDING) {
                            String locStr = loc.lat + "::" + loc.lon + "::" + loc.name + "::" + loc.id;
                            locList.add(locStr);
                        }
                    }

                    locList.addFront(l.lat + "::" + l.lon + "::" + l.name);

                    response = locList.toString();
                }
            }

            t.sendResponseHeaders(200, 0);
            OutputStream os = t.getResponseBody();
            os.write(response.getBytes());
            os.close();
        }
    }

    static class Autocomplete implements HttpHandler {
        @Override
        public void handle(HttpExchange t) throws IOException {
            Optional<String> query = Stream.of(t.getRequestURI().getQuery().split("\\&")).filter(x -> x.startsWith("query=")).map(x -> x.split("=")[1]).findAny();
            String response = "[]";
            if (query.isPresent()) {
                long before = System.currentTimeMillis();
                IDeque<Long> options = COMPLETER.complete(query.get());
                String[] opts = new String[options.size()];
                Long[] ids = new Long[options.size()];

                for (int i = 0; i < opts.length; i++) {
                    long id = options.removeFront();
                    ids[i] = id;
                    opts[i] = graph.getLocationByID(id).displayString();
                }

                long after = System.currentTimeMillis();
                StringBuilder result = new StringBuilder();
                for (int i = 0; i < opts.length && i < NUMBER_OF_OPTIONS; i++) {
                    result.append("{\"value\": \"" + opts[i].replace("\"", "\\\"") + "\", \"id\": " + ids[i] + "}, ");
                }
                System.out.println("\"" + query.get() + "\" took " + (after - before) + " milliseconds!");
                if (result.length() > 0) {
                    response = result.toString();
                    response = "[" + response.substring(0, response.length() - 2) + "]";
                }

            }

            System.out.println(response);
            t.sendResponseHeaders(200, 0);
            OutputStream os = t.getResponseBody();
            os.write(response.getBytes());
            os.close();
        }
    }
}
