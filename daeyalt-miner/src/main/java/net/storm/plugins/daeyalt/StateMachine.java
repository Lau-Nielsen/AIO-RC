package net.storm.plugins.daeyalt;

import lombok.Getter;
import lombok.Setter;
import net.runelite.client.eventbus.EventBus;
import net.storm.plugins.daeyalt.enums.States;

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

    public States getCurrentStateName() {
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