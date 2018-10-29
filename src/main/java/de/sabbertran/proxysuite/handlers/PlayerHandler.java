package de.sabbertran.proxysuite.handlers;

import de.sabbertran.proxysuite.ProxySuite;
import de.sabbertran.proxysuite.ProxySuiteUtils;
import de.sabbertran.proxysuite.utils.WorldInfo;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.PendingConnection;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

public class PlayerHandler {

    private final ProxySuite main;
    private final ArrayList<UUID> pendingFirstSpawnTeleports;
    private final HashMap<ProxiedPlayer, String> prefixes;
    private final HashMap<ProxiedPlayer, String> suffixes;
    private final ArrayList<ProxiedPlayer> vanishedPlayers;
    private final HashMap<String, Integer> ips;
    private final HashMap<ProxiedPlayer, WorldInfo> worldInfos;
    private final ArrayList<ProxiedPlayer> flying;
    private final HashMap<ProxiedPlayer, String> gamemode;

    public PlayerHandler(ProxySuite main) {
        this.main = main;
        pendingFirstSpawnTeleports = new ArrayList<>();
        prefixes = new HashMap<>();
        suffixes = new HashMap<>();
        vanishedPlayers = new ArrayList<>();
        ips = new HashMap<>();
        worldInfos = new HashMap<>();
        flying = new ArrayList<>();
        gamemode = new HashMap<>();
    }

    public ProxiedPlayer getPlayer(String name, CommandSender questioner, boolean useLevenshtein) {
        if (questioner == null)
            return main.getProxy().getPlayer(name);
        else if (questioner.getName().toLowerCase().equals(name.toLowerCase()))
            return (ProxiedPlayer) questioner;
        else {
            ProxiedPlayer p = main.getProxy().getPlayer(name);
            if (p != null) {
                if (!vanishedPlayers.contains(p) || main.getPermissionHandler().hasPermission(questioner, "proxysuite.vanish.useincommands", false))
                    return p;
            } else if (useLevenshtein) {
                HashMap<ProxiedPlayer, Integer> distances = new HashMap<>();
                for (ProxiedPlayer p1 : main.getProxy().getPlayers())
                    if (questioner != p1 && (!vanishedPlayers.contains(p1) || main.getPermissionHandler().hasPermission
                            (questioner, "proxysuite.vanish.useincommands", false)))
                        distances.put(p1, ProxySuiteUtils.levenshteinDistance(p1.getName(), name));
                ProxySuiteUtils.sortMapAsc(distances);
                if (distances.size() > 0) {
                    Map.Entry<ProxiedPlayer, Integer> entry = distances.entrySet().iterator().next();
                    if (entry.getValue() <= 4 || //
                            (entry.getKey().getName().toLowerCase().contains(name.toLowerCase()) && entry.getKey()
                                    .getName().length() - entry.getKey().getName().toLowerCase().replace(name
                                    .toLowerCase(), "").length() >= 4))
                        return entry.getKey();
                }
            }
        }
        return null;
    }

    /**
     * ACHTUNG!!! SQL Verbindung wird nicht asynchron ausgeführt! Nur aus asynchronem Kontext aufrufen
     *
     * @param con .
     */
    public void registerLogin(final PendingConnection con) {
        try {
            ResultSet rs = main.getSQLConnection().createStatement().executeQuery("SELECT id, name FROM " + main
                    .getTablePrefix() + "players WHERE uuid = '" + con.getUniqueId() + "'");
            if (rs.next()) {
                String sql = "UPDATE " + main.getTablePrefix() + "players SET name = '" + con.getName() + "', online " +
                        "= '1', last_seen = now() WHERE uuid = '" + con.getUniqueId() + "'";
                try {
                    main.getSQLConnection().createStatement().execute(sql);
                } catch (SQLException e) {
                    main.getLogger().log(Level.SEVERE, null, e);
                }
            } else {
                pendingFirstSpawnTeleports.add(con.getUniqueId());
                String sql = "INSERT INTO " + main.getTablePrefix() + "players (uuid, name, online, last_seen) VALUES" +
                        " ('" + con.getUniqueId() + "', '" + con.getName() + "', '1', now())";
                try {
                    main.getSQLConnection().createStatement().execute(sql);
                } catch (SQLException e) {
                    main.getLogger().log(Level.SEVERE, null, e);
                }

                main.getProxy().getScheduler().schedule(main, () -> announceNewPlayer(con.getName()), 500L, TimeUnit.MILLISECONDS);
            }
        } catch (SQLException e) {
            main.getLogger().log(Level.SEVERE, null, e);
        }

    }

    public void registerLogout(final ProxiedPlayer p) {
        main.getProxy().getScheduler().runAsync(main, () -> {
            String sql = "UPDATE " + main.getTablePrefix() + "players SET name = '" + p.getName() + "', online = " +
                    "'0', last_seen = now() WHERE uuid = '" + p.getUniqueId() + "'";
            try {
                main.getSQLConnection().createStatement().execute(sql);
            } catch (SQLException e) {
                main.getLogger().log(Level.SEVERE, null, e);
            }
        });
    }

    public void sendVanishToServer(ProxiedPlayer p) {
        if (p != null && p.getServer() != null) {
            ByteArrayOutputStream b = new ByteArrayOutputStream();
            DataOutputStream out = new DataOutputStream(b);
            try {
                out.writeUTF("Vanish");
                out.writeUTF(p.getName());
            } catch (IOException e) {
                main.getLogger().log(Level.SEVERE, null, e);
            }
            p.getServer().sendData("proxysuite:channel", b.toByteArray());
        }
    }

