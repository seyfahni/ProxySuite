package de.sabbertran.proxysuite.commands.warn;

import de.sabbertran.proxysuite.ProxySuite;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;

public class WarningCommand extends Command {
    
    private final ProxySuite main;

    
    public WarningCommand(ProxySuite main) {
        super("warning");
        this.main = main;
    }

    @Override
    public void execute(final CommandSender sender, final String[] args) {
        main.getProxy().getScheduler().runAsync(main, () -> {
            switch (args.length) {
                case 1:
                    if (main.getPermissionHandler().hasPermission(sender, "proxysuite.commands.warning")) {
                        try {
                            final int id = Integer.parseInt(args[0]);
                            main.getProxy().getScheduler().runAsync(main, () -> {
                                try {
                                    ResultSet rs = main.getSQLConnection().createStatement().executeQuery("SELECT " + main.getTablePrefix() +
                                            "warnings.*, " + main.getTablePrefix() + "players.name AS authorName FROM " + main.getTablePrefix
                                                                        () + "warnings, " + main.getTablePrefix() + "players WHERE " + main.getTablePrefix() + "warnings" +
                                                                                ".id = '" + id + "' AND " + main.getTablePrefix() + "warnings.player = " + main.getTablePrefix() + "players.uuid");
                                    String message;
                                    if (rs.next()) {
                                        if (!rs.getBoolean("archived"))
                                            message = main.getMessageHandler().getMessage("warning.entry");
                                        else
                                            message = main.getMessageHandler().getMessage("warning.entry.archived");
                                        
                                        message = message.replace("%id%",
                                                "" + rs.getInt("id")).replace("%warning%", rs.getString("reason"))
                                                .replace("%dateCreated%", main.getDateFormat().format(rs.getTimestamp
                                                                                    ("date"))).replace("%author%", rs.getString("authorName"))
                                                .replace("%server%", rs.getString("server")).replace("%world%", rs
                                                        .getString("world")).replace("%coordX%", "" + rs.getInt("x"))
                                                .replace("%coordY%", "" + rs.getInt("y")).replace("%coordZ%", "" + rs
                                                        .getInt("z"));
                                        main.getMessageHandler().sendMessage(sender, message);
                                    } else {
                                        main.getMessageHandler().sendMessage(sender, main.getMessageHandler().getMessage
                                                                            ("warning.notexists").replace("%id%", "" + id));
                                    }
                                } catch (SQLException e) {
                                    main.getLogger().log(Level.SEVERE, null, e);
                                }
                            });
                        } catch (NumberFormatException ex) {
                            main.getCommandHandler().sendUsage(sender, this);
                        }
                    } else {
                        main.getPermissionHandler().sendMissingPermissionInfo(sender);
                    }   break;
                case 2:
                    if (args[0].equalsIgnoreCase("tp") || args[0].equalsIgnoreCase("teleport")) {
                        if (main.getPermissionHandler().hasPermission(sender, "proxysuite.commands.warning.teleport")) {
                            if (sender instanceof ProxiedPlayer) {
                                final ProxiedPlayer p = (ProxiedPlayer) sender;
                                try {
                                    final int id = Integer.parseInt(args[1]);
                                    main.getProxy().getScheduler().runAsync(main, () -> {
                                        try {
                                            ResultSet rs = main.getSQLConnection().createStatement().executeQuery
                                                                                ("SELECT player FROM " + main.getTablePrefix() + "warnings WHERE id = '" + id + "'");
                                            if (rs.next()) {
                                                main.getWarningHandler().teleportToWarn(p, id);
                                            } else {
                                                main.getMessageHandler().sendMessage(sender, main.getMessageHandler().getMessage
                                                                                    ("warning.notexists").replace("%id%", "" + id));
                                            }
                                        } catch (SQLException e) {
                                            main.getLogger().log(Level.SEVERE, null, e);
                                        }
                                    });
                                } catch (NumberFormatException ex) {
                                    main.getCommandHandler().sendUsage(sender, this);
                                }
                            } else {
                                main.getMessageHandler().sendMessage(sender, main.getMessageHandler().getMessage("command.noplayer"));
                            }
                        } else {
                            main.getPermissionHandler().sendMissingPermissionInfo(sender);
                        }
                    } else if (args[0].equalsIgnoreCase("arch") || args[0].equalsIgnoreCase("archive")) {
                        if (main.getPermissionHandler().hasPermission(sender, "proxysuite.commands.warning.archive")) {
                            try {
                                final int id = Integer.parseInt(args[1]);
                                main.getProxy().getScheduler().runAsync(main, () -> {
                                    try {
                                        ResultSet rs = main.getSQLConnection().createStatement().executeQuery
                                                                            ("SELECT player FROM " + main.getTablePrefix() + "warnings WHERE id = '" + id + "'");
                                        if (rs.next()) {
                                            main.getWarningHandler().archiveWarn(id);
                                            main.getMessageHandler().sendMessage(sender, main.getMessageHandler()
                                                    .getMessage("warning.archived.success").replace("%id%", "" + id));
                                        } else {
                                            main.getMessageHandler().sendMessage(sender, main.getMessageHandler().getMessage
                                                                                ("warning.notexists").replace("%id%", "" + id));
                                        }
                                    } catch (SQLException e) {
                                        main.getLogger().log(Level.SEVERE, null, e);
                                    }
                                });
                            } catch (NumberFormatException ex) {
                                main.getCommandHandler().sendUsage(sender, this);
                            }
                        } else {
                            main.getPermissionHandler().sendMissingPermissionInfo(sender);
                        }
                    } else if (args[0].equalsIgnoreCase("del") || args[0].equalsIgnoreCase("delete")) {
                        if (main.getPermissionHandler().hasPermission(sender, "proxysuite.commands.warning.delete")) {
                            try {
                                final int id = Integer.parseInt(args[1]);
                                main.getProxy().getScheduler().runAsync(main, () -> {
                                    try {
                                        ResultSet rs = main.getSQLConnection().createStatement().executeQuery
                                                                            ("SELECT player FROM " + main.getTablePrefix() + "warnings WHERE " +
                                                                                    "id = '" + id + "'");
                                        if (rs.next()) {
                                            main.getWarningHandler().deleteWarn(id);
                                            main.getMessageHandler().sendMessage(sender, main.getMessageHandler()
                                                    .getMessage("warning.deleted.success").replace("%id%", "" + id));
                                        } else {
                                            main.getMessageHandler().sendMessage(sender, main.getMessageHandler()
                                                    .getMessage("warning.notexists").replace("%id%", "" + id));
                                        }
                                    } catch (SQLException e) {
                                        main.getLogger().log(Level.SEVERE, null, e);
                                    }
                                });
                            } catch (NumberFormatException ex) {
                                main.getCommandHandler().sendUsage(sender, this);
                            }
                        } else {
                            main.getPermissionHandler().sendMissingPermissionInfo(sender);
                        }
                    } else {
                        main.getCommandHandler().sendUsage(sender, this);
                    }   break;
                case 3:
                    if (main.getPermissionHandler().hasPermission(sender, "proxysuite.commands.warning.archive.all")) {
                        if ((args[0].equalsIgnoreCase("arch") || args[0].equalsIgnoreCase("archive")) && args[1].equalsIgnoreCase
                                                ("all")) {
                            final String player = args[2];
                            main.getProxy().getScheduler().runAsync(main, () -> {
                                try {
                                    ResultSet rs = main.getSQLConnection().createStatement().executeQuery("SELECT" +
                                            " uuid, name FROM " + main.getTablePrefix() + "players WHERE LOWER" +
                                                    "(name) = '" + player.toLowerCase() + "'");
                                    if (rs.next()) {
                                        ResultSet rs2 = main.getSQLConnection().createStatement().executeQuery
                                                                            ("SELECT id FROM " + main.getTablePrefix() + "warnings WHERE " +
                                                                                    "player = '" + rs.getString("uuid") + "' AND archived = " +
                                                                                            "'0'");
                                        while (rs2.next()) {
                                            main.getWarningHandler().archiveWarn(rs.getInt("id"));
                                        }
                                        main.getMessageHandler().sendMessage(sender, main.getMessageHandler().getMessage
                                                                            ("warning.archived.all.success").replace("%player%", rs.getString("name")));
                                    }
                                } catch (SQLException e) {
                                    main.getLogger().log(Level.SEVERE, null, e);
                                }
                            });
                        } else {
                            main.getCommandHandler().sendUsage(sender, this);
                        }
                    } else {
                        main.getPermissionHandler().sendMissingPermissionInfo(sender);
                    }   break;
                default:
                    main.getCommandHandler().sendUsage(sender, this);
                    break;
            }
        });
    }
}
