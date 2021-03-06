package de.sabbertran.proxysuite.api.transport;

import com.google.gson.Gson;
import de.sabbertran.proxysuite.api.transport.bukkit.BukkitPlayerTarget;
import de.sabbertran.proxysuite.api.transport.bukkit.BukkitTeleportTarget;
import de.sabbertran.proxysuite.api.transport.bungee.BungeePlayerTarget;
import de.sabbertran.proxysuite.api.transport.bungee.BungeeTeleportTarget;
import de.sabbertran.proxysuite.utils.Regestry;
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
    public BukkitTeleportTarget getBukkitTeleportTarget() {
        return new BukkitPlayerTarget(targetPlayer);
    }

    @Override
    public BungeeTeleportTarget getBungeeTeleportTarget() {
        return new BungeePlayerTarget(targetPlayer);
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
    
    @Override
    public String toString() {
        return Regestry.optionalGson().orElseGet(Gson::new).toJson(this);
    }

}
