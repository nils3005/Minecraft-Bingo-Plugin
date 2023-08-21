package de.nils.bingo.utils;

import org.bukkit.entity.Player;

import java.util.ArrayList;

public class Team {
    private ArrayList<Player> members = new ArrayList<Player>();

    public Team() {
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

    public boolean containsPlayer(Player player) {
        return members.contains(player);
    }

    public void removePlayer(Player player){
        if (members.contains(player)){
            members.remove(player);
        }
    }
}
