package de.sabbertran.proxysuite.api.transport;

import de.sabbertran.proxysuite.utils.Location;
import java.util.Objects;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;

/**
 * Teleport to a target player.
 */
public class LocationTarget implements TeleportTarget {
    
    private final Location targetLocation;

    public LocationTarget(Location targetLocation) {
        this.targetLocation = Objects.requireNonNull(targetLocation);
    }

    @Override
    public void teleportToTarget(org.bukkit.Server server, org.bukkit.entity.Player toTeleport) {
        org.bukkit.Location bukkitLocation = targetLocation.toBukkitLocation(server);
        toTeleport.teleport(bukkitLocation, org.bukkit.event.player.PlayerTeleportEvent.TeleportCause.COMMAND);
    }

    @Override
    public ServerInfo getTargetServer(ProxyServer proxy) {
        return proxy.getServerInfo(targetLocation.getServer());
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
    
}
