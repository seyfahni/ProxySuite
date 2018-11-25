package de.sabbertran.proxysuite.bungee;

import com.google.common.io.ByteStreams;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapterFactory;
import de.sabbertran.proxysuite.api.transport.LocationTarget;
import de.sabbertran.proxysuite.api.transport.PlayerTarget;
import de.sabbertran.proxysuite.api.transport.TeleportTarget;

import de.sabbertran.proxysuite.bungee.handlers.*;
import de.sabbertran.proxysuite.libraries.com.google.gson.typeadapters.RuntimeTypeAdapterFactory;
import de.sabbertran.proxysuite.utils.Regestry;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

import java.io.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.logging.Level;

public class ProxySuite extends Plugin {

    private static ProxySuite instance;

    private Configuration config;
    private List<String> sql;
    private Connection sql_connection;
    private String tablePrefix;
    private SimpleDateFormat dateFormat;
    private boolean bungeeTabListPlusInstalled;

    private TeleportHandler teleportHandler;
    private PermissionHandler permissionHandler;
    private WarpHandler warpHandler;
    private PositionHandler positionHandler;
    private SpawnHandler spawnHandler;
    private PlayerHandler playerHandler;
    private HomeHandler homeHandler;
    private BanHandler banHandler;
    private WarningHandler warningHandler;
    private MessageHandler messageHandler;
    private NoteHandler noteHandler;
    private PortalHandler portalHandler;
    private AnnouncementHandler announcementHandler;
    private CustomCommandHandler customCommandHandler;
    private CommandHandler commandHandler;

    public ProxySuite() {
        instance = this;
    }

    @Override
    public void onLoad() {
        TypeAdapterFactory teleportTargetTypeAdapterFactory = RuntimeTypeAdapterFactory.of(TeleportTarget.class)
                .registerSubtype(PlayerTarget.class)
                .registerSubtype(LocationTarget.class);
        Gson gson = new GsonBuilder()
                .registerTypeAdapterFactory(teleportTargetTypeAdapterFactory)
                .create();
        Regestry.getInstance().registerGson(gson);
    }

    @Override
    public void onEnable() {
        if (!getDataFolder().exists()) {
            getDataFolder().mkdir();
        }
        File configFile = new File(getDataFolder(), "config.yml");
        File messagesFile = new File(getDataFolder(), "messages.yml");
        File announcementsFile = new File(getDataFolder(), "announcements.yml");
        try {
            if (!configFile.exists()) {
                configFile.createNewFile();
                InputStream is = getResourceAsStream("config.yml");
                OutputStream os = new FileOutputStream(configFile);
                ByteStreams.copy(is, os);
            }
            if (!messagesFile.exists()) {
                messagesFile.createNewFile();
                InputStream is = getResourceAsStream("messages.yml");
                OutputStream os = new FileOutputStream(messagesFile);
                ByteStreams.copy(is, os);
            }
            if (!announcementsFile.exists()) {
                announcementsFile.createNewFile();
                InputStream is = getResourceAsStream("announcements.yml");
                OutputStream os = new FileOutputStream(announcementsFile);
                ByteStreams.copy(is, os);
            }
        } catch (IOException e) {
            throw new RuntimeException("Unable to create file", e);
        }
        try {
            config = ConfigurationProvider.getProvider(YamlConfiguration.class).load(new InputStreamReader(new FileInputStream(configFile), "UTF8"));
        } catch (IOException e) {
            this.getLogger().log(Level.SEVERE, null, e);
        }
        sql = config.getStringList("ProxySuite.SQL");
        tablePrefix = config.getString("ProxySuite.TablePrefix");

        dateFormat = new SimpleDateFormat(config.getString("ProxySuite.Messages.TimeFormat"));

        messageHandler = new MessageHandler(this);
        teleportHandler = new TeleportHandler(this);
        permissionHandler = new PermissionHandler(this);
        warpHandler = new WarpHandler(this);
        positionHandler = new PositionHandler(this);
        spawnHandler = new SpawnHandler(this);
        playerHandler = new PlayerHandler(this);
        homeHandler = new HomeHandler(this);
        banHandler = new BanHandler(this);
        warningHandler = new WarningHandler(this);
        noteHandler = new NoteHandler(this);
        portalHandler = new PortalHandler(this);
        announcementHandler = new AnnouncementHandler(this);
        commandHandler = new CommandHandler(this);
        customCommandHandler = new CustomCommandHandler(this);

        messageHandler.readMessagesFromFile();
        permissionHandler.readAvailablePermissionsFromFile();
        announcementHandler.readAnnouncementsFromFile();
        announcementHandler.startScheduler();
        commandHandler.registerCommands();
        customCommandHandler.registerCustomCommandsFromFile();

        setupDatabase();
        readDatabase();

        getProxy().getPluginManager().registerListener(this, new Events(this));
        getProxy().registerChannel("proxysuite:channel");
        getProxy().registerChannel("proxysuite:teleport");
        getProxy().getPluginManager().registerListener(this, new PMessageListener(this));

        getProxy().getServers().values().forEach(s -> portalHandler.sendPortalsToServer(s));

        bungeeTabListPlusInstalled = getProxy().getPluginManager().getPlugin("BungeeTabListPlus") != null;

        getLogger().log(Level.INFO, "{0} {1} by {2} enabled", new Object[]{getDescription().getName(), getDescription().getVersion(), getDescription().getAuthor()});
    }

