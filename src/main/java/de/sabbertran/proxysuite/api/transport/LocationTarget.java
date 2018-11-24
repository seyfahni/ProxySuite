package de.sabbertran.proxysuite.api.transport;

import de.sabbertran.proxysuite.utils.Location;
import java.util.Objects;

/**
 * Teleport to a target player.
 */
public class LocationTarget implements TeleportTarget {
    
    private final Location location;

    public LocationTarget(Location location) {
        this.location = location;
    }

    @Override
    public void teleportPlayer(org.bukkit.Server server, org.bukkit.entity.Player toTeleport) {
        org.bukkit.Location bukkitLocation = location.toBukkitLocation(server);
        toTeleport.teleport(bukkitLocation, org.bukkit.event.player.PlayerTeleportEvent.TeleportCause.COMMAND);
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 37 * hash + Objects.hashCode(this.location);
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
        if (!Objects.equals(this.location, other.location)) {
            return false;
        }
        return true;
    }
    
}
