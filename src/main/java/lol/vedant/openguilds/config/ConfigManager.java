package lol.vedant.openguilds.config;

import org.bukkit.plugin.java.JavaPlugin;

public class ConfigManager {

    private Config config;
    private Config messages;
    private Config levels;

    public ConfigManager(JavaPlugin plugin) {
        config =  new Config(plugin, "config.yml", true);
        messages = new Config(plugin, "messages.yml", true);
        levels = new Config(plugin, "levels.yml", false);
    }

    public Config getConfig() {
        return config;
    }

    public Config getMessages() {
        return messages;
    }

    public Config getLevels() {
        return levels;
    }
}
