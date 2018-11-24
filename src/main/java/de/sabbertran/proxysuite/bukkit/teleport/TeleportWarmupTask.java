package de.sabbertran.proxysuite.bukkit.teleport;

import de.sabbertran.proxysuite.api.transport.TeleportTarget;
import de.sabbertran.proxysuite.bukkit.ProxySuiteBukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.plugin.Plugin;

/**
 *
 */
public class TeleportWarmupTask implements Runnable {

    private final ProxySuiteBukkit main;
    private final OfflinePlayer player;
    private final TeleportTarget target;

    public TeleportWarmupTask(ProxySuiteBukkit main, OfflinePlayer player, TeleportTarget target) {
        this.main = main;
        this.player = player;
        this.target = target;
    }

    @Override
    public void run() {
        if (!player.isOnline()) {
            main.getPendingTeleportRequests().put(player, target);
        } else {
            target.teleportPlayer(main.getServer(), player.getPlayer());
        }
    }
    
}
