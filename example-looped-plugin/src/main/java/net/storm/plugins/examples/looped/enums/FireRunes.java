package net.storm.plugins.examples.looped.enums;

import lombok.Getter;
import net.runelite.api.ItemID;

public enum FireRunes {
    LAVA_RUNE(ItemID.LAVA_RUNE, ItemID.EARTH_RUNE, ItemID.EARTH_TALISMAN),
    STEAM_RUNE(ItemID.STEAM_RUNE, ItemID.WATER_RUNE, ItemID.WATER_TALISMAN),
    SMOKE_RUNE(ItemID.SMOKE_RUNE, ItemID.AIR_RUNE, ItemID.AIR_TALISMAN),
    FIRE_RUNE(ItemID.FIRE_RUNE, null, null);

    @Getter
    private final int runeID;
    @Getter
    private final Integer oppositeRuneId;
    @Getter
    private final Integer oppositeTalismanId;

    FireRunes(int runeID, Integer oppositeRuneId, Integer oppositeTalismanId) {
        this.runeID = runeID;
        this.oppositeRuneId = oppositeRuneId;
        this.oppositeTalismanId = oppositeTalismanId;
    }
}
