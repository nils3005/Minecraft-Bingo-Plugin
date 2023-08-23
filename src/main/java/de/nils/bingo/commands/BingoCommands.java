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
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            sendUsage(sender);
            return false;
        }
        switch (args[0].toLowerCase()) {
            case "top": {
                if (!(sender instanceof Player)) return false;
                if (Main.getBingoManger().getGameState() == BingoManager.BingoGameState.INGAME) {
                    Player player = (Player) sender;
                    if (player.getWorld().getName().equals("world")) {
                        player.sendMessage(ChatColor.GOLD + "Teleport zur Oberwelt...");
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
                sender.sendMessage(ChatColor.STRIKETHROUGH + "                                                                              ");
                sender.sendMessage("1. Wähle ein Team aus.");
                sender.sendMessage("2. Warte bis ein Admin die Runde startet.");
                sender.sendMessage("Tipp: Mit /bingo top kommst du zur Oberwelt zurück.");
                sender.sendMessage("Tipp: Achte auf die Rechtschreibung bei Commands. Viel Spaß!");
                sender.sendMessage(ChatColor.STRIKETHROUGH + "                                                                              ");
                break;
            }
            default:
                sendUsage(sender);
                break;
        }
        return false;
    }

    public void sendUsage(CommandSender sender) {
        sender.sendMessage(ChatColor.GOLD + "Befehle: /bingo help, /bingo top");
    }
}
