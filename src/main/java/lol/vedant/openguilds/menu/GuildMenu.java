package lol.vedant.openguilds.menu;

import fr.mrmicky.fastinv.FastInv;
import lol.vedant.openguilds.OpenGuilds;
import lol.vedant.openguilds.menu.item.ItemBuilder;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

public class GuildMenu extends FastInv {

    private final OpenGuilds plugin;
    private final Player player;

    public GuildMenu(int size, String title, Player player) {
        super(size, title);
        this.player = player;
        this.plugin = OpenGuilds.getInstance();

        ConfigurationSection items = plugin.getConfig().getConfigurationSection("guilds-menu");

        if (items != null) {
            ConfigurationSection guildInfoItemSection = items.getConfigurationSection("guild-info-item");

            if (guildInfoItemSection != null) {
                int slot = guildInfoItemSection.getInt("slot");
                ConfigurationSection itemSection = guildInfoItemSection.getConfigurationSection("item");

                if (itemSection != null) {
                    setItem(slot, new ItemBuilder(itemSection).build(player));
                }
            }
        }
    }
}
