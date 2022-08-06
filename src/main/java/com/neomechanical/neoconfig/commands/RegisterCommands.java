package com.neomechanical.neoconfig.commands;

import com.neomechanical.neoconfig.NeoConfig;
import com.neomechanical.neoutils.commandManager.CommandManager;

public class RegisterCommands {
    public static void register() {
        CommandManager commandManager = new CommandManager();
        commandManager.setAudiences(NeoConfig.adventure());
        commandManager.registerMainCommand(new MainCommand());
        commandManager.registerSubCommand(new EditCommand());
        commandManager.registerSubCommand(new HelpCommand(commandManager));
        commandManager.init(NeoConfig.getInstance(), "neoconfig");
    }
}
