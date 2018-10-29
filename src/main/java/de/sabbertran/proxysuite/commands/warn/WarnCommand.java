package de.sabbertran.proxysuite.commands.warn;

import de.sabbertran.proxysuite.ProxySuite;
import de.sabbertran.proxysuite.utils.Location;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;

public class WarnCommand extends Command {

    private final ProxySuite main;

    public WarnCommand(ProxySuite main) {
        super("warn");
        this.main = main;
    }

    @Override
    public void execute(final CommandSender sender, final String[] args) {
        main.getProxy().getScheduler().runAsync(main, () -> {
            if (main.getPermissionHandler().hasPermission(sender, "proxysuite.commands.warn")) {
                if (args.length > 1) {
                    final String player = args[0];
                        try {
                            ResultSet rs = main.getSQLConnection().createStatement().executeQuery("SELECT " +
                                    "uuid FROM " + main.getTablePrefix() + "players WHERE name = '" + player +
                                    "'");
                            if (rs.next()) {
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
                                        main.getWarningHandler().addWarn(player, reason2, main.getPositionHandler().getLocalPositions().remove(p.getUniqueId()), sender);
                                    });
                                } else {
                                    main.getWarningHandler().addWarn(player, reason2, new Location(null, ""), sender);
                                }
                            } else {
                                main.getMessageHandler().sendMessage(sender, main.getMessageHandler().getMessage
                                                                ("command.player.notseen").replace("%player%", player));
                            }
                        } catch (SQLException e) {
                            main.getLogger().log(Level.SEVERE, null, e);
                        }
                } else {
                    main.getCommandHandler().sendUsage(sender, this);
                }
            } else {
                main.getPermissionHandler().sendMissingPermissionInfo(sender);
            }
        });
    }
}
