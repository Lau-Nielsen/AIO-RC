package net.storm.plugins.examples.looped.enums;

import lombok.Getter;
import net.runelite.api.ItemID;

public enum WaterRunes {
    MUD_RUNE(ItemID.MUD_RUNE, ItemID.EARTH_RUNE, ItemID.EARTH_TALISMAN),
    MIST_RUNE(ItemID.MIST_RUNE, ItemID.AIR_RUNE, ItemID.AIR_TALISMAN),
    STEAM_RUNE(ItemID.STEAM_RUNE, ItemID.FIRE_RUNE, ItemID.FIRE_TALISMAN),
    WATER_RUNES(ItemID.WATER_RUNE, null, null);

    @Getter
    private final int runeID;
    @Getter
    private final Integer oppositeRuneId;
    @Getter
    private final Integer oppositeTalismanId;

    WaterRunes(int runeID, Integer oppositeRuneId, Integer oppositeTalismanId) {
        this.runeID = runeID;
        this.oppositeRuneId = oppositeRuneId;
        this.oppositeTalismanId = oppositeTalismanId;
    }

}
