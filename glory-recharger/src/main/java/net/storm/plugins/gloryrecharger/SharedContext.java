package net.storm.plugins.gloryrecharger;

import com.google.inject.Singleton;
import lombok.Getter;
import lombok.Setter;
import net.runelite.api.WorldType;
import net.runelite.api.widgets.ComponentID;
import net.storm.api.domain.actors.IPlayer;
import net.storm.api.entities.IPlayers;
import net.storm.plugins.gloryrecharger.enums.RunningState;
import net.storm.sdk.entities.Players;
import net.storm.sdk.game.Game;
import net.storm.sdk.game.Worlds;
import net.storm.sdk.widgets.Widgets;

import java.text.DecimalFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Setter
@Getter
@Singleton
public class SharedContext {
    private Integer gloriesCharged = 0; // done
    private Integer eternalGlories = 0; // done
    private String currentState; // done;
    private RunningState currentRunningState = RunningState.AWAITING_START; // done;

    Set<WorldType> excludedWorldTypes = EnumSet.of(
            WorldType.LEGACY_ONLY, WorldType.HIGH_RISK, WorldType.BETA_WORLD,
            WorldType.DEADMAN, WorldType.BOUNTY, WorldType.FRESH_START_WORLD,
            WorldType.PVP_ARENA, WorldType.PVP, WorldType.SKILL_TOTAL, WorldType.TOURNAMENT_WORLD
    );

    private long startTime;
    private long totalElapsedTime = 0;
    private boolean isTimeTracking = false;


    @Getter
    private GloryRechargerConfig config;

    public SharedContext (GloryRechargerConfig config){ this.config = config;}

    public void start() {
        if (!isTimeTracking) {
            this.startTime = System.currentTimeMillis();
            this.isTimeTracking = true;
        }
    }

    public void pause() {
        if (isTimeTracking) {
            this.totalElapsedTime += System.currentTimeMillis() - startTime;
            this.isTimeTracking = false;
        }
    }

    public long getElapsedTimeSeconds() {
        if (isTimeTracking) {
            return (totalElapsedTime + (System.currentTimeMillis() - startTime)) / 1000;
        } else {
            return totalElapsedTime / 1000;
        }
    }

    public String formatTime() {
        long totalTime = this.getElapsedTimeSeconds();

        long hours = totalTime / 3600;
        long minutes = (totalTime % 3600) / 60;
        long seconds = (totalTime % 60);

        // Format as HH:MM:SS.mmm with leading zeros
        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }

    public String calculateRatePerHour(long amount) {
        double elapsedTimeHours = (double) getElapsedTimeSeconds() / 3600;

        if (elapsedTimeHours == 0) {
            return "0k";
        }

        double rate = (amount / elapsedTimeHours) / 1000;

        DecimalFormat df = new DecimalFormat("#.00k");

        return df.format(rate);
    }

    public boolean canAttackMe(IPlayer player) {
        String input = Widgets.get(ComponentID.PVP_WILDERNESS_LEVEL).getText();
        Pattern pattern = Pattern.compile("(\\d+)-(\\d+)");
        Matcher matcher = pattern.matcher(input);

        if (matcher.find()) {
            System.out.println("??");
            return Integer.parseInt(matcher.group(1)) <= player.getCombatLevel() || Integer.parseInt(matcher.group(2)) >= player.getCombatLevel();
        }

        return false;
    }

    public int wildyLevel() {
        String input = Widgets.get(ComponentID.PVP_WILDERNESS_LEVEL).getText();

        Pattern pattern = Pattern.compile("Level: (\\d+)");
        Matcher matcher = pattern.matcher(input);

        if (matcher.find()) {
            return Integer.parseInt(matcher.group(1));
        }
        return 0;
    }

    public void hopCheck() {
        if (!this.config.hopOnAttackablePlayer()) {
            return;
        }

        if(Game.isInWilderness() && config.hopOnAttackablePlayer()) {
            List<IPlayer> players = Players.getAll(p -> p.getId() != Players.getLocal().getId());

            for (IPlayer player : players) {
                if (canAttackMe(player) && player.distanceTo(Players.getLocal().getWorldArea().toWorldPoint()) < 20) {
                    Worlds.hopTo(Worlds.getRandom(w -> w.getLocation() == Worlds.getCurrent().getLocation() && w.getTypes().contains(WorldType.MEMBERS) &&
                            w.getTypes().stream().noneMatch(this.excludedWorldTypes::contains)));
                }
            }
        }
    }
}