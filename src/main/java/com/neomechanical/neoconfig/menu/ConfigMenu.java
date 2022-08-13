package com.neomechanical.neoconfig.menu;

import com.neomechanical.neoconfig.menu.actions.ChangeKey;
import com.neomechanical.neoutils.config.ConfigUtil;
import com.neomechanical.neoutils.inventory.InventoryUtil;
import com.neomechanical.neoutils.inventory.actions.OpenInventory;
import com.neomechanical.neoutils.inventory.managers.data.InventoryGUI;
import com.neomechanical.neoutils.inventory.managers.data.InventoryItem;
import com.neomechanical.neoutils.items.ItemUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

public class ConfigMenu {
    private final Plugin plugin;
    private BiConsumer<Player, String> completeFunction;
    private String perm = null;
    private Supplier<String> permMessage = () -> "<red><bold>You do not have permission to use this command";

    public ConfigMenu(Plugin plugin) {
        this.plugin = plugin;
    }

    private InventoryGUI generateMenu(@Nullable Plugin plugin) {
        if (plugin != null) {
            //Create plugin menu with all the keys
            InventoryGUI pluginMenu = InventoryUtil.createInventoryGUI(null, 54, plugin.getName());
            if (addFiles(plugin, pluginMenu, plugin.getDataFolder().listFiles())) {
                //Add pluginMenu item to main menu
                return pluginMenu;
            }
        }
        InventoryGUI menu = InventoryUtil.createInventoryGUI(null, 54, "NeoConfig");
        //get plugins
        List<Plugin> plugins = Arrays.stream(Bukkit.getPluginManager().getPlugins()).toList();
        for (Plugin p : plugins) {
            createPluginItem(p, menu);
        }
        return menu;
    }

    @SuppressWarnings("unused")
    public ConfigMenu onComplete(BiConsumer<Player, String> completeFunction) {
        this.completeFunction = completeFunction;
        return this;
    }

    @SuppressWarnings("unused")
    public ConfigMenu permission(String perm, Supplier<String> permMessage) {
        this.perm = perm;
        this.permMessage = permMessage;
        return this;
    }

    public void open(Player player, @Nullable Plugin plugin) {
        InventoryGUI menu = generateMenu(plugin);
        InventoryUtil.openInventory(player, menu);
    }

    //PluginMenu contains all the plugins interface items
    private boolean addFiles(Plugin plugin, InventoryGUI pluginMenu, File[] dataFolder) {
        if (dataFolder == null) {
            return false;
        }
        for (File file : dataFolder) {
            // Add all yml files in the data folder that are directories
            if (file.isDirectory()) {
                InventoryGUI directory = InventoryUtil.createInventoryGUI(null, 54, file.getName());
                addFiles(plugin, directory, file.listFiles());
                pluginMenu.addItem(new InventoryItem(ItemUtil.createItem(Material.CHEST, ChatColor.RESET + file.getName()),
                        new OpenInventory(directory), null));
            }
            // Add all yml files inside the plugin data folder (excluding directories)
            addFile(file, pluginMenu);
        }
        return true;
    }

    private void addFile(File file, InventoryGUI pluginMenu) {
        if (file.getName().endsWith(".yml")) {
            FileConfiguration config = YamlConfiguration.loadConfiguration(file);
            //Create YML item with all keys
            InventoryGUI keysMenu = InventoryUtil.createInventoryGUI(null, 54, "Keys");
            keysMenu.setOpenOnClose(pluginMenu);
            //Add Key items to configYMLMenu
            addKeys(config, file, keysMenu, plugin);
            ItemStack item = ItemUtil.createItem(Material.BOOK, ChatColor.RESET + file.getName());
            InventoryItem ymlFile = new InventoryItem(item, new OpenInventory(keysMenu), null);
            pluginMenu.addItem(ymlFile);
        }
    }

    private void addKeys(FileConfiguration config, File file, InventoryGUI configYMLMenu, Plugin pluginEditing) {
        ConfigurationSection[] keys = ConfigUtil.getConfigurationSections(config);
        for (ConfigurationSection key : keys) {
            if (key == null) {
                continue;
            }
            ItemStack item = ItemUtil.createItem(Material.PAPER, ChatColor.RESET + key.getName());
            //Create GUI for all the keys
            InventoryGUI keyMenu = InventoryUtil.createInventoryGUI(null, 54, key.getName());
            keyMenu.setOpenOnClose(configYMLMenu);
            addSubKeys(config, file, key, keyMenu, pluginEditing);
            InventoryItem inventoryItem = new InventoryItem(item, new OpenInventory(keyMenu), null);
            //Add keyMenu item to configYMLMenu
            configYMLMenu.addItem(inventoryItem);
        }
    }

    private void addSubKeys(FileConfiguration config, File file, ConfigurationSection key, InventoryGUI keyMenu, Plugin pluginEditing) {
        for (String subKey : key.getKeys(false)) {
            if (subKey == null) {
                continue;
            }
            ItemStack item = ItemUtil.createItem(Material.TRIPWIRE_HOOK, ChatColor.RESET + subKey);
            String perm2 = Objects.requireNonNullElseGet(perm, () -> "neoconfig.edit." + pluginEditing.getName());
            InventoryItem inventoryItem = new InventoryItem(item, new ChangeKey(subKey, config, file, key, keyMenu,
                    completeFunction, perm2, permMessage, plugin), null);
            keyMenu.addItem(inventoryItem);
        }
        //Set not to unregister the keyMenu as it opens an anvil that tricks functionality to unregister all
        keyMenu.setUnregisterOnClose(false);
    }

    private void createPluginItem(Plugin p, InventoryGUI menu) {
        ItemStack item = ItemUtil.createItem(Material.BOOKSHELF, ChatColor.RESET + p.getName());

        //Create plugin menu with all the keys
        InventoryGUI pluginMenu = InventoryUtil.createInventoryGUI(null, 54, p.getName());
        if (addFiles(p, pluginMenu, plugin.getDataFolder().listFiles())) {
            //Add pluginMenu item to main menu
            InventoryItem inventoryItem = new InventoryItem(item, new OpenInventory(pluginMenu), null);
            menu.addItem(inventoryItem);
        }
        pluginMenu.setOpenOnClose(menu);
    }
}
