package me.polishkrowa.structurecompass;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.StructureType;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class StructureCompassCommand implements CommandExecutor, TabCompleter {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "This command is only available for players.");
            return true;
        }

        Player player = (Player) sender;

        if (!player.hasPermission("structure-compass.use")) {
            player.sendMessage(ChatColor.RED + "You don't have access to this command !");
            return true;
        }

        if (args == null || args.length == 0) {
            player.sendMessage(ChatColor.RED + "Usage: /structure-compass <structure-type> [only-not-found]");
            return true;
        }

        String structureName = args[0];
        if (!StructureType.getStructureTypes().containsKey(structureName.toLowerCase())) {
            player.sendMessage(ChatColor.RED + "Invalid structure type. Please look at https://minecraft.fandom.com/wiki/Commands/locate#Arguments in the Java Edition column for valid structure types. Usage: /structure-compass <structure-type> [only-not-found]");
            return true;
        }
        StructureType structureType = StructureType.getStructureTypes().get(structureName.toLowerCase());


        boolean useUnexplored;
        if (args.length == 1) {
            useUnexplored = false;
        } else {
            if (!args[1].equalsIgnoreCase("true") && !args[1].equalsIgnoreCase("false")) {
                player.sendMessage(ChatColor.RED + "Incorrect true/false value for second argument. Usage: /structure-compass <structure-type> [only-not-found]");
                return true;
            }
            useUnexplored = Boolean.parseBoolean(args[1]);
        }


        Location structureLoc = player.getWorld().locateNearestStructure(player.getLocation(), structureType, 200000, useUnexplored);

        if (structureLoc == null) {
            player.sendMessage(ChatColor.RED + "No structures of that type have been found near you.");
            return true;
        }

        player.sendMessage(structureLoc.toVector().toString());
        //TODO Give a compass with loc hardcoded

        //? ///// if true OR scan always if false.
        return true;
    }


    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> output = new ArrayList<>();

        if (args == null || args.length == 0) {
            return output;
        } else if (args.length == 1) {
            for (String s : StructureType.getStructureTypes().keySet()) {
                output.add(s);
            }
        } else if (args.length == 2) {
            output.add("false");
            output.add("true");
        }

        return output;
    }
}
