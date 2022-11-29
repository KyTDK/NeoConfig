package com.neomechanical.neoconfig.menu.actions;

import org.bukkit.conversations.*;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class ConversationEditor {
    private final JavaPlugin plugin;

    public ConversationEditor(JavaPlugin plugin) {
        this.plugin = plugin;
    }
    public void main(Player player) {
        Prompt prompt = new StringPrompt() {
            @Override
            public String getPromptText(ConversationContext context) {
                return "Long live the king!";
            }

            @Override
            public Prompt acceptInput(ConversationContext context, String input) {
                // Here we connect the next prompt
                return Prompt.END_OF_CONVERSATION;
            }
        };
        ;
        ConversationFactory factory = new ConversationFactory(plugin); // We need our plugin reference here
        factory.withFirstPrompt(prompt);
        Conversation conversation = factory.buildConversation(player);
        conversation.begin();

    }
}
