package de.sabbertran.proxysuite.handlers;

import de.sabbertran.proxysuite.ProxySuite;
import de.sabbertran.proxysuite.utils.Home;
import de.sabbertran.proxysuite.utils.Location;
import de.sabbertran.proxysuite.utils.PendingTeleport;
import de.sabbertran.proxysuite.utils.Warp;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.*;
import java.util.logging.Level;

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

    public void teleportToPlayer(ProxiedPlayer p, ProxiedPlayer to, boolean ignoreCooldown, boolean force) {
        int warmup = force ? 0 : getWarmup(p.getName());

        if (ignoreCooldown || getRemainingCooldown(p) == 0) {
            savePlayerLocation(p);

            ByteArrayOutputStream b = new ByteArrayOutputStream();
            DataOutputStream out = new DataOutputStream(b);
            try {
                out.writeUTF("Teleport");
                out.writeUTF(p.getName());
                out.writeUTF("PLAYER" + (warmup > 0 ? "_WARMUP" : ""));
                out.writeUTF(to.getName());
                if(warmup > 0) {
                    out.writeUTF("" + 20*warmup);
                    main.getMessageHandler().sendMessage(p, main.getMessageHandler()
                            .getMessage("teleport.warmup")
                            .replace("%warmup%", "" + warmup));
                }
            } catch (IOException e) {
                main.getLogger().log(Level.SEVERE, null, e);
            }
            to.getServer().sendData("proxysuite:channel", b.toByteArray());

            if (p.getServer().getInfo() != to.getServer().getInfo())
                p.connect(to.getServer().getInfo());

            lastTeleports.put(p, new Date());
        }
    }

        public void teleportToLocation(ProxiedPlayer p, Location loc, boolean ignoreCooldown, boolean ignoreBackSave, boolean force) {
        if (ignoreCooldown || getRemainingCooldown(p) == 0) {
            if (!ignoreBackSave)
                savePlayerLocation(p);

            int warmup = force ? 0 : getWarmup(p.getName());

            String sY = "" + loc.getY();
            if (loc.getY() == Double.MAX_VALUE)
                sY = "HIGHEST";

            ByteArrayOutputStream b = new ByteArrayOutputStream();
            DataOutputStream out = new DataOutputStream(b);
            try {
                out.writeUTF("Teleport");
                out.writeUTF(p.getName());
                out.writeUTF("LOCATION" + (warmup > 0 ? "_WARMUP" : ""));
                out.writeUTF(loc.getWorld());
                out.writeUTF("" + loc.getX());
                out.writeUTF(sY);
                out.writeUTF("" + loc.getZ());
                out.writeUTF("" + loc.getPitch());
                out.writeUTF("" + loc.getYaw());
                if(warmup > 0) {
                    out.writeUTF("" + 20*warmup);
                    main.getMessageHandler().sendMessage(p, main.getMessageHandler()
                            .getMessage("teleport.warmup")
                            .replace("%warmup%", "" + warmup));
                }
            } catch (IOException e) {
                main.getLogger().log(Level.SEVERE, null, e);
            }
            loc.getServer().sendData("proxysuite:channel", b.toByteArray());

            if (p.getServer().getInfo() != loc.getServer())
                p.connect(loc.getServer());

            lastTeleports.put(p, new Date());
        }
    }

    public void teleportToSpawn(ProxiedPlayer p, Location loc, boolean ignoreCooldown) {
        if (ignoreCooldown || getRemainingCooldown(p) == 0) {
            int warmup = getWarmup(p.getName());

            savePlayerLocation(p);

            ByteArrayOutputStream b = new ByteArrayOutputStream();
            DataOutputStream out = new DataOutputStream(b);
            try {
                out.writeUTF("Teleport");
                out.writeUTF(p.getName());
                out.writeUTF("SPAWN" + ((warmup > 0) ? "_WARMUP" : ""));
                out.writeUTF(loc.getWorld());
                if(warmup > 0) {
                    out.writeUTF("" + 20*warmup);
                    main.getMessageHandler().sendMessage(p, main.getMessageHandler()
                            .getMessage("teleport.warmup")
                            .replace("%warmup%", "" + warmup));
                }
            } catch (IOException e) {
                main.getLogger().log(Level.SEVERE, null, e);
            }
            loc.getServer().sendData("proxysuite:channel", b.toByteArray());

            if (p.getServer().getInfo() != loc.getServer())
                p.connect(loc.getServer());

            lastTeleports.put(p, new Date());
        }
    }

    public void teleportToWarp(ProxiedPlayer p, Warp w, boolean ignoreCooldown) {
        if (ignoreCooldown || getRemainingCooldown(p) == 0) {
            savePlayerLocation(p);

            int warmup = getWarmup(p.getName());

            ByteArrayOutputStream b = new ByteArrayOutputStream();
            DataOutputStream out = new DataOutputStream(b);
            try {
                out.writeUTF("Teleport");
                out.writeUTF(p.getName());
                out.writeUTF("LOCATION" + ((warmup > 0) ? "_WARMUP" : ""));
                out.writeUTF(w.getLocation().getWorld());
                out.writeUTF("" + w.getLocation().getX());
                out.writeUTF("" + w.getLocation().getY());
                out.writeUTF("" + w.getLocation().getZ());
                out.writeUTF("" + w.getLocation().getPitch());
                out.writeUTF("" + w.getLocation().getYaw());
                if(warmup > 0) {
                    out.writeUTF("" + 20*warmup);
                    main.getMessageHandler().sendMessage(p, main.getMessageHandler()
                            .getMessage("teleport.warmup")
                            .replace("%warmup%", "" + warmup));
                }
            } catch (IOException e) {
                main.getLogger().log(Level.SEVERE, null, e);
            }
            w.getLocation().getServer().sendData("proxysuite:channel", b.toByteArray());

            if (p.getServer().getInfo() != w.getLocation().getServer())
                p.connect(w.getLocation().getServer());

            lastTeleports.put(p, new Date());
        }
    }

    public void teleportToHome(ProxiedPlayer p, Home h, boolean ignoreCooldown) {
        if (ignoreCooldown || getRemainingCooldown(p) == 0) {
            savePlayerLocation(p);

            ByteArrayOutputStream b = new ByteArrayOutputStream();
            DataOutputStream out = new DataOutputStream(b);
            try {
                out.writeUTF("Teleport");
                out.writeUTF(p.getName());
                out.writeUTF("LOCATION");
                out.writeUTF(h.getLocation().getWorld());
                out.writeUTF("" + h.getLocation().getX());
                out.writeUTF("" + h.getLocation().getY());
                out.writeUTF("" + h.getLocation().getZ());
                out.writeUTF("" + h.getLocation().getPitch());
                out.writeUTF("" + h.getLocation().getYaw());
            } catch (IOException e) {
                main.getLogger().log(Level.SEVERE, null, e);
            }
            h.getLocation().getServer().sendData("proxysuite:channel", b.toByteArray());

            if (p.getServer().getInfo() != h.getLocation().getServer())
                p.connect(h.getLocation().getServer());

            lastTeleports.put(p, new Date());
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
            for (String s : main.getPermissionHandler().getPermissions().get(player))
                if (s.startsWith("proxysuite.teleport.cooldown.")) {
                    String amount = s.replace("proxysuite.teleport.cooldown.", "");
                    try {
                        int temp = Integer.parseInt(amount);
                        if (temp < lowest)
                            lowest = temp;
                    } catch (NumberFormatException ex) {
                    }
                }
        }
        return lowest;
    }

    private int getWarmup(String player) {
        int lowest = main.getConfig().getInt("ProxySuite.Teleport.DefaultWarmup");
        if (main.getPermissionHandler().getPermissions().containsKey(player)) {
            for (String s : main.getPermissionHandler().getPermissions().get(player))
                if (s.startsWith("proxysuite.teleport.warmup.")) {
                    String amount = s.replace("proxysuite.teleport.warmup.", "");
                    try {
                        int temp = Integer.parseInt(amount);
                        if (temp < lowest)
                            lowest = temp;
                    } catch (NumberFormatException ex) {
                    }
                }
        }
        return lowest;
    }

    public PendingTeleport getPendingTeleport(ProxiedPlayer p) {
        List<PendingTeleport> test = (List<PendingTeleport>) pendingTeleports.clone();
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
