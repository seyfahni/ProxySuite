package de.sabbertran.proxysuite.api.transport;

import com.google.gson.Gson;
import de.sabbertran.proxysuite.api.transport.bukkit.BukkitLocationTarget;
import de.sabbertran.proxysuite.api.transport.bukkit.BukkitTeleportTarget;
import de.sabbertran.proxysuite.api.transport.bungee.BungeeLocationTarget;
import de.sabbertran.proxysuite.api.transport.bungee.BungeeTeleportTarget;
import de.sabbertran.proxysuite.utils.Location;
import de.sabbertran.proxysuite.utils.Regestry;
import java.util.Objects;

/**
 * Teleport to a target player.
 */
public class LocationTarget implements TeleportTarget {
    
    private final Location targetLocation;

    public LocationTarget(Location targetLocation) {
        this.targetLocation = Objects.requireNonNull(targetLocation);
    }

    @Override
    public BukkitTeleportTarget getBukkitTeleportTarget() {
        return new BukkitLocationTarget(targetLocation);
    }

    @Override
    public BungeeTeleportTarget getBungeeTeleportTarget() {
        return new BungeeLocationTarget(targetLocation);
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 37 * hash + Objects.hashCode(this.targetLocation);
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
        final LocationTarget other = (LocationTarget) obj;
        if (!Objects.equals(this.targetLocation, other.targetLocation)) {
            return false;
        }
        return true;
    }
    
    @Override
    public String toString() {
        return Regestry.optionalGson().orElseGet(Gson::new).toJson(this);
    }

}
