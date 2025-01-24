package net.storm.plugins.aio.rc.states;

import net.runelite.api.ItemID;
import net.runelite.api.Varbits;
import net.runelite.client.eventbus.Subscribe;
import net.storm.api.domain.actors.IPlayer;
import net.storm.api.domain.tiles.ITileObject;
import net.storm.api.events.AnimationChanged;
import net.storm.api.events.ExperienceGained;
import net.storm.api.events.InventoryChanged;
import net.storm.api.magic.SpellBook;
import net.storm.plugins.aio.rc.AIORCConfig;
import net.storm.plugins.aio.rc.SharedContext;
import net.storm.plugins.aio.rc.StateMachine;
import net.storm.plugins.aio.rc.StateMachineInterface;
import net.storm.plugins.aio.rc.enums.States;
import net.storm.plugins.aio.rc.enums.EssPouch;
import net.storm.sdk.entities.Players;
import net.storm.sdk.entities.TileObjects;
import net.storm.sdk.game.Vars;
import net.storm.sdk.items.Equipment;
import net.storm.sdk.items.Inventory;
import net.storm.sdk.movement.Movement;

public class CraftRunes implements StateMachineInterface {
    boolean interactedWithAltar = false;

    @Subscribe
    private void onExperienceGained(ExperienceGained gained) {
        System.out.println("Gained: " + gained.getXpGained());
    }

    @Subscribe
    private void onInventoryChanged(InventoryChanged invChange) {
        // System.out.println("INVENTORY CHANGED: " + invChange.getAmount());
    }

    @Subscribe
    public void onAnimationChanged(AnimationChanged e) {
        if(e.getActor().getId() == Players.getLocal().getId() && e.getActor().getAnimation() == 791) {

            e.getActor().setAnimation(-1);
        }
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
        ITileObject altar = TileObjects.getFirstSurrounding(Players.getLocal().getWorldArea().toWorldPoint(), 20, context.getConfig().runes().getAltarID());
        if(context.getRuneNeededForComboRunesId() != null) {
            Inventory.getFirst(context.getRuneNeededForComboRunesId()).useOn(altar);
        } else {
            altar.interact("Craft-rune");
        }
        this.interactedWithAltar = true;
    }

    @Override
    public void handleState(StateMachine stateMachine, States state) {
        SharedContext context = stateMachine.getContext();
        AIORCConfig config = context.getConfig();
        ITileObject altar = TileObjects.getFirstSurrounding(Players.getLocal().getWorldArea().toWorldPoint(), 2, context.getConfig().runes().getAltarID());

        if (config.bringBindingNecklace() && Inventory.contains(ItemID.BINDING_NECKLACE) &&
                !Equipment.contains(ItemID.BINDING_NECKLACE)) {
            Inventory.getFirst(ItemID.BINDING_NECKLACE).interact("Wear");
        }

        if (config.useImbue() && SpellBook.Lunar.MAGIC_IMBUE.canCast() &&
                Vars.getBit(Varbits.MAGIC_IMBUE) == 0) {
            SpellBook.Lunar.MAGIC_IMBUE.cast();
        }

        if(!Movement.isWalking() && !interactedWithAltar) {
            craftRunes(context);
        }

        context.checkTotalEssencesInInv();
        if (altar != null && context.getTotalEssencesInInv() > 0) {
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
