package de.sabbertran.proxysuite.bungee.commands;

import de.sabbertran.proxysuite.bungee.ProxySuite;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

public class FlyCommand extends Command {

    private final ProxySuite main;

    public FlyCommand(ProxySuite main) {
        super("fly");
        this.main = main;
    }

    @Override
    public void execute(final CommandSender sender, final String[] args) {
        main.getProxy().getScheduler().runAsync(main, () -> executeFlyCommand(sender, args));
    }

    private void executeFlyCommand(CommandSender sender, String[] args) {
        if (hasBasicPermission(sender)) {
            executeFlyCommandWithPermission(sender, args);
        } else {
            sendErrorNoPermissions(sender);
        }
    }

    private boolean hasBasicPermission(CommandSender sender) {
        return main.getPermissionHandler().hasPermission(sender, "proxysuite.commands.fly");
    }

    private void sendErrorNoPermissions(CommandSender sender) {
        main.getPermissionHandler().sendMissingPermissionInfo(sender);
    }

    private void executeFlyCommandWithPermission(CommandSender sender, String[] args) {
        if (args.length < 2) {
            executeFlyCommandWithValidArgumentCount(sender, args);
        } else {
            sendErrorWrongUsage(sender);
        }
    }

    private void sendErrorWrongUsage(CommandSender sender) {
        main.getCommandHandler().sendUsage(sender, this);
    }

    private void executeFlyCommandWithValidArgumentCount(CommandSender sender, String[] args) {
        if (args.length == 1) {
            executeFlyCommandChangeOther(sender, args[0]);
        } else {
            executeFlyCommandChangeSelf(sender);
        }
    }

    private void executeFlyCommandChangeOther(CommandSender sender, String target) {
        if (hasPermissionModifyOther(sender)) {
            executeFlyCommandChangeOtherWithPermission(sender, target);
        } else {
            sendErrorNoPermissions(sender);
        }
    }

    private boolean hasPermissionModifyOther(CommandSender sender) {
        return main.getPermissionHandler().hasPermission(sender, "proxysuite.commands.fly.other");
    }

    private void executeFlyCommandChangeOtherWithPermission(CommandSender sender, String target) {
        ProxiedPlayer player = parsePlayer(sender, target);
        if (player != null) {
            toggleFlight(sender, player);
        } else {
            sendErrorInvalidTarget(sender, target);
        }
    }

    private ProxiedPlayer parsePlayer(CommandSender sender, String target) {
        return main.getPlayerHandler().getPlayer(target, sender, true);
    }

    private void sendErrorInvalidTarget(CommandSender sender, String player) {
        main.getMessageHandler().sendMessage(sender, main.getMessageHandler().getMessage("command.player.notonline").replace("%player%", player));
    }

    private void executeFlyCommandChangeSelf(CommandSender sender) {
        if (isPlayer(sender)) {
            toggleFlight(sender, (ProxiedPlayer) sender);
        } else {
            sendErrorSenderNotPlayer(sender);
        }
    }

    private boolean isPlayer(CommandSender sender) {
        return sender instanceof ProxiedPlayer;
    }

    private void sendErrorSenderNotPlayer(CommandSender sender) {
        main.getMessageHandler().sendMessage(sender, main.getMessageHandler().getMessage("command.noplayer"));
    }

    private void toggleFlight(CommandSender sender, ProxiedPlayer target) {
        if (isFlying(target)) {
            disableFlight(sender, target);
        } else {
            enableFlight(sender, target);
        }
    }

    private boolean isFlying(ProxiedPlayer target) {
        return main.getPlayerHandler().getFlying().contains(target);
    }

    private void disableFlight(CommandSender sender, ProxiedPlayer target) {
        main.getPlayerHandler().sendUnflyToServer(target);
        main.getPlayerHandler().getFlying().remove(target);
        main.getMessageHandler().sendMessage(sender, main.getMessageHandler().getMessage("fly.disabled"));
        main.getPlayerHandler().writeFlyToDatabase(target, true);
    }

    private void enableFlight(CommandSender sender, ProxiedPlayer target) {
        main.getPlayerHandler().sendFlyToServer(target);
        main.getPlayerHandler().getFlying().add(target);
        main.getMessageHandler().sendMessage(sender, main.getMessageHandler().getMessage("fly.enabled"));
        main.getPlayerHandler().writeFlyToDatabase(target, true);
    }
}
