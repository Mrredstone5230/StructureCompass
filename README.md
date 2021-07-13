# StructureCompass
This spigot plugin gives a compass to track a certain structure type! The compass can have a "give" price and the plugin also supports [Vault](https://www.spigotmc.org/resources/vault.34315/).

Permissions:
 - structure-compass.use: Required to use the plugin command. True by default.
 - structure-compass.reload: Allows usage of /structure-compass reload.
 
### Command: /structure-compass <structure-type> [only-not-found]
This command will give a compass that points to the chunk of the specified structure. If "only-not-found" set to true, the compass will point to a structure that hasn't been detected by the plugin before.

Aliases: /sc, /s-c

### Default config.yml:
```yml
### ENABLED STRUCTURES
#This is the list of enabled structures. See https://minecraft.fandom.com/wiki/Commands/locate#Arguments in the Java Edition column for valid structure types.
structures:
  - bastion_remnant
  - desert_pyramid
  - endcity
  - fortress
  - igloo
  - jungle_pyramid
  - mansion
  - mineshaft
  - monument
  - nether_fossil
  - ocean_ruin
  - pillager_outpost
  - ruined_portal
  - shipwreck
  - swamp_hut
  - village


### COMPASS PRICE

# Put true if you want to use vault
use-vault: false

# This is the amount of items if vault is disabled. Otherwise, this is the amount of money required with Vault.
# Put 0 to disable the price feature.
price-amount: 0

# The item type of the price. This is ignored if Vault is used.
# List of correct entries: https://hub.spigotmc.org/javadocs/bukkit/org/bukkit/Material.html (Use the "Enum Constant" column)
price-item-type: DIAMOND
```
