package net.storm.plugins.aio.rc.enums;

import lombok.Getter;
import net.runelite.api.ItemID;

@Getter
public enum WaterRunes {
    MUD_RUNE(ItemID.MUD_RUNE, ItemID.EARTH_RUNE, ItemID.EARTH_TALISMAN, Runes.MUD),
    MIST_RUNE(ItemID.MIST_RUNE, ItemID.AIR_RUNE, ItemID.AIR_TALISMAN, Runes.MIST),
    STEAM_RUNE(ItemID.STEAM_RUNE, ItemID.FIRE_RUNE, ItemID.FIRE_TALISMAN, Runes.STEAM),
    WATER_RUNES(ItemID.WATER_RUNE, null, null, Runes.WATER);

    private final int runeID;
    private final Integer oppositeRuneId;
    private final Integer oppositeTalismanId;
    private Runes rune;

    WaterRunes(int runeID, Integer oppositeRuneId, Integer oppositeTalismanId, Runes rune) {
        this.runeID = runeID;
        this.oppositeRuneId = oppositeRuneId;
        this.oppositeTalismanId = oppositeTalismanId;
        this.rune = rune;
    }

}
