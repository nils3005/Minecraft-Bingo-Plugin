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
        player.setPlayerListHeaderFooter("" + ChatColor.DARK_GRAY + ChatColor.STRIKETHROUGH
                + "                 " + ChatColor.DARK_GRAY + "[ " + ChatColor.GREEN + "Bingo"
                + ChatColor.DARK_GRAY + " ]" + ChatColor.DARK_GRAY + ChatColor.STRIKETHROUGH
                + "                 ", "by https://github.com/nils3005");
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
        Team spectator = sb.getTeam("9spectator");
        if (spectator == null) {
            spectator = sb.registerNewTeam("9spectator");
        }
        spectator.setPrefix(ChatColor.GRAY + "Team -" + ChatColor.RESET + " | ");
        if (Main.getBingoManger().getTeamManager().getTeamByPlayer(player) == Main.getBingoManger().getTeamManager().getSpectators()) {
            spectator.addEntry(player.getName());
        }
        int i = 0;
        for (de.nils.bingo.utils.Team cur : Main.getBingoManger().getTeamManager().getTeams()) {
            Team team = sb.getTeam(i + "t" + (i + 1));
            if (team == null) {
                team = sb.registerNewTeam(i + "t" + (i + 1));
            }
            team.setPrefix("Team #" + (i + 1) + " | ");
            for (Player entry : cur.getPlayers()) {
                team.addEntry(entry.getName());
            }
            i++;
        }
    }
}
