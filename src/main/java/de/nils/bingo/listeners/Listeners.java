package de.nils.bingo.listeners;

import de.nils.bingo.Main;
import de.nils.bingo.manager.BingoManager;
import org.bukkit.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockDropItemEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.*;

public class Listeners implements Listener {
    /*
    Verwaltet das JoinEvent von einem Spieler.
     */
    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Main.getBingoManger().playerJoinEvent(event);
    }

    /*
    Verhindert, dass Spieler vor Rundenstart Blöcke abbauen können.
     */
    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        if (Main.getBingoManger().getGameState() != BingoManager.BingoGameState.INGAME) event.setCancelled(true);
    }

    /*
    Verwaltet die Interaktion mit einem Item.
     */
    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        if (Main.getBingoManger().getGameState() == BingoManager.BingoGameState.START && event.getItem() != null)
            Main.getBingoManger().onInteractEvent(event);
    }

    /*
    Verwaltet die Interaktion in einem Inventar.
     */
    @EventHandler
    public void onInventoryClickEvent(InventoryClickEvent event) {
        if (Main.getBingoManger().getGameState() == BingoManager.BingoGameState.START && event.getCurrentItem() != null)
            Main.getBingoManger().inventoryClickEvent(event);
    }

    /*
    Spieler können nur in der Spielphase Schaden erleiden.
     */
    @EventHandler
    public void onPlayerDamageOnStart(EntityDamageEvent event) {
        if (Main.getBingoManger().getGameState() != BingoManager.BingoGameState.INGAME) event.setCancelled(true);
    }

    /*
    Verhindert, dass Spieler außerhalb der Spielphase Blöcke droppen können.
     */
    @EventHandler
    public void onDropBlock(PlayerDropItemEvent event) {
        if (Main.getBingoManger().getGameState() != BingoManager.BingoGameState.INGAME) event.setCancelled(true);
    }

    /*
    Verhindert, dass Spieler außerhalb der Spielphase Blöcke setzen können.
     */
    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        if (Main.getBingoManger().getGameState() != BingoManager.BingoGameState.INGAME) event.setCancelled(true);
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
        event.setQuitMessage(ChatColor.GREEN + event.getPlayer().getName() + " hat uns leider verlassen.");
    }

    /*
    Deaktiviert, dass Spieler Hunger erleiden.
     */
    @EventHandler
    public void onFoodChange(FoodLevelChangeEvent event) {
        event.setCancelled(true);
    }
}
