package de.nils.bingo.manager;

import de.nils.bingo.Main;
import de.nils.bingo.scoreboard.BingoScoreboard;
import de.nils.bingo.utils.Team;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.*;

public class TeamManager {
    //private ArrayList<Team> teams = new ArrayList<>();
    private HashMap<String, Team> teams = new HashMap<>();
    private Team spectators = new Team();

    /*
    Zu Rundenstart werden alle Spieler dem Team "undefindedTeam" angefügt, sodass mindestens ein Team einen Spieler haben muss.
    Danach werden Team 1 und Team 2 erzeugt und wiederum einer anderen Liste angefügt, die alle Teams verwaltet.
     */
    public TeamManager() {
        teams.put("1", new Team());
        teams.put("2", new Team());
    }

    /*
    Liefert das Team des im Parameter übergeben Spielers.
     */
    public Team getTeamByPlayer(Player player) {
        for (Team team : teams.values()) {
            if (team.containsPlayer(player)) return team;
        }
        return null;
    }

    /*
    Fügt einen Spieler zu einem Team hinzu.
     */
    public void addPlayerToTeam(Player player, Team team) {
        if (team == null) player.sendMessage(ChatColor.RED.toString() + ChatColor.BOLD + "Das ist kein gültiger Teamwert!");
        if (spectators.containsPlayer(player)){
            spectators.removePlayer(player);
        }

        Team cur = getTeamByPlayer(player);
        if (cur != null) {
            deletePlayerFromTeam(player, cur);
        }
        teams.get(team).addPlayer(player);

        //new BingoScoreboard(player).createStartScoreboard();
        //Main.getBingoManger().getTablistManger().setAllPlayerTeams();
    }

    /*
    Löscht einen Spieler aus seinem Team.
     */
    public void deletePlayerFromTeam(Player player, Team team) {
        teams.get(team).removePlayer(player);
    }

    /*
    Liefert eine Liste aller Spieler eines im Parameter übergeben Teams.
     */
    public ArrayList<Player> getPlayersByTeam(Team team) {
        teams.get(team).getPlayers();
        return null;
    }

    public String getIdByTeam(Team team){
        for (Map.Entry<String, Team> cur : teams.entrySet()) {
            if (Objects.equals(team, cur)) return cur.getKey();
        }
        return null;
    }

    public Collection<Team> getTeams() {
        return teams.values();
    }

    public Team getTeamById(String id){
        return teams.get(id);
    }

    public Team getSpectators(){
        return spectators;
    }
}
