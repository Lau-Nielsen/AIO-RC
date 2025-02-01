package net.storm.plugins.aio.rc.states;

import net.runelite.api.ItemID;
import net.runelite.api.Skill;
import net.runelite.api.Varbits;
import net.runelite.client.eventbus.Subscribe;
import net.storm.api.Static;
import net.storm.api.domain.tiles.ITileObject;
import net.storm.api.events.*;
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
import net.storm.sdk.input.Keyboard;
import net.storm.sdk.items.Equipment;
import net.storm.sdk.items.Inventory;
import net.storm.sdk.movement.Movement;
import net.storm.sdk.widgets.Dialog;

import java.util.Arrays;

public class CraftRunes implements StateMachineInterface {
    private final SharedContext context;
    private final AIORCConfig config;


    public CraftRunes(final SharedContext context) {
        this.context = context;
        this.config = context.getConfig();
    }

    boolean interactedWithAltar = false;

    @Subscribe
    private void onExperienceGained(ExperienceGained gained) {
        if(gained.getSkill() == Skill.RUNECRAFT) {
            context.setExpGained(context.getExpGained() + gained.getXpGained());
        }
    }

    @Subscribe
    private void onPlayerSpawned(PlayerSpawned spawnedPlayer) {
        String[] runners = config.runnerNames().split(",");
        if(Arrays.stream(runners).anyMatch(e -> e.equals(spawnedPlayer.getPlayer().getName()))) {
            context.getTradeOrder().add(spawnedPlayer.getPlayer().getName());
        }
    }

    @Subscribe
    private void onPlayerDespawned(PlayerDespawned despawnedPlayer) {
        String[] runners = config.runnerNames().split(",");
        if(Arrays.stream(runners).anyMatch(e -> e.equals(despawnedPlayer.getPlayer().getName()))) {
            context.getTradeOrder().removeIf(e -> e.equals(despawnedPlayer.getPlayer().getName()));
        }
    }

    @Subscribe
    private void onInventoryChanged(InventoryChanged invChange) {
        if(invChange.getItemId() == context.getCurrentlyCrafting().getItemID()) {
            context.setRunesCrafted(context.getRunesCrafted() + invChange.getAmount());
            context.setEstimatedGpEarned(context.getEstimatedGpEarned() + (invChange.getAmount() * Static.getItemManager().getItemPrice(invChange.getItemId())));
        }
    }

    private boolean hasInventorySpaceRequired(int amount) {
        return Inventory.getFreeSlots() > amount;
    }

    private void emptyPouches() {
        EssPouch small = EssPouch.SMALL;
        EssPouch medium = EssPouch.MEDIUM;
        EssPouch large = EssPouch.LARGE;
        EssPouch giant = EssPouch.GIANT;
        EssPouch colossal = EssPouch.COLOSSAL;

        if(context.isUsingSmallPouch() && small.getAmount() > 0 && hasInventorySpaceRequired(small.getAmount())) {
            small.empty();
        }

        if (context.isUsingGiantPouch() && giant.getAmount() > 0 && hasInventorySpaceRequired(giant.getAmount())) {
            giant.empty();
        }

        if (context.isUsingMediumPouch() && medium.getAmount() > 0 && hasInventorySpaceRequired(medium.getAmount())) {
            medium.empty();
        }

        if (context.isUsingLargePouch() && large.getAmount() > 0 && hasInventorySpaceRequired(large.getAmount())) {
            large.empty();
        }

        if (context.isUsingColossalPouch() && colossal.getAmount() > 0) {
            colossal.empty();
        }
    }

    private void craftRunes(SharedContext context) {
        ITileObject altar = TileObjects.getFirstSurrounding(Players.getLocal().getWorldLocation(), 20, context.getConfig().altar().getAltarID());
        if(context.getOppositeRuneIDForComboRune() != null) {
            Inventory.getFirst(context.getOppositeRuneIDForComboRune()).useOn(altar);
        } else {
            altar.interact("Craft-rune");
        }
        this.interactedWithAltar = true;
    }

    private void destroyBindingNecklace() {
        if(Inventory.contains(ItemID.BINDING_NECKLACE) && !Dialog.isOpen()) {
            if (!Equipment.contains(ItemID.BINDING_NECKLACE)) {
                Inventory.getFirst(ItemID.BINDING_NECKLACE).interact("Wear");
                return;
            }

            Inventory.getFirst(ItemID.BINDING_NECKLACE).interact("Destroy");
        }

        if (Dialog.isOpen()) {
            Keyboard.type(1);
        }
    }

    @Override
    public void handleState(StateMachine stateMachine) {
        ITileObject altar = TileObjects.getFirstSurrounding(Players.getLocal().getWorldLocation(), 4, context.getConfig().altar().getAltarID());

        if (Players.getLocal().isMoving() && interactedWithAltar) {
            // The imbue varbit contains the seconds remaining on Magic Imbue
            if (config.useImbue() && SpellBook.Lunar.MAGIC_IMBUE.canCast() &&
                    Vars.getBit(Varbits.MAGIC_IMBUE) == 0) {
                SpellBook.Lunar.MAGIC_IMBUE.cast();
            }

            destroyBindingNecklace();
        }

        if(!Movement.isWalking() && !interactedWithAltar) {
            craftRunes(context);
        }

        context.checkTotalEssencesInInv();
        if (altar != null && context.getTotalEssencesInInv() > 0) {
            emptyPouches();
            craftRunes(context);
        }
        context.checkTotalEssencesInInv();

        if (context.getTotalEssencesInInv() == 0) {
            context.checkChargesOnRote();
            if(config.isUsingRunners()) {
                stateMachine.setState(new RecieveTrades(context), true);
            } else {
                Players.getLocal().setAnimation(-1);
                stateMachine.setState(new RechargeROTE(context), false);
            }
        }
    }

    @Override
    public States getStateName() {
        return States.CraftRunes;
    }
}
