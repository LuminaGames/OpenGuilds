package lol.vedant.openguilds.level;

import lol.vedant.openguilds.OpenGuilds;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import java.util.LinkedHashMap;
import java.util.Map;

public class LevelManager {

    private final Map<String, GuildLevel> levels = new LinkedHashMap<>();

    public LevelManager(FileConfiguration config) {
        loadLevels(config);
    }

    private void loadLevels(FileConfiguration config) {
        ConfigurationSection levelsSection = config.getConfigurationSection("");
        if (levelsSection != null) {
            for (String key : levelsSection.getKeys(false)) {
                ConfigurationSection levelConfig = levelsSection.getConfigurationSection(key);
                if (levelConfig != null) {
                    String name = levelConfig.getString("name", "");
                    long requiredExp = levelConfig.getLong("required_exp", 0);
                    levels.put(key.toLowerCase(), createGuildLevel(name, requiredExp));
                }
            }
            OpenGuilds.getInstance().getLogger().info("Loaded " + levels.size() + " levels.");
        }
    }

    private GuildLevel createGuildLevel(String name, long requiredExp) {
        GuildLevel level = new GuildLevel();
        level.setName(name);
        level.setExp(0);
        level.setRequiredExp(requiredExp);
        return level;
    }

    public GuildLevel getLevelByExp(long exp) {
        GuildLevel currentLevel = null;
        for (GuildLevel level : levels.values()) {
            if (exp >= level.getRequiredExp()) {
                currentLevel = level;
            } else {
                break;
            }
        }
        return currentLevel;
    }

    public Map<String, GuildLevel> getLevels() {
        return levels;
    }
}
