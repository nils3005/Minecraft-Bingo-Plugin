package de.nils.bingo.manager;

import de.nils.bingo.Main;
import de.nils.bingo.scoreboard.BingoScoreboard;
import de.nils.bingo.utils.Team;
import org.bukkit.entity.Player;

import java.util.ArrayList;

public class TeamManager {
    private ArrayList<Team> teams = new ArrayList<>();
    Team undefindedTeam;
    Team team1;
    Team team2;

    /*
    Zu Rundenstart werden alle Spieler dem Team "undefindedTeam" angefügt, sodass mindestens ein Team einen Spieler haben muss.
    Danach werden Team 1 und Team 2 erzeugt und wiederum einer anderen Liste angefügt, die alle Teams verwaltet.
     */
    public TeamManager(){
        undefindedTeam = new Team("t0");
        team1 = new Team("t1");
        team2 = new Team("t2");
        teams.add(undefindedTeam);
        teams.add(team2);
        teams.add(team1);
    }

    /*
    Liefert das Team des im Parameter übergeben Spielers.
     */
    public Team getTeamByPlayer(Player player){
        for (int i = 0; i < teams.size(); i++) {
            ArrayList<Player> curList = teams.get(i).getPlayers();
            for (int j = 0; j < curList.size(); j++) {
                if (player.getUniqueId().equals(curList.get(j).getUniqueId())){
                    return teams.get(i);
                }
            }
        }
        return null;
    }

    /*
    Fügt bei Spawn des Spielers sein Team standardmäßig auf undefindedTeam fest.
    Wenn ein Spieler ein Team auswählt, den Server verlässt und wiederkommt, würde dieser in zwei Teams sein.
    Daher wird die Methode deletePlayerFromAllTeams aufgerufen.
     */
    public void initPlayer(Player player) {
        deletePlayerFromAllTeams(player);
        undefindedTeam.addPlayer(player);
    }

    /*
    Fügt einen Spieler zu einem Team hinzu.
     */
    public void addPlayerToTeam(Player player, String teamID){
        deletePlayerFromAllTeams(player);
        for (int i = 0; i < teams.size(); i++) {
            Team curTeam = teams.get(i);
            if(curTeam.getID().equals(teamID)){
                curTeam.addPlayer(player);

                new BingoScoreboard(player).createStartScoreboard();

                Main.getBingoManger().getTablistManger().setAllPlayerTeams();
            }
        }
    }

    /*
    Löscht einen Spieler aus allen Teams
     */
    public void deletePlayerFromAllTeams(Player player){
        for (int i = 0; i < teams.size(); i++) {
            ArrayList<Player> curTeam = teams.get(i).getPlayers();
            if (curTeam.contains(player)) curTeam.remove(player);
        }
    }

    /*
    Liefert eine Liste aller Spieler eines im Parameter übergeben Teams.
     */
    public ArrayList<Player> getPlayersByTeam(Team team){
        for (int i = 0; i < teams.size(); i++) {
            if (teams.get(i).getID().equals(team.getID())){
                return teams.get(i).getPlayers();
            }
        }
        return null;
    }

    public ArrayList<Team> getAllTeams(){
        return teams;
    }

    public Team getTeamByID(String id) {
        for (Team team : teams) {
            if (team.getID() == id) return team;
        }
        return null;
    }
}
