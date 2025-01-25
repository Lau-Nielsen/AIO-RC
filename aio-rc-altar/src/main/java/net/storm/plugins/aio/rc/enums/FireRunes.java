package net.storm.plugins.aio.rc.enums;

import lombok.Getter;
import net.runelite.api.ItemID;

@Getter
public enum FireRunes {
    LAVA_RUNE(ItemID.LAVA_RUNE, ItemID.EARTH_RUNE, ItemID.EARTH_TALISMAN, Runes.LAVA),
    STEAM_RUNE(ItemID.STEAM_RUNE, ItemID.WATER_RUNE, ItemID.WATER_TALISMAN, Runes.STEAM),
    SMOKE_RUNE(ItemID.SMOKE_RUNE, ItemID.AIR_RUNE, ItemID.AIR_TALISMAN, Runes.SMOKE),
    FIRE_RUNE(ItemID.FIRE_RUNE, null, null, Runes.FIRE);

    private final int runeID;
    private final Integer oppositeRuneId;
    private final Integer oppositeTalismanId;
    private Runes rune;

    FireRunes(int runeID, Integer oppositeRuneId, Integer oppositeTalismanId, Runes rune) {
        this.runeID = runeID;
        this.oppositeRuneId = oppositeRuneId;
        this.oppositeTalismanId = oppositeTalismanId;
        this.rune = rune;
    }
}
