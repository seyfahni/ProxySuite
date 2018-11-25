package de.sabbertran.proxysuite.api.transport;

import de.sabbertran.proxysuite.api.transport.bukkit.BukkitTeleportTarget;
import de.sabbertran.proxysuite.api.transport.bungee.BungeeTeleportTarget;

/**
 * 
 */
public interface TeleportTarget {

    BukkitTeleportTarget getBukkitTeleportTarget();

    BungeeTeleportTarget getBungeeTeleportTarget();
}
