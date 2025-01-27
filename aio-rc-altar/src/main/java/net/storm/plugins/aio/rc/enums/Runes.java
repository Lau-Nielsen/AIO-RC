package net.storm.plugins.aio.rc.enums;

import lombok.Getter;
import net.runelite.api.ItemID;
import net.runelite.client.game.ItemManager;
import net.storm.api.Static;

public enum Runes {
    AIR(ItemID.AIR_RUNE),
    MIND(ItemID.MIND_RUNE),
    WATER(ItemID.WATER_RUNE),
    EARTH(ItemID.EARTH_RUNE),
    FIRE(ItemID.FIRE),
    BODY(ItemID.BODY_RUNE),
    COSMIC(ItemID.COSMIC_RUNE),
    CHAOS(ItemID.CHAOS_RUNE),
    NATURE(ItemID.NATURE_RUNE),
    LAW(ItemID.LAW_RUNE),
    SUNFIRE(ItemID.SUNFIRE_RUNE),
    ASTRAL(ItemID.ASTRAL_RUNE),
    BLOOD(ItemID.BLOOD_RUNE),
    SOUL(ItemID.SOUL_RUNE),
    WRATH(ItemID.WRATH_RUNE),
    MIST(ItemID.MIST_RUNE),
    DUST(ItemID.DUST_RUNE),
    MUD(ItemID.MUD_RUNE),
    SMOKE(ItemID.SMOKE_RUNE),
    STEAM(ItemID.STEAM_RUNE),
    LAVA(ItemID.LAVA_RUNE),
    DEATH(ItemID.DEATH_RUNE);

    @Getter
    private int itemID;

    private Runes(int itemID) {
        this.itemID = itemID;
    }

    public double runePrice() {
        ItemManager itemManager1 = Static.getItemManager();

        return itemManager1.getItemPrice(this.itemID);
    }
}
