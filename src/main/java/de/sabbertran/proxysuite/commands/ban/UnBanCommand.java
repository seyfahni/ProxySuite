package de.sabbertran.proxysuite.commands.ban;

import de.sabbertran.proxysuite.ProxySuite;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.plugin.Command;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.logging.Level;

public class UnBanCommand extends Command {

    private final ProxySuite main;

    public UnBanCommand(ProxySuite main) {
        super("unban", null, "pardon");
        this.main = main;
    }

    @Override
    public void execute(final CommandSender sender, final String[] args) {
        main.getProxy().getScheduler().runAsync(main, () -> {
            if (main.getPermissionHandler().hasPermission(sender, "proxysuite.commands.unban")) {
                switch (args.length) {
                    case 1:
                        {
                            final String player = args[0];
                            final String sql = "SELECT " + main.getTablePrefix() + "bans.id FROM " + main.getTablePrefix() + "players, " + main
                                    .getTablePrefix() + "bans WHERE " + main.getTablePrefix() + "bans.player = " + main.getTablePrefix() + "players.uuid AND LOWER(" + main.getTablePrefix() + "players.name) = " +
                                    "'" + player.toLowerCase() + "' AND (" + main.getTablePrefix() + "bans.expiration IS NULL OR UNIX_TIMESTAMP(" + main.getTablePrefix() + "bans" +
                                    ".expiration) > UNIX_TIMESTAMP(now())) ORDER BY " + main.getTablePrefix() + "bans.id DESC LIMIT 1";
                            main.getProxy().getScheduler().runAsync(main, () -> {
                                try {
                                    ResultSet rs = main.getSQLConnection().createStatement().executeQuery(sql);
                                    if (rs.next()) {
                                        main.getBanHandler().unban(player, sender, rs.getInt(main.getTablePrefix() + "bans.id"));
                                    } else {
                                        main.getMessageHandler().sendMessage(sender, main.getMessageHandler().getMessage
                                                                        ("command.player.notbanned").replace("%player%", player));
                                    }
                                } catch (SQLException e) {
                                    main.getLogger().log(Level.SEVERE, null, e);
                                }
                            });     break;
                        }
                    case 2:
                        {
                            final String player = args[0];
                            long addtime = main.getBanHandler().getAddTime(args[1]);
                            final Date expiration = new Date();
                            expiration.setTime(expiration.getTime() + addtime);
                            final String sql = "SELECT " + main.getTablePrefix() + "bans.id FROM " + main.getTablePrefix() + "players, " + main
                                    .getTablePrefix() + "bans WHERE " + main.getTablePrefix() + "bans.player = " + main.getTablePrefix() + "players.uuid AND LOWER(" + main.getTablePrefix() + "players.name) = " +
                                    "'" + player.toLowerCase() + "' AND (" + main.getTablePrefix() + "bans.expiration IS NULL OR UNIX_TIMESTAMP(" + main.getTablePrefix() + "bans" +
                                    ".expiration) > UNIX_TIMESTAMP(now())) ORDER BY " + main.getTablePrefix() + "bans.id DESC LIMIT 1";
                            main.getProxy().getScheduler().runAsync(main, () -> {
                                try {
                                    ResultSet rs = main.getSQLConnection().createStatement().executeQuery(sql);
                                    if (rs.next()) {
                                        main.getBanHandler().unban(player, expiration, sender, rs.getInt(main.getTablePrefix() + "bans.id"));
                                    } else {
                                        main.getMessageHandler().sendMessage(sender, main.getMessageHandler().getMessage
                                                                        ("command.player.notbanned").replace("%player%", player));
                                    }
                                } catch (SQLException e) {
                                    main.getLogger().log(Level.SEVERE, null, e);
                                }
                            });     break;
                        }
                    default:
                        main.getCommandHandler().sendUsage(sender, this);
                        break;
                }
            } else {
                main.getPermissionHandler().sendMissingPermissionInfo(sender);
            }
        });
    }
}
