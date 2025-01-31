package net.storm.plugins.daeyalt.states;

import net.runelite.api.coords.WorldArea;
import net.storm.api.domain.actors.IPlayer;
import net.storm.api.domain.tiles.ITileObject;
import net.storm.plugins.daeyalt.SharedContext;
import net.storm.plugins.daeyalt.StateMachine;
import net.storm.plugins.daeyalt.StateMachineInterface;
import net.storm.plugins.daeyalt.enums.States;
import net.storm.sdk.entities.Players;
import net.storm.sdk.entities.TileObjects;
import net.storm.sdk.movement.Movement;

public class WalkToMine implements StateMachineInterface {
    SharedContext context;

    public WalkToMine(SharedContext context) {
        this.context = context;
    }

    @Override
    public void handleState(StateMachine stateMachine) {
        IPlayer localPlayer = Players.getLocal();
        WorldArea mineLocation = new WorldArea(3686, 9757,1,1,2);
        final int daeyaltRockID = 39095;

        ITileObject mineablePillar = TileObjects.getNearest(o -> o.getId() == daeyaltRockID && o.hasAction("Mine"));

        if(localPlayer.getWorldArea().getX() == mineLocation.getX() &&
                localPlayer.getWorldArea().getY() == mineLocation.getY() || mineablePillar != null) {
            stateMachine.setState(new MineShards(context), true);
        } else if (!Movement.isWalking() && !localPlayer.getWorldArea().equals(mineLocation)) {
            Movement.walkTo(mineLocation);
        }
    }

    @Override
    public States getStateName() {
        return States.WalkToMines;
    }
}
