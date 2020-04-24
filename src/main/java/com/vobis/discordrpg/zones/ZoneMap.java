package com.vobis.discordrpg.zones;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ZoneMap {

    private static final ObjectMapper MAPPER = new ObjectMapper();
    private static final String ZONES_FILE = "zones.json";

    private Map<String, Zone> zoneNameMap = new HashMap<>();
    private Map<Location, Zone> zoneLocationMap = new HashMap<>();

    public ZoneMap() {
        try {
            loadZones();
        } catch (IOException ex) {
            throw new RuntimeException("Could not load zones from " + ZONES_FILE, ex);
        }
    }

    public Zone getZone(String channelName) {
        return zoneNameMap.get(channelName);
    }

    public Zone getZone(Location location) {
        return zoneLocationMap.get(location);
    }

    /**
     * @return An array of neighbours, ordered [north, east, south west].
     * Empty cells mean there was no neighbouring zone.
     */
    public Zone[] getNeighbours(Location location) {
        Zone[] neighbours = new Zone[4];

        neighbours[0] = getZone(location.add(0, 1));
        neighbours[1] = getZone(location.add(1, 0));
        neighbours[2] = getZone(location.add(0, -1));
        neighbours[3] = getZone(location.add(-1, 0));

        return neighbours;
    }

    private void loadZones() throws IOException {
        MAPPER.readValue(ClassLoader.getSystemClassLoader().getResourceAsStream(ZONES_FILE), new TypeReference<ArrayList<Zone>>() {
        })
                .forEach(this::addZone);
    }

    private void addZone(Zone zone) {
        zoneNameMap.put(zone.getChannelName(), zone);
        zoneLocationMap.put(zone.getLocation(), zone);
    }
}
