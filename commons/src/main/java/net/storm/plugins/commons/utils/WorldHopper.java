package net.storm.plugins.commons.utils;

import net.runelite.api.WorldType;
import net.storm.sdk.game.Worlds;

import java.util.EnumSet;
import java.util.Set;

public class WorldHopper {
    Set<WorldType> excludedWorldTypes = EnumSet.of(
            WorldType.LEGACY_ONLY, WorldType.HIGH_RISK, WorldType.BETA_WORLD,
            WorldType.DEADMAN, WorldType.BOUNTY, WorldType.FRESH_START_WORLD,
            WorldType.PVP_ARENA, WorldType.PVP, WorldType.SKILL_TOTAL, WorldType.TOURNAMENT_WORLD,
            WorldType.QUEST_SPEEDRUNNING
    );


    public WorldHopper() {}

    public int getCurrentWorldLocation() {
        return Worlds.getCurrent().getLocation();
    }

    public void hopToLocationSpecificMembersWorld(int worldLocation) {
        Worlds.hopTo(Worlds.getRandom(w -> w.getLocation() == worldLocation && w.getTypes().contains(WorldType.MEMBERS) &&
                w.getTypes().stream().noneMatch(this.excludedWorldTypes::contains)));
    }

    public void hopToRandomMembersWorld() {
        Worlds.hopTo(Worlds.getRandom(w -> w.getTypes().contains(WorldType.MEMBERS) &&
                w.getTypes().stream().noneMatch(this.excludedWorldTypes::contains)));
    }

}
