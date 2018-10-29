package de.sabbertran.proxysuite.commands;

import de.sabbertran.proxysuite.ProxySuite;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

public class ProxySuiteCommand extends Command {

    private final ProxySuite main;

    public ProxySuiteCommand(ProxySuite main) {
        super("proxysuite", null, new String[]{"ps", "deeznutzforpresident"});
        this.main = main;
    }

    @Override
    public void execute(final CommandSender sender, final String[] args) {
        main.getProxy().getScheduler().runAsync(main, () -> {
            switch (args.length) {
                case 1:
                    if ((args[0].equalsIgnoreCase("reloadmessages") || args[0].equalsIgnoreCase("reloadmsg"))) {
                        if (main.getPermissionHandler().hasPermission(sender, "proxysuite.commands.reloadmessages")) {
                            main.getMessageHandler().readMessagesFromFile();
                            main.getMessageHandler().sendMessage(sender, main.getMessageHandler().getMessage("messages.reload.success"));
                        } else {
                            main.getPermissionHandler().sendMissingPermissionInfo(sender);
                        }
                    } else if ((args[0].equalsIgnoreCase("reloadannouncements") || args[0].equalsIgnoreCase("reloadann"))) {
                        if (main.getPermissionHandler().hasPermission(sender, "proxysuite.commands.reloadannouncements")) {
                            main.getAnnouncementHandler().readAnnouncementsFromFile();
                            main.getMessageHandler().sendMessage(sender, main.getMessageHandler().getMessage("announcements.reload.success"));
                        } else {
                            main.getPermissionHandler().sendMissingPermissionInfo(sender);
                        }
                    } else if (args[0].equalsIgnoreCase("reloadperms")) {
                        if (main.getPermissionHandler().hasPermission(sender, "proxysuite.commands.reloadperms")) {
                            main.getPermissionHandler().resetPermissions();
                            main.getPermissionHandler().updatePermissions();
                            main.getMessageHandler().sendMessage(sender, main.getMessageHandler().getMessage("permissions.reload.success"));
                        } else {
                            main.getPermissionHandler().sendMissingPermissionInfo(sender);
                        }
                    }   break;
                case 2:
                    if (args[0].equalsIgnoreCase("reloadperms")) {
                        ProxiedPlayer p = main.getPlayerHandler().getPlayer(args[1], sender, true);
                        if (p != null) {
                            if (main.getPermissionHandler().hasPermission(sender, "proxysuite.commands.reloadperms")) {
                                main.getPermissionHandler().resetPermissions(p);
                                main.getPermissionHandler().updatePermissions(p);
                                main.getMessageHandler().sendMessage(sender, main.getMessageHandler().getMessage("permissions.reload.success.player").replace("%player%", p.getName()));
                            } else {
                                main.getPermissionHandler().sendMissingPermissionInfo(sender);
                            }
                        } else {
                            main.getMessageHandler().sendMessage(sender, main.getMessageHandler().getMessage("command.player.notseen").replace("%player%", args[1]));
                        }
                    } else {
                        main.getCommandHandler().sendUsage(sender, this);
                    }   break;
                default:
                    main.getMessageHandler().sendMessage(sender, "&6Proxy&8Suite &r" + main.getDescription().getVersion() + " by &b" + main.getDescription().getAuthor());
                    break;
            }
        });
    }
}
