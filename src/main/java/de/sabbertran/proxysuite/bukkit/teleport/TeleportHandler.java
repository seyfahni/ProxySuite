package de.sabbertran.proxysuite.bukkit.teleport;

import com.google.common.io.ByteStreams;
import com.google.gson.JsonParseException;
import de.sabbertran.proxysuite.api.transport.TeleportRequest;
import de.sabbertran.proxysuite.bukkit.ProxySuiteBukkit;
import de.sabbertran.proxysuite.utils.Regestry;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;
import java.util.logging.Level;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;

/**
 *
 */
public class TeleportHandler implements PluginMessageListener {

    private final ProxySuiteBukkit main;

    public TeleportHandler(ProxySuiteBukkit main) {
        this.main = main;
    }

    @Override
    public void onPluginMessageReceived(String channel, Player pl, byte[] message) {
        final TeleportRequest teleportRequest = parseTeleportRequest(message);
        final UUID uuid = teleportRequest.getPlayer();
        final OfflinePlayer player = main.getServer().getOfflinePlayer(uuid);
        final long delay = calculateWarmupTicks(teleportRequest);
        if (delay > 0) {
            Runnable warmupTask = new TeleportWarmupTask(main, player, teleportRequest.getTarget());
            main.getServer().getScheduler().scheduleSyncDelayedTask(main, warmupTask, delay);
        } else {
            if (!player.isOnline()) {
                main.getPendingTeleportRequests().put(player, teleportRequest.getTarget());
            } else {
                teleportRequest.getTarget().teleportToTarget(main.getServer(), player.getPlayer());
            }
        }
    }
    
    private TeleportRequest parseTeleportRequest(byte[] message) {
        try {
            String teleportRequestJson = ByteStreams.newDataInput(message).readUTF();
            return Regestry.gson().fromJson(teleportRequestJson, TeleportRequest.class);
        } catch (IllegalStateException | JsonParseException ex) {
            main.getLogger().log(Level.SEVERE, "Received illegal plugin message.", ex);
            return null;
        }
    }

    private long calculateWarmupTicks(final TeleportRequest teleportRequest) {
        long delay = teleportRequest.getWarmup()
                .map(targetTime -> Instant.now().until(targetTime, ChronoUnit.MILLIS))
                .filter(millis -> millis > 0)
                .map(millis -> (millis - 1) / 50 + 1)
                .orElse(0L);
        return delay;
    }
}
