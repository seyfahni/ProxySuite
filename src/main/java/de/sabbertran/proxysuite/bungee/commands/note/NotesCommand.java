package de.sabbertran.proxysuite.bungee.commands.note;

import de.sabbertran.proxysuite.bungee.ProxySuite;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

public class NotesCommand extends Command {
    private final ProxySuite main;

    public NotesCommand(ProxySuite main) {
        super("notes");
        this.main = main;
    }

    @Override
    public void execute(final CommandSender sender, final String[] args) {
        main.getProxy().getScheduler().runAsync(main, () -> {
            switch (args.length) {
                case 0:
                    if (main.getPermissionHandler().hasPermission(sender, "proxysuite.commands.notes")) {
                        if (sender instanceof ProxiedPlayer) {
                            ProxiedPlayer p = (ProxiedPlayer) sender;
                            main.getNoteHandler().sendNoteList(p.getName(), p);
                        } else {
                            main.getMessageHandler().sendMessage(sender, main.getMessageHandler().getMessage("command.noplayer"));
                        }
                    } else {
                        main.getPermissionHandler().sendMissingPermissionInfo(sender);
                    }   break;
                case 1:
                    if (main.getPermissionHandler().hasPermission(sender, "proxysuite.commands.notes.others")) {
                        String player = args[0];
                        main.getNoteHandler().sendNoteList(player, sender);
                    } else {
                        main.getPermissionHandler().sendMissingPermissionInfo(sender);
                    }   break;
                default:
                    main.getCommandHandler().sendUsage(sender, this);
                    break;
            }
        });
    }
}
