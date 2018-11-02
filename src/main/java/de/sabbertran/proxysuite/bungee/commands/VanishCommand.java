package de.sabbertran.proxysuite.bungee.commands;

import codecrafter47.bungeetablistplus.BungeeTabListPlus;
import de.sabbertran.proxysuite.bungee.ProxySuite;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

import java.sql.SQLException;
import java.util.logging.Level;

public class VanishCommand extends Command {

    private final ProxySuite main;

    public VanishCommand(ProxySuite main) {
        super("vanish", null, "v");
        this.main = main;
    }

    @Override
    public void execute(final CommandSender sender, String[] args) {
        main.getProxy().getScheduler().runAsync(main, () -> {
            if (main.getPermissionHandler().hasPermission(sender, "proxysuite.commands.vanish")) {
                if (sender instanceof ProxiedPlayer) {
                    final ProxiedPlayer p = (ProxiedPlayer) sender;
                    if (!main.getPlayerHandler().getVanishedPlayers().contains(p)) {
                        main.getPlayerHandler().getVanishedPlayers().add(p);
                        main.getPlayerHandler().sendVanishToServer(p);
                        if (main.isBungeeTabListPlusInstalled())
                            BungeeTabListPlus.hidePlayer(p);
                        main.getProxy().getScheduler().runAsync(main, () -> {
                            String sql = "UPDATE " + main.getTablePrefix() + "players SET vanished = '1' " +
                                    "WHERE uuid = '" + p.getUniqueId() + "'";
                            try {
                                main.getSQLConnection().createStatement().execute(sql);
                            } catch (SQLException e) {
                                main.getLogger().log(Level.SEVERE, null, e);
                            }
                        });
                        main.getMessageHandler().sendMessage(sender, main.getMessageHandler().getMessage("vanish" +
                                ".vanished"));
                    } else {
                        main.getPlayerHandler().sendUnvanishToServer(p);
                        main.getPlayerHandler().getVanishedPlayers().remove(p);
                        if (main.isBungeeTabListPlusInstalled())
                            BungeeTabListPlus.unhidePlayer(p);
                        try {
                            String sql = "UPDATE " + main.getTablePrefix() + "players SET vanished = '0' WHERE " +
                                    "uuid = '" + p.getUniqueId() + "'";
                            main.getSQLConnection().createStatement().execute(sql);
                        } catch (SQLException e) {
                            main.getLogger().log(Level.SEVERE, null, e);
                        }
                        main.getMessageHandler().sendMessage(sender, main.getMessageHandler().getMessage("vanish" +
                                ".unvanished"));
                    }
                } else {
                    main.getMessageHandler().sendMessage(sender, main.getMessageHandler().getMessage("command.noplayer"));
                }
            } else {
                main.getPermissionHandler().sendMissingPermissionInfo(sender);
            }
        });
    }
}