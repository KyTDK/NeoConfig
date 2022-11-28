package com.neomechanical.neoconfig.menu.actions;

import org.bukkit.conversations.Conversation;
import org.bukkit.conversations.Prompt;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class ConversationEditor {
    private final JavaPlugin plugin;

    public ConversationEditor(JavaPlugin plugin) {
        this.plugin = plugin;
    }
    public void main(Player player) {
        Conversation conversation = new Conversation(plugin, player, new Prompt());
    }
}
