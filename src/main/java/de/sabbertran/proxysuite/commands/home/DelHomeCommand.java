package de.sabbertran.proxysuite.commands.home;

import de.sabbertran.proxysuite.ProxySuite;
import de.sabbertran.proxysuite.utils.Home;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

public class DelHomeCommand extends Command {

    private final ProxySuite main;

    public DelHomeCommand(ProxySuite main) {
        super("delhome");
        this.main = main;
    }

    @Override
    public void execute(final CommandSender sender, final String[] args) {
        main.getProxy().getScheduler().runAsync(main, () -> {
            switch (args.length) {
                case 0:
                    if (main.getPermissionHandler().hasPermission(sender, "proxysuite.commands.delhome")) {
                        if (sender instanceof ProxiedPlayer) {
                            final ProxiedPlayer p = (ProxiedPlayer) sender;
                            main.getProxy().getScheduler().runAsync(main, () -> {
                                if (main.getHomeHandler().getHome(p.getName(), "home") != null) {
                                    main.getHomeHandler().deleteHome(p.getName(), "home");
                                    main.getMessageHandler().sendMessage(sender, main.getMessageHandler().getMessage
                                                                        ("home.delete.success.default"));
                                } else {
                                    main.getMessageHandler().sendMessage(sender, main.getMessageHandler().getMessage
                                                                        ("home.notset.default"));
                                }
                            });
                        } else {
                            main.getMessageHandler().sendMessage(sender, main.getMessageHandler().getMessage("command.noplayer"));
                        }
                    } else {
                        main.getPermissionHandler().sendMissingPermissionInfo(sender);
                    }   break;
                case 1:
                    if (main.getPermissionHandler().hasPermission(sender, "proxysuite.commands.delhome")) {
                        if (sender instanceof ProxiedPlayer) {
                            final ProxiedPlayer p = (ProxiedPlayer) sender;
                            main.getProxy().getScheduler().runAsync(main, () -> {
                                Home h = main.getHomeHandler().getHome(p.getName(), args[0]);
                                if (h != null) {
                                    main.getHomeHandler().deleteHome(p.getName(), h.getName());
                                    main.getMessageHandler().sendMessage(sender, main.getMessageHandler().getMessage
                                                                        ("home.delete.success").replace("%home%", h.getName()));
                                } else {
                                    main.getMessageHandler().sendMessage(sender, main.getMessageHandler().getMessage
                                                                        ("home.notset").replace("%home%", args[0]));
                                }
                            });
                        } else {
                            main.getMessageHandler().sendMessage(sender, main.getMessageHandler().getMessage("command.noplayer"));
                        }
                    } else {
                        main.getPermissionHandler().sendMissingPermissionInfo(sender);
                    }   break;
                case 2:
                    if (main.getPermissionHandler().hasPermission(sender, "proxysuite.commands.delhome.others")) {
                        main.getProxy().getScheduler().runAsync(main, () -> {
                            Home h = main.getHomeHandler().getHome(args[0], args[1]);
                            if (h != null) {
                                main.getHomeHandler().deleteHome(args[0], h.getName());
                                main.getMessageHandler().sendMessage(sender, main.getMessageHandler().getMessage
                                                                    ("home.delete.others.success").replace("%player%", args[0]).replace("%home%", h
                                                                            .getName()));
                            } else {
                                main.getMessageHandler().sendMessage(sender, main.getMessageHandler().getMessage
                                                                    ("home.notset.others").replace("%player%", args[0]).replace("%home%", args[1]));
                            }
                        });
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
