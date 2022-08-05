package com.neomechanical.neoconfig.commands;

import com.neomechanical.neoconfig.NeoConfig;
import com.neomechanical.neoutils.commandManager.CommandManager;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import java.io.File;

public class RegisterCommands {
    public static void register() {
        CommandManager commandManager = new CommandManager();
        commandManager.setAudiences(NeoConfig.adventure());
        commandManager.registerMainCommand(new MainCommand());
        for (Plugin plugin : Bukkit.getPluginManager().getPlugins()) {
            File[] dataFolder = plugin.getDataFolder().listFiles((directory, fileName) -> fileName.endsWith(".yml"));
            if (dataFolder == null) {
                continue;
            }
            commandManager.registerSubCommand(new IndividualPluginCommand(plugin.getName()));
        }
        commandManager.init(NeoConfig.getInstance(), "neoconfig");
    }
}
