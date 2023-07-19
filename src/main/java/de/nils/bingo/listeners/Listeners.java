package de.nils.bingo.listeners;

import de.nils.bingo.Main;
import de.nils.bingo.manager.BingoManager;
import de.nils.bingo.scoreboard.BingoScoreboard;
import org.bukkit.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class Listeners implements Listener {

    /*
    Bereitet das Spiel vor, indem jedem Spieler das Standard-Team zugewiesen wird und der GameMode angepasst wird.
    Falls ein Spieler reconnected wird er seinem ursprünglichen Team zugewiesen.
     */
    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        if (Main.getBingoManger().getGameState() == BingoManager.BingoGameState.START) {
            event.getPlayer().setGameMode(GameMode.SURVIVAL);
            event.setJoinMessage(event.getPlayer().getName() + ChatColor.GREEN + " ist nun auch dabei!");
            Main.getBingoManger().getTeamManager().initPlayer(event.getPlayer());
            Bukkit.getOnlinePlayers().forEach(player -> player.playSound(player.getLocation(), Sound.BLOCK_AMETHYST_BLOCK_BREAK, 1, 1));
        } else {
            if (Main.getBingoManger().getTeamManager().getTeamByPlayer(event.getPlayer()) != null &&!Main.getBingoManger().getTeamManager().getTeamByPlayer(event.getPlayer()).getID().equals("t0")){
                Main.getBingoManger().getTeamManager().addPlayerToTeam(event.getPlayer(), Main.getBingoManger().getTeamManager().getTeamByPlayer(event.getPlayer()).getID());
                event.getPlayer().setGameMode(GameMode.SURVIVAL);
                event.setJoinMessage(event.getPlayer().getName() + ChatColor.GREEN + " ist zurückgekommen!");
                Bukkit.getOnlinePlayers().forEach(player -> player.playSound(player.getLocation(), Sound.BLOCK_AMETHYST_BLOCK_BREAK, 1, 1));
            }else{
                event.getPlayer().setGameMode(GameMode.SPECTATOR);
                event.getPlayer().sendMessage(ChatColor.RED + "Da du während der Spielphase beigetreten bist, kannst du erst in der nächsten Runde teilnehmen :(");
            }
        }
        new BingoScoreboard(event.getPlayer());
        Main.getBingoManger().getTablistManger().setAllPlayerTeams();
        Main.getBingoManger().getTablistManger().setTablist(event.getPlayer());
    }

    /*
    Verhindert, dass Spieler vor Rundenstart Blöcke abbauen können.
     */
    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        if (Main.getBingoManger().getGameState() == BingoManager.BingoGameState.START) {
            event.setCancelled(true);
            event.getPlayer().sendMessage((ChatColor.RED + "Sobald die Runde beginnt kannst du Blöcke abbauen"));
        }
    }

    /*
    Verhindert, dass Spieler vor Rundenstart vom Spawn weglaufen können.
     */
    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        if (Main.getBingoManger().getGameState() == BingoManager.BingoGameState.START) {
            event.setCancelled(true);
            event.getPlayer().sendMessage(ChatColor.RED + "Sobald die Runde beginnt kannst du dich bewegen!");
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
