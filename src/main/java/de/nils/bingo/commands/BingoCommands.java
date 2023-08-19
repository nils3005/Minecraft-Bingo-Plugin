package de.nils.bingo.commands;

import de.nils.bingo.Main;
import de.nils.bingo.manager.BingoManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class BingoCommands implements CommandExecutor {
    /*
    Verwaltet alle Eingabemöglichkeiten nach "/bingo" (start, help, team und top).
    start: Startet die Runde.
    help: Gibt Auskunft über die Bedienung.
    team: Erwartet einen zweiten Parameter (1 oder 2) und fügt den Spieler diesem Team hinzu.
    top: Teleportiert den Spieler von allen Dimensionen auf den höchsten Block derselben x- und z-Koordinate in
        Oberwelt.
     */
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            sendUsage(sender);
            return false;
        }
        switch (args[0].toLowerCase()) {
            case "start": {
                if (sender.isOp()){
                    if (Main.getBingoManger().getGameState() == BingoManager.BingoGameState.START) {
                        Main.getBingoManger().startGame();
                    } else {
                        sender.sendMessage("Die Runde läuft bereits!");
                    }
                }else{
                    sender.sendMessage(ChatColor.RED + "Du hast keine Berechtigung die Runde zu starten!");
                }
                break;
            }
            case "team": {
                if (Main.getBingoManger().getGameState() == BingoManager.BingoGameState.START) {
                    if (args.length != 2) {
                        sender.sendMessage("Verwendung: /bingo team 1 oder /bingo team 2");
                    } else {
                        Player player = (Player) sender;
                        switch (args[1].toLowerCase()) {
                            case "1":
                                Main.getBingoManger().getTeamManager().addPlayerToTeam(player, "t1");
                                player.sendMessage("Du bist nun in Team 1!");
                                break;
                            case "2":
                                Main.getBingoManger().getTeamManager().addPlayerToTeam(player, "t2");
                                player.sendMessage("Du bist nun in Team 2!");
                                break;
                            default:
                                player.sendMessage(ChatColor.RED.toString() + ChatColor.BOLD + "Das ist kein gültiger Teamwert!");
                        }
                    }
                } else {
                    sender.sendMessage("Die Teamauswahl ist nur während der Startphase möglich!");
                }
                break;
            }
            case "top": {
                if (!(sender instanceof Player)) return false;
                if (Main.getBingoManger().getGameState() == BingoManager.BingoGameState.INGAME) {
                    Player player = (Player) sender;
                    if (player.getWorld().getName().equals("world")) {
                        int x = (int) player.getLocation().getX();
                        int z = (int) player.getLocation().getZ();
                        int y = Bukkit.getWorld("world").getHighestBlockYAt(x, z);
                        player.teleport(new Location(Bukkit.getWorld("world"), x, y + 2, z));
                        player.playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1, 1);
                    }
                } else {
                    sender.sendMessage(ChatColor.RED + "Dieser Command ist nur während des Spiels verfügbar!");
                }
                break;
            }
            case "help": {
                sender.sendMessage("___________________________________________________________________");
                sender.sendMessage(ChatColor.BOLD + "1. Wähle ein Team aus");
                sender.sendMessage(ChatColor.BOLD + "2. Starte die Runde");
                sender.sendMessage(ChatColor.BOLD + "Tipp: Mit /top kommst du zur Oberwelt zurück");
                sender.sendMessage(ChatColor.BOLD + "Tipp: Achte auf die Rechtschreibung. Viel Spaß! ;)");
                sender.sendMessage("___________________________________________________________________");
            }
            default:
                sendUsage(sender);
                break;
        }
        return false;
    }

    public void sendUsage(CommandSender sender) {
        sender.sendMessage(ChatColor.GOLD.toString() + ChatColor.BOLD + "Verwendung: /bingo start, /bingo team 1 " + ChatColor.UNDERLINE + "oder" + ChatColor.RESET + ChatColor.GOLD + ChatColor.BOLD + " 2, /bingo top");
    }
}
