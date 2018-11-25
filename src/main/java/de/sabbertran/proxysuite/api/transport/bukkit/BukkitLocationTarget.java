package de.sabbertran.proxysuite.api.transport.bukkit;

import com.google.gson.Gson;
import de.sabbertran.proxysuite.utils.Location;
import de.sabbertran.proxysuite.utils.Regestry;
import java.util.Objects;

/**
 * Teleport to a target location on a bukkit server.
 */
public class BukkitLocationTarget implements BukkitTeleportTarget {
    
    private final Location targetLocation;

    public BukkitLocationTarget(Location targetLocation) {
        this.targetLocation = Objects.requireNonNull(targetLocation);
    }

    @Override
    public void teleportToTarget(org.bukkit.Server server, org.bukkit.entity.Player toTeleport) {
        org.bukkit.Location bukkitLocation = targetLocation.toBukkitLocation(server);
        if (bukkitLocation.getWorld() == null) {
            bukkitLocation.setWorld(toTeleport.getWorld());
        }
        if (bukkitLocation.getY() == Double.MAX_VALUE) {
            bukkitLocation.setY(bukkitLocation.getWorld().getHighestBlockYAt(bukkitLocation));
        }
        toTeleport.teleport(bukkitLocation, org.bukkit.event.player.PlayerTeleportEvent.TeleportCause.COMMAND);
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
        final BukkitLocationTarget other = (BukkitLocationTarget) obj;
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
