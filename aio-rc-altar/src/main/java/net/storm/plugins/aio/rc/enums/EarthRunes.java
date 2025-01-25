package net.storm.plugins.aio.rc.enums;

import lombok.Getter;
import net.runelite.api.ItemID;

@Getter
public enum EarthRunes {
    LAVA_RUNE(ItemID.LAVA_RUNE, ItemID.FIRE_RUNE, ItemID.FIRE_TALISMAN, Runes.LAVA),
    DUST_RUNE(ItemID.DUST_RUNE, ItemID.AIR_RUNE, ItemID.AIR_TALISMAN, Runes.DUST),
    MUD_RUNE(ItemID.MUD_RUNE, ItemID.WATER_RUNE, ItemID.WATER_TALISMAN, Runes.MUD),
    EARTH_RUNE(ItemID.EARTH_RUNE, null, null, Runes.EARTH);


    private final int runeID;
    private final Integer oppositeRuneId;
    private final Integer oppositeTalismanId;
    private Runes rune;

    EarthRunes(int runeID, Integer oppositeRuneId, Integer oppositeTalismanId, Runes rune) {
        this.runeID = runeID;
        this.oppositeRuneId = oppositeRuneId;
        this.oppositeTalismanId = oppositeTalismanId;
        this.rune = rune;
    }
}
