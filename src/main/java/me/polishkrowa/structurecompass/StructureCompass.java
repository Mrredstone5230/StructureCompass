package me.polishkrowa.structurecompass;

import org.bukkit.plugin.java.JavaPlugin;

public final class StructureCompass extends JavaPlugin {

    @Override
    public void onEnable() {
        // Plugin startup logic
        this.getCommand("structure-compass").setExecutor(new StructureCompassCommand());
        this.getCommand("structure-compass").setTabCompleter(new StructureCompassCommand());
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
