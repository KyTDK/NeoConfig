package com.neomechanical.neoconfig.commands;

import com.neomechanical.neoconfig.NeoConfig;
import com.neomechanical.neoconfig.menu.ConfigMenu;
import com.neomechanical.neoutils.commandManager.SubCommand;
import com.neomechanical.neoutils.inventory.InventoryUtil;
import com.neomechanical.neoutils.inventory.managers.data.InventoryGUI;
import com.neomechanical.neoutils.messages.MessageUtil;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

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
        InventoryGUI inventoryGUI;
        Plugin plugin = Bukkit.getPluginManager().getPlugin(args[0]);
        if (plugin == null) {
            new MessageUtil(NeoConfig.adventure()).sendMM(player, "<red><bold>Plugin not found");
            return;
        }
        inventoryGUI = ConfigMenu.generateMenu(plugin);
        InventoryUtil.openInventory(playerAsPlayer, inventoryGUI);
    }

    @Override
    public List<String> tabSuggestions() {
        return null;
    }

    @Override
    public Map<String, List<String>> mapSuggestions() {
        return null;
    }
}
