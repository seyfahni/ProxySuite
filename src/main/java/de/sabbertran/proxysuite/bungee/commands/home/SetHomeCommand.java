package de.sabbertran.proxysuite.bungee.commands.home;

import de.sabbertran.proxysuite.bungee.ProxySuite;
import de.sabbertran.proxysuite.utils.Location;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;

public class SetHomeCommand extends Command {

    private final ProxySuite main;

    public SetHomeCommand(ProxySuite main) {
        super("sethome");
        this.main = main;
    }

    @Override
    public void execute(final CommandSender sender, final String[] args) {
        main.getProxy().getScheduler().runAsync(main, () -> {
            switch (args.length) {
                case 0:
                    if (main.getPermissionHandler().hasPermission(sender, "proxysuite.commands.sethome", false)) {
                        if (sender instanceof ProxiedPlayer) {
                            final ProxiedPlayer p = (ProxiedPlayer) sender;
                            
                            int maximum = main.getHomeHandler().getMaximumHomes(p.getName());
                            if (main.getHomeHandler().getHome(p.getName(), "home") != null || maximum == -1 || main.getHomeHandler().getHomeAmount(p) < maximum) {
                                main.getPositionHandler().requestPosition(p);
                                main.getPositionHandler().addPositionRunnable(p, () -> {
                                    main.getHomeHandler().setHome(p.getName(), "home", main.getPositionHandler().getLocalPositions().remove(p.getUniqueId()));
                                    main.getMessageHandler().sendMessage(sender, main.getMessageHandler().getMessage("home.set.success.default"));
                                });
                            } else {
                                main.getMessageHandler().sendMessage(sender, main.getMessageHandler().getMessage("home.set.maximum").replace("%maximum%", "" + maximum));
                            }
                        } else {
                            main.getMessageHandler().sendMessage(sender, main.getMessageHandler().getMessage("command.noplayer"));
                        }
                    } else {
                        main.getPermissionHandler().sendMissingPermissionInfo(sender);
                    }   break;
                case 1:
                    if (main.getPermissionHandler().hasPermission(sender, "proxysuite.commands.sethome", false)) {
                        if (sender instanceof ProxiedPlayer) {
                            final ProxiedPlayer p = (ProxiedPlayer) sender;
                            final String name1 = args[0];
                            final int maximum = main.getHomeHandler().getMaximumHomes(p.getName());
                            if (main.getHomeHandler().getHome(p.getName(), name1) != null || maximum == -1 || main
                                    .getHomeHandler().getHomeAmount(p) < maximum) {
                                main.getPositionHandler().requestPosition(p);
                                main.getPositionHandler().addPositionRunnable(p, () -> {
                                    Location loc = main.getPositionHandler().getLocalPositions().remove(p.getUniqueId());
                                    int maximumPerWorld = main.getHomeHandler().getMaximumHomesPerWorld(p.getName());
                                    if (main.getHomeHandler().getHome(p.getName(), name1) != null || maximumPerWorld == -1 || main.getHomeHandler().getHomesInWorld(p, main.getProxy().getServerInfo(loc.getServer()), loc.getWorld()) < maximumPerWorld) {
                                        main.getHomeHandler().setHome(p.getName(), name1, loc);
                                        main.getMessageHandler().sendMessage(sender, main.getMessageHandler().getMessage("home.set.success").replace("%home%", name1));
                                    } else {
                                        main.getMessageHandler().sendMessage(sender, main.getMessageHandler().getMessage
                                                                            ("home.set.maximum.world").replace("%maximum%", "" + maximumPerWorld));
                                    }
                                });
                            } else {
                                main.getMessageHandler().sendMessage(sender, main.getMessageHandler().getMessage
                                                        ("home.set.maximum").replace("%maximum%", "" + maximum));
                            }
                        } else {
                            main.getMessageHandler().sendMessage(sender, main.getMessageHandler().getMessage("command.noplayer"));
                        }
                    } else {
                        main.getPermissionHandler().sendMissingPermissionInfo(sender);
                    }   break;
                case 2:
                    if (main.getPermissionHandler().hasPermission(sender, "proxysuite.commands.sethome.others", false)) {
                        if (sender instanceof ProxiedPlayer) {
                            final ProxiedPlayer p = (ProxiedPlayer) sender;
                            final String player = args[0];
                            final String homeName = args[1];
                            
                            main.getProxy().getScheduler().runAsync(main, () -> {
                                try {
                                    final ResultSet rs = main.getSQLConnection().createStatement().executeQuery
                                                                        ("SELECT name FROM " + main.getTablePrefix() + "players WHERE LOWER" +
                                                                                "(name) = '" + player + "'");
                                    if (rs.next()) {
                                        final String fullName = rs.getString("name");
                                        main.getPositionHandler().requestPosition(p);
                                        main.getPositionHandler().addPositionRunnable(p, () -> {
                                            main.getHomeHandler().setHome(player, homeName, main.getPositionHandler().getLocalPositions().remove(p.getUniqueId()));
                                            main.getMessageHandler().sendMessage(sender, main.getMessageHandler().getMessage("home.set.others.success").replace("%home%", homeName).replace("%player%", fullName));
                                        });
                                    } else {
                                        main.getMessageHandler().sendMessage(sender, main.getMessageHandler().getMessage
                                                                            ("command.player.notseen").replace("%player%", player));
                                    }
                                } catch (SQLException e) {
                                    main.getLogger().log(Level.SEVERE, null, e);
                                }
                            });
                        } else {
                            main.getMessageHandler().sendMessage(sender, main.getMessageHandler().getMessage("command.noplayer"));
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
