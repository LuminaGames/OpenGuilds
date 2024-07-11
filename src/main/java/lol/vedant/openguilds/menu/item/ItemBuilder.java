package lol.vedant.openguilds.menu.item;

import de.tr7zw.nbtapi.NBTCompound;
import de.tr7zw.nbtapi.NBTItem;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public class ItemBuilder {

    private ConfigurationSection item;

    public ItemBuilder(ConfigurationSection item) {
        this.item = item;
    }

    public ItemStack build() {
        Material material = Material.valueOf(item.getString("material", "BARRIER").toUpperCase());
        int amount = item.getInt("amount", 1);
        ItemStack itemStack = new ItemStack(material, amount);
        ItemMeta meta = itemStack.getItemMeta();

        if (item.contains("name")) {
            meta.setDisplayName(item.getString("name"));
        }

        if (item.contains("lore")) {
            List<String> lore = item.getStringList("lore");
            meta.setLore(lore);
        }

        if (item.contains("enchantments")) {
            ConfigurationSection enchantsSection = item.getConfigurationSection("enchantments");
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
            ConfigurationSection nbt = item.getConfigurationSection("nbt");
            for (String key : nbt.getKeys(false)) {
                Object value = nbt.get(key);

                if (value instanceof ConfigurationSection) {
                    NBTCompound compound = nbtItem.addCompound(key);
                    setCompoundTags(compound, (ConfigurationSection) value);
              } else {
                    nbtItem.setObject(key, value);
                }
            }
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
