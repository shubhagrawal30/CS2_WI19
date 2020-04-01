package edu.caltech.cs2.project07;

import edu.caltech.cs2.datastructures.*;
import edu.caltech.cs2.interfaces.IDeque;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;
import java.io.File;
import java.io.FileReader;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class OSMToJSON {

    public static final QName NODE_ID = new QName("id");
    public static final QName NODE_LAT = new QName("lat");
    public static final QName NODE_LON = new QName("lon");
    public static final QName TAG_TYPE = new QName("k");
    public static final QName TAG_VAL = new QName("v");
    public static final QName ND_REF = new QName("ref");

    private static Map<Long, Location> tempNodes;
    private static Map<Long, Boolean> addedNodes;
    private static PrintWriter outbuilds;
    private static PrintWriter outways;
    private static PrintWriter outroads;

    public static void readTags(IDeque<String> tagNames, IDeque<String> tagValues, XMLEventReader reader) {
        try {
            while (reader.peek().isStartElement()) {
                StartElement data = reader.nextEvent().asStartElement();
                tagNames.addBack(getStringAttr(data, TAG_TYPE));
                tagValues.addBack(getStringAttr(data, TAG_VAL));
                // Self-closing tags are split into start and end
                // So read end
                reader.nextEvent();
                readNewlineEvent(reader);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void readNewlineEvent(XMLEventReader reader) {
        try {
            if (reader.peek().isCharacters() &&
                    reader.peek().asCharacters().toString().strip().length() == 0) {
                reader.nextEvent();
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String getStringAttr(StartElement se, QName name) {
        Attribute a = se.getAttributeByName(name);
        if (a == null) {
            return null;
        }
        return a.getValue();
    }

    public static void buildJSON(String xmlFile) {
        System.out.println("Starting...");
        tempNodes = new HashMap<>();
        addedNodes = new HashMap<>();
        try {
            outbuilds = new PrintWriter(new File("out.buildings"));
            outbuilds.print("[");
            outways = new PrintWriter(new File("out.waypoints"));
            outways.print("[");
            outroads = new PrintWriter(new File("out.roads"));
            outroads.print("[");
        } catch(Exception e) {
            System.exit(1);
        }

        XMLInputFactory factory = XMLInputFactory.newInstance();
        try {
            XMLEventReader reader = factory.createXMLEventReader(new FileReader(xmlFile));
            while (reader.hasNext()) {
                XMLEvent next = reader.nextEvent();

                if (next.isStartElement()) {
                    StartElement elem = next.asStartElement();
                    String elemType = elem.getName().getLocalPart();

                    // Parse single node
                    if (elemType.equals("node")) {
                        String id = getStringAttr(elem, NODE_ID);
                        double lat = Double.parseDouble(getStringAttr(elem, NODE_LAT));
                        double lon = Double.parseDouble(getStringAttr(elem, NODE_LON));
                        String name = null;
                        String amenity = null;
                        String shop = null;
                        IDeque<String> tagNames = new LinkedDeque<>();
                        IDeque<String> tagValues = new LinkedDeque<>();
                        readNewlineEvent(reader);
                        readTags(tagNames, tagValues, reader);
                        Location newNode = buildNode(Long.parseLong(id), lat, lon, tagNames, tagValues);

                        tempNodes.put(newNode.id, newNode);
                        if (newNode.type == Location.Type.BUILDING) {
                            outbuilds.println(newNode);
                        }
                    }

                    // Parse footpath / building shape / lots of things
                    // nd in ways always come after corresponding node tag
                    else if (elemType.equals("way")) {
                        IDeque<String> nodeIDs = new LinkedDeque<>();
                        IDeque<String> tagNames = new LinkedDeque<>();
                        IDeque<String> tagValues = new LinkedDeque<>();

                        readNewlineEvent(reader);
                        // XMLEvent tmp = reader.peek();
                        while (reader.peek().isStartElement() &&
                                reader.peek().asStartElement().getName().getLocalPart().equals("nd")) {
                            StartElement data = reader.nextEvent().asStartElement();
                            nodeIDs.addBack(getStringAttr(data, ND_REF));
                            // Read closing tag of self-closing
                            reader.nextEvent();
                            readNewlineEvent(reader);
                            // tmp = reader.peek();
                        }

                        readTags(tagNames, tagValues, reader);
                        addFootpath(nodeIDs, tagNames, tagValues, Long.parseLong(getStringAttr(elem, NODE_ID)));
                    }
                }
            }
            outroads.print("]");
            outroads.close();
            outbuilds.print("]");
            outbuilds.close();
            outways.print("]");
            outways.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println("Finished generating files...");
    }

    public static Location buildNode(long id, double lat, double lon, IDeque<String> tags, IDeque<String> tagValues) {
        Iterator<String> tagIter = tags.iterator();
        Iterator<String> valIter = tagValues.iterator();
        String name = null;
        String amenity = null;
        String shop = null;
        String addr_num = null;
        String addr_street = null;
        String addr_city = null;
        while (tagIter.hasNext()) {
            String tagType = tagIter.next();
            String tagVal = valIter.next();
            switch (tagType) {
                case "name":
                    name = tagVal;
                    break;
                case "amenity":
                    amenity = tagVal;
                    break;
                case "shop":
                    shop = tagVal;
                    break;
                case "addr:housenumber":
                    addr_num = tagVal;
                    break;
                case "addr:street":
                    addr_street = tagVal;
                    break;
                case "addr:city":
                    addr_city = tagVal;
                    break;
            }
        }

        String addr = null;
        if (addr_city != null && addr_num != null && addr_street != null) {
            addr = addr_num + " " + addr_street + ", " + addr_city;
        }
        // Only considering buildings that sell... something.
        if (name != null || amenity != null || shop != null || addr != null) {
            return new Location(id, lat, lon, name, addr, amenity, shop, "building");
        }
        return new Location(id, lat, lon, name, null, null, null, "footpath");
    }

    public static void addFootpath(IDeque<String> nodes, IDeque<String> tags, IDeque<String> tagValues, long wayID) {
        boolean isWalkable = false;
        String name = null;
        Iterator<String> tagIter = tags.iterator();
        Iterator<String> valIter = tagValues.iterator();
        while (tagIter.hasNext()) {
            String tagType = tagIter.next();
            String tagVal = valIter.next();
            switch (tagType) {
                case "name":
                    name = tagVal;
                    break;
                case "foot":
                    isWalkable = tagVal.equals("yes");
                    break;
                case "highway":
                    // Too many cases - just consider any road not the
                    // freeway to be "walkable"
                    isWalkable = true;//!tagVal.equals("motorway");
                    break;
            }
        }

        // If it's walkable, build edges along the footpath
        if (isWalkable) {
            Location prevNode = null;
            outroads.print("[");
            for (String sid : nodes) {
                long id = Long.parseLong(sid);
                Location currNode = null;
                currNode = tempNodes.get(id);
                currNode.name = name;
                if (addedNodes.put(id, true) == null && currNode.type != Location.Type.BUILDING) {
                    outways.println(currNode);
                }
                if (prevNode != null) {
                    double dist = prevNode.getDistance(currNode);
                    outroads.print("\"" + prevNode.id + "\", ");
                }
                prevNode = currNode;
            }
            outroads.println("\"" + prevNode.id + "\"]");
        }

        // If location we want to track, find center of building and add to buildingNodes
        else if (name != null) {
            double latSum = 0;
            double lonSum = 0;
            for (String id : nodes) {
                Location currNode = tempNodes.get(Long.parseLong(id));
                outways.println(currNode);
                outroads.println("[\"" + wayID + "\", " + "\"" + currNode.id + "\"]");
                latSum += currNode.lat;
                lonSum += currNode.lon;
            }
            Location newNode = buildNode(wayID, latSum / nodes.size(), lonSum / nodes.size(), tags, tagValues);
            if (newNode.type == Location.Type.BUILDING) {
                outbuilds.println(newNode);
            }
        }
    }

    public static void main(String[] args){
        OSMToJSON.buildJSON("data/old/pasadena.osm");
    }
}
