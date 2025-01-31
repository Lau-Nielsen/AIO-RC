package net.storm.plugins.daeyalt.states;

import net.runelite.api.ItemID;
import net.runelite.api.WorldType;
import net.runelite.api.coords.WorldArea;
import net.runelite.api.events.GameTick;
import net.runelite.client.eventbus.Subscribe;
import net.storm.api.domain.actors.IPlayer;
import net.storm.api.domain.tiles.ITileObject;
import net.storm.api.events.InventoryChanged;
import net.storm.api.widgets.Tab;
import net.storm.plugins.daeyalt.DaeyaltMinerConfig;
import net.storm.plugins.daeyalt.SharedContext;
import net.storm.plugins.daeyalt.StateMachine;
import net.storm.plugins.daeyalt.StateMachineInterface;
import net.storm.plugins.daeyalt.enums.States;
import net.storm.sdk.entities.Players;
import net.storm.sdk.entities.TileObjects;
import net.storm.sdk.game.Worlds;
import net.storm.sdk.items.Inventory;
import net.storm.sdk.movement.Movement;
import net.storm.sdk.widgets.Widgets;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class MineShards implements StateMachineInterface {
    DaeyaltMinerConfig config;
    SharedContext context;

    AtomicInteger ticks = new AtomicInteger(0);

    private final int daeyaltRockID = 39095;

    @Subscribe
    private void onGameTick(final GameTick event) {
        this.ticks.getAndIncrement();
    }

    @Subscribe
    private void onInventoryChanged(InventoryChanged change) {
        if(change.getItemId() == ItemID.DAEYALT_SHARD) {
            context.setShardsMined(context.getShardsMined() + change.getAmount());
        }
    }

    public MineShards(SharedContext context) {
        this.context = context;
        this.config = context.getConfig();
    }

    private void walkToTickManipPoint() {
        Map<String, List<WorldArea>> points  = context.getTickManipPoints();
        int localPlayerX = Players.getLocal().getWorldArea().getX();
        int localPlayerY = Players.getLocal().getWorldArea().getY();

        // Checking for a specific x or y value that is unique to the four locations you can end up in while mining daeyalt.
        // Could probably be better, but this is also kinda verbose.
        if(localPlayerX == 3686) {
            points.get("east").stream().filter(e -> e.getY() != localPlayerY).forEach(Movement::walkTo);
        } else if(localPlayerX == 3674) {
            points.get("south-east").stream().filter(e -> e.getY() != localPlayerY).forEach(Movement::walkTo);
        } else if(localPlayerY == 9753) {
            points.get("south-north").stream().filter(e -> e.getX() != localPlayerX).forEach(Movement::walkTo);
        } else if(localPlayerY == 9764) {
            points.get("north").stream().filter(e -> e.getX() != localPlayerX).forEach(Movement::walkTo);
        }
    }

    private void tickManip(){
        ITileObject mineablePillar = TileObjects.getNearest(o -> o.getId() == daeyaltRockID && o.hasAction("Mine"));
        if(this.ticks.get() % 3 == 0) {
            Inventory.getFirst(ItemID.GUAM_LEAF, ItemID.MARRENTILL, ItemID.HARRALANDER)
                    .useOn(Inventory.getFirst(ItemID.SWAMP_TAR));
            walkToTickManipPoint();
        } else if(this.ticks.get() % 3 == 1) {
            mineablePillar.interact("Mine");
        }
    }

    @Override
    public void handleState(StateMachine stateMachine) {
        ITileObject mineablePillar = TileObjects.getNearest(o -> o.getId() == daeyaltRockID && o.hasAction("Mine"));
        boolean aloneQuestionMark = Players.getAll().size() == 1;
        IPlayer localPlayer = Players.getLocal();

        if (!aloneQuestionMark && this.config.skitzoHop()) {
            int currentWorldLocation = Worlds.getCurrent().getLocation();
            Worlds.openHopper();

            if (Worlds.isHopperOpen()) {
                Worlds.hopTo(Worlds.getRandom(w -> Worlds.isMembers(w) && w.getLocation() == currentWorldLocation));
                Widgets.get(Tab.INVENTORY.getWidgetInfo().getId()).click();
            }
        }

        if (mineablePillar != null) {
            if(this.config.tickManip()) {
                if(this.config.stopTickManip() && !aloneQuestionMark) {
                    mineablePillar.interact("Mine");
                } else {
                    if (localPlayer.distanceTo(mineablePillar.getWorldLocation()) == 2) {
                        tickManip();
                    } else if (!localPlayer.isMoving()) {
                        mineablePillar.interact("Mine");
                    }
                }
            } else if (!localPlayer.isAnimating() && !localPlayer.isMoving()) {
                mineablePillar.interact("Mine");
            }

            if (this.config.dropGems() && Inventory.contains(e -> e.getName() != null && e.getName().contains("Uncut"))) {
                Inventory.getFirst(e -> e.getName() != null &&  e.getName().contains("Uncut")).interact("Drop");
                mineablePillar.interact("Mine");
            }
        }

    }

    @Override
    public States getStateName() {
        return States.MineShards;
    }
}
