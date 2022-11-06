package com.neomechanical.neoconfig.menu;

import com.neomechanical.neoconfig.menu.actions.ChangeKey;
import com.neomechanical.neoutils.NeoUtils;
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
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class ConfigMenu {
    private Plugin plugin;
    private Plugin pluginEditing = null;
    private BiConsumer<Player, String> completeFunction;
    private String perm = null;
    private String title = "Change key";
    private Supplier<String> permMessage = () -> "<red><bold>You do not have permission to use this command";
    private Consumer<Player> closeFunction;

    public ConfigMenu(Plugin plugin) {
        this.plugin = plugin;
    }

    private InventoryGUI generateMenu() {
        if (pluginEditing != null) {
            plugin = pluginEditing;
            //Create plugin menu with all the keys
            InventoryGUI pluginMenu = InventoryUtil.createInventoryGUI(null, 54, pluginEditing.getName());
            if (addFiles(pluginMenu, pluginEditing.getDataFolder().listFiles())) {
                //Add pluginMenu item to main menu
                return pluginMenu;
            }
        }
        InventoryGUI menu = InventoryUtil.createInventoryGUI(null, 54, "NeoConfig");
        //get plugins
        List<Plugin> plugins = Arrays.stream(Bukkit.getPluginManager().getPlugins()).collect(Collectors.toList());
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

    public ConfigMenu onClose(Consumer<Player> closeFunction) {
        this.closeFunction = closeFunction;
        return this;
    }

    public ConfigMenu setPluginEditing(Plugin pluginEditing) {
        this.pluginEditing = pluginEditing;
        return this;
    }

    public ConfigMenu title(String title) {
        this.title = title;
        return this;
    }

    @SuppressWarnings("unused")
    public ConfigMenu permission(String perm, Supplier<String> permMessage) {
        this.perm = perm;
        this.permMessage = permMessage;
        return this;
    }

    public void open(Player player) {
        InventoryGUI menu = generateMenu();
        InventoryUtil.openInventory(player, menu);
    }

    private void createPluginItem(Plugin p, InventoryGUI menu) {
        ItemStack item = ItemUtil.createItem(Material.BOOKSHELF, ChatColor.RESET + p.getName());

        //Create plugin menu with all the keys
        InventoryGUI pluginMenu = InventoryUtil.createInventoryGUI(null, 54, p.getName());
        if (p.getDataFolder().listFiles()!=null) {
            //Add pluginMenu item to main menu
            InventoryItem inventoryItem = new InventoryItem.InventoryItemBuilder(
                    () -> item)
                    .setAction((event) -> {
                        addFiles(pluginMenu, p.getDataFolder().listFiles());
                        pluginMenu.open((Player) event.getWhoClicked());
                    })
                    .build();
            menu.addItem(inventoryItem);
        }
        pluginMenu.setOpenOnClose(menu);
    }

    //PluginMenu contains all the plugins interface items
    private boolean addFiles(InventoryGUI pluginMenu, File[] dataFolder) {
        if (dataFolder == null) {
            return false;
        }
        for (File file : dataFolder) {
            // Add all yml files in the data folder that are directories
            if (file.isDirectory()) {
                File[] dirFiles = file.listFiles();
                //if file is yml, then made an array of them
                File[] ymlFiles = file.listFiles((dir, name) -> name.endsWith(".yml"));
                if (ymlFiles != null && ymlFiles.length == 0) {
                    continue;
                }
                InventoryItem inventoryItem = new InventoryItem.InventoryItemBuilder(
                        () -> ItemUtil.createItem(Material.CHEST, ChatColor.RESET + file.getName()))
                        .setAction((event) -> {
                            InventoryGUI directory = InventoryUtil.createInventoryGUI(null, 54, file.getName());
                            directory.setOpenOnClose(pluginMenu);
                            addFiles(directory, dirFiles);
                            directory.open((Player) event.getWhoClicked());
                        })
                        .build();
                pluginMenu.addItem(inventoryItem);
                continue;
            }
            // Add all yml files inside the plugin data folder (excluding directories)
            addFile(file, pluginMenu);
        }
        return true;
    }
    // Adds all yml files inside the plugin data folder (excluding directories)
    private void addFile(File file, InventoryGUI pluginMenu) {
        if (file.getName().endsWith(".yml")) {
            try {
                FileConfiguration config = YamlConfiguration.loadConfiguration(file);
                //Create YML item with all keys
                InventoryGUI keysMenu = InventoryUtil.createInventoryGUI(null, 54, "Keys");
                keysMenu.setOpenOnClose(pluginMenu);
                //Add Key items to configYMLMenu
                if (!config.getKeys(false).isEmpty()) {
                    ItemStack item = ItemUtil.createItem(Material.BOOK, ChatColor.RESET + file.getName());
                    InventoryItem ymlFile = new InventoryItem.InventoryItemBuilder(
                            () -> item)
                            .setAction((event) -> {
                                addKeys(config, config, file, keysMenu, plugin);
                                // Add YAML fields
                                addSubKeys(config, file, config, keysMenu, plugin);
                                keysMenu.open((Player) event.getWhoClicked());
                            })
                            .build();
                    pluginMenu.addItem(ymlFile);
                }
            } catch (Exception ignore) {
                NeoUtils.getNeoUtilities().getFancyLogger().warn("[DO NOT REPORT THIS] Error deserializing " + file.getName()
                        + ". This isn't a problem with the plugin but with the config file of another plugin.");
            }
        }
    }

    private void addKeys(FileConfiguration config, ConfigurationSection configurationSection, File file,
                         InventoryGUI configYMLMenu, Plugin pluginEditing) {
        ConfigurationSection[] keys = ConfigUtil.getConfigurationSections(configurationSection);
        if (configurationSection.getKeys(false).isEmpty()) {
            return;
        }
        for (ConfigurationSection key : keys) {
            if (key == null || key.getKeys(false).isEmpty()) {
                continue;
            }
            ItemStack item = ItemUtil.createItem(Material.PAPER, ChatColor.RESET + key.getName());
            //Create GUI for all the keys
            InventoryGUI keyMenu = InventoryUtil.createInventoryGUI(null, 54, key.getName());
            keyMenu.setOpenOnClose(configYMLMenu);
            if (!key.getKeys(false).isEmpty()) {
                InventoryItem inventoryItem = new InventoryItem.InventoryItemBuilder(
                        () -> item)
                        .setAction((event) -> {
                            addSubKeys(config, file, key, keyMenu, pluginEditing);
                            new OpenInventory(keyMenu).action(event);
                        })
                        .build();
                //Add keyMenu item to configYMLMenu
                configYMLMenu.addItem(inventoryItem);
            }
        }
    }

    private void addSubKeys(FileConfiguration config, File file, ConfigurationSection key, InventoryGUI keyMenu, Plugin pluginEditing) {
        Set<String> keys = key.getKeys(false);
        if (keys.isEmpty()) {
            return;
        }
        for (String subKey : keys) {
            ConfigurationSection section = key.getConfigurationSection(subKey);
            if (key.isConfigurationSection(subKey) && section != null) {
                addKeys(config, section, file, keyMenu, pluginEditing);
                continue;
            }
            ItemStack item = ItemUtil.createItem(Material.TRIPWIRE_HOOK, ChatColor.RESET + subKey);
            String perm2;
            if (perm != null) {
                perm2 = perm;
            } else {
                perm2 = "neoconfig.edit." + pluginEditing.getName();
            }
            InventoryItem inventoryItem = new InventoryItem.InventoryItemBuilder(
                    () -> item)
                    .setAction((event) -> new ChangeKey(subKey, config, file, key, keyMenu,
                            completeFunction, closeFunction, perm2, title, permMessage, plugin).action(event))
                    .build();
            keyMenu.addItem(inventoryItem);
        }
    }
}
