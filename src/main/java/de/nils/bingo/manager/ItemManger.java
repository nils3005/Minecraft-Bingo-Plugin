package de.nils.bingo.manager;

import de.nils.bingo.Main;
import de.nils.bingo.scoreboard.BingoScoreboard;
import de.nils.bingo.utils.Team;
import de.nils.bingo.utils.ItemSelector;
import de.nils.bingo.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;


import java.util.*;

public class ItemManger {
    public ArrayList<Material> startItems = new ArrayList<>();
    public HashMap<Team, ArrayList<Material>> teamItems = new HashMap<>();

    /*
    Mithilfe der Methode "getGameItems" der Klasse ItemSelector, werden im ItemManager jedem Team die Items zugeschrieben.
    Schließlich wird eine weitere Methode aufgerufen.
     */
    public void start() {
        startItems = ItemSelector.getGameItems();
        for (Team team : Main.getBingoManger().getTeamManager().getAllTeams()) {
            teamItems.put(team, (ArrayList<Material>) startItems.clone());
        }
        run();
    }

    /*
    Es wird ein neues BukkitRunnable erstellt.
    Für jeden Spieler auf dem Server muss sekündlich geprüft werden, ob ein Item aus den gesuchten Items
    aufgesammelt wurde.
    Falls ein Item den noch offenen Items des Teams entspricht, wird die Methode foundItem aufgerufen.
    Falls ein Team keine Items mehr finden muss, wird die Methode "endGame" im BingoManager aufgerufen,
    was die Runde beendet.
     */
    public void run() {
        new BukkitRunnable() {
            @Override
            public void run() {
                if (Main.getBingoManger().getGameState() == BingoManager.BingoGameState.INGAME) {
                    for (Player player : Bukkit.getOnlinePlayers()) {
                        Team curTeam = Main.getBingoManger().getTeamManager().getTeamByPlayer(player);
                        for (ItemStack slot : player.getInventory().getContents()) {
                            if (slot != null) {
                                ArrayList<Material> itemsByTeam = getItemsFromTeam(curTeam); //zu findende Items
                                if (itemsByTeam.contains(slot.getType())) {
                                    itemsByTeam.remove(slot.getType());
                                    foundItem(curTeam, slot.getType(), player);
                                    if (itemsByTeam.size() == 0) {
                                        Main.getBingoManger().endGame(curTeam);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }.runTaskTimer(Main.getInstance(), 20, 20);
    }

    /*
    Die Methode wird aufgerufen, um zu signalisieren, dass ein offenes Item des Teams aufgesammelt wurde.
    Es wird angegeben, um welches Item es sich handelt, wer es gefunden hat und wie viele Items übrig sind.
    Schließlich wird das Scoreboard aktualisiert.
     */
    public void foundItem(Team team, Material material, Player player) {
        //Wie viele Items übrig sind
        int amountOfItemsLeft = 9 - getItemsFromTeam(team).size();
        //Teamnachricht und für anderes Team eine Broadcastnachricht
        Main.getBingoManger().getTeamManager().getPlayersByTeam(team).forEach(curPlayer -> {
            if (Bukkit.getPlayer(curPlayer.getName()) != null) {
                curPlayer.sendTitle(ChatColor.GOLD + Utils.getItemName(material), " wurde gefunden!");
                curPlayer.playSound(curPlayer.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1, 1);
            }
        });
        Bukkit.broadcastMessage(ChatColor.GOLD + player.getName() + " aus Team " + team.getID().substring(1) + " hat " + Utils.getItemName(material) + " gefunden! " + amountOfItemsLeft + " / 9");

        //Prüfen, ob notwendig
        Main.getBingoManger().getTeamManager().getAllTeams().forEach(tempTeam -> updateScoreboard(tempTeam));
    }

    /*
    Da sich die Rundeninformationen geändert haben, muss auch das Scoreboard jedes Spielers aktualisiert werden.
    Dies wird hier sichergestellt.
     */
    public void updateScoreboard(Team team) {
        ArrayList<Player> players = team.getPlayers();
        for (Player player : players) {
            new BingoScoreboard(player).createInGameScoreboard();
        }
    }

    /*
    Gibt die Platzierung eines Teams an, indem gezählt wird, wie viele Teams mehr Items gefunden haben.
    Notiz: Bei zwei Teams ist es unnötige Arbeit eine Schleife zu nutzen, jedoch muss die Methode
    nicht geändert werden, falls später mehr Teams hinzugefügt werden.
     */
    public int getRankingPosition(Team team) {
        int paramTeamItemAmount = 9 - getItemsFromTeam(team).size();
        int pos = 1;
        for (Team curTeam : Main.getBingoManger().getTeamManager().getAllTeams()) {
            int curItemAmount = 9 - Main.getBingoManger().getItemManger().getItemsFromTeam(curTeam).size();
            if (curItemAmount > paramTeamItemAmount) {
                pos++;
            }
        }
        return pos;
    }

    public ArrayList<Material> getStartItems() {
        return startItems;
    }

    public ArrayList<Material> getItemsFromTeam(Team team) {
        return teamItems.get(team);
    }
}
