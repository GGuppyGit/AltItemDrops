package net.gguppy.itemdeath;

import org.bukkit.Location;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

public class DropItemsRunnable extends BukkitRunnable {
    private Player eventPlayer;
    private ItemStack toDrop;
    private int slotIndex;
    private Location deathLoc;

    public DropItemsRunnable(Player eventPlayer, ItemStack toDrop, Location deathLoc) {
        this.eventPlayer = eventPlayer;
        this.toDrop = toDrop;
        this.deathLoc = deathLoc;
    }
    @Override
    public void run() {
        eventPlayer.getWorld().dropItem(deathLoc, toDrop);
    }
}
