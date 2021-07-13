package me.polishkrowa.structurecompass;

import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.CompassMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;

public class StructureCompassCommand implements CommandExecutor, TabCompleter {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("structure-compass.use")) {
            sender.sendMessage(ChatColor.RED + "You don't have access to this command !");
            return true;
        }

        if (args == null || args.length == 0) {
            if (StructureCompass.isCommandEnabled())
                sender.sendMessage(ChatColor.RED + "Usage: /structure-compass <structure-type> [only-not-found]");
            else
                sender.sendMessage(ChatColor.RED + "This command is disabled in reason of a bug. Please ask your administrator to look at the console output.");
            return true;
        }

        if (args[0].equalsIgnoreCase("reload")) {
            if (!sender.hasPermission("structure-compass.reload")) {
                sender.sendMessage(ChatColor.RED + "You do not have the required permission to use this sub-command.");
                return true;
            }
            if (StructureCompass.loadConfig())
                sender.sendMessage(ChatColor.GREEN + "Config reloaded !");
            else
                sender.sendMessage(ChatColor.RED + "The config could not be reloaded. Please look at the console for more details. The plugin is therefore disabled.");
            return true;
        }

        if (!StructureCompass.isCommandEnabled()) {
            sender.sendMessage(ChatColor.RED + "This command is disabled in reason of a bug. Please ask your administrator to look at the console output.");
            return true;
        }

        if (args[0].equalsIgnoreCase("help")) {
            sender.sendMessage(ChatColor.YELLOW + "This command will give a compass that points to the chunk of the specified structure. " + ChatColor.BLUE + "If \"only-not-found\" set to true, the compass will point to a structure that hasn't been detected by the plugin before.");
            if (StructureCompass.getInstance().economyState.equals(EconomyState.VAULT))
                sender.sendMessage(ChatColor.YELLOW + "Current compass price: " + StructureCompass.getInstance().priceAmount + "$");
            else if (StructureCompass.getInstance().economyState.equals(EconomyState.ITEM))
                sender.sendMessage(ChatColor.YELLOW + "Current compass price: " + StructureCompass.getInstance().priceAmount + " " + StructureCompass.getPriceMaterial().name().toLowerCase());

            sender.sendMessage(ChatColor.YELLOW + "Usage: /structure-compass <structure-type> [only-not-found]");
            return true;
        }

        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "This command is only available for players.");
            return true;
        }

        Player player = (Player) sender;

        String structureName = args[0];
        if (!StructureType.getStructureTypes().containsKey(structureName.toLowerCase())) {
            player.sendMessage(ChatColor.RED + "Invalid structure type. Please look at https://minecraft.fandom.com/wiki/Commands/locate#Arguments in the Java Edition column for valid structure types. Usage: /structure-compass <structure-type> [only-not-found]");
            return true;
        }
        StructureType structureType = StructureType.getStructureTypes().get(structureName.toLowerCase());
        if (!StructureCompass.getStructures().contains(structureType)) {
            player.sendMessage(ChatColor.RED + "This structure isn't enabled in the configuration file. Please report to an admin if this is an error.");
            return true;
        }


        boolean useUnexploredOnly;
        if (args.length == 1) {
            useUnexploredOnly = false;
        } else {
            if (!args[1].equalsIgnoreCase("true") && !args[1].equalsIgnoreCase("false")) {
                player.sendMessage(ChatColor.RED + "Incorrect true/false value for second argument. Usage: /structure-compass <structure-type> [only-not-found]");
                return true;
            }
            useUnexploredOnly = Boolean.parseBoolean(args[1]);
        }

        // Price check
        if (!StructureCompass.getInstance().economyState.equals(EconomyState.NONE)) {
            if (StructureCompass.getInstance().economyState.equals(EconomyState.VAULT)) {
                //vault
                if (!VaultEconomy.getEcon().has(player, StructureCompass.getInstance().priceAmount)) {
                    player.sendMessage(ChatColor.RED + "You don't have enough money to afford this. You need at least " + StructureCompass.getInstance().priceAmount + "$");
                    return true;
                }
            } else {
                //item
                if (!player.getInventory().contains(StructureCompass.getPriceMaterial(), StructureCompass.getInstance().priceAmount)) {
                    player.sendMessage(ChatColor.RED + "You don't have enough to afford this. You need at least " + StructureCompass.getInstance().priceAmount + " " + StructureCompass.getPriceMaterial().name().toLowerCase());
                    return true;
                }
            }
        }

        // Execute


        Location structureLoc = player.getWorld().locateNearestStructure(player.getLocation(), structureType, 200000, useUnexploredOnly);

        if (structureLoc == null) {
            player.sendMessage(ChatColor.RED + "No structures of that type have been found near you.");
            return true;
        }

        //player.sendMessage(structureLoc.toVector().toString());

        //Price remove
        if (!StructureCompass.getInstance().economyState.equals(EconomyState.NONE)) {
            if (StructureCompass.getInstance().economyState.equals(EconomyState.VAULT)) {
                //vault
                VaultEconomy.getEcon().withdrawPlayer(player, StructureCompass.getInstance().priceAmount);
            } else {
                //item
                player.getInventory().removeItem(new ItemStack(StructureCompass.getPriceMaterial(), StructureCompass.getInstance().priceAmount));

            }
        }

        NamespacedKey key = StructureCompass.getKey();
        ItemStack item = new ItemStack(Material.COMPASS);
        CompassMeta meta = (CompassMeta) item.getItemMeta();
        if (useUnexploredOnly)
            meta.getPersistentDataContainer().set(key, PersistentDataType.STRING, "null");
        else
            meta.getPersistentDataContainer().set(key, PersistentDataType.STRING, structureType.getName());
        meta.setLodestoneTracked(false);
        meta.setLodestone(structureLoc);

        if (useUnexploredOnly)
            meta.setDisplayName("Undetected " + structureType.getName().toLowerCase() + " location");
        else {
            meta.setDisplayName("Nearest " + structureType.getName().toLowerCase());
            List<String> lore = new ArrayList<>();
            lore.add(ChatColor.GRAY + "This compass will track");
            lore.add(ChatColor.GRAY + "the nearest structure of");
            lore.add(ChatColor.GRAY + "the specified type. Update");
            lore.add(ChatColor.GRAY + "tracker with right click.");
            meta.setLore(lore);
        }

        item.setItemMeta(meta);

        Location playerLoc = player.getLocation().clone();
        playerLoc.add(-0.5, -0.3, -0.5);
        Item itemEntity = player.getWorld().dropItemNaturally(playerLoc, item);
        itemEntity.setOwner(player.getUniqueId());
        itemEntity.setPickupDelay(-1);
        return true;
    }


    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> output = new ArrayList<>();

        if (args == null || args.length == 0) {
            return output;
        } else if (args.length == 1) {
            if (StructureCompass.isCommandEnabled()) {
                for (StructureType s : StructureCompass.getStructures()) {
                    output.add(s.getName().toLowerCase());
                }
                output.add("help");
            }
            if (sender.hasPermission("structure-compass.reload"))
                output.add("reload");
        } else if (args.length == 2 && !args[0].equalsIgnoreCase("reload") && StructureCompass.isCommandEnabled()) {
            output.add("false");
            output.add("true");
        }

        return output;
    }
}
