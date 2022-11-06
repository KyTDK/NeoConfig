package com.neomechanical.neoconfig.commands;

import com.neomechanical.neoconfig.utils.messages.Messages;
import com.neomechanical.neoutils.NeoUtils;
import com.neomechanical.neoutils.commands.CommandBuilder;
import com.neomechanical.neoutils.commands.easyCommands.EasyHelpCommand;
import com.neomechanical.neoutils.languages.LanguageManager;
import org.bukkit.plugin.java.JavaPlugin;

public class RegisterCommands {
    private static final LanguageManager languageManager = NeoUtils.getNeoUtilities().getManagers().getLanguageManager();
    private final JavaPlugin plugin;

    public RegisterCommands(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public void register() {
        CommandBuilder commandBuilder = new CommandBuilder(plugin, new MainCommand());
        //set messages
        commandBuilder.setErrorCommandNotFound(() -> languageManager.getString("commandGeneric.errorCommandNotFound", null))
                .setErrorNoPermission(() -> languageManager.getString("commandGeneric.errorNoPermission", null))
                .setErrorNotPlayer(() -> languageManager.getString("commandGeneric.errorNotPlayer", null))
                .setAliases("neoconfig", "config")
                .addSubcommand(new EditCommand())
                .addSubcommand(new EasyHelpCommand("nc",
                        "/np help", "See the help menu", "neoconfig.help", false, Messages.MAIN_PREFIX, Messages.MAIN_SUFFIX))
                .addSubcommand(new ReloadCommand())
                .addSubcommand(new ConfigCommand())
                .register();
    }
}
