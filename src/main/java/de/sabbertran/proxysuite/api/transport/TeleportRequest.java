package de.sabbertran.proxysuite.api.transport;

import com.google.gson.Gson;
import de.sabbertran.proxysuite.utils.Regestry;
import java.time.Instant;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

/**
 * A transmittable request to teleport a player to some target.
 */
public class TeleportRequest {
    
    private UUID player;
    private TeleportTarget target;
    private Instant warmup;

    public TeleportRequest(UUID player, TeleportTarget target) {
        this(player, target, null);
    }

    public TeleportRequest(UUID player, TeleportTarget target, Instant warmup) {
        this.player = player;
        this.target = target;
        this.warmup = warmup;
    }

    public UUID getPlayer() {
        return player;
    }
    
    public TeleportTarget getTarget() {
        return target;
    }
    
    public Optional<Instant> getWarmup() {
        return Optional.ofNullable(warmup);
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 29 * hash + Objects.hashCode(this.player);
        hash = 29 * hash + Objects.hashCode(this.target);
        hash = 29 * hash + Objects.hashCode(this.warmup);
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
        final TeleportRequest other = (TeleportRequest) obj;
        if (!Objects.equals(this.player, other.player)) {
            return false;
        }
        if (!Objects.equals(this.target, other.target)) {
            return false;
        }
        if (!Objects.equals(this.warmup, other.warmup)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return Regestry.optionalGson().orElseGet(Gson::new).toJson(this);
    }

}
