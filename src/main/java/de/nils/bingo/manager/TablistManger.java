package de.nils.bingo.manager;

import de.nils.bingo.Main;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

public class TablistManger {
    /*
    Legt das Design der Tabliste fest.
     */
    public void setTablist(Player player) {
        player.setPlayerListHeaderFooter("" + ChatColor.DARK_GRAY + ChatColor.STRIKETHROUGH + "                 " + ChatColor.DARK_GRAY + "[ " + ChatColor.GREEN + "Bingo" + ChatColor.DARK_GRAY + " ]" + ChatColor.DARK_GRAY + ChatColor.STRIKETHROUGH + "                 ", "by https://github.com/nils3005");
    }

    /*
    Für jeden Spieler wird die Tablist geupdatet. Dies passiert hier durch den Methodenaufruf "setPlayerTeams".
     */
    public void setAllPlayerTeams() {
        Bukkit.getOnlinePlayers().forEach(this::setPlayerTeams);
    }

    /*
    Legt anhand des Teams des Spielers den Prefix in der Tabliste fest.
     */
    public void setPlayerTeams(Player player) {
        Scoreboard sb = player.getScoreboard();
        Team t0 = sb.getTeam("2teamUndef");
        if (t0 == null) {
            t0 = sb.registerNewTeam("2teamUndef");
        }

        Team t1 = sb.getTeam("0team1");
        if (t1 == null) {
            t1 = sb.registerNewTeam("0team1");
        }

        Team t2 = sb.getTeam("1team2");
        if (t2 == null) {
            t2 = sb.registerNewTeam("1team2");
        }

        t0.setPrefix(ChatColor.GRAY + "Team -" + ChatColor.RESET + " | ");

        t1.setPrefix(Main.t1Color + "Team #1" + ChatColor.RESET + " | ");

        t2.setPrefix(Main.t2Color + "Team #2" + ChatColor.RESET + " | ");

        for (Player curr : Bukkit.getOnlinePlayers()) {
            if (Main.getBingoManger().getTeamManager().getTeamByPlayer(curr.getPlayer()).getID().equals("t1")) {
                t1.addEntry(curr.getName());
            } else if (Main.getBingoManger().getTeamManager().getTeamByPlayer(curr.getPlayer()).getID().equals("t2")) {
                t2.addEntry(curr.getName());
            } else {
                t0.addEntry(curr.getName());
            }
        }
    }
}
