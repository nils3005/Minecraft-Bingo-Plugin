package de.nils.bingo.manager;

import de.nils.bingo.Main;
import de.nils.bingo.scoreboard.BingoScoreboard;
import de.nils.bingo.utils.Team;
import de.nils.bingo.utils.Timer;
import org.bukkit.*;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.scheduler.BukkitRunnable;

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
        update();
    }

    public void playerJoinEvent(PlayerJoinEvent event) {
        if (Main.getBingoManger().getGameState() == BingoManager.BingoGameState.START) {
            joinOnStart(event);
        } else {
            joinInGame(event);
        }
        tablistManger.setTablist(event.getPlayer());
        updateUI();
    }

    private void joinOnStart(PlayerJoinEvent event) {
        event.getPlayer().setGameMode(GameMode.SURVIVAL);
        event.setJoinMessage(event.getPlayer().getName() + ChatColor.GREEN + " ist nun auch dabei!");
        if (teamManager.getTeamByPlayer(event.getPlayer()) == null) {
            teamManager.addPlayerToTeam(event.getPlayer(), "spectators");
        }
        Bukkit.getOnlinePlayers().forEach(player -> player.playSound(player.getLocation(), Sound.BLOCK_AMETHYST_BLOCK_BREAK, 1, 1));
    }

    private void joinInGame(PlayerJoinEvent event) {
        if (teamManager.getTeamByPlayer(event.getPlayer()) != null
                && !teamManager.getSpectators().containsPlayer(event.getPlayer())) {

            //Eigentlich unnötig, weil man beim Verlassen sein Team nicht verlässt
            //teamManager.addPlayerToTeam(event.getPlayer(), teamManager.getTeamByPlayer(event.getPlayer()).getID());

            event.getPlayer().setGameMode(GameMode.SURVIVAL);
            event.setJoinMessage(event.getPlayer().getName() + ChatColor.GREEN + " ist zurückgekommen!");
            Bukkit.getOnlinePlayers().forEach(player -> player.playSound(player.getLocation(), Sound.BLOCK_AMETHYST_BLOCK_BREAK, 1, 1));

        } else {
            event.getPlayer().setGameMode(GameMode.SPECTATOR);
            event.getPlayer().sendMessage(ChatColor.RED + "Da du während der Spielphase beigetreten bist, kannst du erst wieder in der nächsten Runde teilnehmen!");
        }
    }

    public void update() {
        new BukkitRunnable() {
            @Override
            public void run() {
                switch (gameState) {
                    case START:
                        break;
                    case INGAME:
                        itemManger.run();
                        break;
                    case END:

                        break;
                }
                timer.run();
            }
        }.runTaskTimer(Main.getInstance(), 20, 20);
    }

    public void updateUI(){
        Bukkit.getOnlinePlayers().forEach(player -> {
            switch (gameState){
                case START:
                    new BingoScoreboard(player, ChatColor.GREEN.toString() + ChatColor.BOLD + "BINGO");
                    break;
                case INGAME, END:
                    Team team = teamManager.getTeamByPlayer(player);
                    if (team != null){
                        new BingoScoreboard(player, ChatColor.GREEN.toString()
                                + ChatColor.BOLD + "BINGO" + ChatColor.RESET + ChatColor.GRAY + " | "
                                + ChatColor.RESET + ChatColor.YELLOW + "#" + teamManager.getIdByTeam(teamManager.getTeamByPlayer(player)));
                    }else{
                        new BingoScoreboard(player, ChatColor.GREEN.toString()
                                + ChatColor.BOLD + "BINGO" + ChatColor.RESET + ChatColor.GRAY
                                + " | " + ChatColor.YELLOW + "-");
                    }
                    break;
            }
        });
        tablistManger.setAllPlayerTeams();
    }

    public void startGame() {
        itemManger.start();
        timer.setRunning(true);
        Bukkit.getOnlinePlayers().forEach(player -> {
            player.closeInventory();
            player.getInventory().clear();
            player.setGameMode(GameMode.SURVIVAL);
            player.sendMessage(ChatColor.GOLD.toString() + ChatColor.BOLD + "Die Runde beginnt. Viel Erfolg!");
            player.playSound(player.getLocation(), Sound.BLOCK_BEACON_ACTIVATE, 1, 1);
        });
        for (Player cur : getTeamManager().getSpectators().getPlayers()) {
            cur.setGameMode(GameMode.SPECTATOR);
            cur.sendMessage(ChatColor.RED + "Da du kein Team gewählt hast, bist du nun Spectator!");
        }
        gameState = BingoGameState.INGAME;
        updateUI();
    }

    public void endGame(Team curTeam) {
        timer.setRunning(false);
        Bukkit.getOnlinePlayers().forEach(player -> {
            Location spawn = Bukkit.getServer().getWorld("world").getSpawnLocation();
            player.teleport(spawn);
            player.getInventory().clear();
            player.sendTitle(ChatColor.GOLD + "Team #" + getTeamManager().getIdByTeam(curTeam) + " hat gewonnen!", "by https://github.com/nils3005");
            player.playSound(player.getLocation(), Sound.UI_TOAST_CHALLENGE_COMPLETE, 1, 1);
            player.sendMessage(ChatColor.GREEN.toString() + ChatColor.STRIKETHROUGH + "                                                                              ");
            player.sendMessage(ChatColor.GOLD + "Team #" + getTeamManager().getIdByTeam(curTeam) + " hat gewonnen. Herzlichen Glückwunsch!");
            player.sendMessage(ChatColor.GOLD + "Benötigte Zeit: " + timer.getTime());
            player.sendMessage(ChatColor.GREEN.toString() + ChatColor.STRIKETHROUGH + "                                                                              ");
            createFirework(player);
        });
        gameState = BingoGameState.END;
        updateUI();
    }

    public void addPlayerToTeam(Player player, String id) {
        teamManager.addPlayerToTeam(player, id);
        updateUI();
    }

    /*
    Das innere Enum gibt die aktuelle GameState an.
     */
    public enum BingoGameState {
        START,
        INGAME,
        END;
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

    public BingoGameState getGameState() {
        return gameState;
    }

    public TeamManager getTeamManager() {
        return teamManager;
    }

    public ItemManger getItemManger() {
        return itemManger;
    }
}
