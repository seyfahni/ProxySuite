package de.sabbertran.proxysuite.api.transport.bungee;

import com.google.gson.Gson;
import de.sabbertran.proxysuite.utils.Regestry;
import java.util.Objects;
import java.util.UUID;

/**
 * Teleport to a target player on a bungee proxy.
 */
public class BungeePlayerTarget implements BungeeTeleportTarget {
    
    private final UUID targetPlayer;

    public BungeePlayerTarget(UUID targetPlayer) {
        this.targetPlayer = Objects.requireNonNull(targetPlayer);
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
        final BungeePlayerTarget other = (BungeePlayerTarget) obj;
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
