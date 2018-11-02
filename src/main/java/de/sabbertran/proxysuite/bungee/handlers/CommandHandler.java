package de.sabbertran.proxysuite.bungee.handlers;

import de.sabbertran.proxysuite.bungee.ProxySuite;
import de.sabbertran.proxysuite.bungee.commands.FlyCommand;
import de.sabbertran.proxysuite.bungee.commands.ProxySuiteCommand;
import de.sabbertran.proxysuite.bungee.commands.SeenCommand;
import de.sabbertran.proxysuite.bungee.commands.VanishCommand;
import de.sabbertran.proxysuite.bungee.commands.ban.BanCommand;
import de.sabbertran.proxysuite.bungee.commands.ban.KickCommand;
import de.sabbertran.proxysuite.bungee.commands.ban.TempBanCommand;
import de.sabbertran.proxysuite.bungee.commands.ban.UnBanCommand;
import de.sabbertran.proxysuite.bungee.commands.gamemode.GamemodeCommand;
import de.sabbertran.proxysuite.bungee.commands.home.DelHomeCommand;
import de.sabbertran.proxysuite.bungee.commands.home.HomeCommand;
import de.sabbertran.proxysuite.bungee.commands.home.HomesCommand;
import de.sabbertran.proxysuite.bungee.commands.home.SetHomeCommand;
import de.sabbertran.proxysuite.bungee.commands.note.NoteCommand;
import de.sabbertran.proxysuite.bungee.commands.note.NoteInfoCommand;
import de.sabbertran.proxysuite.bungee.commands.note.NotesCommand;
import de.sabbertran.proxysuite.bungee.commands.portal.DelPortalCommand;
import de.sabbertran.proxysuite.bungee.commands.portal.PortalCommand;
import de.sabbertran.proxysuite.bungee.commands.portal.PortalsCommand;
import de.sabbertran.proxysuite.bungee.commands.portal.SetPortalCommand;
import de.sabbertran.proxysuite.bungee.commands.spawn.SetSpawnCommand;
import de.sabbertran.proxysuite.bungee.commands.spawn.SpawnCommand;
import de.sabbertran.proxysuite.bungee.commands.teleport.*;
import de.sabbertran.proxysuite.bungee.commands.warn.WarnCommand;
import de.sabbertran.proxysuite.bungee.commands.warn.WarningCommand;
import de.sabbertran.proxysuite.bungee.commands.warn.WarningsCommand;
import de.sabbertran.proxysuite.bungee.commands.warp.DelWarpCommand;
import de.sabbertran.proxysuite.bungee.commands.warp.SetWarpCommand;
import de.sabbertran.proxysuite.bungee.commands.warp.WarpCommand;
import de.sabbertran.proxysuite.bungee.commands.warp.WarpsCommand;
import de.sabbertran.proxysuite.bungee.utils.CheckedCommand;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.plugin.Command;

import java.util.ArrayList;

public class CommandHandler {

    private final ProxySuite main;
    private final ArrayList<CheckedCommand> checkedCommands;

    public CommandHandler(ProxySuite main) {
        this.main = main;
        checkedCommands = new ArrayList<>();
    }

