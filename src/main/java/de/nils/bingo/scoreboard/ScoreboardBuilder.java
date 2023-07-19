package de.nils.bingo.scoreboard;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

public abstract class ScoreboardBuilder {
    protected final Scoreboard sb;
    protected final Objective obj;

    protected final Player player;

    /*
    Jeder Spieler hat ein eigenes Scoreboard. Deshalb speichert jedes Scoreboard auch einen Verweis auf diesen.
     */
    public ScoreboardBuilder(Player player, String displayName){
        this.player = player;
        if(player.getScoreboard().equals(Bukkit.getScoreboardManager().getMainScoreboard())){
            player.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
        }
        this.sb = player.getScoreboard();
        if(sb.getObjective("display") != null){
            sb.getObjective("display").unregister();
        }
        obj = sb.registerNewObjective("display", "dummy", displayName);
        obj.setDisplaySlot(DisplaySlot.SIDEBAR);
    }

    /*
    Die Methode passt Scores des entsprechenden Scoreboard an.
     */
    public void setScore(String content, int score){
        obj.getScore(content).setScore(score);
    }

    /*
    Da ScoreBoardBuilder eine abstrakte Klasse ist, werden die Methoden createStartScoreboard, createInGameScoreboard
    und createEndScoreboard erst in der Klasse BingoScoreboard implementiert.
     */
    public abstract void createStartScoreboard();
    public abstract void createInGameScoreboard();
    public abstract void createEndScoreboard();

}
