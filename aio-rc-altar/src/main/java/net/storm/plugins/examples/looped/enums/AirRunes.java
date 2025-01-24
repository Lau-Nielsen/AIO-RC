package net.storm.plugins.examples.looped.enums;

import lombok.Getter;
import net.runelite.api.ItemID;

public enum AirRunes {
    MIST_RUNE(ItemID.MIST_RUNE, ItemID.WATER_RUNE, ItemID.WATER_TALISMAN),
    SMOKE_RUNE(ItemID.SMOKE_RUNE, ItemID.FIRE_RUNE, ItemID.FIRE_TALISMAN),
    DUST_RUNE(ItemID.DUST_RUNE, ItemID.EARTH_RUNE, ItemID.EARTH_TALISMAN),
    AIR_RUNE(ItemID.AIR_RUNE, null, null);

    @Getter
    private final int runeID;
    @Getter
    private final Integer oppositeRuneId;
    @Getter
    private final Integer oppositeTalismanId;

    AirRunes(int runeID, Integer oppositeRuneId, Integer oppositeTalismanId) {
        this.runeID = runeID;
        this.oppositeRuneId = oppositeRuneId;
        this.oppositeTalismanId = oppositeTalismanId;
    }
}