    public void registerCommands() {
        main.getProxy().getPluginManager().registerCommand(main, new ProxySuiteCommand(main));
        main.getProxy().getPluginManager().registerCommand(main, new SeenCommand(main));
        //Teleport Commands
        if (main.getConfig().getBoolean("ProxySuite.ModulesEnabled.Teleport")) {
            main.getProxy().getPluginManager().registerCommand(main, new TPCommand(main));
            main.getProxy().getPluginManager().registerCommand(main, new TPHereCommand(main));
            main.getProxy().getPluginManager().registerCommand(main, new TPACommand(main));
            main.getProxy().getPluginManager().registerCommand(main, new TPAHereCommand(main));
            main.getProxy().getPluginManager().registerCommand(main, new TPDenyCommand(main));
            main.getProxy().getPluginManager().registerCommand(main, new TPAcceptCommand(main));
            main.getProxy().getPluginManager().registerCommand(main, new BackCommand(main));
        }
        //Warp Commands
        if (main.getConfig().getBoolean("ProxySuite.ModulesEnabled.Warp")) {
            main.getProxy().getPluginManager().registerCommand(main, new WarpsCommand(main));
            main.getProxy().getPluginManager().registerCommand(main, new SetWarpCommand(main));
            main.getProxy().getPluginManager().registerCommand(main, new WarpCommand(main));
            main.getProxy().getPluginManager().registerCommand(main, new DelWarpCommand(main));
        }
        //Spawn Commands
        if (main.getConfig().getBoolean("ProxySuite.ModulesEnabled.Spawn")) {
            main.getProxy().getPluginManager().registerCommand(main, new SetSpawnCommand(main));
            main.getProxy().getPluginManager().registerCommand(main, new SpawnCommand(main));
        }
        //Home Commands
        if (main.getConfig().getBoolean("ProxySuite.ModulesEnabled.Home")) {
            main.getProxy().getPluginManager().registerCommand(main, new HomesCommand(main));
            main.getProxy().getPluginManager().registerCommand(main, new SetHomeCommand(main));
            main.getProxy().getPluginManager().registerCommand(main, new HomeCommand(main));
            main.getProxy().getPluginManager().registerCommand(main, new DelHomeCommand(main));
        }
        //Ban/Kick Commands
        if (main.getConfig().getBoolean("ProxySuite.ModulesEnabled.BanKickWarn")) {
            main.getProxy().getPluginManager().registerCommand(main, new KickCommand(main));
            main.getProxy().getPluginManager().registerCommand(main, new TempBanCommand(main));
            main.getProxy().getPluginManager().registerCommand(main, new BanCommand(main));
            main.getProxy().getPluginManager().registerCommand(main, new UnBanCommand(main));
            //Warn Commands
            main.getProxy().getPluginManager().registerCommand(main, new WarnCommand(main));
            main.getProxy().getPluginManager().registerCommand(main, new WarningCommand(main));
            main.getProxy().getPluginManager().registerCommand(main, new WarningsCommand(main));
        }
        //Note Command
        if (main.getConfig().getBoolean("ProxySuite.ModulesEnabled.Note")) {
            main.getProxy().getPluginManager().registerCommand(main, new NoteCommand(main));
            main.getProxy().getPluginManager().registerCommand(main, new NoteInfoCommand(main));
            main.getProxy().getPluginManager().registerCommand(main, new NotesCommand(main));
        }
        if (main.getConfig().getBoolean("ProxySuite.ModulesEnabled.Portal")) {
            //Portal Commands
            main.getProxy().getPluginManager().registerCommand(main, new PortalsCommand(main));
            main.getProxy().getPluginManager().registerCommand(main, new PortalCommand(main));
            main.getProxy().getPluginManager().registerCommand(main, new SetPortalCommand(main));
            main.getProxy().getPluginManager().registerCommand(main, new DelPortalCommand(main));
        }
        if (main.getConfig().getBoolean("ProxySuite.ModulesEnabled.Vanish")) {
            //Vanish Command
            main.getProxy().getPluginManager().registerCommand(main, new VanishCommand(main));
        }
        if (main.getConfig().getBoolean("ProxySuite.ModulesEnabled.Fly")) {
            //Fly Command
            main.getProxy().getPluginManager().registerCommand(main, new FlyCommand(main));
        }
        if (main.getConfig().getBoolean("ProxySuite.ModulesEnabled.Gamemode")) {
            //Gamemode Command
            main.getProxy().getPluginManager().registerCommand(main, new GamemodeCommand(main));
        }
    }

    public boolean hasFlag(String[] args, String flag, int after) {
        for(int i = after; i < args.length; i++) {
            if(args[i].equalsIgnoreCase(flag)) {
                return true;
            }
        }

        return false;
    }

