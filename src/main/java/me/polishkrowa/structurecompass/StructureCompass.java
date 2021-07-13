package me.polishkrowa.structurecompass;

import org.bukkit.NamespacedKey;
import org.bukkit.permissions.Permission;
import org.bukkit.plugin.java.JavaPlugin;

public final class StructureCompass extends JavaPlugin {

    private static StructureCompass instance;

    @Override
    public void onEnable() {
        instance = this;
        this.getCommand("structure-compass").setExecutor(new StructureCompassCommand());
        this.getCommand("structure-compass").setTabCompleter(new StructureCompassCommand());
        this.getServer().getPluginManager().registerEvents(new RightClickEvent(), this);
        this.saveDefaultConfig();

    }

    @Override
    public void onDisable() {
    }

    public static StructureCompass getInstance() {
        return instance;
    }

    public static NamespacedKey getKey() {
        return new NamespacedKey(StructureCompass.getInstance(), "trackingStructure");
    }
}
