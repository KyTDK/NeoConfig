package com.neomechanical.neoconfig.commands;

import com.neomechanical.neoconfig.NeoConfig;
import com.neomechanical.neoutils.NeoUtils;
import com.neomechanical.neoutils.commandManager.CommandManager;

public class RegisterCommands {
    public static void register() {
        CommandManager commandManager = new CommandManager(NeoConfig.getInstance(), "neoconfig");
        //set messages
        commandManager.setErrorCommandNotFound(NeoUtils.getLanguageManager().getString("commandGeneric.errorCommandNotFound", null));
        commandManager.setErrorNoPermission(NeoUtils.getLanguageManager().getString("commandGeneric.errorNoPermission", null));
        commandManager.setErrorNotPlayer(NeoUtils.getLanguageManager().getString("commandGeneric.errorNotPlayer", null));
        //register commands
        commandManager.registerMainCommand(new MainCommand());
        commandManager.registerSubCommand(new EditCommand());
        commandManager.registerSubCommand(new HelpCommand(commandManager));
        commandManager.registerSubCommand(new ReloadCommand());
    }
}
