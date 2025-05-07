package com.neomechanical.neoconfig.menu.actions;

import com.neomechanical.neoconfig.api.NeoConfigAPI;
import com.neomechanical.neoutils.NeoUtils;
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
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;

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
        if (!isSafeToEdit(initialKeyValue, player)) {
            return;
        }
        new AnvilGUI.Builder()
                .onClick((slot, stateSnapshot) -> {
                    if(slot != AnvilGUI.Slot.OUTPUT) {
                        return Collections.emptyList();
                    }
                    String text = stateSnapshot.getText();
                    setKey(initialKeyValue, key, subKey, text, config, file, completeFunction, player);
                    return Collections.singletonList(AnvilGUI.ResponseAction.close());
                })
                .title(title)
                .onClose(stateSnapshot -> {//called when the inventory is closed
                    if (closeFunction != null) {
                        closeFunction.accept(player);
                    }
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            Player playerAuthor = stateSnapshot.getPlayer();
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
                MessageUtil.sendMM(player, NeoConfigAPI.getProvider().getLanguageManager().getString("generic.errorUnknown", null));
                return;
            }
        } else {
            MessageUtil.sendMM(player, NeoConfigAPI.getProvider().getLanguageManager().getString("generic.errorUnknown", null));
            return;
        }
        if (!isSafeToEdit(initialKeyValueShow, player)) {
            return;
        }
        new AnvilGUI.Builder().
                onClose(completion -> {//called when the inventory output slot is clicked
                    String text = completion.getText();
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
                })
                .title(title)
                .onClose(stateSnapshot -> {//called when the inventory is closed
                    if (closeFunction != null) {
                        closeFunction.accept(player);
                    }
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            Player playerAuthor = stateSnapshot.getPlayer();
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

    public static void setKey(Object initialKeyValue, ConfigurationSection key, String subKey,
                              String text, FileConfiguration config, File file,
                              BiConsumer<Player, String> completeFunction, Player player) {
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
    }
    /**
     *
     * @param initialKeyValue object
     * @param player Player
     * @return If it safe to continue, if not it will return false and handle it accordingly
     */
    private boolean isSafeToEdit(Object initialKeyValue, Player player) {
        if (initialKeyValue.toString().length() > 50) {
            if (initialKeyValue.toString().length() > 256) {
                MessageUtil.sendMM(player, "<red><bold>Key value is too long.");
                return false;
            }
            InventoryGUI currentInventory = NeoUtils.getNeoUtilities().getManagers().getInventoryManager().getInventoryGUI(player.getOpenInventory().getTopInventory());
            if (currentInventory!=null) {
                currentInventory.close(player);
            }
            ConversationEditor conversationEditor = new ConversationEditor((JavaPlugin) pluginInstance);
            conversationEditor.main(player, initialKeyValue, key, subKey, config, file, completeFunction, closeFunction, restoreInventory);
            return false;
        }
        return true;
    }
}
