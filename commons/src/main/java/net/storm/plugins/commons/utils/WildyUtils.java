package net.storm.plugins.commons.utils;

import net.runelite.api.widgets.ComponentID;
import net.storm.api.domain.actors.IPlayer;
import net.storm.sdk.entities.Players;
import net.storm.sdk.game.Game;
import net.storm.sdk.widgets.Widgets;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WildyUtils {
    WorldHopper worldHopper = new WorldHopper();


    public WildyUtils() {};

    public boolean canAttackMe(IPlayer player) {
        String input = Widgets.get(ComponentID.PVP_WILDERNESS_LEVEL).getText();
        Pattern pattern = Pattern.compile("(\\d+)-(\\d+)");
        Matcher matcher = pattern.matcher(input);

        if (matcher.find()) {
            return Integer.parseInt(matcher.group(1)) <= player.getCombatLevel() && Integer.parseInt(matcher.group(2)) >= player.getCombatLevel();
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

    public void hopCheckAndHop(boolean hopOnAttackAblePlayerNearby) {
        if (!hopOnAttackAblePlayerNearby) {
            return;
        }

        if(Game.isInWilderness()) {
            List<IPlayer> players = Players.getAll(p -> p.getId() != Players.getLocal().getId());
            int radius = wildyLevel() < 20 ? 10 : 20;

            for (IPlayer player : players) {
                if (canAttackMe(player) && player.distanceTo(Players.getLocal().getWorldArea().toWorldPoint()) < radius) {
                    worldHopper.hopToLocationSpecificMembersWorld(worldHopper.getCurrentWorldLocation());
                }
            }
        }
    }
}
