package de.nils.bingo.scoreboard;

import de.nils.bingo.Main;
import de.nils.bingo.utils.Team;
import de.nils.bingo.utils.Utils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

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

    /*
    Ruft je nach GameState eine Methode auf, die das Scoreboard dementsprechend anpasst.
    (BukkitRunnable)
     */
    private void run() {
        new BukkitRunnable() {
            @Override
            public void run() {
                switch (Main.getBingoManger().getGameState()) {
                    case START -> createStartScoreboard();
                    case INGAME -> createInGameScoreboard();
                    case END -> createEndScoreboard();
                }
            }
        }.runTaskTimer(Main.getInstance(), 20, 20);
    }

    /*
    Erzeugt ein Scoreboard für die Teamauswahlphase.
     */
    @Override
    public void createStartScoreboard() {
        switch (Main.getBingoManger().getTeamManager().getTeamByPlayer(player).getID()) {
            case "t0":
                setScore(ChatColor.BOLD + "Team /", 7);
                break;
            case "t1":
                setScore(ChatColor.BOLD + "Team " + Main.t1Color + "#1", 7);
                break;
            case "t2":
                setScore(ChatColor.BOLD + "Team " + Main.t2Color + "#2", 7);
                break;
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
            setScore(ChatColor.DARK_PURPLE + " ", 6);
            setScore(ChatColor.BOLD.toString() + ChatColor.GOLD + "Danke für's Spielen!", 5);
            setScore(ChatColor.DARK_BLUE.toString(), 4); //Platzhalter
            setScore("Bei Bugs gerne an", 3);
            setScore("einen Admin wenden.", 2);
            setScore("<3", 1);
        }
    }

    /*
    Erzeugt ein Scoreboard während der Runde
     */
    public void createInGameScoreboard() {
        if (player != null) {
            Team playerTeam = Main.getBingoManger().getTeamManager().getTeamByPlayer(player);
            ArrayList<Material> itemsT1 = Main.getBingoManger().getItemManger().getItemsFromTeam(Main.getBingoManger().getTeamManager().getTeamByID("t1"));
            ArrayList<Material> itemsT2 = Main.getBingoManger().getItemManger().getItemsFromTeam(Main.getBingoManger().getTeamManager().getTeamByID("t2"));
            if (playerTeam != null && itemsT1 != null && itemsT2 != null) {

                //Platzierung
                setScore(ChatColor.BOLD + "Platz " + Main.getBingoManger().getItemManger().getRankingPosition(playerTeam), 14);

                //Platzhalter
                setScore(ChatColor.DARK_AQUA.toString(), 13);

                setScore(ChatColor.RESET + ChatColor.BOLD.toString() + Main.t1Color + "#1" + ChatColor.RESET + " | " + (9 - itemsT1.size()) + " / 9" + ChatColor.RESET, 12);
                setScore(ChatColor.RESET + ChatColor.BOLD.toString() + Main.t2Color + "#2" + ChatColor.RESET + " | " + (9 - itemsT2.size()) + " / 9" + ChatColor.RESET, 11);

                //Platzhalter
                setScore(ChatColor.DARK_PURPLE.toString(), 10);

                ArrayList<Material> allItems = Main.getBingoManger().getItemManger().getStartItems();
                ArrayList<Material> teamItems = Main.getBingoManger().getItemManger().getItemsFromTeam(playerTeam);

                for (int i = (allItems.size() - 1); i >= 0; i--) {
                    if (teamItems.contains(allItems.get(i))) {
                        setScore(ChatColor.RED + Utils.getItemName(allItems.get(i)) + ChatColor.RESET, (i + 1)); //Falls von Team gefunden
                    } else {
                        setScore(ChatColor.GREEN + Utils.getItemName(allItems.get(i)) + ChatColor.RESET, (i + 1));
                    }
                }
            }
        }
    }
}
