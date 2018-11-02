package de.sabbertran.proxysuite.bungee.commands.spawn;

import de.sabbertran.proxysuite.bungee.ProxySuite;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

public class SetSpawnCommand extends Command {

    private final ProxySuite main;

    public SetSpawnCommand(ProxySuite main) {
        super("setspawn");
        this.main = main;
    }

    @Override
    public void execute(final CommandSender sender, final String[] args) {
        main.getProxy().getScheduler().runAsync(main, () -> {
            if (main.getPermissionHandler().hasPermission(sender, "proxysuite.commands.setspawn")) {
                if (sender instanceof ProxiedPlayer) {
                    final ProxiedPlayer p = (ProxiedPlayer) sender;
                    if (args.length == 1 && args[0].equalsIgnoreCase("first")) {
                        main.getPositionHandler().requestPosition(p);
                        main.getPositionHandler().addPositionRunnable(p, () -> {
                            main.getSpawnHandler().setFirstSpawn(main.getPositionHandler().getLocalPositions().remove(p.getUniqueId()));
                            main.getMessageHandler().sendMessage(sender, main.getMessageHandler().getMessage("spawn.set.firstspawn.success"));
                        });
                    } else {
                        main.getPositionHandler().requestPosition(p);
                        main.getPositionHandler().addPositionRunnable(p, () -> {
                            main.getSpawnHandler().setNormalSpawn(main.getPositionHandler().getLocalPositions().remove(p.getUniqueId()));
                            main.getMessageHandler().sendMessage(sender, main.getMessageHandler().getMessage("spawn.set.success"));
                        });
                    }
                } else {
                    main.getMessageHandler().sendMessage(sender, main.getMessageHandler().getMessage("command.noplayer"));
                }
            } else {
                main.getPermissionHandler().sendMissingPermissionInfo(sender);
            }
        });
    }
}
