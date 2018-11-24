package de.sabbertran.proxysuite.utils;

import com.google.gson.Gson;
import java.util.Objects;

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
        this(location.server, location.world, location.x, location.y, location.z, location.pitch, location.yaw);
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

    /**
     * Convert this location into a (Bukkit-){@link org.bukkit.Location}. Beware: The world may be set to null, if no
     * valid world could be found.
     *
     * @param server the server to work on
     * @return the Bukkit-Location
     */
    public org.bukkit.Location toBukkitLocation(org.bukkit.Server server) {
        org.bukkit.World bukkitWorld = server.getWorld(getWorld());
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

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 41 * hash + Objects.hashCode(this.server);
        hash = 41 * hash + Objects.hashCode(this.world);
        hash = 41 * hash + (int) (Double.doubleToLongBits(this.x) ^ (Double.doubleToLongBits(this.x) >>> 32));
        hash = 41 * hash + (int) (Double.doubleToLongBits(this.y) ^ (Double.doubleToLongBits(this.y) >>> 32));
        hash = 41 * hash + (int) (Double.doubleToLongBits(this.z) ^ (Double.doubleToLongBits(this.z) >>> 32));
        hash = 41 * hash + Float.floatToIntBits(this.pitch);
        hash = 41 * hash + Float.floatToIntBits(this.yaw);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Location other = (Location) obj;
        if (Double.doubleToLongBits(this.x) != Double.doubleToLongBits(other.x)) {
            return false;
        }
        if (Double.doubleToLongBits(this.y) != Double.doubleToLongBits(other.y)) {
            return false;
        }
        if (Double.doubleToLongBits(this.z) != Double.doubleToLongBits(other.z)) {
            return false;
        }
        if (Float.floatToIntBits(this.pitch) != Float.floatToIntBits(other.pitch)) {
            return false;
        }
        if (Float.floatToIntBits(this.yaw) != Float.floatToIntBits(other.yaw)) {
            return false;
        }
        if (!Objects.equals(this.server, other.server)) {
            return false;
        }
        if (!Objects.equals(this.world, other.world)) {
            return false;
        }
        return true;
    }
    
}
