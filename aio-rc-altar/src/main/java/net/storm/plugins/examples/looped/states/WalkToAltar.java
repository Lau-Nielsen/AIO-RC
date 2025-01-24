package net.storm.plugins.examples.looped.states;

import net.storm.api.domain.actors.IPlayer;
import net.storm.api.domain.tiles.ITileObject;
import net.storm.plugins.examples.looped.ExampleLoopedConfig;
import net.storm.plugins.examples.looped.enums.Altar;
import net.storm.plugins.examples.looped.StateMachine;
import net.storm.plugins.examples.looped.StateMachineInterface;
import net.storm.plugins.examples.looped.enums.States;
import net.storm.sdk.entities.Players;
import net.storm.sdk.entities.TileObjects;
import net.storm.sdk.movement.Movement;

public class WalkToAltar implements StateMachineInterface {
    public void enterRuins(Altar altar) {
        IPlayer localPlayer = Players.getLocal();
        if(localPlayer != null) {
            boolean isRuinsNearby = TileObjects.getFirstSurrounding(localPlayer.getWorldArea().toWorldPoint(), 10, altar.getRuinID()) != null;
            if(isRuinsNearby) {
                ITileObject ruinTileObject = TileObjects.getFirstSurrounding(Players.getLocal().getWorldArea().toWorldPoint(), 10, altar.getRuinID());

                if(ruinTileObject.getId() == altar.getRuinID()) {
                    ruinTileObject.interact("Enter");
                }
            }
        }
    }

    @Override
    public void handleState(StateMachine stateMachine, States state) {
        ExampleLoopedConfig config = stateMachine.getContext().getConfig();
        Altar altar = config.runes();
        boolean closeToAltar = TileObjects.getFirstSurrounding(Players.getLocal().getWorldArea().toWorldPoint(), 20, altar.getAltarID()) != null;

        if (!Movement.isWalking()) {
            altar.walkToAltar();
        }

        if (!closeToAltar) {
            enterRuins(altar);
        }

        if (closeToAltar) {
            if(config.isRunner()) {
                stateMachine.setState(new TradePlayer(), true);
            } else {
                stateMachine.setState(new CraftRunes(), true);
            }
        }
    }

    @Override
    public States getStateName() {
        return States.WalkToAltar;
    }
}