    public void sendFlyToServer(ProxiedPlayer p) {
        if (p != null && p.getServer() != null) {
            ByteArrayOutputStream b = new ByteArrayOutputStream();
            DataOutputStream out = new DataOutputStream(b);
            try {
                out.writeUTF("EnableFlight");
                out.writeUTF(p.getName());
            } catch (IOException e) {
                main.getLogger().log(Level.SEVERE, null, e);
            }
            p.getServer().sendData("proxysuite:channel", b.toByteArray());
        }
    }

    public void sendUnflyToServer(ProxiedPlayer p) {
        if (p != null && p.getServer() != null) {
            ByteArrayOutputStream b = new ByteArrayOutputStream();
            DataOutputStream out = new DataOutputStream(b);
            try {
                out.writeUTF("DisableFlight");
                out.writeUTF(p.getName());
            } catch (IOException e) {
                main.getLogger().log(Level.SEVERE, null, e);
            }
            p.getServer().sendData("proxysuite:channel", b.toByteArray());
        }
    }

    public void writeFlyToDatabase(final ProxiedPlayer p, final boolean flying) {
        main.getProxy().getScheduler().runAsync(main, () -> {
            try {
                String sql = "UPDATE " + main.getTablePrefix() + "players SET flying = '" + (flying ? "1"
                        : "0") + "' WHERE uuid = '" + p.getUniqueId() + "'";
                main.getSQLConnection().createStatement().execute(sql);
            } catch (SQLException e) {
                main.getLogger().log(Level.SEVERE, null, e);
            }
        });
    }

    public void setGamemode(final ProxiedPlayer p, final String gamemode) {
        this.gamemode.put(p, gamemode);
        sendGamemodeToServer(p, gamemode);

        main.getProxy().getScheduler().runAsync(main, () -> {
            try {
                String sql = "UPDATE " + main.getTablePrefix() + "players SET gamemode = '" + gamemode + "', flying = "
                        + (gamemode.equals("CREATIVE") || gamemode.equals("SPECTATOR") ? "1" : "0") + " WHERE uuid = " +
                        "'" + p.getUniqueId() + "'";
                main.getSQLConnection().createStatement().execute(sql);
            } catch (SQLException e) {
                main.getLogger().log(Level.SEVERE, null, e);
            }
        });

        if (flying.contains(p))
            sendFlyToServer(p);
    }

    public void sendGamemodeToServer(ProxiedPlayer p, String gamemode) {
        if (p != null && p.getServer() != null) {
            ByteArrayOutputStream b = new ByteArrayOutputStream();
            DataOutputStream out = new DataOutputStream(b);
            try {
                out.writeUTF("Gamemode");
                out.writeUTF(p.getName());
                out.writeUTF(gamemode);
            } catch (IOException e) {
                main.getLogger().log(Level.SEVERE, null, e);
            }
            p.getServer().sendData("proxysuite:channel", b.toByteArray());
        }
    }

    public void sendUnvanishToServer(ProxiedPlayer p) {
        if (p != null && p.getServer() != null) {
            ByteArrayOutputStream b = new ByteArrayOutputStream();
            DataOutputStream out = new DataOutputStream(b);
            try {
                out.writeUTF("Unvanish");
                out.writeUTF(p.getName());
            } catch (IOException e) {
                main.getLogger().log(Level.SEVERE, null, e);
            }
            p.getServer().sendData("proxysuite:channel", b.toByteArray());
        }
    }

    public void announceNewPlayer(String name) {
        main.getMessageHandler().broadcast(main.getMessageHandler().getMessage("join.newplayer.announcement").replace("%player%", name));
        if (main.getConfig().getBoolean("ProxySuite.WelcomeSound.Enabled")) {
            String sound = main.getConfig().getString("ProxySuite.WelcomeSound.Sound");
            float volume = main.getConfig().getFloat("ProxySuite.WelcomeSound.Volume");
            float pitch = main.getConfig().getFloat("ProxySuite.WelcomeSound.Pitch");
            for (ProxiedPlayer p : main.getProxy().getPlayers()) {
                if (main.getPermissionHandler().hasPermission(p, "proxysuite.join.welcomesound.receive")) {
                    ByteArrayOutputStream b = new ByteArrayOutputStream();
                    DataOutputStream out = new DataOutputStream(b);
                    try {
                        out.writeUTF("PlaySound");
                        out.writeUTF(p.getName());
                        out.writeUTF(sound.toUpperCase());
                        out.writeUTF("" + volume);
                        out.writeUTF("" + pitch);
                    } catch (IOException e) {
                        main.getLogger().log(Level.SEVERE, null, e);
                    }
                    p.getServer().sendData("proxysuite:channel", b.toByteArray());
                }
            }
        }
    }

    public String getPrefix(ProxiedPlayer p) {
        return prefixes.containsKey(p) ? prefixes.get(p) : "";
    }

    public String getSuffix(ProxiedPlayer p) {
        return suffixes.containsKey(p) ? suffixes.get(p) : "";
    }

    public ArrayList<UUID> getPendingFirstSpawnTeleports() {
        return pendingFirstSpawnTeleports;
    }

    public HashMap<ProxiedPlayer, String> getPrefixes() {
        return prefixes;
    }

    public HashMap<ProxiedPlayer, String> getSuffixes() {
        return suffixes;
    }

    public ArrayList<ProxiedPlayer> getVanishedPlayers() {
        return vanishedPlayers;
    }

    public HashMap<String, Integer> getIps() {
        return ips;
    }

    public HashMap<ProxiedPlayer, WorldInfo> getWorldInfos() {
        return worldInfos;
    }

    public ArrayList<ProxiedPlayer> getFlying() {
        return flying;
    }

    public HashMap<ProxiedPlayer, String> getGamemode() {
        return gamemode;
    }
}
