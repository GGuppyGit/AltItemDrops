package net.gguppy.itemdeath;


import org.apache.commons.text.WordUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.LinkedList;
import java.util.concurrent.ThreadLocalRandom;

public final class  ItemDeath extends JavaPlugin implements Listener {

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(this, this);

    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player eventPlayer = event.getEntity().getPlayer();
        if(!eventPlayer.hasMetadata("NPC")){
            event.setKeepInventory(true);
            event.getDrops().clear();


            Inventory inv = eventPlayer.getInventory();
            //Generate a random number between 1-5 to determine the amount of item slots
            //to drop.
            int randomItemDrop = ThreadLocalRandom.current().nextInt(1, 5 + 1);
            //eventPlayer.sendMessage("You dropped " + randomItemDrop + " item slots.");
            //Generate a number between 5-40% to determine the amount of
            //experience to drop.
            int randomExpDrop = ThreadLocalRandom.current().nextInt(5, 40 + 1);

            ItemStack[] contents = inv.getContents();

            //This will hold the string of the item names for sending to the player later.
            LinkedList<String> itemNames = new LinkedList<>();
            //Similarly, this will hold the number of items of each type.
            LinkedList<Integer> itemCounts = new LinkedList<>();


            //Loops randomItemDrop amount of times, randomItemDrop being the number of items randomly generated
            //to be dropped.
            for(int i = 0; i < randomItemDrop && i >= 0; i++) {

                //This gets the index of a random item in the inventory.
                int randomItem = (int) (Math.random() * contents.length);
                //This checks if that item is air. If not, it'll continue. If so, it'll
                //check another item slot.
                if(contents[randomItem] != null) {
                    //This adds the item to the list of item that will be dropped.
                    event.getDrops().add(contents[randomItem]);
                    //This stores the itemName for later.
                    String itemName = contents[randomItem].getType().toString();
                    //If it has a custom name, use that instead.
                    if(contents[randomItem].getItemMeta().hasDisplayName()){
                        itemName = contents[randomItem].getItemMeta().getDisplayName();
                    }
                    //Below code changes name formats from "IRON_AXE" to "Iron Axe".
                    itemName = itemName.replace("_", " ");
                    itemName = WordUtils.capitalizeFully(itemName);
                    //Stores the itemName inside the list of itemNames.
                    itemNames.add(itemName);
                    //Similar for the itemCount.
                    int itemCount = contents[randomItem].getAmount();
                    itemCounts.add(itemCount);
                    //This removes the item from the inventory (since we are dropping it).
                    contents[randomItem] = null;
                    //Updates the inventory with the new contents.
                    eventPlayer.getInventory().setContents(contents);
                } else {
                    //This runs if the item is air, allowing the loop to continue to
                    //search for a valid item to drop.
                    i--;
                }
            }

            //Sends a message to the player containing all the items they dropped with amounts.
            String messageToSend = "You dropped: ";
            for(int i = 0; i < itemNames.size(); i++) {
                messageToSend += itemNames.get(i) + " x" + itemCounts.get(i) + ", ";
            }
            int lastCommaIndex = messageToSend.lastIndexOf(",");
            messageToSend = messageToSend.substring(0, lastCommaIndex) + messageToSend.substring(lastCommaIndex + 1);
            eventPlayer.sendMessage(messageToSend);

            //Get player current level
            int level = eventPlayer.getLevel();
            //Store that level
            int cachedLevel = level;
            //Calculate the new level. It is equal to level - randomXP dropped.
            level = (int) (level - (level * (randomExpDrop / 100.0)));

            //Now we need to calculate the amount of XP to drop on the ground.
            //It is equal to their old total XP minus the new level's total XP.
            //That number is then multiplied by 0.25 as requested so effectively 75%
            //of xp that would've been dropped is lost permanently.
            float oldXP = levelToExp(cachedLevel);
            float newXP = levelToExp(level);
            event.setNewLevel(level);
            //0.25 is the modifier chosen by person who commissioned the plugin.
            event.setDroppedExp((int) ((oldXP - newXP + 1) * 0.25));
        }

    }

    private float levelToExp(int level) {
        //Different formulas for levels 0-15, 16-30, and 31+;
        float totalXP = 0;
        //Level 0-15
        if(level < 16) {
            totalXP = (level * level) + 6 * level;
        //Level 16-30
        } else if(level < 31) {
            totalXP = (float) (2.5 * (level * level) - 40.5 * level + 360);
        //Level 31+
        } else {
            totalXP = (float) (4.5 * (level * level) - 162.5 * level + 2220);
        }
        return totalXP;
    }
}

