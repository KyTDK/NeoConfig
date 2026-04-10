package com.neomechanical.neoconfig.menu.actions;

import com.neomechanical.neoutils.NeoUtils;
import com.neomechanical.neoutils.inventory.InventoryUtil;
import com.neomechanical.neoutils.inventory.managers.data.InventoryGUI;
import com.neomechanical.neoutils.inventory.managers.data.InventoryItem;
import com.neomechanical.neoutils.items.ItemUtil;
import com.neomechanical.neoutils.version.items.ItemVersionWrapper;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class ListEditor {
    //Required
    private final Player player;
    private final Plugin pluginInstance;
    private final List<?> initialKeyValue;
    private final String subKey;
    private final FileConfiguration config;
    private final File file;
    private final ConfigurationSection key;
    private final InventoryGUI restoreInventory;

    //Optional
    private final BiConsumer<Player, String> completeFunction;
    private final Consumer<Player> closeFunction;
    private final String title;
    private final String perm;
    private final Supplier<String> permMessage;

    public ListEditor(ListEditorBuilder listEditorBuilder) {
        this.player = listEditorBuilder.player;
        this.pluginInstance = listEditorBuilder.pluginInstance;
        this.initialKeyValue = listEditorBuilder.initialKeyValue;
        this.subKey = listEditorBuilder.subKey;
        this.config = listEditorBuilder.config;
        this.file = listEditorBuilder.file;
        this.key = listEditorBuilder.key;
        this.restoreInventory = listEditorBuilder.restoreInventory;
        this.completeFunction = listEditorBuilder.completeFunction;
        this.closeFunction = listEditorBuilder.closeFunction;
        this.title = listEditorBuilder.title;
        this.perm = listEditorBuilder.perm;
        this.permMessage = listEditorBuilder.permMessage;
    }

    public Player getPlayer() {
        return player;
    }

    public Plugin getPluginInstance() {
        return pluginInstance;
    }

    public List<?> getInitialKeyValue() {
        return initialKeyValue;
    }

    public String getSubKey() {
        return subKey;
    }

    public FileConfiguration getConfig() {
        return config;
    }

    public File getFile() {
        return file;
    }

    public ConfigurationSection getKey() {
        return key;
    }

    public InventoryGUI getRestoreInventory() {
        return restoreInventory;
    }

    public BiConsumer<Player, String> getCompleteFunction() {
        return completeFunction;
    }

    public Consumer<Player> getCloseFunction() {
        return closeFunction;
    }

    public String getTitle() {
        return title;
    }

    public String getPerm() {
        return perm;
    }

    public Supplier<String> getPermMessage() {
        return permMessage;
    }

    public void open() {
        InventoryGUI elementalGUI = InventoryUtil.createInventoryGUI(player, 9, "List Editor");
        elementalGUI.setOpenOnClose(restoreInventory);
        List<InventoryGUI> pages = elementalGUI.getPages();
        if (initialKeyValue.isEmpty()) {
            InventoryItem close = new InventoryItem.InventoryItemBuilder(
                    () -> ItemUtil.createItem(Material.BARRIER, "&cClose"))
                    .setAction((event) -> InventoryUtil.openInventory(player, restoreInventory))
                    .build();
            InventoryItem empty = new InventoryItem.InventoryItemBuilder(
                    () -> ItemUtil.createItem(Material.PAPER, "&eThis list is empty"))
                    .build();
            elementalGUI.setItem(0, close);
            elementalGUI.setItem(4, empty);
            InventoryUtil.openInventory(player, elementalGUI);
            return;
        }
        Material button = ((ItemVersionWrapper) NeoUtils.getNeoUtilities().getInternalVersions().get("items")).oakButton();
        for (int initialKeyValueIndex = 0; initialKeyValueIndex < initialKeyValue.size(); initialKeyValueIndex++) {
            final int listIndex = initialKeyValueIndex;
            final Object object = initialKeyValue.get(listIndex);
            InventoryGUI inventoryToHandle = pages.isEmpty() ? elementalGUI : pages.get(pages.size() - 1);
            ChangeKey changeKey = new ChangeKey(subKey, config, file, key,
                    elementalGUI, completeFunction, closeFunction, perm, title, permMessage, pluginInstance);
            if (listIndex == 0) {
                InventoryItem close = new InventoryItem.InventoryItemBuilder(
                        () -> ItemUtil.createItem(Material.BARRIER, "&cClose"))
                        .setAction((event) -> InventoryUtil.openInventory(player, restoreInventory))
                        .build();
                inventoryToHandle.setItem(0, close);
            } else {
                InventoryGUI previousInventory = listIndex == 1 ? elementalGUI : pages.get(pages.size() - 2);
                InventoryItem left = new InventoryItem.InventoryItemBuilder(
                        () -> ItemUtil.createItem(button, "&aLeft"))
                        .setAction((event) -> InventoryUtil.openInventory(player, previousInventory))
                        .build();
                inventoryToHandle.setItem(0, left);
            }
            //inventoryToHandle is an empty page
            InventoryItem edit = new InventoryItem.InventoryItemBuilder(() -> {
                List<?> initialKeyValueList;
                String initialKeyValueShow;
                if (key.get(subKey) instanceof List) {
                    initialKeyValueList = (List<?>) key.get(subKey);
                    if (initialKeyValueList != null) {
                        initialKeyValueShow = initialKeyValueList.get(listIndex).toString();
                    } else {
                        initialKeyValueShow = object.toString();
                    }
                } else {
                    initialKeyValueShow = object.toString();
                }
                return ItemUtil.createItem(Material.TRIPWIRE_HOOK, "&aEdit '" + initialKeyValueShow + "'");
            }).setAction((event) -> changeKey.actionList(event, listIndex))
                    .build();
            inventoryToHandle.setItem(4, edit);
            if (listIndex != initialKeyValue.size() - 1) {
                elementalGUI.addPage(InventoryUtil.createInventoryGUI(player, elementalGUI.getSize(), elementalGUI.getTitle()));
                InventoryGUI nextInventory = pages.get(pages.size() - 1);
                InventoryItem right = new InventoryItem.InventoryItemBuilder(
                        () -> ItemUtil.createItem(button, "&aRight"))
                        .setAction((event) -> InventoryUtil.openInventory(player, nextInventory))
                        .build();
                inventoryToHandle.setItem(8, right);
            }
        }
        InventoryUtil.openInventory(player, elementalGUI);
    }

    public static class ListEditorBuilder {
        //Required
        private Player player;
        private Plugin pluginInstance;
        private List<?> initialKeyValue;
        private String subKey;
        private FileConfiguration config;
        private File file;
        private ConfigurationSection key;
        private InventoryGUI restoreInventory;

        //Optional
        private BiConsumer<Player, String> completeFunction;
        private Consumer<Player> closeFunction;
        private String title;
        private String perm;
        private Supplier<String> permMessage;

        public ListEditorBuilder(Player player, Plugin pluginInstance, List<?> initialKeyValue, String subKey, FileConfiguration config, File file, ConfigurationSection key,
                                 InventoryGUI restoreInventory) {
            this.player = player;
            this.pluginInstance = pluginInstance;
            this.initialKeyValue = initialKeyValue;
            this.subKey = subKey;
            this.config = config;
            this.file = file;
            this.key = key;
            this.restoreInventory = restoreInventory;
        }

        public ListEditorBuilder setPlayer(Player player) {
            this.player = player;
            return this;
        }

        public ListEditorBuilder setPluginInstance(Plugin pluginInstance) {
            this.pluginInstance = pluginInstance;
            return this;
        }

        public ListEditorBuilder setInitialKeyValue(List<?> initialKeyValue) {
            this.initialKeyValue = initialKeyValue;
            return this;
        }

        public ListEditorBuilder setSubKey(String subKey) {
            this.subKey = subKey;
            return this;
        }

        public ListEditorBuilder setConfig(FileConfiguration config) {
            this.config = config;
            return this;
        }

        public ListEditorBuilder setFile(File file) {
            this.file = file;
            return this;
        }

        public ListEditorBuilder setKey(ConfigurationSection key) {
            this.key = key;
            return this;
        }

        public ListEditorBuilder setRestoreInventory(InventoryGUI restoreInventory) {
            this.restoreInventory = restoreInventory;
            return this;
        }

        public ListEditorBuilder setCompleteFunction(BiConsumer<Player, String> completeFunction) {
            this.completeFunction = completeFunction;
            return this;
        }

        public ListEditorBuilder setCloseFunction(Consumer<Player> closeFunction) {
            this.closeFunction = closeFunction;
            return this;
        }

        public ListEditorBuilder setTitle(String title) {
            this.title = title;
            return this;
        }

        public ListEditorBuilder setPerm(String perm) {
            this.perm = perm;
            return this;
        }

        public ListEditorBuilder setPermMessage(Supplier<String> permMessage) {
            this.permMessage = permMessage;
            return this;
        }

        public ListEditor create() {
            return new ListEditor(this);
        }
    }
}
