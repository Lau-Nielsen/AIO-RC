package net.storm.plugins.gloryrecharger.states;

import net.runelite.api.ItemID;
import net.runelite.api.Varbits;
import net.runelite.api.coords.WorldArea;
import net.runelite.api.events.GameTick;
import net.runelite.client.eventbus.Subscribe;
import net.storm.api.domain.actors.IPlayer;
import net.storm.api.domain.tiles.ITileObject;
import net.storm.api.events.InventoryChanged;
import net.storm.api.magic.SpellBook;
import net.storm.api.movement.pathfinder.model.BankLocation;
import net.storm.plugins.commons.utils.WildyUtils;
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

import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

public class WalkToFountain implements StateMachineInterface {
    SharedContext context;
    GloryRechargerConfig config;
    AtomicInteger ticks = new AtomicInteger(0);
    private final int fountainOfRuneID = 26782;

    WildyUtils wildyUtils = new WildyUtils();

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

    @Subscribe
    private void onGameTick(final GameTick event) {
        ticks.incrementAndGet();
    }

    private void obeliskFountainRoute() {
        if(config.fountainTransport() != FountainTransportation.OBELISK) return;

        WorldArea feroxObelisk = new WorldArea(3155, 3619, 3, 3, 0);
        IPlayer localPlayer = Players.getLocal();
        ITileObject obelisk = TileObjects.getFirstSurrounding(localPlayer.getWorldLocation(), 10,o -> o != null && o.hasAction("Activate"));
        int animatingObeliskID = 14825;
        int obeliskDestinationVarbit = 4966;

        if(!Movement.isWalking() && obelisk == null && TileObjects.getNearest(animatingObeliskID) == null) {
            Movement.walkTo(feroxObelisk);
            return;
        }

        if((obelisk != null || TileObjects.getNearest(animatingObeliskID) != null) && !localPlayer.isAnimating() && wildyUtils.wildyLevel() < 50) {
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

    private void wildernessSwordRoute() {
        if(config.fountainTransport() != FountainTransportation.WILDERNESS_SWORD) return;
        IPlayer localPlayer = Players.getLocal();

        if (Dialog.isOpen()) {
            Dialog.chooseOption("Yes");
            return;
        }

        if (Equipment.contains(ItemID.WILDERNESS_SWORD_4) && !Dialog.isOpen() && !localPlayer.isAnimating()){
            Equipment.getFirst(ItemID.WILDERNESS_SWORD_4).interact("Teleport");
        }

    }

    private void annakarlRoute() {
        Set<FountainTransportation> validTransports = Set.of(
                FountainTransportation.ANNAKARL_TP,
                FountainTransportation.ANNAKARL_TABLET
        );

        if (!validTransports.contains(config.fountainTransport())) return;

        if (Dialog.isOpen()) {
            Dialog.chooseOption("Yes");
            return;
        }

        IPlayer localPlayer = Players.getLocal();

        if (localPlayer.isAnimating()) {
            return;
        }

        if(wildyUtils.wildyLevel() < 20) {
            if(config.fountainTransport() == FountainTransportation.ANNAKARL_TP) {
                if (SpellBook.Ancient.ANNAKARL_TELEPORT.canCast()) {
                    SpellBook.Ancient.ANNAKARL_TELEPORT.cast();
                    return;
                }
            }

            if(config.fountainTransport() == FountainTransportation.ANNAKARL_TABLET) {
                if (Inventory.contains(ItemID.ANNAKARL_TELEPORT)) {
                    Inventory.getFirst(ItemID.ANNAKARL_TELEPORT).interact("Break");
                    return;
                }
            }
        }

        WorldArea fountainOfRuneLocation = new WorldArea(3377, 3891, 3, 3, 0);
        if (!Movement.isWalking() && wildyUtils.wildyLevel() > 30) {
            Movement.walkTo(fountainOfRuneLocation);
        }
    }

    private void walkerRouter() {
        if (config.fountainTransport() != FountainTransportation.WALKER) return;

        WorldArea fountainOfRuneLocation = new WorldArea(3377, 3891, 3, 3, 0);
        if (!Movement.isWalking()) {
            Movement.walkTo(fountainOfRuneLocation);
        }
    }

    @Override
    public void handleState(StateMachine stateMachine) {
        IPlayer localPlayer = Players.getLocal();
        ITileObject fountainOfRune = TileObjects.getNearest(o -> o.getId() == fountainOfRuneID);

        wildyUtils.hopCheckAndHop(config.hopOnAttackablePlayer());
        context.handleProtectItem();

        if(Inventory.contains(ItemID.AMULET_OF_GLORY)) {
            if(fountainOfRune != null && fountainOfRune.distanceTo(localPlayer.getWorldArea().toWorldPoint()) < 10) {
                if(Inventory.contains(ItemID.AMULET_OF_GLORY)) {
                    if(ticks.get() % 5 == 0) {
                        Inventory.getFirst(ItemID.AMULET_OF_GLORY).useOn(fountainOfRune);
                        ticks.set(0);
                    }
                }
            } else {
                wildernessSwordRoute();
                obeliskFountainRoute();
                annakarlRoute();
                walkerRouter();
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
