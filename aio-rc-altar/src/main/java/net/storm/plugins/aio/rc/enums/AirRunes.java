package net.storm.plugins.aio.rc.enums;

import lombok.Getter;
import net.runelite.api.ItemID;

@Getter
public enum AirRunes {
    MIST_RUNE(ItemID.MIST_RUNE, ItemID.WATER_RUNE, ItemID.WATER_TALISMAN, Runes.MIST),
    SMOKE_RUNE(ItemID.SMOKE_RUNE, ItemID.FIRE_RUNE, ItemID.FIRE_TALISMAN, Runes.SMOKE),
    DUST_RUNE(ItemID.DUST_RUNE, ItemID.EARTH_RUNE, ItemID.EARTH_TALISMAN, Runes.DUST),
    AIR_RUNE(ItemID.AIR_RUNE, null, null, Runes.AIR);

    private final int runeID;
    private final Integer oppositeRuneId;
    private final Integer oppositeTalismanId;
    private Runes rune;

    AirRunes(int runeID, Integer oppositeRuneId, Integer oppositeTalismanId, Runes rune) {
        this.runeID = runeID;
        this.oppositeRuneId = oppositeRuneId;
        this.oppositeTalismanId = oppositeTalismanId;
        this.rune = rune;
    }
}
