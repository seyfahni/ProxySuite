package de.sabbertran.proxysuite.commands.home;

import de.sabbertran.proxysuite.ProxySuite;
import de.sabbertran.proxysuite.utils.Home;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

public class HomeCommand extends Command {

    private final ProxySuite main;

    public HomeCommand(ProxySuite main) {
        super("home");
        this.main = main;
    }

    @Override
    public void execute(final CommandSender sender, final String[] args) {
        main.getProxy().getScheduler().runAsync(main, () -> {
            if (sender instanceof ProxiedPlayer) {
                final ProxiedPlayer p = (ProxiedPlayer) sender;
                switch (args.length) {
                    case 0:
                        if (main.getPermissionHandler().hasPermission(sender, "proxysuite.commands.home")) {
                            Home h = main.getHomeHandler().getHome(p.getName(), "home");
                            if (h != null) {
                                int remainingCooldown = main.getTeleportHandler().getRemainingCooldown(p);
                                boolean ignoreCooldown = main.getTeleportHandler().canIgnoreCooldown(sender);
                                if (remainingCooldown == 0 || ignoreCooldown) {
                                    main.getTeleportHandler().teleportToHome(p, h, ignoreCooldown);
                                } else {
                                    main.getMessageHandler().sendMessage(sender, main.getMessageHandler().getMessage
                                                            ("teleport.cooldown").replace("%cooldown%", "" + remainingCooldown));
                                }
                            } else {
                                main.getMessageHandler().sendMessage(sender, main.getMessageHandler().getMessage
                                                        ("home.notset.default"));
                            }
                        } else {
                            main.getPermissionHandler().sendMissingPermissionInfo(sender);
                        }   break;
                    case 1:
                        if (main.getPermissionHandler().hasPermission(sender, "proxysuite.commands.home")) {
                            Home h = main.getHomeHandler().getHome(p.getName(), args[0]);
                            if (h != null) {
                                int remainingCooldown = main.getTeleportHandler().getRemainingCooldown(p);
                                boolean ignoreCooldown = main.getTeleportHandler().canIgnoreCooldown(sender);
                                if (remainingCooldown == 0 || ignoreCooldown) {
                                    main.getTeleportHandler().teleportToHome(p, h, ignoreCooldown);
                                } else {
                                    main.getMessageHandler().sendMessage(sender, main.getMessageHandler().getMessage
                                                            ("teleport.cooldown").replace("%cooldown%", "" + remainingCooldown));
                                }
                            } else {
                                main.getMessageHandler().sendMessage(sender, main.getMessageHandler().getMessage
                                                        ("home.notset").replace("%home%", args[0]));
                            }
                        } else {
                            main.getPermissionHandler().sendMissingPermissionInfo(sender);
                        }   break;
                    case 2:
                        if (main.getPermissionHandler().hasPermission(sender, "proxysuite.commands.home.others")) {
                            final String player = args[0];
                            final String home = args[1];
                            main.getProxy().getScheduler().runAsync(main, () -> {
                                Home h = main.getHomeHandler().getHome(player, home);
                                if (h != null) {
                                    int remainingCooldown = main.getTeleportHandler().getRemainingCooldown(p);
                                    boolean ignoreCooldown = main.getTeleportHandler().canIgnoreCooldown(sender);
                                    if (remainingCooldown == 0 || ignoreCooldown) {
                                        main.getTeleportHandler().teleportToHome(p, h, ignoreCooldown);
                                    } else {
                                        main.getMessageHandler().sendMessage(sender, main.getMessageHandler().getMessage
                                                                            ("teleport.cooldown").replace("%cooldown%", "" + remainingCooldown));
                                    }
                                } else
                                    main.getMessageHandler().sendMessage(sender, main.getMessageHandler()
                                            .getMessage("home.notset.others").replace("%home%", home).replace
                                                                                ("%player%", player));
                            });
                        } else {
                            main.getPermissionHandler().sendMissingPermissionInfo(sender);
                        }   break;
                    default:
                        main.getCommandHandler().sendUsage(sender, this);
                        break;
                }
            } else {
                main.getMessageHandler().sendMessage(sender, main.getMessageHandler().getMessage("command.noplayer"));
            }
        });
    }
}
