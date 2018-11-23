package de.sabbertran.proxysuite.bungee.commands.spawn;

import de.sabbertran.proxysuite.bungee.ProxySuite;
import de.sabbertran.proxysuite.utils.Location;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

public class SpawnCommand extends Command {

    private final ProxySuite main;

    public SpawnCommand(ProxySuite main) {
        super("hub", null, "lobby");
        this.main = main;
    }


    @Override
    public void execute(final CommandSender sender, String[] args) {
        main.getProxy().getScheduler().runAsync(main, () -> {
            if (main.getPermissionHandler().hasPermission(sender, "proxysuite.commands.spawn")) {
                if (sender instanceof ProxiedPlayer) {
                    ProxiedPlayer p = (ProxiedPlayer) sender;
                    Location normalSpawn = main.getSpawnHandler().getNormalSpawn();
                    if (normalSpawn != null) {
                        int remainingCooldown = main.getTeleportHandler().getRemainingCooldown(p);
                        boolean ignoreCooldown = main.getTeleportHandler().canIgnoreCooldown(sender);
                        if (remainingCooldown == 0 || ignoreCooldown) {
                            main.getTeleportHandler().teleportToLocation(p, normalSpawn, ignoreCooldown, false, false);
                        } else {
                            main.getMessageHandler().sendMessage(sender, main.getMessageHandler().getMessage
                                    ("teleport.cooldown").replace("%cooldown%", "" + remainingCooldown));
                        }
                    } else {
                        main.getMessageHandler().sendMessage(sender, main.getMessageHandler().getMessage("spawn.notset"));
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
