package de.nils.bingo.scoreboard;

import de.nils.bingo.Main;
import de.nils.bingo.manager.TeamManager;
import de.nils.bingo.utils.Team;
import de.nils.bingo.utils.Utils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.ArrayList;

public class BingoScoreboard extends ScoreboardBuilder {
    /*
    Jeder Spieler hat ein eigenes Scoreboard.
    Die Klasse erbt von ScoreboardBuilder, weshalb Methoden überschrieben werden müssen.
    Außerdem wird die Methode "run" aufgerufen.
     */
    public BingoScoreboard(Player player, String disyplayName) {
        super(player, disyplayName);
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
        Team team = teamManager.getTeamByPlayer(player);
        setScore(ChatColor.DARK_BLUE + "  ", 15);
        if (team != null) {
            String id = teamManager.getIdByTeam(team);
            setScore(ChatColor.RESET + "Team " + ChatColor.YELLOW + "#" + id + ChatColor.RESET, 14);
        } else {
            setScore(ChatColor.GRAY + "Kein Team" + ChatColor.RESET, 14);
        }
        setScore(ChatColor.DARK_PURPLE + "  ", 13);
        setScore(ChatColor.GRAY + "Waiting...", 12);
    }


    /*
    Erzeugt ein Scoreboard für das Rundenende.
     */
    public void createEndScoreboard() {
        if (player != null) {
            setScore(ChatColor.DARK_PURPLE + " ", 5);
            setScore(ChatColor.BOLD.toString() + ChatColor.GOLD + "Danke für's Spielen!", 4);
            setScore(ChatColor.DARK_BLUE.toString(), 3); //Platzhalter
            setScore(ChatColor.GRAY + "Bei Bugs bitte an", 2);
            setScore(ChatColor.GRAY + "einen Admin wenden!", 1);
        }
    }

    /*
    Erzeugt ein Scoreboard während der Runde
     */
    public void createInGameScoreboard() {
        if (player != null) {
            TeamManager teamManager = Main.getBingoManger().getTeamManager();
            ArrayList<ArrayList<Material>> list = new ArrayList<>();
            teamManager.getTeams().forEach(team -> list.add(Main.getBingoManger().getItemManger().getItemsFromTeam(team)));
            int index = 15;
            setScore(ChatColor.DARK_AQUA.toString(), index--); //Platzhalter
            for (int i = 0; i < list.size(); i++) {
                setScore(ChatColor.YELLOW + "#" + (i + 1)  + ChatColor.GRAY + " | " + ChatColor.BLUE + (9 - list.get(i).size()) + " / 9" + ChatColor.RESET, index--);
            }
            setScore(ChatColor.DARK_PURPLE.toString(), index--); //Platzhalter

            ArrayList<Material> allItems = Main.getBingoManger().getItemManger().getStartItems();
            ArrayList<Material> teamItems = Main.getBingoManger().getItemManger().getItemsFromTeam(teamManager.getTeamByPlayer(player));

            for (Material item : allItems) {
                if (teamManager.getSpectators().containsPlayer(player)) { //Falls Spectator
                    setScore(ChatColor.GRAY + Utils.getItemName(item) + ChatColor.RESET, index--);
                    continue;
                }
                if (teamItems.contains(item)) {
                    setScore(ChatColor.RED + Utils.getItemName(item) + ChatColor.RESET, index--); //Falls von Team nicht gefunden
                } else {
                    setScore(ChatColor.GREEN + Utils.getItemName(item) + ChatColor.RESET, index--); //Falls von Team gefunden
                }
            }
        }
    }
}
