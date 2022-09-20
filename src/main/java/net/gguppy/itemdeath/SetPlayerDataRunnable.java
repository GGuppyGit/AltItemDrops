package net.gguppy.itemdeath;

import com.sun.org.apache.xpath.internal.operations.Bool;
import org.apache.commons.text.WordUtils;
import org.bukkit.Location;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ExperienceOrb;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.Locale;
import java.util.concurrent.ThreadLocalRandom;

public class SetPlayerDataRunnable extends BukkitRunnable {
    private Player eventPlayer;
    private Location deathLoc;
    private int itemDropPercent;
    private boolean honorVanishCurse;
    private boolean logDeathLoot;

    public SetPlayerDataRunnable(Player eventPlayer, Location deathLoc, int itemDropPercent, boolean honorVanishCurse, boolean logDeathLoot) {
        this.eventPlayer = eventPlayer;
        this.deathLoc = deathLoc;
        this.itemDropPercent = itemDropPercent;
        this.honorVanishCurse = honorVanishCurse;
        this.logDeathLoot = logDeathLoot;
    }
    @Override
    public void run() {


        //Player inventory
        ItemStack[] contents = eventPlayer.getInventory().getContents();
        //A list to be filled with the indexes of items that aren't air.
        ArrayList<Integer> nonAirItemIndexes = new ArrayList<>();
        for(int i = 0;  i < contents.length; i++){
            if(contents[i] != null){
                nonAirItemIndexes.add(i);
                //eventPlayer.sendMessage("Item slot " + contents[i] + " at slot " + i + " is not air." );
            }
        }

        double temp = nonAirItemIndexes.size() * ((double)itemDropPercent / 100);
        //eventPlayer.sendMessage(String.valueOf((nonAirItemIndexes.size() * ((double)itemDropPercent / 100))));
        int dropCount = (int) Math.round(temp);
        if(dropCount == 0) dropCount = 1;
        if(dropCount > nonAirItemIndexes.size()) dropCount = nonAirItemIndexes.size();

        //Message to be sent to player containing what they dropped
        StringBuilder sb = new StringBuilder();
        boolean droppedAnItem = false;
        sb.append(ItemDeath.colorFormat("&8[&c&l!&8] &cYou dropped: &4"));
        for(int i = 0; i < dropCount; i++){
            int randomItemSlot = ThreadLocalRandom.current().nextInt(0, nonAirItemIndexes.size());
            ItemStack itemToDrop = contents[nonAirItemIndexes.get(randomItemSlot)];
            if(deathLoc.getWorld() != null){
                //eventPlayer.sendMessage("Trying to drop " + contents[nonAirItemIndexes.get(randomItemSlot)] + " at slot " + nonAirItemIndexes.get(randomItemSlot) + "." );
                if(!itemToDrop.containsEnchantment(Enchantment.VANISHING_CURSE)){
                    deathLoc.getWorld().dropItem(deathLoc, itemToDrop);
                } else {
                    if(!honorVanishCurse) deathLoc.getWorld().dropItem(deathLoc, itemToDrop);
                }
                contents[nonAirItemIndexes.get(randomItemSlot)] = null;
                String itemName = "";

                //No custom item name
                if(!itemToDrop.getItemMeta().hasDisplayName()){
                    itemName = WordUtils.capitalizeFully(itemToDrop.getType().toString().replaceAll("_", " "));
                    droppedAnItem = true;

                }
                //Custom Item name
                else {
                    itemName = itemToDrop.getItemMeta().getDisplayName();
                    droppedAnItem = true;
                }
                sb.append(itemName).append(" x").append(itemToDrop.getAmount()).append(ItemDeath.colorFormat("&8,&4 "));
                nonAirItemIndexes.remove(randomItemSlot);
            } else {
                ItemDeath.getPlugin().getLogger().severe("Could not find location to drop items. This shouldn't happen.");
            }
        }
        if(droppedAnItem){
            sb = new StringBuilder(sb.toString().substring(0, sb.length() - 4));
            eventPlayer.sendMessage(sb.toString());
            if(logDeathLoot) {
                ItemDeath.getPlugin().getLogger().info(eventPlayer.getDisplayName() +"'s death loot: " + sb.substring(13, sb.length()));
            }
        }
        eventPlayer.getInventory().setContents(contents);



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
