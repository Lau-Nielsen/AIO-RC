package net.storm.plugins.examples.looped.enums;

import lombok.Getter;
import net.runelite.api.ItemID;

public enum EarthRunes {
    LAVA_RUNE(ItemID.LAVA_RUNE, ItemID.FIRE_RUNE, ItemID.FIRE_TALISMAN),
    DUST_RUNE(ItemID.DUST_RUNE, ItemID.AIR_RUNE, ItemID.AIR_TALISMAN),
    MUD_RUNE(ItemID.MUD_RUNE, ItemID.WATER_RUNE, ItemID.WATER_TALISMAN),
    EARTH_RUNE(ItemID.EARTH_RUNE, null, null);


    @Getter
    private final int runeID;
    @Getter
    private final Integer oppositeRuneId;
    @Getter
    private final Integer oppositeTalismanId;

    EarthRunes(int runeID, Integer oppositeRuneId, Integer oppositeTalismanId) {
        this.runeID = runeID;
        this.oppositeRuneId = oppositeRuneId;
        this.oppositeTalismanId = oppositeTalismanId;
    }
}
