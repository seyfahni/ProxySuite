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
            final boolean hidden = main.getCommandHandler().hasFlag(args, "hidden", 1) &&
                    main.getPermissionHandler().hasPermission(sender, "proxysuite.commands.setwarp.hidden");
            final boolean local = main.getCommandHandler().hasFlag(args, "local", 1) &&
                    main.getPermissionHandler().hasPermission(sender, "proxysuite.commands.setwarp.local");
            if (sender instanceof ProxiedPlayer) {
                if (args.length > 0) {
                    if (main.getPermissionHandler().hasPermission(sender, "proxysuite.commands.setwarp")) {
                        final ProxiedPlayer p = (ProxiedPlayer) sender;
                        final String name1 = args[0];
                        main.getPositionHandler().requestPosition(p);
                        main.getPositionHandler().addPositionRunnable(p, () -> {
                            main.getWarpHandler().setWarp(name1, main.getPositionHandler().getLocalPositions().remove(p.getUniqueId()), local, hidden);
                            main.getMessageHandler().sendMessage(sender, main.getMessageHandler()
                                    .getMessage(getIdentifier(local, hidden)).replace("%warp%", name1));
                        });
                    } else {
                        main.getPermissionHandler().sendMissingPermissionInfo(sender);
                    }
                } else {
                    main.getCommandHandler().sendUsage(sender, SetWarpCommand.this);
                }
            } else {
                main.getMessageHandler().sendMessage(sender, main.getMessageHandler().getMessage("command.noplayer"));
            }
        });
    }

    private String getIdentifier(boolean local, boolean hidden) {
        String identifier;
        if(local && hidden) {
            identifier = "created.local-hidden.success";
        } else if(local) {
            identifier = "created.local.success";
        } else if(hidden) {
            identifier = "created.hidden.success";
        } else {
            identifier = "created.success";
        }
        return "warp." + identifier;
    }
}
