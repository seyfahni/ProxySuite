package de.sabbertran.proxysuite.api.transport.bukkit;

import com.google.gson.Gson;
import de.sabbertran.proxysuite.utils.Regestry;
import java.util.Objects;
import java.util.UUID;

/**
 * Teleport to a target player on a bukkit server.
 */
public class BukkitPlayerTarget implements BukkitTeleportTarget {
    
    private final UUID targetPlayer;

    public BukkitPlayerTarget(UUID targetPlayer) {
        this.targetPlayer = Objects.requireNonNull(targetPlayer);
    }

    @Override
    public void teleportToTarget(org.bukkit.Server server, org.bukkit.entity.Player toTeleport) {
        org.bukkit.OfflinePlayer player = server.getOfflinePlayer(targetPlayer);
        if (player.isOnline()) {
            toTeleport.teleport(player.getPlayer(), org.bukkit.event.player.PlayerTeleportEvent.TeleportCause.COMMAND);
        }
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 59 * hash + Objects.hashCode(this.targetPlayer);
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
        final BukkitPlayerTarget other = (BukkitPlayerTarget) obj;
        if (!Objects.equals(this.targetPlayer, other.targetPlayer)) {
            return false;
        }
        return true;
    }
    
    @Override
    public String toString() {
        return Regestry.optionalGson().orElseGet(Gson::new).toJson(this);
    }

}
