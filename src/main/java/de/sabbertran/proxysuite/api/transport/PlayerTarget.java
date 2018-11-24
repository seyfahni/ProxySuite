package de.sabbertran.proxysuite.api.transport;

import java.util.Objects;
import java.util.UUID;

/**
 * Teleport to a target player.
 */
public class PlayerTarget implements TeleportTarget {
    
    private final UUID uuid;

    public PlayerTarget(UUID uuid) {
        this.uuid = uuid;
    }

    @Override
    public void teleportPlayer(org.bukkit.Server server, org.bukkit.entity.Player toTeleport) {
        org.bukkit.OfflinePlayer player = server.getOfflinePlayer(uuid);
        if (player.isOnline()) {
            toTeleport.teleport(player.getPlayer(), org.bukkit.event.player.PlayerTeleportEvent.TeleportCause.COMMAND);
        }
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 59 * hash + Objects.hashCode(this.uuid);
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
        final PlayerTarget other = (PlayerTarget) obj;
        if (!Objects.equals(this.uuid, other.uuid)) {
            return false;
        }
        return true;
    }
    
}
