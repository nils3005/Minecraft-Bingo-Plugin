package de.nils.bingo.manager;

import de.nils.bingo.Main;
import de.nils.bingo.scoreboard.BingoScoreboard;
import de.nils.bingo.utils.Team;
import de.nils.bingo.utils.Timer;
import org.bukkit.*;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import javax.swing.plaf.ButtonUI;
import java.util.HashMap;
import java.util.Map;

public class BingoManager {
    private TeamManager teamManager;
    private ItemManger itemManger;
    private static Timer timer;
    private BingoGameState gameState;
    private TablistManger tablistManger;
    private static final int initialCountdownTime = 5;
    private int curCountdown = initialCountdownTime;
    private boolean isCountdownRunning = false;

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
        giveLobbyitems(event.getPlayer());
        Bukkit.getOnlinePlayers().forEach(player -> player.playSound(player.getLocation(), Sound.BLOCK_AMETHYST_BLOCK_BREAK, 1, 1));
    }

    private void joinInGame(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if (teamManager.replaceOldPlayerInstance(player)) {
            event.getPlayer().setGameMode(GameMode.SURVIVAL);
            event.setJoinMessage(event.getPlayer().getName() + ChatColor.GREEN + " ist zurückgekommen!");
            Bukkit.getOnlinePlayers().forEach(temp -> temp.playSound(temp.getLocation(), Sound.BLOCK_AMETHYST_BLOCK_BREAK, 1, 1));
        } else {
            teamManager.replaceOldSpectatorInstace(player);
            event.getPlayer().setGameMode(GameMode.SPECTATOR);
            event.setJoinMessage(ChatColor.GREEN + event.getPlayer().getName() + " schaut nun zu!");
            event.getPlayer().sendMessage(ChatColor.RED + "Da du während der Spielphase beigetreten bist, kannst du erst wieder in der nächsten Runde teilnehmen!");
        }
    }

    private void giveLobbyitems(Player player) {
        player.getInventory().clear();
        ItemStack i = new ItemStack(Material.WHITE_BED);
        ItemMeta im = i.getItemMeta();
        im.setDisplayName(ChatColor.GOLD + "Teamauswahl");
        i.setItemMeta(im);
        player.getInventory().setItem(0, i);
        if (player.isOp()) {
            ItemStack itemStack = new ItemStack(Material.LIME_DYE);
            ItemMeta itemMeta = itemStack.getItemMeta();
            itemMeta.setDisplayName(ChatColor.GOLD + "Start");
            itemStack.setItemMeta(itemMeta);
            player.getInventory().setItem(8, itemStack);
        }
    }

    /*
    Verarbeitet die Interaktion eines Spielers mit einem Item in der Startphase.
     */
    public void onInteractEvent(PlayerInteractEvent event) {
        if (event.getItem().getItemMeta().getDisplayName().equals(ChatColor.GOLD + "Teamauswahl")) {
            createTeamSelectionGUI(event);
        }
        if (event.getPlayer().isOp()
                && (event.getItem().getItemMeta().getDisplayName().equals(ChatColor.GOLD + "Start")
                || event.getItem().getItemMeta().getDisplayName().equals(ChatColor.GOLD + "Stop"))){
            roundControlItemClicked(event);
        }
    }

    /*
    Gibt den Admins ein Item je nach Situation, um die Runde zu starten oder den Startvorgang zu pausieren.
     */
    private void roundControlItemClicked(PlayerInteractEvent event) {
        curCountdown = initialCountdownTime;
        isCountdownRunning ^= true; //Dreht den Wert bei Aufruf: true → false, false → true oder: a = !a;
        Bukkit.getOnlinePlayers().forEach(player -> {
            if (isCountdownRunning && player.isOp()){
                ItemStack itemStack = new ItemStack(Material.GRAY_DYE);
                ItemMeta itemMeta = itemStack.getItemMeta();
                itemMeta.setDisplayName(ChatColor.GOLD + "Stop");
                itemStack.setItemMeta(itemMeta);
                player.getInventory().setItem(8, itemStack);
            }else{
                if(player.isOp()){
                    ItemStack itemStack = new ItemStack(Material.LIME_DYE);
                    ItemMeta itemMeta = itemStack.getItemMeta();
                    itemMeta.setDisplayName(ChatColor.GOLD + "Start");
                    itemStack.setItemMeta(itemMeta);
                    player.getInventory().setItem(8, itemStack);
                }
                player.setLevel(0);
                player.setExp(1f);
                player.sendMessage(ChatColor.GOLD + "Der Rundenstart wurde unterbrochen!");
                player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_BREAK, 1, 1);
            }
        });
    }

    /*
    Erzeugt eine grafische Oberfläche für die Teamauswahl.
     */
    private void createTeamSelectionGUI(PlayerInteractEvent event) {
        HashMap<Integer, ItemStack> map = new HashMap<>();
        for (int i = 0; i < teamManager.getTeams().size(); i++) {
            ItemStack itemStack = new ItemStack(Material.RED_BED);
            ItemMeta itemMeta = itemStack.getItemMeta();
            itemMeta.setDisplayName("Team #" + (i + 1));
            itemStack.setItemMeta(itemMeta);
            map.put(i, itemStack);
        }
        Inventory inventory = Bukkit.createInventory(null, 9, ChatColor.GOLD + "Teamauswahl");
        for (Map.Entry<Integer, ItemStack> entry : map.entrySet()) {
            inventory.setItem(entry.getKey(), entry.getValue());
        }
        event.getPlayer().openInventory(inventory);
    }

    /*
    Entscheidet je nach Auswahl des Spielers, welchem Team dieser zugewiesen wird.
     */
    public void inventoryClickEvent(InventoryClickEvent event) {
        if (event.getView().getTitle().equals(ChatColor.GOLD + "Teamauswahl")) {
            event.setCancelled(true);
            event.getView().close();
            addPlayerToTeam((Player) event.getWhoClicked(),
                    event.getCurrentItem().getItemMeta().getDisplayName().substring(6));
        }
    }

    public void update() {
        new BukkitRunnable() {
            @Override
            public void run() {
                switch (gameState) {
                    case START:
                        if (isCountdownRunning) {
                            Bukkit.getOnlinePlayers().forEach(player -> {
                                player.setLevel(curCountdown);
                                player.setExp(curCountdown / 5f);
                                player.sendMessage(ChatColor.GOLD + "Runde beginnt in " + curCountdown + " Sekunde(n)!");
                                player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BIT, 1, 1);
                            });
                            if (curCountdown == 0) {
                                startGame();
                                return;
                            }
                            curCountdown--;
                        }
                        break;
                    case INGAME:
                        itemManger.run();
                        break;
                }
                timer.run();
            }
        }.runTaskTimer(Main.getInstance(), 20, 20);
    }

    public void updateUI() {
        Bukkit.getOnlinePlayers().forEach(player -> {
            switch (gameState) {
                case START:
                    new BingoScoreboard(player, ChatColor.GREEN.toString() + ChatColor.BOLD + "BINGO");
                    break;
                case INGAME, END:
                    Team team = teamManager.getTeamByPlayer(player);
                    if (team != null) {
                        new BingoScoreboard(player, ChatColor.GREEN.toString()
                                + ChatColor.BOLD + "BINGO" + ChatColor.RESET + ChatColor.GRAY + " | "
                                + ChatColor.RESET + ChatColor.YELLOW + "#" + teamManager.getIdByTeam(teamManager.getTeamByPlayer(player)));
                    } else {
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
        player.playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1, 1);
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