    @Override
    public void onDisable() {
        getProxy().getScheduler().cancel(this);

        getLogger().log(Level.INFO, "{0} {1} by {2} disabled", new Object[]{getDescription().getName(), getDescription().getVersion(), getDescription().getAuthor()});
    }

    public void readDatabase() {
        warpHandler.readWarpsFromDatabase();
        portalHandler.readPortalsFromDatabase();
        spawnHandler.readSpawnsFromDatabase();
    }

    private void setupDatabase() {
        if (sql != null && sql.size() == 5 && !sql.get(4).equals("Password")) {
            try {
                getSQLConnection().createStatement().execute("CREATE TABLE IF NOT EXISTS `" + tablePrefix + "players` (`id` INT " +
                        "NOT NULL" +
                        " AUTO_INCREMENT, `uuid` CHAR(36) NOT NULL, `name` VARCHAR(255) NOT NULL, `vanished` " +
                        "BOOLEAN NOT NULL DEFAULT FALSE, `flying` BOOLEAN NOT NULL DEFAULT FALSE, `gamemode` VARCHAR" +
                        "(256) NOT NULL DEFAULT 'SURVIVAL', `online` BOOLEAN NOT NULL, `first_join` TIMESTAMP NOT " +
                        "NULL DEFAULT CURRENT_TIMESTAMP , `last_seen` TIMESTAMP NULL DEFAULT NULL, PRIMARY KEY (`id`), UNIQUE(`uuid`)" +
                        ")");
                getSQLConnection().createStatement().execute("CREATE TABLE IF NOT EXISTS `" + tablePrefix + "portals` (`id` INT NOT NULL" +
                        " AUTO_INCREMENT, `name` VARCHAR(255) NOT NULL, `type` VARCHAR(255) NOT NULL, `server` " +
                        "VARCHAR(255) NOT NULL, `world` VARCHAR(255) NOT NULL, `loc1_x` DOUBLE NOT NULL, `loc1_y` " +
                        "DOUBLE NOT NULL, `loc1_z` DOUBLE NOT NULL, `loc1_pitch` DOUBLE NOT NULL, `loc1_yaw` DOUBLE " +
                        "NOT NULL, `loc2_x` DOUBLE NOT NULL, `loc2_y` DOUBLE NOT NULL, `loc2_z` DOUBLE NOT NULL, " +
                        "`loc2_pitch` DOUBLE NOT NULL, `loc2_yaw` DOUBLE NOT NULL, `destination` VARCHAR(255) NOT " +
                        "NULL, PRIMARY KEY (`id`))");
                getSQLConnection().createStatement().execute("CREATE TABLE IF NOT EXISTS `" + tablePrefix + "warps` (`id` INT NOT NULL " +
                        "AUTO_INCREMENT, `name` VARCHAR(255) NOT NULL, `hidden` BOOLEAN NOT NULL, `server` VARCHAR" +
                        "(256) NOT NULL, `world` VARCHAR(255) NOT NULL, `x` DOUBLE NOT NULL, `y` DOUBLE NOT NULL, `z`" +
                        " DOUBLE NOT NULL, `pitch` FLOAT NOT NULL, `yaw` FLOAT NOT NULL, `local` BOOLEAN NOT NULL, PRIMARY KEY (`id`))");
                getSQLConnection().createStatement().execute("CREATE TABLE IF NOT EXISTS `" + tablePrefix + "homes` (`id` INT NOT NULL " +
                        "AUTO_INCREMENT, `player` VARCHAR(255) NOT NULL, `name` VARCHAR(255) NOT NULL, `server` " +
                        "VARCHAR(255) NOT NULL, `world` VARCHAR(255) NOT NULL, `x` DOUBLE NOT NULL, `y` DOUBLE NOT NULL, " +
                        "`z` DOUBLE NOT NULL, `pitch` FLOAT NOT NULL, `yaw` FLOAT NOT NULL, PRIMARY KEY (`id`))");
                getSQLConnection().createStatement().execute("CREATE TABLE IF NOT EXISTS `" + tablePrefix + "warnings` (`id` INT NOT " +
                        "NULL AUTO_INCREMENT, `deleted` BOOLEAN NOT NULL DEFAULT FALSE, `player` VARCHAR(255) NOT " +
                        "NULL, `player_read` BOOLEAN NOT NULL DEFAULT FALSE, `reason` TEXT NOT NULL, `author` VARCHAR(255) " +
                        "NOT NULL, `date` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, `archived` BOOLEAN NOT NULL " +
                        "DEFAULT FALSE, `server` VARCHAR(255) NOT NULL, `world` VARCHAR(255) NOT NULL, `x` DOUBLE NOT" +
                        " NULL, `y` DOUBLE NOT NULL, `z` DOUBLE NOT NULL, `pitch` FLOAT NOT NULL, `yaw` FLOAT NOT " +
                        "NULL, `lastMessages` VARCHAR(255) NOT NULL DEFAULT '', PRIMARY KEY (`id`))");
                getSQLConnection().createStatement().execute("CREATE TABLE IF NOT EXISTS `" + tablePrefix + "bans` (`id` INT NOT NULL " +
                        "AUTO_INCREMENT, `player` VARCHAR(255) NOT NULL, `reason` TEXT NOT NULL, `author` VARCHAR" +
                        "(256) NOT NULL, `expiration` TIMESTAMP NULL DEFAULT NULL, `created` TIMESTAMP NOT NULL DEFAULT " +
                        "CURRENT_TIMESTAMP, `lastMessages` VARCHAR(255) NOT NULL DEFAULT '', PRIMARY KEY (`id`))");
                getSQLConnection().createStatement().execute("CREATE TABLE IF NOT EXISTS `" + tablePrefix + "spawns` (`id` INT NOT NULL " +
                        "AUTO_INCREMENT, `type` VARCHAR(255) NOT NULL, `server` VARCHAR(255) NOT NULL, `world` " +
                        "VARCHAR(255) NOT NULL, `x` DOUBLE NOT NULL, `y` DOUBLE NOT NULL, `z` DOUBLE NOT NULL, " +
                        "`pitch` FLOAT NOT NULL, `yaw` FLOAT NOT NULL, PRIMARY KEY (`id`))");
                getSQLConnection().createStatement().execute("CREATE TABLE IF NOT EXISTS `" + tablePrefix + "notes` (`id` INT NOT NULL AUTO_INCREMENT," +
                        " `player` VARCHAR(255) NOT NULL, `note` TEXT NOT NULL, `deleted` BOOLEAN NOT NULL DEFAULT " +
                        "FALSE, `author` VARCHAR(255) NOT NULL, `server` VARCHAR(255) NOT NULL, `world` VARCHAR(255) " +
                        "NOT NULL, `x` DOUBLE NOT NULL, `y` DOUBLE NOT NULL, `z` DOUBLE NOT NULL, `pitch` FLOAT NOT " +
                        "NULL, `yaw` FLOAT NOT NULL, `date` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, `lastMessages` VARCHAR(255) NOT NULL DEFAULT '', " +
                        "PRIMARY KEY (`id`))");
                getSQLConnection().createStatement().execute("CREATE TABLE IF NOT EXISTS `" + tablePrefix + "lastMessages` (`id` INT NOT" +
                        " NULL AUTO_INCREMENT, `player` VARCHAR(255) NOT NULL, `message` TEXT NOT NULL, `date` " +
                        "TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, PRIMARY KEY (`id`))");
            } catch (SQLException e) {
                this.getLogger().log(Level.SEVERE, null, e);
            }
        } else {
            getLogger().info("Error while setting up the SQL Connection! Please check you SQL data!");
        }
    }

