package de.nils.bingo.manager;

import de.nils.bingo.scoreboard.BingoScoreboard;
import de.nils.bingo.utils.Team;
import de.nils.bingo.utils.Timer;
import org.bukkit.*;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.FireworkMeta;

public class BingoManager {
    private TeamManager teamManager;
    private ItemManger itemManger;
    private static Timer timer;
    private BingoGameState gameState;
    private TablistManger tablistManger;

    /*
    Legt die GameState fest (zu Beginn: START).
    Erzeugt alle benötigten Ressourcen.
     */
    public BingoManager() {
        gameState = BingoGameState.START;
        teamManager = new TeamManager();
        itemManger = new ItemManger();
        timer = new Timer();
        tablistManger = new TablistManger();
    }

    /*
    Die Methode wird aufgerufen, wenn das Spiel startet. Die GameState wird am Ende auf "INGAME" gesetzt.
    Die Items, die gefunden werden müssen werden durch einen Methodenaufruf am Objekt itemManager erzeugt.
    Für jeden Spieler wird das Scoreboard aktualisiert, um die Platzierung und die zu findenden Items anzuzeigen.
    Der Timer zur Erfassung der Rundenzeit wird gestartet.
    Alle Spieler, die kein Team ausgewählt haben werden in den GameMode "Spectator" versetzt.
     */
    public void startGame() {
        itemManger.start();

        Bukkit.getOnlinePlayers().forEach(player -> {
            player.closeInventory();
            player.getInventory().clear();
            player.setGameMode(GameMode.SURVIVAL);
            player.sendMessage(ChatColor.GOLD.toString() + ChatColor.BOLD + "Die Bingo-Runde beginnt! Viel Erfolg!");
            player.playSound(player.getLocation(), Sound.BLOCK_BEACON_ACTIVATE, 1, 1);
            new BingoScoreboard(player).createInGameScoreboard();
        });
        timer.setRunning(true);

        for (Player cur : getTeamManager().getTeamByID("t0").getPlayers()) {
            if (cur != null) {
                cur.setGameMode(GameMode.SPECTATOR);
                cur.sendMessage("Da du kein Team gewählt hast, bist du nun Spectator!");
            }
        }
        gameState = BingoGameState.INGAME;
    }

    /*
    Die Methode wird aufgerufen, wenn das Spiel endet. Die GameState wird am Ende auf "END" gesetzt.
    Der Timer wird gestoppt.
    Alle Spieler werden zum Spawn teleportiert.
    Das Scoreboard aller Spieler wird aktualisiert.
    Das Siegerteam wird bekannt gegeben und Feuerwerk wird gezündet :)
     */
    public void endGame(Team curTeam) {
        timer.setRunning(false);
        Bukkit.getOnlinePlayers().forEach(player -> {
            new BingoScoreboard(player).createEndScoreboard();

            Location spawn = Bukkit.getServer().getWorld("world").getSpawnLocation();
            player.teleport(spawn);
            player.getInventory().clear();
            player.sendTitle(ChatColor.GOLD + "Team #" + curTeam.getID().substring(1) + " hat Bingo gewonnen!", "by https://github.com/nils3005");
            player.playSound(player.getLocation(), Sound.UI_TOAST_CHALLENGE_COMPLETE, 1, 1);
            player.sendMessage(ChatColor.GREEN + "____________________________________________________");
            player.sendMessage(ChatColor.BOLD + "Team #" + curTeam.getID().substring(1) + " hat Bingo gewonnen! Herzlichen Glückwunsch!");
            player.sendMessage(ChatColor.BOLD + "Benötigte Zeit: " + timer.getTime());
            player.sendMessage(ChatColor.GREEN + "____________________________________________________");
            createFirework(player);
        });
        gameState = BingoGameState.END;
    }

    /*
    Erzeugt das Feuerwerk für die Methode "endGame".
     */
    private void createFirework(Player player) {
        Firework fw = player.getWorld().spawn(player.getLocation(), Firework.class);
        FireworkMeta fireworkMeta = fw.getFireworkMeta();
        fireworkMeta.addEffect(FireworkEffect.builder().withColor(Color.GREEN).withColor(Color.BLUE).with(FireworkEffect.Type.BALL_LARGE).withFlicker().build());
        fireworkMeta.setPower(1);
        fw.setFireworkMeta(fireworkMeta);
    }

    /*
    Die innere "Enum-Klasse" gibt die aktuelle GameState an.
     */
    public enum BingoGameState {
        START,
        INGAME,
        END,
    }

    public BingoGameState getGameState() {
        return gameState;
    }

    public TeamManager getTeamManager() {
        return teamManager;
    }

    public ItemManger getItemManger() {
        return itemManger;
    }

    public TablistManger getTablistManger() {
        return tablistManger;
    }
}
