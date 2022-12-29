package com.neomechanical.neoconfig.menu.actions;

import com.neomechanical.neoutils.inventory.InventoryUtil;
import com.neomechanical.neoutils.inventory.managers.data.InventoryGUI;
import com.neomechanical.neoutils.kyori.adventure.text.Component;
import com.neomechanical.neoutils.kyori.adventure.text.event.ClickEvent;
import com.neomechanical.neoutils.kyori.adventure.text.format.TextDecoration;
import com.neomechanical.neoutils.messages.MessageUtil;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.conversations.*;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class ConversationEditor {
    private final JavaPlugin plugin;

    public ConversationEditor(JavaPlugin plugin) {
        this.plugin = plugin;
    }
    public void main(Player player, Object initialKeyValue, ConfigurationSection key, String subKey,
                     FileConfiguration config, File file, BiConsumer<Player, String> completeFunction,
                     Consumer<Player> closeFunction, InventoryGUI restoreInventory) {
        // Set the initial value as the conversation's input
        ConversationFactory factory = new ConversationFactory(plugin)
                .withFirstPrompt(new StringPrompt() {
                    @Override
                    public String getPromptText(ConversationContext context) {
                        Component notice = Component.text(initialKeyValue.toString())
                                .decorate(TextDecoration.UNDERLINED)
                                .clickEvent(ClickEvent.copyToClipboard(initialKeyValue.toString()));
                        MessageUtil.sendMM(player, notice);
                        return "Click text above to copy to clipboard:";
                    }
                    @Override
                    public Prompt acceptInput(ConversationContext context, String input) {
                        // Save the new value of the key and end the conversation
                        context.setSessionData("input", input);
                        String newKeyValue = (String) context.getSessionData("input");
                        ChangeKey.setKey(initialKeyValue, key, subKey, newKeyValue, config, file, completeFunction, player);
                        if (closeFunction != null) {
                            closeFunction.accept(player);
                        }
                        InventoryUtil.openInventory(player, restoreInventory);
                        return Prompt.END_OF_CONVERSATION;
                    }
                });
        Conversation conversation = factory.buildConversation(player);
        conversation.begin();
    }
}