    public ServerInfo getServerInfo(net.md_5.bungee.api.connection.Connection sender) {
        for (ServerInfo s : getProxy().getServers().values())
            if (s.getAddress().equals(sender.getAddress()))
                return s;
        return null;
    }

    public Connection getSQLConnection() {
        try {
            if (sql_connection == null || sql_connection.isClosed()) {
                Class.forName("com.mysql.jdbc.Driver");
                String url = "jdbc:mysql://" + sql.get(0) + ":" + sql.get(1) + "/" + sql.get(2);
                sql_connection = DriverManager.getConnection(url, sql.get(3), sql.get(4));
            }
        } catch (ClassNotFoundException | SQLException e) {
            this.getLogger().log(Level.SEVERE, null, e);
        }

        return sql_connection;
    }

    public String getTablePrefix() {
        return tablePrefix;
    }

    public Configuration getConfig() {
        return config;
    }

    public SimpleDateFormat getDateFormat() {
        return dateFormat;
    }

    public TeleportHandler getTeleportHandler() {
        return teleportHandler;
    }

    public PermissionHandler getPermissionHandler() {
        return permissionHandler;
    }

    public WarpHandler getWarpHandler() {
        return warpHandler;
    }

    public PositionHandler getPositionHandler() {
        return positionHandler;
    }

    public SpawnHandler getSpawnHandler() {
        return spawnHandler;
    }

    public PlayerHandler getPlayerHandler() {
        return playerHandler;
    }

    public HomeHandler getHomeHandler() {
        return homeHandler;
    }

    public BanHandler getBanHandler() {
        return banHandler;
    }

    public WarningHandler getWarningHandler() {
        return warningHandler;
    }

    public MessageHandler getMessageHandler() {
        return messageHandler;
    }

    public NoteHandler getNoteHandler() {
        return noteHandler;
    }

    public PortalHandler getPortalHandler() {
        return portalHandler;
    }

    public CommandHandler getCommandHandler() {
        return commandHandler;
    }

    public CustomCommandHandler getCustomCommandHandler() {
        return customCommandHandler;
    }

    public AnnouncementHandler getAnnouncementHandler() {
        return announcementHandler;
    }

    public boolean isBungeeTabListPlusInstalled() {
        return bungeeTabListPlusInstalled;
    }

    public static ProxySuite getInstance() {
        return instance;
    }
}
