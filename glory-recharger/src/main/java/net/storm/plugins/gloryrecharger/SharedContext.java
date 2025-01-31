package net.storm.plugins.gloryrecharger;

import com.google.inject.Singleton;
import lombok.Getter;
import lombok.Setter;
import net.runelite.api.ItemID;
import net.runelite.api.Prayer;
import net.runelite.api.WorldType;
import net.runelite.api.coords.WorldArea;
import net.runelite.api.widgets.ComponentID;
import net.storm.api.domain.actors.IPlayer;
import net.storm.api.domain.tiles.ITileObject;
import net.storm.plugins.gloryrecharger.enums.RunningState;
import net.storm.sdk.entities.Players;
import net.storm.sdk.entities.TileObjects;
import net.storm.sdk.game.Game;
import net.storm.sdk.game.Worlds;
import net.storm.sdk.input.Keyboard;
import net.storm.sdk.items.Bank;
import net.storm.sdk.movement.Movement;
import net.storm.sdk.widgets.Prayers;
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
    private Integer lawRunes = 0; // done
    private Integer bloodRunes = 0; // done
    private Integer annakarlTabs = 0; // done
    private Integer wildySwords = 0; // done
    private Integer staminas = 0; // done
    private Integer glories = 0; // done
    private String currentState; // done;
    private RunningState currentRunningState = RunningState.AWAITING_START; // done;

    Set<WorldType> excludedWorldTypes = EnumSet.of(
            WorldType.LEGACY_ONLY, WorldType.HIGH_RISK, WorldType.BETA_WORLD,
            WorldType.DEADMAN, WorldType.BOUNTY, WorldType.FRESH_START_WORLD,
            WorldType.PVP_ARENA, WorldType.PVP, WorldType.SKILL_TOTAL, WorldType.TOURNAMENT_WORLD,
            WorldType.QUEST_SPEEDRUNNING
    );

    private long startTime;
    private long totalElapsedTime = 0;
    private boolean isTimeTracking = false;

    @Getter
    private GloryRechargerConfig config;

    private int setDestinationWidgetID = 12255235;
    private int animatingObeliskID = 14825;


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
            return "0";
        }

        double rate = (amount / elapsedTimeHours) ;

        DecimalFormat df = new DecimalFormat("#");

        return df.format(rate);
    }

    public boolean canAttackMe(IPlayer player) {
        String input = Widgets.get(ComponentID.PVP_WILDERNESS_LEVEL).getText();
        Pattern pattern = Pattern.compile("(\\d+)-(\\d+)");
        Matcher matcher = pattern.matcher(input);

        if (matcher.find()) {
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
            int radius = wildyLevel() < 20 ? 10 : 20;

            for (IPlayer player : players) {
                if (canAttackMe(player) && player.distanceTo(Players.getLocal().getWorldArea().toWorldPoint()) < radius) {
                    Worlds.hopTo(Worlds.getRandom(w -> w.getLocation() == Worlds.getCurrent().getLocation() && w.getTypes().contains(WorldType.MEMBERS) &&
                            w.getTypes().stream().noneMatch(this.excludedWorldTypes::contains)));
                }
            }
        }
    }

    public WorldArea calculateMiddleOfObelisk() {
        int sumX = 0;
        int sumY = 0;
        List<ITileObject> obelisk = TileObjects.getAll(o -> o.getName() != null && (o.getId() == animatingObeliskID || o.getName().equals("Obelisk")));

        for (ITileObject o : obelisk) {
            sumX += o.getWorldX();
            sumY += o.getWorldY();
        }

        return new WorldArea(sumX / 4, sumY / 4, 1, 1, 0);
    }

    public void handleProtectItem() {
        if(Game.isInWilderness()) {
            List<IPlayer> players = Players.getAll(p -> p.getId() != Players.getLocal().getId());
            IPlayer me = Players.getLocal();

            for (IPlayer player : players) {
                if (player.isInteracting() && player.getInteracting().getId() == me.getId() && Prayers.canUse(Prayer.PROTECT_ITEM)) {
                    if(Prayers.isEnabled(Prayer.PROTECT_ITEM)) {
                        Prayers.toggle(Prayer.PROTECT_ITEM);
                    }
                }
            }
        } else if (Prayers.isEnabled(Prayer.PROTECT_ITEM)) {
            Prayers.toggle(Prayer.PROTECT_ITEM);
        }
    }

    public void teleportToDestination() {
        IPlayer localPlayer = Players.getLocal();
        ITileObject obelisk = TileObjects.getFirstSurrounding(localPlayer.getWorldLocation(), 10,o -> o != null && o.hasAction("Activate"));
        if (TileObjects.getNearest(animatingObeliskID) == null) {
            obelisk.interact("Teleport to Destination");
        } else {
            Movement.walkTo(calculateMiddleOfObelisk());
        }
    }

    public void setDestinationToFerox() {
        IPlayer localPlayer = Players.getLocal();
        ITileObject obelisk = TileObjects.getFirstSurrounding(localPlayer.getWorldLocation(), 10,o -> o != null && o.hasAction("Activate"));

        // honestly just some random widget ID from the Set destination widget on obelisks
        if (Widgets.isVisible(setDestinationWidgetID)) {
            Keyboard.type(1);
        } else {
            obelisk.interact("Set Destination");
        }
    }

    public void setDestinationToRogues() {
        IPlayer localPlayer = Players.getLocal();
        ITileObject obelisk = TileObjects.getFirstSurrounding(localPlayer.getWorldLocation(), 10,o -> o != null && o.hasAction("Activate"));

        // honestly just some random widget ID from the Set destination widget on obelisks
        if (Widgets.isVisible(setDestinationWidgetID)) {
            Keyboard.type(6);
        } else {
            obelisk.interact("Set Destination");
        }
    }

    public void checkStock() {
        this.glories = Bank.getCount(true, ItemID.AMULET_OF_GLORY);
        this.lawRunes = Bank.getCount(true, ItemID.LAW_RUNE);
        this.bloodRunes = Bank.getCount(true, ItemID.BLOOD_RUNE);
        this.annakarlTabs = Bank.getCount(true, ItemID.ANNAKARL_TELEPORT);
        this.wildySwords = Bank.getCount(true, ItemID.WILDERNESS_SWORD_4);
        this.staminas = Bank.getCount(true, ItemID.STAMINA_POTION1);
    }
}