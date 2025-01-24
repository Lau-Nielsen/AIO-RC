package net.storm.plugins.examples.looped.states;

import net.runelite.api.coords.WorldArea;
import net.storm.api.domain.actors.INPC;
import net.storm.api.domain.actors.IPlayer;
import net.storm.plugins.examples.looped.StateMachine;
import net.storm.plugins.examples.looped.StateMachineInterface;
import net.storm.plugins.examples.looped.enums.States;
import net.storm.sdk.entities.NPCs;
import net.storm.sdk.entities.Players;
import net.storm.sdk.movement.Movement;

public class EnterAbyss implements StateMachineInterface {
    @Override
    public void handleState(StateMachine stateMachine, States state) {
        INPC zamorakMage = NPCs.getNearest(x -> x.hasAction("Teleport"));
        IPlayer localPlayer = Players.getLocal();
        boolean isInteractingWithCuck = false;

        if (localPlayer.isInteracting()) {
            isInteractingWithCuck = localPlayer.getInteracting().getId() == 2581;
        }



        if(!Movement.isWalking() && !isInteractingWithCuck && zamorakMage == null) {
            Movement.walkTo(new WorldArea(3105,3559,5,5,0));
        }

        if(zamorakMage != null && !isInteractingWithCuck && zamorakMage.getAnimation() != 1818) {
            zamorakMage.interact("Teleport");
        }

        if(zamorakMage != null && zamorakMage.isAnimating() && zamorakMage.getAnimation() == 1818) {
            stateMachine.setState(new SolveAbyss(), false);
        }
    }

    @Override
    public States getStateName() {
        return States.EnterAbyss;
    }
}
