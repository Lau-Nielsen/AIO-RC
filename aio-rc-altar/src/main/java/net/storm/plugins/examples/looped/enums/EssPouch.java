package net.storm.plugins.examples.looped.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.runelite.api.ItemID;
import net.runelite.api.Skill;
import net.runelite.api.VarPlayer;
import net.runelite.api.Varbits;
import net.runelite.api.annotations.Varbit;
import net.storm.sdk.game.Skills;
import net.storm.sdk.game.Vars;
import net.storm.sdk.items.Bank;
import net.storm.sdk.items.Inventory;

@RequiredArgsConstructor
public enum EssPouch
{
    SMALL(Varbits.ESSENCE_POUCH_SMALL_AMOUNT, null, new int[]{
            // script 3198
    }, 3, ItemID.SMALL_POUCH, null),
    MEDIUM(Varbits.ESSENCE_POUCH_MEDIUM_AMOUNT, Vars.getVarp(VarPlayer.ESSENCE_POUCH_MEDIUM_DEGRADE), new int[]{
            // script 3670
            800, 0,
            400, 3,
    }, 6,  ItemID.MEDIUM_POUCH, ItemID.MEDIUM_POUCH_5511),
    LARGE(Varbits.ESSENCE_POUCH_LARGE_AMOUNT, Vars.getVarp(VarPlayer.ESSENCE_POUCH_LARGE_DEGRADE), new int[]{
            // script 3671
            1000, 0,
            800, 3,
            600, 5,
            400, 7,
    }, 9, ItemID.LARGE_POUCH, ItemID.LARGE_POUCH_5513),
    GIANT(Varbits.ESSENCE_POUCH_GIANT_AMOUNT, Vars.getVarp(VarPlayer.ESSENCE_POUCH_GIANT_DEGRADE), new int[]{
            // script 3695
            1200, 0,
            1000, 3,
            800, 5,
            600, 6,
            400, 7,
            300, 8,
            200, 9,
    }, 12,  ItemID.GIANT_POUCH, ItemID.GIANT_POUCH_5515),
    COLOSSAL(Varbits.ESSENCE_POUCH_COLOSSAL_AMOUNT, null, new int[]{
            // script 4592
            1020, 0,
            1015, 5,
            995, 10,
            950, 15,
            870, 20,
            745, 25,
            565, 30,
            320, 35,
    }, 40,  ItemID.COLOSSAL_POUCH, ItemID.COLOSSAL_POUCH_26786)
            {
                @Override
                int scaleLimit(int limit)
                {
                    // script 4592
                    int rc = Skills.getLevel(Skill.RUNECRAFT);
                    int scaledMax;
                    if (rc >= 85)
                    {
                        scaledMax = 40;
                    }
                    else if (rc >= 75)
                    {
                        scaledMax = 27;
                    }
                    else if (rc >= 50)
                    {
                        scaledMax = 16;
                    }
                    else
                    {
                        scaledMax = 8;
                    }

                    return Math.max(1, (limit * scaledMax) / 40);
                }
            },
    ;

    @Varbit
    private final int amountVarbit;
    private final Integer getDegradation;
    private final int[] degradationLevels;
    private final int maxFill;
    @Getter
    private final int itemID;
    @Getter
    private final Integer brokenItemID;

    int scaleLimit(int limit) {
        return limit;
    }

    public int maxAmount() {
        int deg = getDegradation();
        int limit = this.maxFill;
        for (int i = 0; i < degradationLevels.length; i += 2)
        {
            if (deg >= degradationLevels[i])
            {
                limit = degradationLevels[i + 1];
                break;
            }
        }

        if (limit > 0)
        {
            limit = this.scaleLimit(limit);
        }

        return limit;
    }

    public int getDegradation() {
        if (this.getDegradation == null)
        {
            return 0;
        }

        return this.getDegradation();
    }

    public int getAmount() {
        int[] ints = {};
        return Vars.getBit(amountVarbit);
    }

    public void fill() {
        Bank.Inventory.getFirst(itemID).interact("Fill");
    }

    public void empty() {
        Inventory.getFirst(itemID).interact("Empty");
    }

}
