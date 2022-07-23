package net.gguppy.itemdeath;


import org.apache.commons.lang3.Range;
import org.apache.commons.text.WordUtils;
import org.bukkit.Location;
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


    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(this, this);

    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Entity eventEntity = event.getEntity();

        if (!eventEntity.hasMetadata("NPC")) {
            Player eventPlayer = event.getEntity().getPlayer();

            event.getDrops().clear();

            ItemStack[] contents = eventPlayer.getInventory().getContents();
            //Generate a random number between 1-5 to determine the amount of item slots
            //to drop.
            int randomItemsToDrop = ThreadLocalRandom.current().nextInt(1, 5 + 1);
            //eventPlayer.sendMessage("You dropped " + randomItemsToDrop + " item slots.");
            //Generate a number between 5-40% to determine the amount of
            //experience to drop.
            int randomExpDrop = ThreadLocalRandom.current().nextInt(5, 40 + 1);


            //Will hold the names of the dropped items and their amounts.
            LinkedHashMap<String, Integer> droppedItems = new LinkedHashMap<>();


            //This will get the indexes of every non-empty item slot, allowing us to randomly choose from them.
            LinkedList<Integer> nonAirItemSlots = new LinkedList<>();
            for (int i = 0; i < contents.length; i++) {
                if (contents[i] != null) {
                    nonAirItemSlots.add(i);
                }
            }

            if (randomItemsToDrop > nonAirItemSlots.size()) {
                randomItemsToDrop = nonAirItemSlots.size();
            }

            int slotIndex = 0;
            Location deathLoc = eventPlayer.getLocation();
            LinkedList droppedItemsList = new LinkedList();
            //Randomly choose "randomItemsToDrop" amount of non-air item slots to drop.
            for (int i = 0; i < randomItemsToDrop; i++) {
                //Choose a random item slot.
                int randomItemSlot = ThreadLocalRandom.current().nextInt(0, nonAirItemSlots.size());
                slotIndex = nonAirItemSlots.get(randomItemSlot);
                //Remove that item from contents

                ItemStack currItem = contents[slotIndex];


                //Also remember there could be duplicate items
                if (droppedItems.get(currItem.getType().name()) == null) {
                    droppedItems.put(currItem.getType().name(), currItem.getAmount());
                } else {
                    int t = currItem.getAmount() + droppedItems.get(currItem.getType().name());
                    droppedItems.put(currItem.getType().name(), t);
                }

                DropItemsRunnable dropItemsRunnable = new DropItemsRunnable(eventPlayer, currItem, deathLoc);
                contents[slotIndex] = null;
                dropItemsRunnable.runTaskLater(this, 5L);
                nonAirItemSlots.remove(randomItemSlot);


            }
            SetPlayerDataRunnable setPlayerDataRunnable = new SetPlayerDataRunnable(eventPlayer, contents, randomExpDrop, deathLoc);
            setPlayerDataRunnable.runTaskLater(this, 20L);

            double newTotalExperience = eventPlayer.getLevel();
            newTotalExperience += eventPlayer.getExp();
            newTotalExperience = levelToExp(newTotalExperience);
            double oldTotalExperience = newTotalExperience;


            //Calculate the new level. It is equal to level - randomXP dropped.
            newTotalExperience = (newTotalExperience - (newTotalExperience * (randomExpDrop  / 100.0)));
            Entity entity = deathLoc.getWorld().spawnEntity(deathLoc, org.bukkit.entity.EntityType.EXPERIENCE_ORB);
            event.setDroppedExp(0);
            ExperienceOrb expOrb = (ExperienceOrb) entity;
            expOrb.setExperience((int) ((oldTotalExperience - newTotalExperience) * 0.25));
            double orbNum = (oldTotalExperience - newTotalExperience) * 0.25;
            expOrb.setExperience((int) orbNum);

            eventPlayer.setLevel(0);
            eventPlayer.setExp(0);
            eventPlayer.giveExp((int)newTotalExperience);
            event.setKeepLevel(true);
            
            String mes = "You dropped:\n";
            Set<String> keys = droppedItems.keySet();

            for (String key : keys) {
                //Below code changes name formats from "IRON_AXE" to "Iron Axe".
                //itemName = itemName.replace("_", " ");
//              //itemName = WordUtils.capitalizeFully(itemName);
                String temp = key.replace("_", " ");
                temp = WordUtils.capitalizeFully(temp);
                mes += temp + " x" + droppedItems.get(key) + ", ";
            }
            eventPlayer.sendMessage(mes);


        }
    }

    private Plugin getPlugin() {
        return this;
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
}





