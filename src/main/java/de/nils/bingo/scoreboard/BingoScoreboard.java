package de.nils.bingo.scoreboard;

import de.nils.bingo.Main;
import de.nils.bingo.manager.BingoManager;
import de.nils.bingo.manager.TeamManager;
import de.nils.bingo.utils.Team;
import de.nils.bingo.utils.Utils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class BingoScoreboard extends ScoreboardBuilder {
    /*
    Jeder Spieler hat ein eigenes Scoreboard.
    Die Klasse erbt von ScoreboardBuilder, weshalb Methoden überschrieben werden müssen.
    Außerdem wird die Methode "run" aufgerufen.
     */
    public BingoScoreboard(Player player) {
        super(player, ChatColor.GREEN.toString() + ChatColor.BOLD + "BINGO");
        run();
    }

    private void run() {
        switch (Main.getBingoManger().getGameState()) {
            case START -> createStartScoreboard();
            case INGAME -> createInGameScoreboard();
            case END -> createEndScoreboard();
        }
    }

    /*
    Erzeugt ein Scoreboard für die Teamauswahlphase.
     */
    @Override
    public void createStartScoreboard() {
        TeamManager teamManager = Main.getBingoManger().getTeamManager();
        if (teamManager.getTeamByPlayer(player) != null) {
            setScore(ChatColor.BOLD + "Team " + Main.getBingoManger().getTeamManager().getIdByTeam(Main.getBingoManger().getTeamManager().getTeamByPlayer(player)) + ChatColor.RESET, 7);
        } else {
            setScore(ChatColor.BOLD + "Team " + "-" + ChatColor.RESET, 7);
        }
        setScore(ChatColor.DARK_GREEN.toString(), 6);
        setScore(ChatColor.BOLD.toString() + ChatColor.UNDERLINE + "Ziel" + ChatColor.RESET, 5);
        setScore(ChatColor.DARK_PURPLE.toString(), 4);
        setScore("Sammelt alle Items", 3);
        setScore("aus dem Scoreboard,", 2);
        setScore("um zu gewinnen", 1);
    }

    /*
    Erzeugt ein Scoreboard für das Rundenende.
     */
    public void createEndScoreboard() {
        if (player != null) {
            setScore(ChatColor.DARK_PURPLE + " ", 5);
            setScore(ChatColor.BOLD.toString() + ChatColor.GOLD + "Danke für's Spielen!", 4);
            setScore(ChatColor.DARK_BLUE.toString(), 3); //Platzhalter
            setScore("Bei Bugs gerne an", 2);
            setScore("einen Admin wenden.", 1);
        }
    }

    /*
    Erzeugt ein Scoreboard während der Runde
     */
    public void createInGameScoreboard() {
        if (player != null) {
            ArrayList<ArrayList<Material>> list = new ArrayList<>();
            Main.getBingoManger().getTeamManager().getTeams().forEach(team -> {
                list.add(Main.getBingoManger().getItemManger().getItemsFromTeam(team));
            });
            int index = 14;
            setScore(ChatColor.BOLD + "Platz " + Main.getBingoManger().getItemManger().getRankingPosition(Main.getBingoManger().getTeamManager().getTeamByPlayer(player)), index--);
            setScore(ChatColor.DARK_AQUA.toString(), index--); //Platzhalter
            for (int i = 0; i < list.size(); i++) {
                setScore(ChatColor.RESET + ChatColor.BOLD.toString() + "#" + i + ChatColor.RESET + " | " + (9 - list.get(i).size()) + " / 9" + ChatColor.RESET, index--);
            }
            setScore(ChatColor.DARK_PURPLE.toString(), index--); //Platzhalter

            ArrayList<Material> allItems = Main.getBingoManger().getItemManger().getStartItems();
            ArrayList<Material> teamItems = Main.getBingoManger().getItemManger().getItemsFromTeam(Main.getBingoManger().getTeamManager().getTeamByPlayer(player));

            for (Material item : allItems) {
                if (teamItems.contains(item)) {
                    setScore(ChatColor.RED + Utils.getItemName(item) + ChatColor.RESET, index--); //Falls von Team nicht gefunden
                } else {
                    setScore(ChatColor.GREEN + Utils.getItemName(item) + ChatColor.RESET, index--); //Falls von Team gefunden
                }
            }
        }
    }
}
