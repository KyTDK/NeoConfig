package com.neomechanical.neoconfig.menu.actions;

import com.neomechanical.neoutils.inventory.InventoryUtil;
import com.neomechanical.neoutils.inventory.managers.data.InventoryGUI;
import com.neomechanical.neoutils.java.Lists;
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
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;

import static com.neomechanical.neoconfig.NeoConfig.getLanguageManager;

public class ChangeKey {
    private final ConfigurationSection key;
    private final String subKey;
    private final File file;
    private final FileConfiguration config;
    private final Plugin pluginInstance;
    private final InventoryGUI restoreInventory;
    private final BiConsumer<Player, String> completeFunction;
    private final String perm;
    private final Supplier<String> permMessage;
    private final Consumer<Player> closeFunction;
    private final String title;

    public ChangeKey(String subKey, FileConfiguration config, File file, ConfigurationSection key,
                     InventoryGUI restoreInventory, BiConsumer<Player, String> completeFunction,
                     Consumer<Player> closeFunction, String perm, String title, Supplier<String> permMessage, Plugin pluginInstance) {
        this.subKey = subKey;
        this.config = config;
        this.key = key;
        this.file = file;
        this.restoreInventory = restoreInventory;
        this.completeFunction = completeFunction;
        this.closeFunction = closeFunction;
        this.perm = perm;
        this.title = title;
        this.permMessage = permMessage;
        this.pluginInstance = pluginInstance;
    }

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
        if (initialKeyValue instanceof List) {
            new ListEditor.ListEditorBuilder(player, pluginInstance, (List<?>) initialKeyValue, subKey, config, file, key, restoreInventory)
                    .setCompleteFunction(completeFunction)
                    .setCloseFunction(closeFunction)
                    .setTitle(title)
                    .setPerm(perm)
                    .setPermMessage(permMessage)
                    .create()
                    .open();
            return;
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
                    return AnvilGUI.Response.close();
                })
                .title(title)
                .onClose(playerAuthor -> {//called when the inventory is closed
                    if (closeFunction != null) {
                        closeFunction.accept(player);
                    }
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

    public void actionList(InventoryClickEvent event, int initialKeyValueIndex) {
        Player player = (Player) event.getWhoClicked();
        String initialKeyValueShow;
        List<?> initialKeyValueList;
        if (key.get(subKey) instanceof List) {
            initialKeyValueList = (List<?>) key.get(subKey);
            if (initialKeyValueList != null) {
                initialKeyValueShow = initialKeyValueList.get(initialKeyValueIndex).toString();
            } else {
                MessageUtil.sendMM(player, getLanguageManager().getString("generic.errorUnknown", null));
                return;
            }
        } else {
            MessageUtil.sendMM(player, getLanguageManager().getString("generic.errorUnknown", null));
            return;
        }
        new AnvilGUI.Builder()
                .onComplete((playerAuthor, text) -> {//called when the inventory output slot is clicked
                    if (initialKeyValueList.get(0) instanceof String) {
                        updateList(initialKeyValueShow, initialKeyValueList, text);
                    } else if (initialKeyValueList.get(0) instanceof Integer) {
                        updateList(initialKeyValueShow, initialKeyValueList, text);
                    } else if (initialKeyValueList.get(0) instanceof Double) {
                        updateList(initialKeyValueShow, initialKeyValueList, text);
                    } else if (initialKeyValueList.get(0) instanceof Boolean) {
                        updateList(initialKeyValueShow, initialKeyValueList, text);
                    } else {
                        throw new IllegalArgumentException("Unsupported type");
                    }
                    try {
                        config.save(file);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    if (completeFunction != null) {
                        completeFunction.accept(player, text);
                    }
                    return AnvilGUI.Response.close();
                })
                .title(title)
                .onClose(playerAuthor -> {//called when the inventory is closed
                    if (closeFunction != null) {
                        closeFunction.accept(player);
                    }
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            InventoryUtil.openInventory(playerAuthor, restoreInventory);
                        }
                    }.runTaskLater(pluginInstance, 1L);
                })
                .text(initialKeyValueShow)                              //sets the text the GUI should start with
                .title("Change key")                                       //set the title of the GUI (only works in 1.14+)
                .plugin(pluginInstance)                                          //set the plugin instance
                .open(player);
    }

    private <type> void updateList(String initialKeyValueShow, List<?> initialKeyValueList, String text) {
        List<type> newList = Lists.cast(initialKeyValueList);
        newList.set(newList.indexOf(initialKeyValueShow), (type) text);
        key.set(subKey, newList);
    }
}
