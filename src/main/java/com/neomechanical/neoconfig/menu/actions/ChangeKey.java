package com.neomechanical.neoconfig.menu.actions;

import com.neomechanical.neoutils.config.yaml.YamlConfSection;
import com.neomechanical.neoutils.config.yaml.YamlUtils;
import com.neomechanical.neoutils.inventory.InventoryUtil;
import com.neomechanical.neoutils.inventory.managers.data.InventoryGUI;
import com.neomechanical.neoutils.java.Lists;
import com.neomechanical.neoutils.messages.MessageUtil;
import net.wesjd.anvilgui.AnvilGUI;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;

import static com.neomechanical.neoconfig.NeoConfig.getLanguageManager;

public class ChangeKey {
    //Required
    private final String subKey;
    private final File file;
    private final YamlConfSection dataSect;
    private final Map<String, Object> dataMain;
    private final Plugin pluginInstance;
    //Optional
    private final BiConsumer<Player, String> completeFunction;
    private final String perm;
    private final Supplier<String> permMessage;
    private final Consumer<Player> closeFunction;
    private final String title;
    private final InventoryGUI restoreInventory;

    public ChangeKey(ChangeKeyBuilder changeKeyBuilder) {
        //Required
        this.subKey = changeKeyBuilder.subKey;
        this.dataSect = changeKeyBuilder.dataSect;
        this.dataMain = changeKeyBuilder.dataMain;
        this.file = changeKeyBuilder.file;
        this.pluginInstance = changeKeyBuilder.pluginInstance;
        //Optional
        this.restoreInventory = changeKeyBuilder.restoreInventory;
        this.completeFunction = changeKeyBuilder.completeFunction;
        this.closeFunction = changeKeyBuilder.closeFunction;
        this.perm = changeKeyBuilder.perm;
        this.title = changeKeyBuilder.title;
        this.permMessage = changeKeyBuilder.permMessage;
    }

    public void action(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        if (perm != null && !player.hasPermission(perm)) {
            MessageUtil.sendMM(player, permMessage.get());
            return;
        }
        Object initialKeyValue = data.get(subKey);
        if (initialKeyValue == null) {
            throw new IllegalArgumentException("Key " + subKey + " does not exist in " + file.getName());
        }
        if (initialKeyValue instanceof List) {
            new ListEditor.ListEditorBuilder(player, pluginInstance, this, (List<?>) initialKeyValue, subKey, yaml, file, data, restoreInventory)
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
                        data.put(subKey, text);
                    } else if (initialKeyValue instanceof Integer) {
                        data.put(subKey, Integer.parseInt(text));
                    } else if (initialKeyValue instanceof Double) {
                        data.put(subKey, Double.parseDouble(text));
                    } else if (initialKeyValue instanceof Boolean) {
                        data.put(subKey, Boolean.parseBoolean(text));
                    } else {
                        throw new IllegalArgumentException("Unsupported type: " + initialKeyValue.getClass().getName());
                    }
                    try {
                        YamlUtils.save(file, data);
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
                    if (restoreInventory != null) {
                        new BukkitRunnable() {
                            @Override
                            public void run() {
                                InventoryUtil.openInventory(playerAuthor, restoreInventory);
                            }
                        }.runTaskLater(pluginInstance, 1L);
                    }
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
        if (data.get(subKey) instanceof List) {
            initialKeyValueList = (List<?>) data.get(subKey);
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
                        YamlUtils.save(file, data);
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
        data.put(subKey, newList);
    }

    public static class ChangeKeyBuilder {
        //Required
        private final String subKey;
        private final File file;
        private final YamlConfSection dataSect;
        private final Map<String, Object> dataMain;
        private final Plugin pluginInstance;
        //Optional
        private BiConsumer<Player, String> completeFunction;
        private String perm;
        private Supplier<String> permMessage;
        private Consumer<Player> closeFunction;
        private String title;
        private InventoryGUI restoreInventory;

        public ChangeKeyBuilder(String subKey, File file, YamlConfSection dataSect, Map<String, Object> dataMain, Plugin pluginInstance) {
            this.subKey = subKey;
            this.file = file;
            this.dataSect = dataSect;
            this.dataMain = dataMain;
            this.pluginInstance = pluginInstance;
        }

        public ChangeKeyBuilder setCompleteFunction(BiConsumer<Player, String> completeFunction) {
            this.completeFunction = completeFunction;
            return this;
        }

        public ChangeKeyBuilder setPerm(String perm) {
            this.perm = perm;
            return this;
        }

        public ChangeKeyBuilder setPermMessage(Supplier<String> permMessage) {
            this.permMessage = permMessage;
            return this;
        }

        public ChangeKeyBuilder setCloseFunction(Consumer<Player> closeFunction) {
            this.closeFunction = closeFunction;
            return this;
        }

        public ChangeKeyBuilder setTitle(String title) {
            this.title = title;
            return this;
        }

        public ChangeKeyBuilder setRestoreInventory(InventoryGUI restoreInventory) {
            this.restoreInventory = restoreInventory;
            return this;
        }
        public ChangeKey build() {
            return new ChangeKey(this);
        }
    }
}
