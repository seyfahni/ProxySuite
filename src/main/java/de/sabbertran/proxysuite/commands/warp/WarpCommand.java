package de.sabbertran.proxysuite.commands.warp;

import de.sabbertran.proxysuite.ProxySuite;
import de.sabbertran.proxysuite.utils.Warp;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

public class WarpCommand extends Command {

    private final ProxySuite main;

    public WarpCommand(ProxySuite main) {
        super("warp");
        this.main = main;
    }

    @Override
    public void execute(final CommandSender sender, final String[] args) {
        main.getProxy().getScheduler().runAsync(main, () -> {
            if (main.getPermissionHandler().hasPermission(sender, "proxysuite.commands.warp")) {
                ProxiedPlayer p;
                switch (args.length) {
                    case 1:
                        if (sender instanceof ProxiedPlayer) {
                            p = (ProxiedPlayer) sender;
                        } else {
                            main.getMessageHandler().sendMessage(sender, main.getMessageHandler().getMessage("command.noplayer"));
                            return;
                        }   break;
                    case 2:
                        if (main.getPermissionHandler().hasPermission(sender, "proxysuite.commands.warp.others")) {
                            p = main.getPlayerHandler().getPlayer(args[0], sender, true);
                            if (p == null) {
                                main.getMessageHandler().sendMessage(sender, main.getMessageHandler().getMessage
                                                                ("command.player.notonline").replace("%player%", args[0]));
                                return;
                            }
                        } else {
                            main.getPermissionHandler().sendMissingPermissionInfo(sender);
                            return;
                        }   break;
                    default:
                        main.getCommandHandler().sendUsage(sender, this);
                        return;
                }
                
                boolean includeHidden = main.getPermissionHandler().hasPermission(sender, "proxysuite.warps.showhidden");
                Warp w = main.getWarpHandler().getWarp(args[args.length - 1], includeHidden);
                if (w != null) {
                    int remainingCooldown = main.getTeleportHandler().getRemainingCooldown(p);
                    boolean ignoreCooldown = main.getTeleportHandler().canIgnoreCooldown(sender);
                    if (remainingCooldown == 0 || ignoreCooldown) {
                        main.getTeleportHandler().teleportToWarp(p, w, ignoreCooldown);
                    } else {
                        main.getMessageHandler().sendMessage(sender, main.getMessageHandler().getMessage
                                                    ("teleport.cooldown" + ((sender.getName().equals(p.getName())) ? "" : ".other"))
                                .replace("%cooldown%", "" + remainingCooldown));
                    }
                } else {
                    main.getMessageHandler().sendMessage(sender, main.getMessageHandler().getMessage
                                                ("warp.notexists").replace("%warp%", args[args.length - 1]));
                }
            } else {
                main.getPermissionHandler().sendMissingPermissionInfo(sender);
            }
        });
    }
}
