package net.storm.plugins.commons.utils;

import net.runelite.api.ItemID;
import net.runelite.client.game.ItemVariationMapping;
import net.storm.api.domain.items.IItem;

import java.util.Collection;
import java.util.function.Predicate;

public class TpJewelry {

    public TpJewelry() {}

    public int[] getGloryIds() {
        return new int[]{
                ItemID.AMULET_OF_GLORY1,
                ItemID.AMULET_OF_GLORY2,
                ItemID.AMULET_OF_GLORY3,
                ItemID.AMULET_OF_GLORY4,
                ItemID.AMULET_OF_GLORY5,
                ItemID.AMULET_OF_GLORY6
        };
    }

    public int[] getDuelingRingIds() {
        return ItemVariationMapping.getVariations(ItemVariationMapping.map(ItemID.RING_OF_DUELING8))
                .stream()
                .mapToInt(Integer::intValue)
                .toArray();
    }

    public Predicate<IItem> getDuelingRingPredicate() {
        return i -> i.getName() != null && i.getName().contains("Ring of dueling");
    }

    public Predicate<IItem> getChargedGloryPredicate() {
        return i -> i.getName() != null && i.getName().contains("Amulet of glory(");
    }
}
