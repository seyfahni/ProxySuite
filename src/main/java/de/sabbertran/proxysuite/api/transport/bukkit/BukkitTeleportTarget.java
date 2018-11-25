package de.sabbertran.proxysuite.api.transport.bukkit;

import org.bukkit.Server;
import org.bukkit.entity.Player;

/**
 * 
 */
public interface BukkitTeleportTarget {

    /**
     * Teleport the given player to the target on this server.
     *
     * @param server the server to work on
     * @param toTeleport the player to teleport
     */
    void teleportToTarget(Server server, Player toTeleport);

}
