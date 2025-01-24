package net.storm.plugins.examples.looped.states;

import net.runelite.api.ItemID;
import net.runelite.api.Varbits;
import net.runelite.client.eventbus.Subscribe;
import net.storm.api.domain.tiles.ITileObject;
import net.storm.api.events.ExperienceGained;
import net.storm.api.events.InventoryChanged;
import net.storm.api.magic.SpellBook;
import net.storm.plugins.examples.looped.ExampleLoopedConfig;
import net.storm.plugins.examples.looped.SharedContext;
import net.storm.plugins.examples.looped.StateMachine;
import net.storm.plugins.examples.looped.StateMachineInterface;
import net.storm.plugins.examples.looped.enums.EssPouch;
import net.storm.plugins.examples.looped.enums.States;
import net.storm.sdk.entities.Players;
import net.storm.sdk.entities.TileObjects;
import net.storm.sdk.game.Vars;
import net.storm.sdk.items.Equipment;
import net.storm.sdk.items.Inventory;

public class CraftRunes implements StateMachineInterface {

    @Subscribe
    private void onExperienceGained(ExperienceGained gained) {
        System.out.println("Gained: " + gained.getXpGained());
    }

    @Subscribe
    private void onInventoryChanged(InventoryChanged invChange) {
        // System.out.println("INVENTORY CHANGED: " + invChange.getAmount());
    }

    private boolean hasSpace(int amount) {
        return Inventory.getFreeSlots() > amount;
    }

    private void emptyPouches(SharedContext context) {
        EssPouch small = EssPouch.SMALL;
        EssPouch medium = EssPouch.MEDIUM;
        EssPouch large = EssPouch.LARGE;
        EssPouch giant = EssPouch.GIANT;
        EssPouch colossal = EssPouch.COLOSSAL;

        if(context.isUsingSmallPouch() && small.getAmount() > 0 && hasSpace(small.getAmount())) {
            small.empty();
        }

        if (context.isUsingGiantPouch() && giant.getAmount() > 0 && hasSpace(giant.getAmount())) {
            giant.empty();
        }

        if (context.isUsingMediumPouch() && medium.getAmount() > 0 && hasSpace(medium.getAmount())) {
            medium.empty();
        }

        if (context.isUsingLargePouch() && large.getAmount() > 0 && hasSpace(large.getAmount())) {
            large.empty();
        }

        if (context.isUsingColossalPouch() && colossal.getAmount() > 0) {
            colossal.empty();
        }
    }

    private void craftRunes(SharedContext context) {
        ITileObject altar = TileObjects.getNearest(obj -> obj.getId() == context.getConfig().runes().getAltarID());
        if(context.getRuneNeededForComboRunesId() != null) {
            Inventory.getFirst(context.getRuneNeededForComboRunesId()).useOn(altar);
        } else {
            altar.interact("Craft-rune");
        }
    }

    @Override
    public void handleState(StateMachine stateMachine, States state) {
        SharedContext context = stateMachine.getContext();
        ExampleLoopedConfig config = context.getConfig();

        if (config.bringBindingNecklace() && Inventory.contains(ItemID.BINDING_NECKLACE) &&
                !Equipment.contains(ItemID.BINDING_NECKLACE)) {
            Inventory.getFirst(ItemID.BINDING_NECKLACE).interact("Wear");
        }

        if (config.useImbue() && SpellBook.Lunar.MAGIC_IMBUE.canCast() &&
                Vars.getBit(Varbits.MAGIC_IMBUE) == 0) {
            SpellBook.Lunar.MAGIC_IMBUE.cast();
        }

        if(!Players.getLocal().isMoving() && !Players.getLocal().isAnimating()) {
            craftRunes(context);
        }

        context.checkTotalEssencesInInv();
        if (Players.getLocal().isAnimating() && Players.getLocal().getAnimation() == 791 &&
                context.getTotalEssencesInInv() > 0) {
            emptyPouches(context);
            craftRunes(context);
        }

        context.checkTotalEssencesInInv();
        if (context.getTotalEssencesInInv() == 0) {
            stateMachine.setState(new Banking(), false);
        }

    }

    @Override
    public States getStateName() {
        return States.CraftRunes;
    }
}
