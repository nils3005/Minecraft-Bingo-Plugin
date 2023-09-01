package de.nils.bingo;

import de.nils.bingo.commands.BingoCommands;
import de.nils.bingo.listeners.Listeners;
import de.nils.bingo.manager.BingoManager;
import net.minecraft.world.level.GameRules;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameRule;
import org.bukkit.World;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;

public final class Main extends JavaPlugin {
    private static Main instance;
    private static BingoManager bingoManager;

    /*
    Lädt für jeden Neustart des Servers eine neue Welt mit zufälligem Seed.
    Passt die GameRule an, dass PvP deaktiviert ist, dass Inventar nach dem Tod behalten wird und keine Advancements im Chat angezeigt werden.
     */
    @Override
    public void onLoad() {
        instance = this;
        loadNewWorld();
        Bukkit.getWorld("world").setGameRule(GameRule.ANNOUNCE_ADVANCEMENTS, false);
        Bukkit.getWorld("world_nether").setGameRule(GameRule.ANNOUNCE_ADVANCEMENTS, false);
        Bukkit.getWorld("world_end").setGameRule(GameRule.ANNOUNCE_ADVANCEMENTS, false);
    }

    /*
    Lädt für jeden Neustart des Servers eine neue Welt mit zufälligem Seed.
     */
    private void loadNewWorld(){
        try {
            File[] worlds = {new File(Bukkit.getWorldContainer(), "world"),
                    new File(Bukkit.getWorldContainer(), "world_nether"),
                    new File(Bukkit.getWorldContainer(), "world_the_end")};
            for (int i = 0; i < worlds.length; i++) {
                Files.walk(worlds[i].toPath()).sorted(Comparator.reverseOrder())
                        .map(Path::toFile)
                        .forEach(File::delete);
                worlds[i].mkdirs();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /*
    Erzeugt ein Objekt von BingoManager, der die Runde verwaltet.
    Fügt "bingo" einen CommandExecutor hinzu.
    Die Listener werden aufgenommen.
     */
    @Override
    public void onEnable() {
        // Plugin startup logic
        bingoManager = new BingoManager();
        getCommand("bingo").setExecutor(new BingoCommands());
        Bukkit.getPluginManager().registerEvents(new Listeners(), this);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public static BingoManager getBingoManger() {
        return bingoManager;
    }

    public static Main getInstance() {
        return instance;
    }

}
