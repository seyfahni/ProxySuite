package de.sabbertran.proxysuite.bungee.commands.note;

import de.sabbertran.proxysuite.bungee.ProxySuite;
import de.sabbertran.proxysuite.utils.Location;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;

public class NoteCommand extends Command {

    private final ProxySuite main;

    public NoteCommand(ProxySuite main) {
        super("note");
        this.main = main;
    }

    @Override
    public void execute(final CommandSender sender, final String[] args) {
        main.getProxy().getScheduler().runAsync(main, () -> {
            if (main.getPermissionHandler().hasPermission(sender, "proxysuite.commands.note")) {
                if (args.length > 1) {
                    final String player = args[0];
                    main.getProxy().getScheduler().runAsync(main, () -> {
                        try {
                            final ResultSet rs = main.getSQLConnection().createStatement().executeQuery("SELECT name FROM " + main.getTablePrefix() + "players WHERE LOWER(name) = '" + player.toLowerCase() + "'");
                            if (rs.next()) {
                                final String fullName = rs.getString("name");
                                String reason = "";
                                for (int i = 1; i < args.length; i++) {
                                    reason += args[i] + " ";
                                }
                                if (reason.length() > 0)
                                    reason = reason.substring(0, reason.length() - 1);
                                final String reason2 = reason;
                                
                                if (sender instanceof ProxiedPlayer) {
                                    final ProxiedPlayer p = (ProxiedPlayer) sender;
                                    main.getPositionHandler().requestPosition(p);
                                    main.getPositionHandler().addPositionRunnable(p, () -> {
                                        main.getNoteHandler().addNote(fullName, reason2, main.getPositionHandler().getLocalPositions().remove(p.getUniqueId()), sender);
                                    });
                                } else {
                                    main.getNoteHandler().addNote(fullName, reason2, new Location(null, ""), sender);
                                }
                            } else {
                                main.getMessageHandler().sendMessage(sender, main.getMessageHandler().getMessage
                                                                ("command.player.notseen").replace("%player%", player));
                            }
                        } catch (SQLException e) {
                            main.getLogger().log(Level.SEVERE, null, e);
                        }
                    });
                } else {
                    main.getCommandHandler().sendUsage(sender, this);
                }
            } else {
                main.getPermissionHandler().sendMissingPermissionInfo(sender);
            }
        });
    }
}
