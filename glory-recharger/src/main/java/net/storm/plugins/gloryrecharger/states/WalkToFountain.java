package net.storm.plugins.gloryrecharger.states;

import net.runelite.api.ItemID;
import net.runelite.api.Varbits;
import net.runelite.api.coords.WorldArea;
import net.runelite.client.eventbus.Subscribe;
import net.storm.api.domain.actors.IPlayer;
import net.storm.api.domain.tiles.ITileObject;
import net.storm.api.events.InventoryChanged;
import net.storm.api.events.PlayerSpawned;
import net.storm.plugins.gloryrecharger.GloryRechargerConfig;
import net.storm.plugins.gloryrecharger.SharedContext;
import net.storm.plugins.gloryrecharger.StateMachine;
import net.storm.plugins.gloryrecharger.StateMachineInterface;
import net.storm.plugins.gloryrecharger.enums.FountainTransportation;
import net.storm.plugins.gloryrecharger.enums.Obelisk;
import net.storm.plugins.gloryrecharger.enums.States;
import net.storm.sdk.entities.Players;
import net.storm.sdk.entities.TileObjects;
import net.storm.sdk.game.Vars;
import net.storm.sdk.items.Equipment;
import net.storm.sdk.items.Inventory;
import net.storm.sdk.movement.Movement;
import net.storm.sdk.widgets.Dialog;

public class WalkToFountain implements StateMachineInterface {
    SharedContext context;
    GloryRechargerConfig config;
    boolean ObeliskCompleteFlag = false;
    private final int fountainOfRuneID = 26782;

    public WalkToFountain(SharedContext context) {
        this.context = context;
        this.config = context.getConfig();
    }

    @Subscribe
    private void onInventoryChanged(final InventoryChanged event) {
        if(event.getItemId() == ItemID.AMULET_OF_GLORY6) {
            context.setGloriesCharged(context.getGloriesCharged() + 1);
        }
    }

    private void obeliskFountainRoute() {
        WorldArea feroxObelisk = new WorldArea(3155, 3619, 3, 3, 0);
        IPlayer localPlayer = Players.getLocal();
        ITileObject obelisk = TileObjects.getFirstSurrounding(localPlayer.getWorldLocation(), 10,o -> o != null && o.hasAction("Activate"));
        int animatingObeliskID = 14825;
        int obeliskDestinationVarbit = 4966;

        if(!Movement.isWalking() && obelisk == null && TileObjects.getNearest(animatingObeliskID) == null) {
            Movement.walkTo(feroxObelisk);
        }

        if(obelisk != null || TileObjects.getNearest(animatingObeliskID) != null && !localPlayer.isAnimating() && context.wildyLevel() < 50) {
            if (Vars.getBit(Varbits.DIARY_WILDERNESS_HARD) == 1) {
                if (Vars.getBit(obeliskDestinationVarbit) != Obelisk.ROUGES_CASTE.getVarbitValue()) {
                    context.setDestinationToRogues();
                }  else {
                    context.teleportToDestination();
                }
            } else {
                if (TileObjects.getNearest(animatingObeliskID) == null) {
                    obelisk.interact("Activate");
                } else {
                    Movement.walkTo(context.calculateMiddleOfObelisk());
                }
            }
        }
    }

    @Override
    public void handleState(StateMachine stateMachine) {
        IPlayer localPlayer = Players.getLocal();
        WorldArea fountainOfRuneLocation = new WorldArea(3372, 3890, 6, 6, 0);
        ITileObject fountainOfRune = TileObjects.getNearest(o -> o.getId() == fountainOfRuneID);

        context.hopCheck();
        context.handleProtectItem();

        if(context.wildyLevel() > 49) {
            this.ObeliskCompleteFlag = true;
        }

        if(Inventory.contains(ItemID.AMULET_OF_GLORY)) {
            if(fountainOfRune != null && fountainOfRune.distanceTo(localPlayer.getWorldArea().toWorldPoint()) < 15) {
                if(Inventory.contains(ItemID.AMULET_OF_GLORY)) {
                    Inventory.getFirst(ItemID.AMULET_OF_GLORY).useOn(fountainOfRune);
                }
            } else if (Equipment.contains(ItemID.WILDERNESS_SWORD_4) && !Dialog.isOpen() && !localPlayer.isAnimating() &&
                    config.fountainTransport() == FountainTransportation.WILDERNESS_SWORD){
                Equipment.getFirst(ItemID.WILDERNESS_SWORD_4).interact("Teleport");
            } else if(config.fountainTransport() == FountainTransportation.OBELISK && !this.ObeliskCompleteFlag) {
                obeliskFountainRoute();
            } else if (!Movement.isWalking() && !localPlayer.getWorldArea().intersectsWith(fountainOfRuneLocation)) {
                Movement.walkTo(fountainOfRuneLocation);
            }

            if (Dialog.isOpen()) {
                Dialog.chooseOption("Yes");
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
