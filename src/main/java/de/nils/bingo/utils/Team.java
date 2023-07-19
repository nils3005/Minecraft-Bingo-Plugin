package de.nils.bingo.utils;

import org.bukkit.entity.Player;

import java.util.ArrayList;

public class Team {
    private String id;
    private ArrayList<Player> members = new ArrayList<Player>();

    /*
    Ein Team kennt eine eindeutige ID und seine Member (Player)
     */
    public Team(String name) {
        this.id = name;
    }

    /*
    Die Methode fügt einen Spieler dem Team hinzu.
     */
    public void addPlayer(Player player) {
        if (members.contains(player)) {
            player.sendMessage("Du bist bereits in diesem Team!");
        } else {
            members.add(player);
        }
    }

    /*
    Die Methode liefert alle Spieler des Teams zurück.
     */
    public ArrayList<Player> getPlayers() {
        return members;
    }

    public String getID() {
        return id;
    }
}
