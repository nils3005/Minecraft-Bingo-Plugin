package de.nils.bingo.listeners;

import de.nils.bingo.Main;
import de.nils.bingo.manager.BingoManager;
import org.bukkit.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRecipeDiscoverEvent;

public class Listeners implements Listener {
    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Main.getBingoManger().playerJoinEvent(event);
    }

    /*
    Verhindert, dass Spieler vor Rundenstart Blöcke abbauen können.
     */
    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        if (Main.getBingoManger().getGameState() != BingoManager.BingoGameState.INGAME) {
            event.setCancelled(true);
        }
    }

    /*
    Verhindert, dass Spieler vor Rundenstart vom Spawn weglaufen können.
     */
    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        if (Main.getBingoManger().getGameState() == BingoManager.BingoGameState.START) {
            event.getPlayer().sendMessage(ChatColor.RED + "Du kannst dich erst bewegen, sobald die Runde startet!");
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void discoverRecipe(PlayerRecipeDiscoverEvent event){
        event.setCancelled(true);
    }

    /*
    Spieler werden nach dem Tod zum Spawn teleportiert (Geänderte Respawnpoints z.B. durch Betten könne dies
    evtl. überschreiben).
     */
    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        event.getEntity().teleport(Bukkit.getWorld("world").getSpawnLocation());
    }

    /*
    Ändert die standardmäßige Nachricht, wenn ein Spieler den Server verlässt.
     */
    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        event.setQuitMessage(ChatColor.WHITE + event.getPlayer().getName() + " hat uns leider verlassen.");
    }

    /*
    Deaktiviert, dass Spieler Hunger erleiden.
     */
    @EventHandler
    public void onFoodChange(FoodLevelChangeEvent event) {
        event.setCancelled(true);
    }
}
