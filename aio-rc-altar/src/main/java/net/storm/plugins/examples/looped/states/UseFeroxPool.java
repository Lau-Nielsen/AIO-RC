package net.storm.plugins.examples.looped.states;

import net.runelite.api.events.GameTick;
import net.runelite.client.eventbus.Subscribe;
import net.storm.api.domain.tiles.ITileObject;
import net.storm.api.movement.pathfinder.model.BankLocation;
import net.storm.plugins.examples.looped.*;
import net.storm.plugins.examples.looped.enums.States;
import net.storm.sdk.entities.TileObjects;
import net.storm.sdk.game.Combat;
import net.storm.sdk.items.Bank;
import net.storm.sdk.movement.Movement;
import net.storm.sdk.widgets.Prayers;

import java.util.concurrent.atomic.AtomicInteger;

public class UseFeroxPool implements StateMachineInterface {
    private boolean hasClickedPool = false;
    private boolean startCountingTicks = false;

    private AtomicInteger ticksSincePoolRefreshment = new AtomicInteger(0);

    @Subscribe
    private void onGameTick(GameTick tick) {
        if(startCountingTicks) {
            ticksSincePoolRefreshment.incrementAndGet();
        }
    }

    @Override
    public void handleState(StateMachine stateMachine, States state) {
        boolean isFullHP = Combat.getHealthPercent() == 100;
        boolean isFullRunEnergy = Movement.getRunEnergy() == 100;
        boolean isFullPrayer = Prayers.getMissingPoints() == 0;

        if (stateMachine.getContext().getConfig().usePoolAtFerox()) {
            if(Bank.isOpen()) {
                Bank.close();
            }

            ITileObject pool = TileObjects.getNearest(x -> x.hasAction("Drink"));
            if(!hasClickedPool && pool != null) {
                Movement.walkTo(BankLocation.FEROX_ENCLAVE_BANK);
            }

            if((!isFullRunEnergy || !isFullHP || !isFullPrayer) && !hasClickedPool && !Bank.isOpen()) {
                if(pool != null) {
                    pool.interact("Drink");
                    this.hasClickedPool = true;
                }
            }

            if (hasClickedPool && isFullHP && isFullRunEnergy && isFullPrayer) {
                this.startCountingTicks = true;
                if(ticksSincePoolRefreshment.get() >= 2) {
                    stateMachine.setState(new Banking(), false);
                }
            }
        } else {
            System.out.println("Invalid event in PAUSED state.");
        }
    }

    @Override
    public States getStateName() {
        return States.UseFeroxPool;
    }
}