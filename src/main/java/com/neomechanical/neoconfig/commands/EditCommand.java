package com.neomechanical.neoconfig.commands;

import com.neomechanical.neoconfig.NeoConfig;
import com.neomechanical.neoconfig.menu.ConfigMenu;
import com.neomechanical.neoutils.NeoUtils;
import com.neomechanical.neoutils.commandManager.SubCommand;
import com.neomechanical.neoutils.messages.MessageUtil;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class EditCommand extends SubCommand {
    @Override
    public String getName() {
        return "edit";
    }

    @Override
    public String getDescription() {
        return "Edit the config file of a specific plugin";
    }

    @Override
    public String getSyntax() {
        return "/nc edit <plugin name>";
    }

    @Override
    public String getPermission() {
        return "neoconfig.edit";
    }

    @Override
    public boolean playerOnly() {
        return true;
    }

    @Override
    public void perform(CommandSender player, String[] args) {
        Player playerAsPlayer = (Player) player;
        if (args.length == 1) {
            MessageUtil.sendMM(player, NeoUtils.getLanguageManager().getString("commandGeneric.errorInvalidSyntax", null));
            return;
        }
        Plugin plugin = Bukkit.getPluginManager().getPlugin(args[1]);
        if (plugin == null) {
            MessageUtil.sendMM(player, NeoUtils.getLanguageManager().getString("commandGeneric.errorInvalidSyntax", null));
            return;
        }
        if (player.hasPermission("neoconfig.edit." + plugin.getName())) {
            new ConfigMenu(NeoConfig.getInstance())
                    .open(playerAsPlayer, plugin);
        } else {
            MessageUtil.sendMM(player, NeoUtils.getLanguageManager().getString("commandGeneric.errorNoPermission", null));
        }
    }

    @Override
    public List<String> tabSuggestions() {
        List<String> suggestions = new ArrayList<>();
        for (Plugin plugin : Bukkit.getPluginManager().getPlugins()) {
            File[] dataFolder = plugin.getDataFolder().listFiles((directory, fileName) -> fileName.endsWith(".yml"));
            if (dataFolder == null) {
                continue;
            }
            suggestions.add(plugin.getName());
        }
        return suggestions;
    }

    @Override
    public Map<String, List<String>> mapSuggestions() {
        return null;
    }
}
