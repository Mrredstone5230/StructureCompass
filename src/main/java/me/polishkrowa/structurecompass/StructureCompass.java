package me.polishkrowa.structurecompass;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.StructureType;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

public final class StructureCompass extends JavaPlugin {

    private static StructureCompass instance;
    private static List<StructureType> structures = new ArrayList<>();
    private boolean enabled;

    private VaultEconomy economy = new VaultEconomy();
    public EconomyState economyState;
    public Material priceMaterial;
    public int priceAmount;

    @Override
    public void onEnable() {
        instance = this;
        this.getCommand("structure-compass").setExecutor(new StructureCompassCommand());
        this.getCommand("structure-compass").setTabCompleter(new StructureCompassCommand());
        this.getServer().getPluginManager().registerEvents(new RightClickEvent(), this);
        loadConfig();
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

    public static List<StructureType> getStructures() {
        return structures;
    }

    public static boolean loadConfig() {
        instance.saveDefaultConfig();
        instance.reloadConfig();
        boolean disable = false;

        // Structures check
        structures.clear();
        for (String s : instance.getConfig().getStringList("structures")) {
            if (!StructureType.getStructureTypes().containsKey(s.toLowerCase())) {
                instance.getServer().getLogger().log(Level.SEVERE, "[StructureCompass] Error while loading the config file: Structure " + s + " is invalid. Disabling plugin.");
                disable = true;
            } else {
                structures.add(StructureType.getStructureTypes().get(s.toLowerCase()));
            }
        }
        if (checkDisablePlugin(disable))
            return false;

        //Vault check
        if (instance.getConfig().getBoolean("use-vault")) {
            if (!instance.economy.setupEconomy()) {
                instance.getServer().getLogger().log(Level.SEVERE, "[StructureCompass] Error while loading the config file: Vault is supposed to be used but it's not found on the server.");
                disable = true;
            } else {
                instance.economyState = EconomyState.VAULT;
            }
        } else
            instance.economyState = EconomyState.ITEM;
        if (checkDisablePlugin(disable))
            return false;

        //price check
        int amount = instance.getConfig().getInt("price-amount");
        if (amount <= 0) {
            instance.economyState = EconomyState.NONE;
        } else if (amount > 64 && instance.economyState.equals(EconomyState.ITEM)) {
            instance.getServer().getLogger().log(Level.SEVERE, "[StructureCompass] Error while loading the config file: The price amount is bigger than 64 !");
            disable = true;
        } else if (instance.economyState.equals(EconomyState.ITEM)) {
            Material mat = Material.getMaterial(instance.getConfig().getString("price-item-type").toUpperCase());
            if (mat.equals(null)) {
                instance.getServer().getLogger().log(Level.SEVERE, "[StructureCompass] Error while loading the config file: The price item type isn't valid.");
                disable = true;
            } else {
                instance.priceMaterial = mat;
            }
        } else if (instance.economyState.equals(EconomyState.VAULT)) {
            instance.priceMaterial = null;
        }
        instance.priceAmount = amount;
        if (checkDisablePlugin(disable))
            return false;

        instance.enabled = true;
        return true;
    }

    private static boolean checkDisablePlugin(boolean disable) {
        if (!disable)
            return false;
        instance.enabled = false;
        instance.getServer().getLogger().log(Level.SEVERE, "[StructureCompass] Please do /structure-compass reload when the issue is fixed.");
        return true;
    }

    public static boolean isCommandEnabled() {
        return instance.enabled;
    }

    public static Material getPriceMaterial() {
        if (instance.priceMaterial != null)
            return instance.priceMaterial;
        else
            return null;
    }

}
