package de.sabbertran.proxysuite.bungee.handlers;

import de.sabbertran.proxysuite.api.transport.*;
import de.sabbertran.proxysuite.bungee.ProxySuite;
import de.sabbertran.proxysuite.bungee.utils.PendingTeleport;
import de.sabbertran.proxysuite.utils.Location;
import de.sabbertran.proxysuite.utils.Regestry;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.logging.Level;
import net.md_5.bungee.api.config.ServerInfo;

public class TeleportHandler {

    private final HashMap<ProxiedPlayer, Location> lastPositions;
    private final ProxySuite main;
    private final ArrayList<PendingTeleport> pendingTeleports;
    private final HashMap<ProxiedPlayer, Date> lastTeleports;

    public TeleportHandler(ProxySuite main) {
        this.main = main;

        pendingTeleports = new ArrayList<>();
        lastTeleports = new HashMap<>();
        lastPositions = new HashMap<>();
    }

    public void teleportToLocation(ProxiedPlayer player, Location location, boolean ignoreCooldown, boolean ignoreBackSave, boolean ignoreWarmup) {
        teleportToTarget(player, new LocationTarget(location), ignoreCooldown, ignoreBackSave, ignoreWarmup);
    }

    public void teleportToPlayer(ProxiedPlayer player, ProxiedPlayer to, boolean ignoreCooldown, boolean ignoreBackSave, boolean ignoreWarmup) {
        teleportToTarget(player, new PlayerTarget(to.getUniqueId()), ignoreCooldown, ignoreBackSave, ignoreWarmup);
    }

    public void teleportToTarget(ProxiedPlayer player, TeleportTarget target, boolean ignoreCooldown, boolean ignoreBackSave, boolean ignoreWarmup) {
        if (ignoreCooldown || getRemainingCooldown(player) == 0) {
            if (!ignoreBackSave) {
                savePlayerLocation(player);
            }

            TeleportRequest request;
            if (ignoreWarmup) {
                request = new TeleportRequest(player.getUniqueId(), target);
            } else {
                Instant warmup = Instant.now().plus(getWarmupDuration(player.getName()));
                request = new TeleportRequest(player.getUniqueId(), target, warmup);
            }
            sendPlayerAndRequestToTargetServer(request, player);

            lastTeleports.put(player, new Date());
        }
    }

    private void sendPlayerAndRequestToTargetServer(TeleportRequest teleportRequest, ProxiedPlayer player) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            DataOutputStream out = new DataOutputStream(baos);
            String json = Regestry.gson().toJson(teleportRequest);
            out.writeUTF(json);
        } catch (IOException e) {
            main.getLogger().log(Level.SEVERE, null, e);
        }
        TeleportTarget target = teleportRequest.getTarget();
        ServerInfo targetServer = target.getTargetServer(main.getProxy());
        if (targetServer != null) {
            targetServer.sendData("proxysuite:teleport", baos.toByteArray());
            target.connectToServer(main.getProxy(), player);
        } else {
            main.getLogger().log(Level.SEVERE, "teleport request failed: {0}", teleportRequest);
        }
    }

    private void savePlayerLocation(final ProxiedPlayer p) {
        if (main.getPermissionHandler().hasPermission(p, "proxysuite.teleport.savelocation")) {
            main.getPositionHandler().requestPosition(p);
            main.getPositionHandler().addPositionRunnable(p, () -> lastPositions.put(p, main.getPositionHandler().getLocalPositions().remove(p.getUniqueId())));
        }
    }

    public void savePlayerLocation(ProxiedPlayer p, Location loc) {
        lastPositions.put(p, loc);
    }

    public int getRemainingCooldown(ProxiedPlayer p) {
        int cooldown = getCooldown(p.getName());
        if (lastTeleports.containsKey(p)) {
            double since = (new Date().getTime() - lastTeleports.get(p).getTime());
            return since < cooldown * 1000 ? (int) (cooldown * 1000 - since) / 1000 : 0;
        }
        return 0;
    }

    public boolean canIgnoreCooldown(CommandSender sender) {
        return main.getPermissionHandler().hasPermission(sender, "proxysuite.teleport.ignorecooldown");
    }

    public boolean canIgnoreWarmup(CommandSender sender) {
        return main.getPermissionHandler().hasPermission(sender, "proxysuite.teleport.ignorewarmup");
    }

    private int getCooldown(String player) {
        int lowest = main.getConfig().getInt("ProxySuite.Teleport.DefaultCooldown");
        if (main.getPermissionHandler().getPermissions().containsKey(player)) {
            for (String s : main.getPermissionHandler().getPermissions().get(player)) {
                if (s.startsWith("proxysuite.teleport.cooldown.")) {
                    String amount = s.replace("proxysuite.teleport.cooldown.", "");
                    try {
                        int temp = Integer.parseInt(amount);
                        if (temp < lowest) {
                            lowest = temp;
                        }
                    } catch (NumberFormatException ex) {
                    }
                }
            }
        }
        return lowest;
    }

    private int getWarmup(String player) {
        int lowest = main.getConfig().getInt("ProxySuite.Teleport.DefaultWarmup");
        if (main.getPermissionHandler().getPermissions().containsKey(player)) {
            for (String s : main.getPermissionHandler().getPermissions().get(player)) {
                if (s.startsWith("proxysuite.teleport.warmup.")) {
                    String amount = s.replace("proxysuite.teleport.warmup.", "");
                    try {
                        int temp = Integer.parseInt(amount);
                        if (temp < lowest) {
                            lowest = temp;
                        }
                    } catch (NumberFormatException ex) {
                    }
                }
            }
        }
        return lowest;
    }

    private Duration getWarmupDuration(String player) {
        return Duration.of(getWarmup(player), ChronoUnit.SECONDS);
    }

    public PendingTeleport getPendingTeleport(ProxiedPlayer p) {
        List<PendingTeleport> test = new ArrayList<>(pendingTeleports);
        Collections.reverse(test);
        for (PendingTeleport teleport : test) {
            if ((teleport.getType() == PendingTeleport.TeleportType.TPA && teleport.getTo() == p) || (teleport.getType() == PendingTeleport.TeleportType.TPAHERE && teleport.getFrom() == p)) {
                return teleport;
            }
        }
        return null;
    }

    public ProxySuite getMain() {
        return main;
    }

    public ArrayList<PendingTeleport> getPendingTeleports() {
        return pendingTeleports;
    }

    public HashMap<ProxiedPlayer, Location> getLastPositions() {
        return lastPositions;
    }
}
