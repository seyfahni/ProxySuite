package de.sabbertran.proxysuite.commands.warp;

import de.sabbertran.proxysuite.ProxySuite;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

public class SetWarpCommand extends Command {
    private ProxySuite main;
    private SetWarpCommand self;

    public SetWarpCommand(ProxySuite main) {
        super("setwarp");
        this.main = main;
        this.self = this;
    }

    @Override
    public void execute(final CommandSender sender, final String[] args) {
        main.getCommandHandler().executeCommand(sender, "setwarp", new Runnable() {
            public void run() {
                final boolean hidden = main.getCommandHandler().hasFlag(args, "hidden", 1) &&
                        main.getPermissionHandler().hasPermission(sender, "proxysuite.commands.setwarp.hidden");
                final boolean local = main.getCommandHandler().hasFlag(args, "local", 1) &&
                        main.getPermissionHandler().hasPermission(sender, "proxysuite.commands.setwarp.local");


                if (sender instanceof ProxiedPlayer) {
                    if (args.length > 0) {
                        if (main.getPermissionHandler().hasPermission(sender, "proxysuite.commands.setwarp")) {
                            final ProxiedPlayer p = (ProxiedPlayer) sender;
                            final String name = args[0];
                            main.getPositionHandler().requestPosition(p);
                            main.getPositionHandler().addPositionRunnable(p, new Runnable() {
                                public void run() {
                                    main.getWarpHandler().setWarp(name, main.getPositionHandler().getLocalPositions().remove(p.getUniqueId()), local, hidden);
                                    main.getMessageHandler()
                                            .sendMessage(sender, main.getMessageHandler()
                                                    .getMessage(getIdentifier(local, hidden)).replace("%warp%", name));
                                }
                            });
                        } else {
                            main.getPermissionHandler().sendMissingPermissionInfo(sender);
                        }
                    } else {
                        main.getCommandHandler().sendUsage(sender, self);
                    }
                } else {
                    main.getMessageHandler().sendMessage(sender, main.getMessageHandler().getMessage("command.noplayer"));
                }
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
