package net.storm.plugins.daeyalt.states;

import net.runelite.api.ItemID;
import net.runelite.api.Quest;
import net.storm.plugins.daeyalt.DaeyaltMinerConfig;
import net.storm.plugins.daeyalt.SharedContext;
import net.storm.plugins.daeyalt.StateMachine;
import net.storm.plugins.daeyalt.StateMachineInterface;
import net.storm.plugins.daeyalt.enums.States;
import net.storm.sdk.items.Equipment;
import net.storm.sdk.items.Inventory;
import net.storm.sdk.quests.Quests;
import net.storm.sdk.utils.MessageUtils;

import java.awt.*;
import java.util.Arrays;
import java.util.List;

public class Setup implements StateMachineInterface {
    SharedContext context;
    DaeyaltMinerConfig config;

    public Setup(SharedContext context) {
        this.context = context;
        this.config = context.getConfig();
    }

    private boolean hasPickaxe() {
        List<Integer> pickaxes = Arrays.asList(ItemID.BRONZE_PICKAXE, ItemID.IRON_PICKAXE, ItemID.STEEL_PICKAXE, ItemID.BLACK_PICKAXE,
                ItemID.MITHRIL_PICKAXE, ItemID.ADAMANT_PICKAXE, ItemID.RUNE_PICKAXE, ItemID.DRAGON_PICKAXE, ItemID.DRAGON_PICKAXE_OR,
                ItemID.DRAGON_PICKAXE_OR_25376, ItemID.DRAGON_PICKAXE_OR_30351, ItemID.CRYSTAL_PICKAXE, ItemID.GILDED_PICKAXE);

        return Inventory.getAll().stream().anyMatch(item -> pickaxes.contains(item.getId())) ||
                Equipment.getAll().stream().anyMatch(item -> pickaxes.contains(item.getId()));
    }

    @Override
    public void handleState(StateMachine stateMachine) {
        context.initTickManipMap();

        if (!hasPickaxe()) {
            MessageUtils.addMessage("You should probably equip or put a pickaxe in your inventory...", Color.RED);
        }

        if (config.tickManip() && !Inventory.contains(ItemID.GUAM_LEAF, ItemID.MARRENTILL, ItemID.HARRALANDER) ||
                !Inventory.containsAll(ItemID.SWAMP_TAR, ItemID.PESTLE_AND_MORTAR)) {
            MessageUtils.addMessage("You don't have the items to tick manip, bring a guam, harralander, marrentil + swamp tar + peste & mortar...", Color.RED);
        }

        if(!Quests.isFinished(Quest.SINS_OF_THE_FATHER)) {
            MessageUtils.addMessage("You need sins of the farther compelted before you can mine daeyalt...", Color.RED);
        }

        stateMachine.setState(new WalkToMine(context), false);
    }

    @Override
    public States getStateName() {
        return States.Setup;
    }
}