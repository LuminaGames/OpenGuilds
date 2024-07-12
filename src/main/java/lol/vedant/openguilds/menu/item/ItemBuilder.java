package lol.vedant.openguilds.menu.item;

import de.tr7zw.nbtapi.NBTCompound;
import de.tr7zw.nbtapi.NBTItem;
import lol.vedant.openguilds.OpenGuilds;
import lol.vedant.openguilds.utils.Utils;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;
import java.util.stream.Collectors;

public class ItemBuilder {

    private ConfigurationSection item;

    public ItemBuilder(ConfigurationSection item) {
        this.item = item;
    }

    public ItemStack build(Player player) {
        Material material = Material.valueOf(item.getString("material", "STONE").toUpperCase());
        int amount = item.getInt("amount", 1);
        ItemStack itemStack = new ItemStack(material, amount);
        ItemMeta meta = itemStack.getItemMeta();

        if (item.contains("name")) {
            String name = Utils.cc(item.getString("name"));
            if (OpenGuilds.PLACEHOLDER_API) {
                name = PlaceholderAPI.setPlaceholders(player, name);
            }
            meta.setDisplayName(name);
        }

        if (item.contains("lore")) {
            List<String> lore = item.getStringList("lore").stream()
                    .map(line -> {
                        line = Utils.cc(line);
                        if (OpenGuilds.PLACEHOLDER_API) {
                            return PlaceholderAPI.setPlaceholders(player, line);
                        }
                        return line;
                    })
                    .collect(Collectors.toList());
            meta.setLore(lore);
        }

        if (item.contains("enchants")) {
            ConfigurationSection enchantsSection = item.getConfigurationSection("enchants");
            for (String enchantKey : enchantsSection.getKeys(false)) {
                Enchantment enchantment = Enchantment.getByName(enchantKey.toUpperCase());
                int level = enchantsSection.getInt(enchantKey);
                meta.addEnchant(enchantment, level, true);
            }
        }

        if (item.contains("flags")) {
            List<String> flags = item.getStringList("flags");
            for (String flag : flags) {
                meta.addItemFlags(ItemFlag.valueOf(flag.toUpperCase()));
            }
        }

        itemStack.setItemMeta(meta);

        if (item.contains("nbt")) {
            NBTItem nbtItem = new NBTItem(itemStack);
            ConfigurationSection nbtSection = item.getConfigurationSection("nbt");
            for (String key : nbtSection.getKeys(false)) {
                Object value = nbtSection.get(key);

                if (value instanceof ConfigurationSection) {
                    NBTCompound compound = nbtItem.addCompound(key);
                    setCompoundTags(compound, (ConfigurationSection) value);
                } else {
                    nbtItem.setObject(key, value);
                }
            }
            itemStack = nbtItem.getItem();
        }

        return itemStack;
    }

    private void setCompoundTags(NBTCompound compound, ConfigurationSection section) {
        for (String key : section.getKeys(false)) {
            Object value = section.get(key);
            compound.setObject(key, value);
        }
    }
}
