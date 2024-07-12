package lol.vedant.openguilds;

import fr.mrmicky.fastinv.FastInvManager;
import lol.vedant.openguilds.commands.GuildCommand;
import lol.vedant.openguilds.config.ConfigManager;
import lol.vedant.openguilds.database.Database;
import lol.vedant.openguilds.database.MySQL;
import lol.vedant.openguilds.database.SQLite;
import lol.vedant.openguilds.level.LevelManager;
import lol.vedant.openguilds.utils.Message;
import me.despical.commandframework.CommandFramework;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public final class OpenGuilds extends JavaPlugin {

    private ConfigManager configManager;
    private CommandFramework commandFramework;
    private LevelManager levelManager;

    private Database database;

    public static boolean PLACEHOLDER_API = false;
    private static OpenGuilds instance;

    @Override
    public void onEnable() {
        instance = this;

        configManager = new ConfigManager(this);
        levelManager = new LevelManager(configManager.getLevels().getConfig());
        commandFramework = new CommandFramework(this);
        commandFramework.registerCommands(new GuildCommand());

        if(getConfiguration().getBoolean("database.enabled")) {
            database = new MySQL(this);
        } else {
            database = new SQLite(this);
        }

        FastInvManager.register(this);

        if(Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            PLACEHOLDER_API = true;
        }

        Message.setConfiguration(configManager.getMessages().getConfig());
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public FileConfiguration getConfiguration() {
        return configManager.getConfig().getConfig();
    }

    public Database getDb() {
        return database;
    }

    public static OpenGuilds getInstance() {
        return instance;
    }
}
