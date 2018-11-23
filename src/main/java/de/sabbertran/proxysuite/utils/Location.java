package de.sabbertran.proxysuite.utils;

import de.sabbertran.proxysuite.utils.Regestry;
import com.google.gson.Gson;

public final class Location {

    private final String server, world;
    private final double x, y, z;
    private final float pitch, yaw;

    public Location(String server, String world) {
        this(server, world, Double.NaN, Double.NaN, Double.NaN);
    }

    public Location(String server, String world, double x, double y, double z) {
        this(server, world, x, y, z, Float.NaN, Float.NaN);
    }

    public Location(String server, String world, double x, double y, double z, float pitch, float yaw) {
        this.server = server != null ? server : "";
        this.world = world != null ? world : "";
        this.x = x;
        this.y = y;
        this.z = z;
        this.pitch = pitch;
        this.yaw = yaw;
    }

    public Location(Location location) {
        this.server = location.server;
        this.world = location.world;
        this.x = location.x;
        this.y = location.y;
        this.z = location.z;
        this.pitch = location.pitch;
        this.yaw = location.yaw;
    }

    public String getServer() {
        return server;
    }
    
    public String getWorld() {
        return world;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getZ() {
        return z;
    }

    public int getBlockX() {
        return (int) Math.floor(x);
    }

    public int getBlockY() {
        return (int) Math.floor(y);
    }

    public int getBlockZ() {
        return (int) Math.floor(z);
    }

    public float getPitch() {
        return pitch;
    }

    public float getYaw() {
        return yaw;
    }

    @Override
    public String toString() {
        return Regestry.optionalGson().orElseGet(Gson::new).toJson(this);
    }

    public boolean hasPosition() {
        return x != Double.NaN && y != Double.NaN && z != Double.NaN;
    }

    public boolean hasDirection() {
        return yaw != Float.NaN && pitch != Float.NaN;
    }

    public org.bukkit.Location toBukkitLocation(org.bukkit.Server server) {
        org.bukkit.World bukkitWorld = server.getWorld(getWorld());
        if (bukkitWorld == null) bukkitWorld = server.getWorlds().get(0);
        org.bukkit.Location bukkitLocation;
        if (hasPosition()) {
            bukkitLocation = new org.bukkit.Location(bukkitWorld, getX(), getY(), getZ());
        } else {
            bukkitLocation = bukkitWorld.getSpawnLocation();
        }
        if (hasDirection()) {
            bukkitLocation.setYaw(getYaw());
            bukkitLocation.setPitch(getPitch());
        }
        return bukkitLocation;
    }
    
}
