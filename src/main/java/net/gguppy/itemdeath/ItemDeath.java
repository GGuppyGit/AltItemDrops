package net.gguppy.itemdeath;


import org.apache.commons.lang3.Range;
import org.apache.commons.text.WordUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ExperienceOrb;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.checkerframework.common.value.qual.IntRange;


import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public final class  ItemDeath extends JavaPlugin implements Listener {
    public FileConfiguration config = getConfig();
    public static int itemDropPercent = 0;
    public static boolean honorVanishingCurse = false;
    public static boolean logDeathLoot = false;


    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(this, this);
        setupConfigs();
        itemDropPercent = Integer.parseInt(config.getString("percent-items-to-drop"));
        getLogger().info("Players will drop " + itemDropPercent + "% of their items on death.");
        honorVanishingCurse = Boolean.parseBoolean(config.getString("honor-vanish-curse"));
        getLogger().info("Honor curse of vanishing: " + honorVanishingCurse);
        logDeathLoot = Boolean.parseBoolean(config.getString("log-death-loot"));
        getLogger().info("Log loot on death: " + logDeathLoot);


    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
//        Entity eventEntity = event.getEntity();
//
//        if (!eventEntity.hasMetadata("NPC")) {
            Player eventPlayer = event.getEntity().getPlayer();
//
//            event.getDrops().clear();
//
//            ItemStack[] contents = eventPlayer.getInventory().getContents();
//            //Generate a random number between 1-5 to determine the amount of item slots
//            //to drop.
//            int randomItemsToDrop = ThreadLocalRandom.current().nextInt(1, 5 + 1);
//            //eventPlayer.sendMessage("You dropped " + randomItemsToDrop + " item slots.");
//            //Generate a number between 5-40% to determine the amount of
//            //experience to drop.
//            int randomExpDrop = ThreadLocalRandom.current().nextInt(5, 40 + 1);
//
//
//            //Will hold the names of the dropped items and their amounts.
//            LinkedHashMap<String, Integer> droppedItems = new LinkedHashMap<>();
//
//
//            //This will get the indexes of every non-empty item slot, allowing us to randomly choose from them.
//            LinkedList<Integer> nonAirItemSlots = new LinkedList<>();
//            for (int i = 0; i < contents.length; i++) {
//                if (contents[i] != null) {
//                    nonAirItemSlots.add(i);
//                }
//            }
//
//            if (randomItemsToDrop > nonAirItemSlots.size()) {
//                randomItemsToDrop = nonAirItemSlots.size();
//            }
//
//            int slotIndex = 0;
            Location deathLoc = eventPlayer.getLocation();
//            LinkedList droppedItemsList = new LinkedList();
//            //Randomly choose "randomItemsToDrop" amount of non-air item slots to drop.
//            for (int i = 0; i < randomItemsToDrop; i++) {
//                //Choose a random item slot.
//                int randomItemSlot = ThreadLocalRandom.current().nextInt(0, nonAirItemSlots.size());
//                slotIndex = nonAirItemSlots.get(randomItemSlot);
//                //Remove that item from contents
//
//                ItemStack currItem = contents[slotIndex];
//
//
//                //Also remember there could be duplicate items
//                if (droppedItems.get(currItem.getType().name()) == null) {
//                    droppedItems.put(currItem.getType().name(), currItem.getAmount());
//                } else {
//                    int t = currItem.getAmount() + droppedItems.get(currItem.getType().name());
//                    droppedItems.put(currItem.getType().name(), t);
//                }
//
//                contents[slotIndex] = null;
//
//                nonAirItemSlots.remove(randomItemSlot);
//
//
//            }
            SetPlayerDataRunnable setPlayerDataRunnable = new SetPlayerDataRunnable(eventPlayer, deathLoc, itemDropPercent, honorVanishingCurse, logDeathLoot);
            setPlayerDataRunnable.runTaskLater(this, 20L);
//
//
//
//            String mes = "You dropped:\n";
//            Set<String> keys = droppedItems.keySet();
//
//            for (String key : keys) {
//                //Below code changes name formats from "IRON_AXE" to "Iron Axe".
//                String temp = key.replace("_", " ");
//                temp = WordUtils.capitalizeFully(temp);
//                mes += temp + " x" + droppedItems.get(key) + ", ";
//            }
//            eventPlayer.sendMessage(mes);
//
//
//        }
    }

    static Plugin getPlugin() {
        return Bukkit.getServer().getPluginManager().getPlugin("ItemDeath");
    }

    private JavaPlugin getJavaPlugin() {
        return this;
    }


    private double levelToExp(double level) {
        //Different formulas for levels 0-16, 17-31, and 32+;
        double totalXP = 0;
        //Level 0-16
        if (level < 17) {
            totalXP = (level * level) + (6 * level);
            //Level 17-31
        } else if (level < 32) {
            totalXP = (2.5 * (level * level)) - (40.5 * level) + 360;
            //Level 32+
        } else {
            totalXP = (4.5 * (level * level)) - (162.5 * level) + 2220;
        }
        return totalXP;
    }

    private void setupConfigs(){
        config.addDefault("percent-items-to-drop", "25");
        config.addDefault("log-death-loot", "true");
        config.addDefault("honor-vanish-curse", "true");
        config.options().copyDefaults(true);
        saveConfig();

    }

    public static String colorFormat(String msg){
        return ChatColor.translateAlternateColorCodes('&',msg);
    }
}





