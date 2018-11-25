package de.sabbertran.proxysuite.bukkit.teleport;

import de.sabbertran.proxysuite.api.transport.bukkit.BukkitTeleportTarget;
import de.sabbertran.proxysuite.bukkit.ProxySuiteBukkit;
import org.bukkit.OfflinePlayer;

/**
 *
 */
public class TeleportWarmupTask implements Runnable {

    private final ProxySuiteBukkit main;
    private final OfflinePlayer player;
    private final BukkitTeleportTarget target;

    public TeleportWarmupTask(ProxySuiteBukkit main, OfflinePlayer player, BukkitTeleportTarget target) {
        this.main = main;
        this.player = player;
        this.target = target;
    }

    @Override
    public void run() {
        if (!player.isOnline()) {
            main.getPendingTeleportRequests().put(player, target);
        } else {
            target.teleportToTarget(main.getServer(), player.getPlayer());
        }
    }
    
}
