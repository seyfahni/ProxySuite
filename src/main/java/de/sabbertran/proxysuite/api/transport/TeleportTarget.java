package de.sabbertran.proxysuite.api.transport;

/**
 * 
 */
public interface TeleportTarget {

    /**
     * Teleport the given player to this target.
     *
     * @param server the server to work on
     * @param toTeleport the player to teleport
     */
    void teleportPlayer(org.bukkit.Server server, org.bukkit.entity.Player toTeleport);
}
