package net.storm.plugins.gloryrecharger.states;

import net.storm.api.domain.tiles.ITileObject;
import net.storm.api.movement.pathfinder.model.BankLocation;
import net.storm.plugins.gloryrecharger.GloryRechargerConfig;
import net.storm.plugins.gloryrecharger.SharedContext;
import net.storm.plugins.gloryrecharger.StateMachine;
import net.storm.plugins.gloryrecharger.StateMachineInterface;
import net.storm.plugins.gloryrecharger.enums.States;
import net.storm.sdk.entities.TileObjects;
import net.storm.sdk.game.Combat;
import net.storm.sdk.items.Bank;
import net.storm.sdk.movement.Movement;
import net.storm.sdk.widgets.Prayers;

public class UseFeroxPool implements StateMachineInterface {
    private final SharedContext context;
    private final GloryRechargerConfig config;

    public UseFeroxPool(final SharedContext context) {
        this.context = context;
        this.config = context.getConfig();
    }

    private boolean hasClickedPool = false;

    @Override
    public void handleState(StateMachine stateMachine) {
        boolean isFullHP = Combat.getHealthPercent() == 100;
        boolean isFullRunEnergy = Movement.getRunEnergy() == 100;
        boolean isFullPrayer = Prayers.getMissingPoints() == 0;

        if (config.useFeroxPool() && (!isFullHP || !isFullRunEnergy || !isFullPrayer)) {
            if(Bank.isOpen()) {
                Bank.close();
            }

            ITileObject pool = TileObjects.getNearest(x -> x.hasAction("Drink"));
            if(!hasClickedPool && pool != null) {
                Movement.walkTo(BankLocation.FEROX_ENCLAVE_BANK);
            }

            if(!hasClickedPool && !Bank.isOpen()) {
                if(pool != null) {
                    pool.interact("Drink");
                    this.hasClickedPool = true;
                }
            }
        } else {
            stateMachine.setState(new WalkToFountain(context), true);
        }
    }

    @Override
    public States getStateName() {
        return States.usePool;
    }
}