package de.sabbertran.proxysuite.api.transport.bungee;

import com.google.gson.Gson;
import de.sabbertran.proxysuite.utils.Location;
import de.sabbertran.proxysuite.utils.Regestry;
import java.util.Objects;

/**
 * Teleport to a target location on a bungee proxy.
 */
public class BungeeLocationTarget implements BungeeTeleportTarget {
    
    private final Location targetLocation;

    public BungeeLocationTarget(Location targetLocation) {
        this.targetLocation = Objects.requireNonNull(targetLocation);
    }

    @Override
    public net.md_5.bungee.api.config.ServerInfo getTargetServer(net.md_5.bungee.api.ProxyServer proxy) {
        return targetLocation.getServer() == null ? null : proxy.getServerInfo(targetLocation.getServer());
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
        final BungeeLocationTarget other = (BungeeLocationTarget) obj;
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
