package de.sabbertran.proxysuite.bungee.commands;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.plugin.Command;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;

import de.sabbertran.proxysuite.bungee.ProxySuite;

public class SeenCommand extends Command {

    private final ProxySuite main;

    public SeenCommand(ProxySuite main) {
        super("seen");
        this.main = main;
    }

    @Override
    public void execute(final CommandSender sender, final String[] args) {
        main.getProxy().getScheduler().runAsync(main, () -> {
            if (main.getPermissionHandler().hasPermission(sender, "proxysuite.commands.seen")) {
                if (args.length == 1) {
                    try {
                        ResultSet rs = main.getSQLConnection().createStatement().executeQuery("SELECT * "
                                + "FROM " + main.getTablePrefix() + "players WHERE LOWER(name) = '"
                                + args[0].toLowerCase() + "'");
                        if (rs.next()) {
                            PreparedStatement pst = main.getSQLConnection().prepareStatement("SELECT b.reason, b.expiration, COALESCE((SELECT name FROM " + main.getTablePrefix() + "players WHERE uuid = b.author), 'CONSOLE') AS banAuthor "
                                    + "FROM " + main.getTablePrefix() + "bans b "
                                    + "WHERE b.player = ? AND (b.expiration IS NULL OR UNIX_TIMESTAMP(b.expiration) > UNIX_TIMESTAMP(now())) "
                                    + "ORDER BY b.id DESC "
                                    + "LIMIT 1");
                            pst.setString(1, rs.getString("uuid"));
                            ResultSet rs2 = pst.executeQuery();
                            if (rs2.next()) {
                                if (rs2.getTimestamp("expiration") == null) {
                                    main.getMessageHandler().sendMessage(sender, main.getMessageHandler()
                                            .getMessage("seen.banned").replace("%name%", rs.getString("name")).replace("%author%", rs2.getString("banAuthor")).replace("%reason%", rs2.getString("reason")));
                                } else {
                                    Date expiration = new Date(rs2.getTimestamp("expiration")
                                            .getTime());
                                    main.getMessageHandler().sendMessage(sender, "The player " + rs
                                            .getString("name") + " has been banned by " + rs2.getString("banAuthor") + " until " + main.getDateFormat().format(expiration) + ": " + rs2.getString("reason"));
                                }
                            } else {
                                if (rs.getBoolean("online")) {
                                    Date login = new Date(rs.getTimestamp("last_seen").getTime());
                                    main.getMessageHandler().sendMessage(sender, main.getMessageHandler()
                                            .getMessage("seen.online").replace("%player%", rs.getString("name")).replace("%date%", main.getDateFormat()
                                            .format(login)));
                                } else {
                                    Date logout = new Date(rs.getTimestamp("last_seen").getTime());
                                    main.getMessageHandler().sendMessage(sender, main.getMessageHandler()
                                            .getMessage("seen.offline").replace("%player%", rs.getString("name")).replace("%date%", main.getDateFormat()
                                            .format(logout)));
                                }
                            }
                        } else {
                            main.getMessageHandler().sendMessage(sender, main.getMessageHandler()
                                    .getMessage("command.player.notseen").replace("%player%", args[0]));
                        }
                    } catch (SQLException e) {
                        main.getLogger().log(Level.SEVERE, null, e);
                    }
                } else {
                    main.getCommandHandler().sendUsage(sender, this);
                }
            } else {
                main.getPermissionHandler().sendMissingPermissionInfo(sender);
            }
        });
    }
}
