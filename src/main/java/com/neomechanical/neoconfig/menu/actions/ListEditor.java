package com.neomechanical.neoconfig.menu.actions;

import com.neomechanical.neoutils.inventory.InventoryUtil;
import com.neomechanical.neoutils.inventory.actions.OpenInventory;
import com.neomechanical.neoutils.inventory.managers.data.InventoryGUI;
import com.neomechanical.neoutils.inventory.managers.data.InventoryItem;
import com.neomechanical.neoutils.items.ItemUtil;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.plugin.Plugin;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class ListEditor {
    //Required
    private final Player player;
    private final Plugin pluginInstance;
    private final ChangeKey changeKey;
    private final List<?> initialKeyValue;
    private final String subKey;
    private final Yaml config;
    private final File file;
    private final Map<String, Object> key;
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
        this.changeKey = listEditorBuilder.changeKey;
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

    public Yaml getConfig() {
        return config;
    }

    public File getFile() {
        return file;
    }

    public Map<String, Object> getKey() {
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
        for (Object object : initialKeyValue) {
            InventoryGUI inventoryToHandle;
            if (!pages.isEmpty()) {
                //Get the last page in the list
                inventoryToHandle = pages.get(pages.size() - 1);
            } else {
                inventoryToHandle = elementalGUI;
                InventoryItem close = new InventoryItem(ItemUtil.createItem(Material.BARRIER, "&cClose"),
                        (event) -> InventoryUtil.openInventory(player, restoreInventory), null);
                inventoryToHandle.setItem(0, close);
            }
            //inventoryToHandle is an empty page
            InventoryItem edit = new InventoryItem(ItemUtil.createItem(Material.TRIPWIRE_HOOK, "&aEdit '" + object.toString() + "'"),
                    (event) -> changeKey.actionList(event, initialKeyValue.indexOf(object)), null);
            inventoryToHandle.setItem(4, edit);
            if (!pages.isEmpty()) {
                Consumer<InventoryClickEvent> close;
                if (pages.size() == 1) {
                    close = (event) -> InventoryUtil.openInventory(player, elementalGUI);
                } else {
                    close = (event) -> new OpenInventory(pages.get(pages.indexOf(inventoryToHandle) - 1)).action(event);
                }
                InventoryItem left = new InventoryItem(ItemUtil.createItem(Material.OAK_BUTTON, "&aLeft"), close, null);
                inventoryToHandle.setItem(0, left);
            }
            if (initialKeyValue.indexOf(object) != initialKeyValue.size() - 1) {
                InventoryItem right = new InventoryItem(ItemUtil.createItem(Material.OAK_BUTTON, "&aRight"),
                        (event) -> InventoryUtil.openInventory(player, pages.get(pages.indexOf(inventoryToHandle) + 1)), null);
                inventoryToHandle.setItem(8, right);
            }
            //Create a new empty page if there is another object next
            if (initialKeyValue.indexOf(object) != initialKeyValue.size() - 1) {
                pages.add(InventoryUtil.createInventoryGUI(player, elementalGUI.getSize(), elementalGUI.getTitle()));
            }
        }
        InventoryUtil.openInventory(player, elementalGUI);
    }

    public static class ListEditorBuilder {
        //Required
        private final Player player;
        private final Plugin pluginInstance;
        private final ChangeKey changeKey;
        private final List<?> initialKeyValue;
        private final String subKey;
        private final Yaml config;
        private final File file;
        private final Map<String, Object> key;
        private InventoryGUI restoreInventory;

        //Optional
        private BiConsumer<Player, String> completeFunction;
        private Consumer<Player> closeFunction;
        private String title;
        private String perm;
        private Supplier<String> permMessage;

        public ListEditorBuilder(Player player, Plugin pluginInstance, ChangeKey changeKey, List<?> initialKeyValue, String subKey, Yaml config, File file, Map<String, Object> key,
                                 InventoryGUI restoreInventory) {
            this.player = player;
            this.pluginInstance = pluginInstance;
            this.changeKey = changeKey;
            this.initialKeyValue = initialKeyValue;
            this.subKey = subKey;
            this.config = config;
            this.file = file;
            this.key = key;
            this.restoreInventory = restoreInventory;
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