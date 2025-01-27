package net.storm.plugins.aio.rc;

import lombok.Getter;
import lombok.Setter;
import net.runelite.client.eventbus.EventBus;
import net.storm.plugins.aio.rc.enums.States;
import net.storm.plugins.aio.rc.states.Banking;

public class StateMachine {
    private StateMachineInterface currentState;
    @Getter
    private final EventBus eventBus;
    @Setter
    private static boolean hasEventBusSubscription = false;

    public StateMachine(EventBus eventBus, SharedContext context) {
        this.eventBus = eventBus;
        this.currentState = new Banking(context); // Default state
    }

    public void setState(StateMachineInterface newState, boolean withEventbus) {
        System.out.println("EVENTBUS TERMINATE FOR: " + this.currentState.getStateName() + " " + hasEventBusSubscription);
        if (hasEventBusSubscription){
            eventBus.unregister(this.currentState);
            setHasEventBusSubscription(false);
        }

        this.currentState = newState;

        if(withEventbus) {
            eventBus.register(newState);
            setHasEventBusSubscription(true);
        }
    }

    public States getCurrentStateName() {
        return this.currentState.getStateName();
    }

    public void handleState(States state) {
        currentState.handleState(this, state);
    }
}