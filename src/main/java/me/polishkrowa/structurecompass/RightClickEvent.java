package me.polishkrowa.structurecompass;

import org.bukkit.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.CompassMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

public class RightClickEvent implements Listener {

    @EventHandler
    public static void onRightClick(PlayerInteractEvent event) {
        if (event.getPlayer().getGameMode().equals(GameMode.SPECTATOR))
            return;
        if (!event.getAction().equals(Action.RIGHT_CLICK_AIR) && !event.getAction().equals(Action.RIGHT_CLICK_BLOCK))
            return;
        if (event.getHand() == null || event.getItem() == null || !event.getMaterial().equals(Material.COMPASS))
            return;
        NamespacedKey key = StructureCompass.getKey();
        ItemStack item = event.getItem();
        CompassMeta meta = (CompassMeta) item.getItemMeta();
        PersistentDataContainer container = meta.getPersistentDataContainer();

        if (!container.has(key, PersistentDataType.STRING))
            return;

        String data = container.get(key, PersistentDataType.STRING);

        if (data.equalsIgnoreCase("null")) {
            event.getPlayer().sendMessage(ChatColor.RED + "This compass can only track the initially found structure.");
            return;
        }

        if (!StructureType.getStructureTypes().containsKey(data.toLowerCase())) {
            event.getPlayer().sendMessage(ChatColor.RED + "An error occurred. This is probably because of an update issue.");
            return;
        }
        StructureType structureType = StructureType.getStructureTypes().get(data.toLowerCase());

        Location structureLoc = event.getPlayer().getWorld().locateNearestStructure(event.getPlayer().getLocation(), structureType, 200000, false);
        if (structureLoc == null) {
            event.getPlayer().sendMessage(ChatColor.RED + "No structures of that type have been found near you. Keeping old coordinates.");
            return;
        }

        if (!structureLoc.toVector().toString().equals(meta.getLodestone().toVector().toString())) {
            meta.setLodestone(structureLoc);
            item.setItemMeta(meta);

            event.getPlayer().sendMessage(ChatColor.GREEN + "Position updated !");
        }

    }
}
