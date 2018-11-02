package de.sabbertran.proxysuite.bungee.commands.teleport;

import de.sabbertran.proxysuite.bungee.ProxySuite;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

public class TPHereCommand extends Command {

    private final ProxySuite main;

    public TPHereCommand(ProxySuite main) {
        super("tphere", null, "s");
        this.main = main;
    }

    @Override
    public void execute(final CommandSender sender, final String[] args) {
        main.getProxy().getScheduler().runAsync(main, () -> {
            if (main.getPermissionHandler().hasPermission(sender, "proxysuite.commands.tphere")) {
                if (args.length == 1) {
                    if (sender instanceof ProxiedPlayer) {
                        ProxiedPlayer p = (ProxiedPlayer) sender;
                        ProxiedPlayer teleport = main.getPlayerHandler().getPlayer(args[0], sender, true);
                        if (teleport != null) {
                            boolean ignoreCooldown = main.getTeleportHandler().canIgnoreCooldown(sender);
                            main.getTeleportHandler().teleportToPlayer(teleport, p, ignoreCooldown, true);
                        } else {
                            main.getMessageHandler().sendMessage(sender, main.getMessageHandler().getMessage
                                                ("command.player.notonline").replace("%player%", args[0]));
                        }
                    } else {
                        main.getMessageHandler().sendMessage(sender, main.getMessageHandler().getMessage("command.noplayer"));
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
