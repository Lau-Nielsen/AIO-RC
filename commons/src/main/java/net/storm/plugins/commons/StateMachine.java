package net.storm.plugins.commons;

import lombok.Getter;
import lombok.Setter;
import net.runelite.client.eventbus.EventBus;

public class StateMachine {
    private StateMachineInterface currentState;
    @Getter
    private final EventBus eventBus;
    @Setter
    private static boolean hasEventBusSubscription = false;

    public StateMachine(EventBus eventBus) {
        this.eventBus = eventBus;
    }

    public void setState(StateMachineInterface newState, boolean withEventbus) {
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

    public String getCurrentStateName() {
        if(this.currentState == null) {
            return null;
        }
        return this.currentState.getStateName();
    }

    public void handleState() {
        if(currentState != null) {
            currentState.handleState(this);
        }
    }
}