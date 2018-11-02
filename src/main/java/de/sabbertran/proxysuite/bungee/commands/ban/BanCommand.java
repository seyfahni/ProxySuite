package de.sabbertran.proxysuite.bungee.commands.ban;

import de.sabbertran.proxysuite.bungee.ProxySuite;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.plugin.Command;

public class BanCommand extends Command {

    private final ProxySuite main;

    public BanCommand(ProxySuite main) {
        super("ban");
        this.main = main;
    }

    @Override
    public void execute(final CommandSender sender, final String[] args) {
        main.getProxy().getScheduler().runAsync(main, () -> {
            if (main.getPermissionHandler().hasPermission(sender, "proxysuite.commands.ban")) {
                if (args.length == 1) {
                    String player = args[0];
                    main.getBanHandler().banPlayer(player, null, sender);
                } else if (args.length > 1) {
                    String player = args[0];
                    String reason = "";
                    for (int i = 1; i < args.length; i++) {
                        reason += args[i] + " ";
                    }
                    if (reason.length() > 0)
                        reason = reason.substring(0, reason.length() - 1);
                    main.getBanHandler().banPlayer(player, reason, sender);
                } else {
                    main.getCommandHandler().sendUsage(sender, this);
                }
            } else {
                main.getPermissionHandler().sendMissingPermissionInfo(sender);
            }
        });
    }
}
