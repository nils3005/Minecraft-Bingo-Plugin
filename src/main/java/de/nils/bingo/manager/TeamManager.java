package de.nils.bingo.manager;

import de.nils.bingo.Main;
import de.nils.bingo.utils.Team;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class TeamManager {
    private HashMap<String, Team> teams = new HashMap<>();
    private Team spectators = new Team();

    public TeamManager() {
        teams.put("1", new Team());
        teams.put("2", new Team());
        teams.put("3", new Team());
        teams.put("4", new Team());
    }

    /*
    Liefert das Team des im Parameter übergeben Spielers.
     */
    public Team getTeamByPlayer(Player player) {
        for (Team team : teams.values()) {
            if (team.containsPlayer(player)){
                return team;
            }
        }
        return null;
    }

    /*
    Fügt einen Spieler zu einem Team hinzu.
     */
    public void addPlayerToTeam(Player player, String id) {
        if (id.equals("spectators")){
            spectators.addPlayer(player);
            return;
        }
        Team team = teams.get(id);
        if (team == null){
            player.sendMessage(ChatColor.RED + "Das ist kein gültiger Teamwert!");
            return;
        }
        if (spectators.containsPlayer(player)){
            spectators.removePlayer(player);
        }
        Team cur = getTeamByPlayer(player);
        if (cur != null){
            deletePlayerFromTeam(player, getIdByTeam(cur));
        }
        team.addPlayer(player);
        player.sendMessage(ChatColor.GOLD + "Du bist nun in Team #" + getIdByTeam(team));
        Main.getBingoManger().updateUI();
        }

    /*
    Löscht einen Spieler aus seinem Team.
     */
    public void deletePlayerFromTeam(Player player, String id) {
        teams.get(id).removePlayer(player);
    }

    /*
    Liefert eine Liste aller Spieler eines im Parameter übergeben Teams.
     */
    public ArrayList<Player> getPlayersByTeam(Team team) {
        String id = getIdByTeam(team);
        return teams.get(id).getPlayers();
    }

    public String getIdByTeam(Team team){
        String result = "NO_ID_FOUND";
        for (Map.Entry<String, Team> entry : teams.entrySet()){
            if (entry.getValue().equals(team)){
                result = entry.getKey();
            }
        }
        return result;
    }

    public Collection<Team> getTeams() {
        return teams.values();
    }

    public Team getSpectators(){
        return spectators;
    }

    public Team getTeamById(String number) {
        return teams.get(number);
    }

    public boolean replaceOldPlayerInstance(Player player) {
        for (Team team : teams.values()) {
            for (Player member : team.getPlayers()) {
                if (member.getName().equals(player.getName())){
                    team.removePlayer(member);
                    team.addPlayer(player);
                    return true;
                }
            }
        }
        return false;
    }

    public void replaceOldSpectatorInstace(Player player){
        for (Player member : spectators.getPlayers()) {
            if (member.getName().equals(player.getName())){
                spectators.removePlayer(member);
                spectators.addPlayer(player);
            }
        }
    }
}
