package com.neomechanical.neoconfig.menu.actions;

import com.neomechanical.neoutils.inventory.GUIAction;
import com.neomechanical.neoutils.inventory.InventoryUtil;
import com.neomechanical.neoutils.inventory.managers.data.InventoryGUI;
import com.neomechanical.neoutils.messages.MessageUtil;
import net.wesjd.anvilgui.AnvilGUI;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.io.IOException;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

public class ChangeKey extends GUIAction {
    private final ConfigurationSection key;
    private final String subKey;
    private final File file;
    private final FileConfiguration config;
    private final Plugin pluginInstance;
    private final InventoryGUI restoreInventory;
    private final BiConsumer<Player, String> completeFunction;
    private final String perm;
    private final Supplier<String> permMessage;

    public ChangeKey(String subKey, FileConfiguration config, File file, ConfigurationSection key,
                     InventoryGUI restoreInventory, BiConsumer<Player, String> completeFunction, String perm, Supplier<String> permMessage, Plugin pluginInstance) {
        this.subKey = subKey;
        this.config = config;
        this.key = key;
        this.file = file;
        this.restoreInventory = restoreInventory;
        this.completeFunction = completeFunction;
        this.perm = perm;
        this.permMessage = permMessage;
        this.pluginInstance = pluginInstance;
    }

    @Override
    public void action(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        if (!player.hasPermission(perm)) {
            MessageUtil.sendMM(player, permMessage.get());
            return;
        }
        Object initialKeyValue = key.get(subKey);
        if (initialKeyValue == null) {
            throw new IllegalArgumentException("Key " + subKey + " does not exist in " + file.getName());
        }
        if (initialKeyValue.toString().length() > 50) {
            MessageUtil.sendMM(player, "<red><bold>Key value is too long.");
            return;
        }
        new AnvilGUI.Builder()
                .onComplete((playerAuthor, text) -> {                                    //called when the inventory output slot is clicked
                    if (initialKeyValue instanceof String) {
                        key.set(subKey, text);
                    } else if (initialKeyValue instanceof Integer) {
                        key.set(subKey, Integer.parseInt(text));
                    } else if (initialKeyValue instanceof Double) {
                        key.set(subKey, Double.parseDouble(text));
                    } else if (initialKeyValue instanceof Boolean) {
                        key.set(subKey, Boolean.parseBoolean(text));
                    } else {
                        throw new IllegalArgumentException("Unsupported type: " + initialKeyValue.getClass().getName());
                    }
                    try {
                        config.save(file);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    if (completeFunction != null) {
                        completeFunction.accept(player, text);
                    }
                    InventoryUtil.openInventory(playerAuthor, restoreInventory);
                    return AnvilGUI.Response.close();
                })
                .onClose(playerAuthor -> {//called when the inventory is closed
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            InventoryUtil.openInventory(playerAuthor, restoreInventory);
                        }
                    }.runTaskLater(pluginInstance, 1L);
                })
                .text(initialKeyValue.toString())                              //sets the text the GUI should start with
                .title("Change key")                                       //set the title of the GUI (only works in 1.14+)
                .plugin(pluginInstance)                                          //set the plugin instance
                .open(player);
    }
}