    public void sendUsage(CommandSender sender, Command cmd) {
        if (cmd instanceof ProxySuiteCommand)
            main.getMessageHandler().sendMessage(sender, main.getMessageHandler().getMessage("command.usage" +
                    ".proxysuite"));
        else if (cmd instanceof FlyCommand)
            main.getMessageHandler().sendMessage(sender, main.getMessageHandler().getMessage("command.usage" +
                    ".fly"));
        else if (cmd instanceof SeenCommand)
            main.getMessageHandler().sendMessage(sender, main.getMessageHandler().getMessage("command.usage" +
                    ".seen"));
        else if (cmd instanceof VanishCommand)
            main.getMessageHandler().sendMessage(sender, main.getMessageHandler().getMessage("command.usage" +
                    ".vanish"));
        else if (cmd instanceof BanCommand)
            main.getMessageHandler().sendMessage(sender, main.getMessageHandler().getMessage("command.usage" +
                    ".ban"));
        else if (cmd instanceof KickCommand)
            main.getMessageHandler().sendMessage(sender, main.getMessageHandler().getMessage("command.usage" +
                    ".kick"));
        else if (cmd instanceof TempBanCommand)
            main.getMessageHandler().sendMessage(sender, main.getMessageHandler().getMessage("command.usage" +
                    ".tempban"));
        else if (cmd instanceof UnBanCommand)
            main.getMessageHandler().sendMessage(sender, main.getMessageHandler().getMessage("command.usage" +
                    ".unban"));
        else if (cmd instanceof GamemodeCommand)
            main.getMessageHandler().sendMessage(sender, main.getMessageHandler().getMessage("command.usage" +
                    ".gamemode"));
        else if (cmd instanceof DelHomeCommand)
            main.getMessageHandler().sendMessage(sender, main.getMessageHandler().getMessage("command.usage" +
                    ".delhome"));
        else if (cmd instanceof HomeCommand)
            main.getMessageHandler().sendMessage(sender, main.getMessageHandler().getMessage("command.usage" +
                    ".home"));
        else if (cmd instanceof HomesCommand)
            main.getMessageHandler().sendMessage(sender, main.getMessageHandler().getMessage("command.usage" +
                    ".homes"));
        else if (cmd instanceof SetHomeCommand)
            main.getMessageHandler().sendMessage(sender, main.getMessageHandler().getMessage("command.usage" +
                    ".sethome"));
        else if (cmd instanceof NoteCommand)
            main.getMessageHandler().sendMessage(sender, main.getMessageHandler().getMessage("command.usage" +
                    ".note"));
        else if (cmd instanceof NoteInfoCommand)
            main.getMessageHandler().sendMessage(sender, main.getMessageHandler().getMessage("command.usage" +
                    ".noteinfo"));
        else if (cmd instanceof NotesCommand)
            main.getMessageHandler().sendMessage(sender, main.getMessageHandler().getMessage("command.usage" +
                    ".notes"));
        else if (cmd instanceof DelPortalCommand)
            main.getMessageHandler().sendMessage(sender, main.getMessageHandler().getMessage("command.usage" +
                    ".delportal"));
        else if (cmd instanceof PortalCommand)
            main.getMessageHandler().sendMessage(sender, main.getMessageHandler().getMessage("command.usage" +
                    ".portal"));
        else if (cmd instanceof PortalsCommand)
            main.getMessageHandler().sendMessage(sender, main.getMessageHandler().getMessage("command.usage" +
                    ".portals"));
        else if (cmd instanceof SetPortalCommand)
            main.getMessageHandler().sendMessage(sender, main.getMessageHandler().getMessage("command.usage" +
                    ".setportal"));
        else if (cmd instanceof SetSpawnCommand)
            main.getMessageHandler().sendMessage(sender, main.getMessageHandler().getMessage("command.usage" +
                    ".setspawn"));
        else if (cmd instanceof SpawnCommand)
            main.getMessageHandler().sendMessage(sender, main.getMessageHandler().getMessage("command.usage" +
                    ".spawn"));
        else if (cmd instanceof BackCommand)
            main.getMessageHandler().sendMessage(sender, main.getMessageHandler().getMessage("command.usage" +
                    ".back"));
        else if (cmd instanceof TPAcceptCommand)
            main.getMessageHandler().sendMessage(sender, main.getMessageHandler().getMessage("command.usage" +
                    ".tpaccept"));
        else if (cmd instanceof TPACommand)
            main.getMessageHandler().sendMessage(sender, main.getMessageHandler().getMessage("command.usage" +
                    ".tpa"));
        else if (cmd instanceof TPAHereCommand)
            main.getMessageHandler().sendMessage(sender, main.getMessageHandler().getMessage("command.usage" +
                    ".tpahere"));
        else if (cmd instanceof TPCommand)
            main.getMessageHandler().sendMessage(sender, main.getMessageHandler().getMessage("command.usage" +
                    ".tp"));
        else if (cmd instanceof TPDenyCommand)
            main.getMessageHandler().sendMessage(sender, main.getMessageHandler().getMessage("command.usage" +
                    ".tpdeny"));
        else if (cmd instanceof TPHereCommand)
            main.getMessageHandler().sendMessage(sender, main.getMessageHandler().getMessage("command.usage" +
                    ".tphere"));
        else if (cmd instanceof WarnCommand)
            main.getMessageHandler().sendMessage(sender, main.getMessageHandler().getMessage("command.usage" +
                    ".warn"));
        else if (cmd instanceof WarningCommand)
            main.getMessageHandler().sendMessage(sender, main.getMessageHandler().getMessage("command.usage" +
                    ".warning"));
        else if (cmd instanceof WarningsCommand)
            main.getMessageHandler().sendMessage(sender, main.getMessageHandler().getMessage("command.usage" +
                    ".warnings"));
        else if (cmd instanceof DelWarpCommand)
            main.getMessageHandler().sendMessage(sender, main.getMessageHandler().getMessage("command.usage" +
                    ".delwarp"));
        else if (cmd instanceof SetWarpCommand)
            main.getMessageHandler().sendMessage(sender, main.getMessageHandler().getMessage("command.usage" +
                    ".setwarp"));
        else if (cmd instanceof WarpCommand)
            main.getMessageHandler().sendMessage(sender, main.getMessageHandler().getMessage("command.usage" +
                    ".warp"));
        else if (cmd instanceof WarpsCommand)
            main.getMessageHandler().sendMessage(sender, main.getMessageHandler().getMessage("command.usage" +
                    ".warps"));
    }

    public ArrayList<CheckedCommand> getCheckedCommands() {
        return checkedCommands;
    }
}
