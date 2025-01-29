package net.storm.plugins.gloryrecharger.states;

import net.runelite.api.ItemID;
import net.runelite.api.WorldType;
import net.runelite.api.coords.WorldArea;
import net.runelite.api.widgets.ComponentID;
import net.runelite.client.eventbus.Subscribe;
import net.storm.api.domain.actors.IPlayer;
import net.storm.api.domain.tiles.ITileObject;
import net.storm.api.events.InventoryChanged;
import net.storm.api.events.PlayerSpawned;
import net.storm.plugins.gloryrecharger.SharedContext;
import net.storm.plugins.gloryrecharger.StateMachine;
import net.storm.plugins.gloryrecharger.StateMachineInterface;
import net.storm.plugins.gloryrecharger.enums.States;
import net.storm.sdk.entities.Players;
import net.storm.sdk.entities.TileObjects;
import net.storm.sdk.game.Game;
import net.storm.sdk.game.Worlds;
import net.storm.sdk.items.Equipment;
import net.storm.sdk.items.Inventory;
import net.storm.sdk.movement.Movement;
import net.storm.sdk.widgets.Dialog;
import net.storm.sdk.widgets.Widgets;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WalkToFountain implements StateMachineInterface {
    SharedContext context;

    public WalkToFountain(SharedContext context) {
        this.context = context;
    }

    @Subscribe
    private void onPlayerSpawned(final PlayerSpawned event) {
        context.hopCheck();
    }

    @Subscribe
    private void onInventoryChanged(final InventoryChanged event) {
        if(event.getItemId() == ItemID.AMULET_OF_GLORY6) {
            context.setGloriesCharged(context.getGloriesCharged() + 1);
        }
    }

    @Override
    public void handleState(StateMachine stateMachine) {
        IPlayer localPlayer = Players.getLocal();
        WorldArea fountainOfRuneLocation = new WorldArea(3372, 3890, 6, 6, 0);
        ITileObject fountainOfRune = TileObjects.getNearest(o -> o.getId() == 26782);

        context.hopCheck();

        if(Inventory.contains(ItemID.AMULET_OF_GLORY)) {
            if(fountainOfRune != null && fountainOfRune.distanceTo(localPlayer.getWorldArea().toWorldPoint()) < 15) {
                if(Inventory.contains(ItemID.AMULET_OF_GLORY)) {
                    Inventory.getFirst(ItemID.AMULET_OF_GLORY).useOn(fountainOfRune);
                }
            } else if (Inventory.contains(ItemID.WILDERNESS_SWORD_4) && !Dialog.isOpen() && !localPlayer.isAnimating()) {
                Inventory.getFirst(ItemID.WILDERNESS_SWORD_4).interact("Teleport");
            } else if (Equipment.contains(ItemID.WILDERNESS_SWORD_4) && !Dialog.isOpen() && !localPlayer.isAnimating()){
                Equipment.getFirst(ItemID.WILDERNESS_SWORD_4).interact("Teleport");
            } else if (!Movement.isWalking() && !localPlayer.getWorldArea().intersectsWith(fountainOfRuneLocation)) {
                Movement.walkTo(fountainOfRuneLocation);
            }

            if (Dialog.isOpen()) {
                Dialog.chooseOption("Yes.");
            }
        } else {
            stateMachine.setState(new Banking(context), true);
        }
    }

    @Override
    public States getStateName() {
        return States.WalkToFountain;
    }
}
