package de.sabbertran.proxysuite.bungee.commands.gamemode;

import java.util.Locale;

import de.sabbertran.proxysuite.bungee.ProxySuite;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

/**
 * The /gamemode Command allows changing the gamemode of oneself or others on the whole network.
 * There are three types of permissions: The basic usage permission, gamemode specific usage permissions
 * and a permission to modify the gamemode of others.
 * 
 * The command is executed async.
 *
 */
public class GamemodeCommand extends Command {

    private final ProxySuite main;

    public GamemodeCommand(ProxySuite main) {
        super("gamemode", null, new String[]{"gm"});
        this.main = main;
    }

    @Override
    public void execute(final CommandSender sender, final String[] args) {
        main.getProxy().getScheduler().runAsync(main, () -> executeGamemodeCommand(sender, args));
    }
    
    private void executeGamemodeCommand(final CommandSender sender, final String[] args) {
        if (hasPermissionBasic(sender)) {
            executeGamemodeCommandWithPermission(sender, args);
        } else {
            sendErrorNoPermissions(sender);
        }
    }
    
    private void sendErrorNoPermissions(CommandSender sender) {
        main.getPermissionHandler().sendMissingPermissionInfo(sender);
    }
    
    private boolean hasPermissionBasic(CommandSender sender) {
        return main.getPermissionHandler().hasPermission(sender, "proxysuite.commands.gamemode");
    }
    
    private void executeGamemodeCommandWithPermission(CommandSender sender, String[] args) {
        if (args.length > 0 && args.length < 3) {
            executeGamemodeCommandWithGamemodeArgument(sender, args);
        } else {
            sendErrorWrongUsage(sender);
        }
    }

    private void sendErrorWrongUsage(CommandSender sender) {
        main.getCommandHandler().sendUsage(sender, this);
    }
    
    private void executeGamemodeCommandWithGamemodeArgument(CommandSender sender, String[] args) {
        GameMode mode = parseTargetGameMode(args[0]);
        if (mode != null) {
            executeGamemodeCommandWithGamemode(sender, args, mode);
        } else {
            sendErrorWrongUsage(sender);
        }
    }
    
    private GameMode parseTargetGameMode(String gameModeArgument) {
        switch (gameModeArgument.toLowerCase()) {
            case "0":
            case "s":
            case "survival":
                return GameMode.SURVIVAL;
            case "1":
            case "c":
            case "creative":
                return GameMode.CREATIVE;
            case "2":
            case "a":
            case "adventure":
                return GameMode.ADVENTURE;
            case "3":
            case "sp":
            case "spectator":
                return GameMode.SPECTATOR;
            default:
                return null;
        }
    }
    
    private void executeGamemodeCommandWithGamemode(CommandSender sender, String[] args, GameMode mode) {
        if (hasPermissionForGamemode(sender, mode)) {
            executeGamemodeCommandWithGamemodeAndPermission(sender, args, mode);
        } else {
            sendErrorNoPermissions(sender);
        }
    }
    
    private boolean hasPermissionForGamemode(CommandSender sender, GameMode mode) {
        return main.getPermissionHandler().hasPermission(sender, "proxysuite.commands.gamemode." + mode.toString().toLowerCase(Locale.ROOT));
    }
    
    private void executeGamemodeCommandWithGamemodeAndPermission(CommandSender sender, String[] args, GameMode mode) {
        if (args.length == 2) {
            executeGamemodeCommandTargetToGamemode(sender, args[1], mode);
        } else {
            executeGamemodeCommandChangeOwn(sender, mode);
        }
    }
    
    private boolean isPlayer(CommandSender sender) {
        return sender instanceof ProxiedPlayer;
    }

    private void sendErrorSenderNotPlayer(CommandSender sender) {
        main.getMessageHandler().sendMessage(sender, main.getMessageHandler().getMessage("command.noplayer"));
    }
    
    private void executeGamemodeCommandTargetToGamemode(CommandSender sender, String target, GameMode mode) {
        if (hasPermissionModifyOther(sender)) {
            executeGamemodeCommandTargetToGamemodeWithPermission(sender, target, mode);
        } else {
            sendErrorNoPermissions(sender);
        }
    }
    
    private boolean hasPermissionModifyOther(CommandSender sender) {
        return main.getPermissionHandler().hasPermission(sender, "proxysuite.commands.gamemode.other");
    }
    
    private void executeGamemodeCommandTargetToGamemodeWithPermission(CommandSender sender, String target, GameMode mode) {
        ProxiedPlayer player = main.getPlayerHandler().getPlayer(target, sender, true);
        if (player != null) {
            setGamemode(sender, player, mode);
        } else {
            sendErrorInvalidTarget(sender, target);
        }
    }
    
    private void sendErrorInvalidTarget(CommandSender sender, String player) {
        main.getMessageHandler().sendMessage(sender, main.getMessageHandler().getMessage("command.player.notonline").replace("%player%", player));
    }
    
    private void executeGamemodeCommandChangeOwn(CommandSender sender, GameMode mode) {
        if (isPlayer(sender)) {
            setGamemode(sender, (ProxiedPlayer) sender, mode);
        } else {
            sendErrorSenderNotPlayer(sender);
        }
    }
    
    private void setGamemode(final CommandSender sender, final ProxiedPlayer player, final GameMode targetGamemode) {
        main.getPlayerHandler().setGamemode(player, targetGamemode.toString());
        main.getMessageHandler().sendMessage(player, main.getMessageHandler().getMessage("gamemode." + targetGamemode.toString().toLowerCase(Locale.ROOT)));
    }
}
