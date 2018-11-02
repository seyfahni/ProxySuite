package de.sabbertran.proxysuite.bungee.utils;

public class Warp {
    private String name;
    private Location location;
    private boolean local;
    private boolean hidden;

    public Warp(String name, Location location, boolean local, boolean hidden) {
        this.name = name;
        this.location = location;
        this.local = local;
        this.hidden = hidden;
    }

    public String getName() {
        return name;
    }

    public Location getLocation() {
        return location;
    }

    public boolean isLocal() {
        return local;
    }

    public boolean isHidden() {
        return hidden;
    }
}
