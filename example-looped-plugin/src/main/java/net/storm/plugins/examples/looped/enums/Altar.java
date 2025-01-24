package net.storm.plugins.examples.looped.enums;

import lombok.Getter;
import net.runelite.api.ItemID;
import net.runelite.api.ObjectID;
import net.runelite.api.coords.WorldArea;
import net.storm.sdk.movement.Movement;
import net.storm.sdk.utils.MessageUtils;

import java.awt.*;
import java.util.Arrays;
import java.util.List;

public enum Altar {
    AIR(34813, 34760, new WorldArea(2983,3287,5,5,0), ObjectID.AIR_RIFT, Arrays.asList(ItemID.ELEMENTAL_TIARA, ItemID.ELEMENTAL_TALISMAN, ItemID.AIR_TIARA, ItemID.AIR_TALISMAN)),
    MIND(34814, 34761, new WorldArea(2980,3512,1,1,0), ObjectID.MIND_RIFT, Arrays.asList(ItemID.CATALYTIC_TIARA, ItemID.CATALYTIC_TALISMAN, ItemID.MIND_TIARA, ItemID.MIND_TALISMAN)),
    WATER(34815, 34762, new WorldArea(3177,3163,1,1,0), ObjectID.WATER_RIFT, Arrays.asList(ItemID.ELEMENTAL_TIARA, ItemID.ELEMENTAL_TALISMAN, ItemID.WATER_TIARA, ItemID.WATER_TALISMAN)),
    EARTH(34816, 34763, new WorldArea(3303,3472,1,1,0), ObjectID.EARTH_RIFT, Arrays.asList(ItemID.ELEMENTAL_TIARA, ItemID.ELEMENTAL_TALISMAN, ItemID.EARTH_TIARA, ItemID.EARTH_TALISMAN)),
    FIRE(34817, 34764, new WorldArea(3309,3251,1,1,0), ObjectID.FIRE_RIFT, Arrays.asList(ItemID.ELEMENTAL_TIARA, ItemID.ELEMENTAL_TALISMAN, ItemID.FIRE_TIARA, ItemID.FIRE_TALISMAN)),
    BODY(34818, 34765, new WorldArea(3059,3441,1,1,0), ObjectID.BODY_RIFT, Arrays.asList(ItemID.CATALYTIC_TIARA, ItemID.CATALYTIC_TALISMAN, ItemID.BODY_TIARA, ItemID.BODY_TALISMAN)),
    COSMIC(34819, 34766, new WorldArea(2408,4380,1,1,0), ObjectID.COSMIC_RIFT, Arrays.asList(ItemID.CATALYTIC_TIARA, ItemID.CATALYTIC_TALISMAN, ItemID.COSMIC_TIARA, ItemID.COSMIC_TALISMAN)),
    SUNFIRE(null, 34764, new WorldArea(0,0,0,0,0), null, null),
    CHAOS(34822, 34769, new WorldArea(3060,3588,1,1,0), ObjectID.CHAOS_RIFT, Arrays.asList(ItemID.CATALYTIC_TIARA, ItemID.CATALYTIC_TALISMAN, ItemID.CHAOS_TIARA, ItemID.CHAOS_TALISMAN)),
    ASTRAL(null, 34771, new WorldArea(2155,3864,1,1,0), null, null),
    NATURE(34821, 34768, new WorldArea(2869,3016,1,1,0),ObjectID.NATURE_RIFT, Arrays.asList(ItemID.CATALYTIC_TIARA, ItemID.CATALYTIC_TALISMAN, ItemID.NATURE_TIARA, ItemID.NATURE_TALISMAN)),
    LAW(34820, 34764, new WorldArea(2858,3378,1,1,0), ObjectID.LAW_RIFT, Arrays.asList(ItemID.CATALYTIC_TIARA, ItemID.CATALYTIC_TALISMAN, ItemID.LAW_TIARA, ItemID.LAW_TALISMAN)),
    DEATH(null, 34770, new WorldArea(0,0,0,0,0), ObjectID.DEATH_RIFT, Arrays.asList(ItemID.CATALYTIC_TIARA, ItemID.CATALYTIC_TALISMAN, ItemID.DEATH_TIARA, ItemID.DEATH_TALISMAN)),
    BLOOD(25380, 43479, new WorldArea(3558,9778,1,1,0), ObjectID.BLOOD_RIFT, Arrays.asList(ItemID.CATALYTIC_TIARA, ItemID.CATALYTIC_TALISMAN, ItemID.BLOOD_TIARA, ItemID.BLOOD_TALISMAN)),
    WRATH(34824, 34772, new WorldArea(2448,2823,1,1,0), null, Arrays.asList(ItemID.CATALYTIC_TIARA, ItemID.CATALYTIC_TALISMAN, ItemID.WRATH_TIARA, ItemID.WRATH_TIARA)),;

    @Getter
    private Integer ruinID = 0;
    @Getter
    private Integer altarID = 0;
    @Getter
    private WorldArea worldArea = new WorldArea(0,0,0,0,0);
    @Getter
    private Integer abyssRiftID = 0;
    @Getter
    private List<Integer> validTalismanAndTiaraIds = null;

    Altar(Integer ruinID, Integer altarID, WorldArea worldArea, Integer abyssRiftID, List<Integer> validTalismanAndTiaraIds){
        this.ruinID = ruinID;
        this.altarID = altarID;
        this.worldArea = worldArea;
        this.abyssRiftID = abyssRiftID;
        this.validTalismanAndTiaraIds = validTalismanAndTiaraIds;
    }

    public void walkToAltar() {
        if(this.altarID.equals(Altar.DEATH.getAltarID())) {
            MessageUtils.addMessage("Please do not walk to the death altar, use the abyss instead", Color.red);
        } else {
            Movement.walkTo(this.worldArea);
        }
    }
}
