package lol.vedant.openguilds.config;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;

public class Config {

    private final JavaPlugin plugin;
    private FileConfiguration config;
    private File configFile;
    private final boolean copyDefaults;

    public Config(JavaPlugin plugin, String fileName, boolean copyDefaults) {
        this.plugin = plugin;
        this.copyDefaults = copyDefaults;
        this.configFile = new File(plugin.getDataFolder(), fileName);

        if (!configFile.exists()) {
            plugin.saveResource(fileName, copyDefaults);
        }

        this.config = YamlConfiguration.loadConfiguration(configFile);
        if (copyDefaults) {
            this.config.options().copyDefaults(true);
        }
    }

    public FileConfiguration getConfig() {
        return config;
    }

    public void saveConfig() {
        try {
            config.save(configFile);
        } catch (IOException e) {
            plugin.getLogger().log(Level.SEVERE, "Could not save config to " + configFile, e);
        }
    }

    public void reloadConfig() {
        config = YamlConfiguration.loadConfiguration(configFile);
        if (copyDefaults) {
            config.options().copyDefaults(true);
        }
    }

    public void saveDefaultConfig() {
        if (!configFile.exists()) {
            plugin.saveResource(configFile.getName(), copyDefaults);
        }
    }

}
