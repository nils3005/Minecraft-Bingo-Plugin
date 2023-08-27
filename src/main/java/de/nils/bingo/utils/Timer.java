package de.nils.bingo.utils;

import de.nils.bingo.Main;
import de.nils.bingo.manager.BingoManager;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class Timer {
    private int sec, min, hr;
    private boolean running;

    /*
    Die Klasse Timer gibt die Rundenzeit in der Actionbar an.
    Während der Startphase gibt sie Informationen über die Bedienung an.
     */
    public Timer() {
        sec = 0;
        min = 0;
        hr = 0;
        running = false;
    }

    /*
    Die Methode aktualisiert die Zeit, falls der Timer läuft.
     */
    public void run() {
        sendActionBar();
        if (!running) {
            return;
        }
        if (sec == 59) {
            sec = 0;
            if (min == 59) {
                hr++;
                min = 0;
            } else {
                min++;
            }
        } else {
            sec++;
        }
    }

    /*
    Die Methode ist für die Ausgabe des Timers in der Actionbar aller Spieler verantwortlich.
     */
    public void sendActionBar() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (running) {
                player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(ChatColor.GOLD.toString() + ChatColor.BOLD + getTime()));
            } else {
                if (Main.getBingoManger().getGameState() == BingoManager.BingoGameState.START) {
                    player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(ChatColor.GREEN + "Willkommen! /bingo help für Hilfe"));
                } else if (Main.getBingoManger().getGameState() == BingoManager.BingoGameState.END) {
                    player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(ChatColor.GREEN + "Server neustarten für eine neue Runde"));
                }
            }
        }
    }

    public void setRunning(boolean running) {
        this.running = running;
    }

    public String getTime() {
        return hr + "h " + min + "m " + sec + "s";
    }
}
