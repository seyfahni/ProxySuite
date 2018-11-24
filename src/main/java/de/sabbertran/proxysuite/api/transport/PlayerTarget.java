package de.sabbertran.proxysuite.api.transport;

import java.util.Objects;
import java.util.UUID;

/**
 * Teleport to a target player.
 */
public class PlayerTarget implements TeleportTarget {
    
    private final UUID targetPlayer;

    public PlayerTarget(UUID targetPlayer) {
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
    public net.md_5.bungee.api.config.ServerInfo getTargetServer(net.md_5.bungee.api.ProxyServer proxy) {
        net.md_5.bungee.api.connection.ProxiedPlayer player = proxy.getPlayer(targetPlayer);
        if (player != null) {
            return player.getServer().getInfo();
        } else {
            return null;
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
        final PlayerTarget other = (PlayerTarget) obj;
        if (!Objects.equals(this.targetPlayer, other.targetPlayer)) {
            return false;
        }
        return true;
    }
    
}
