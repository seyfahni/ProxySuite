package de.sabbertran.proxysuite.bungee.commands.warp;

import de.sabbertran.proxysuite.bungee.ProxySuite;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

public class WarpsCommand extends Command {

    private final ProxySuite main;

    public WarpsCommand(ProxySuite main) {
        super("warps");
        this.main = main;
    }

    @Override
    public void execute(final CommandSender sender, final String[] args) {
        main.getProxy().getScheduler().runAsync(main, () -> {
            if (!main.getPermissionHandler().hasPermission(sender, "proxysuite.commands.warps")) {
                main.getPermissionHandler().sendMissingPermissionInfo(sender);
                return;
            }
            
            final boolean includeHidden = main.getPermissionHandler()
                    .hasPermission(sender, "proxysuite.warps.showhidden");
            
            if(sender instanceof ProxiedPlayer) {
                final ProxiedPlayer p = (ProxiedPlayer) sender;
                main.getPositionHandler().requestPosition(p);
                main.getPositionHandler().addPositionRunnable(p, () -> {
                    ServerInfo server = main.getProxy().getServerInfo(main.getPositionHandler().getLocalPositions().remove(p.getUniqueId()).getServer());
                    main.getWarpHandler().sendWarpList(sender, (main.getCommandHandler().hasFlag(args, "true", 0) || main.getCommandHandler().hasFlag(args, "all", 0)) ? null : server, includeHidden);
                });
            } else {
                main.getWarpHandler().sendWarpList(sender, null, includeHidden);
            }
        });
    }
}
