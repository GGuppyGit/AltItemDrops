package net.gguppy.itemdeath;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ExperienceOrb;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Locale;

public class SetPlayerDataRunnable extends BukkitRunnable {
    private Player eventPlayer;
    private ItemStack[] contents;
    private int randomExpDrop;
    private Location deathLoc;

    public SetPlayerDataRunnable(Player eventPlayer, ItemStack[] contents, int randomExpDrop, Location deathLoc) {
        this.randomExpDrop = randomExpDrop;
        this.eventPlayer = eventPlayer;
        this.contents = contents;
        this.deathLoc = deathLoc;
    }
    @Override
    public void run() {
        eventPlayer.getInventory().setContents(contents);



    }




}
