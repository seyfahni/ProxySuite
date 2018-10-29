package de.sabbertran.proxysuite.commands.warp;

import de.sabbertran.proxysuite.ProxySuite;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

public class SetWarpCommand extends Command {
    private final ProxySuite main;

    public SetWarpCommand(ProxySuite main) {
        super("setwarp");
        this.main = main;
    }

    @Override
    public void execute(final CommandSender sender, final String[] args) {
        main.getProxy().getScheduler().runAsync(main, () -> {
            if (sender instanceof ProxiedPlayer) {
                if (args.length == 1) {
                    if (main.getPermissionHandler().hasPermission(sender, "proxysuite.commands.setwarp")) {
                        final ProxiedPlayer p = (ProxiedPlayer) sender;
                        final String name1 = args[0];
                        main.getPositionHandler().requestPosition(p);
                        main.getPositionHandler().addPositionRunnable(p, () -> {
                            main.getWarpHandler().setWarp(name1, main.getPositionHandler().getLocalPositions().remove(p.getUniqueId()), false);
                            main.getMessageHandler().sendMessage(sender, main.getMessageHandler().getMessage("warp.created.success").replace("%warp%", name1));
                        });
                    } else {
                        main.getPermissionHandler().sendMissingPermissionInfo(sender);
                    }
                } else if (args.length == 2 && args[1].equalsIgnoreCase("hidden")) {
                    if (main.getPermissionHandler().hasPermission(sender, "proxysuite.commands.setwarp.hidden")) {
                        final ProxiedPlayer p = (ProxiedPlayer) sender;
                        final String name2 = args[0];
                        main.getPositionHandler().requestPosition(p);
                        main.getPositionHandler().addPositionRunnable(p, () -> {
                            main.getWarpHandler().setWarp(name2, main.getPositionHandler().getLocalPositions().remove(p.getUniqueId()), true);
                            main.getMessageHandler().sendMessage(sender, main.getMessageHandler().getMessage("warp.created.hidden.success").replace("%warp%", name2));
                        });
                    } else {
                        main.getPermissionHandler().sendMissingPermissionInfo(sender);
                    }
                } else {
                    main.getCommandHandler().sendUsage(sender, this);
                }
            } else {
                main.getMessageHandler().sendMessage(sender, main.getMessageHandler().getMessage("command.noplayer"));
            }
        });
    }
}
