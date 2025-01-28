package net.storm.plugins.aio.rc;

import lombok.Getter;
import lombok.Setter;
import net.runelite.client.eventbus.EventBus;
import net.storm.plugins.aio.rc.enums.States;
import net.storm.plugins.aio.rc.states.BankSetupAndStock;
import net.storm.plugins.aio.rc.states.Banking;

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

        // Speed up state transitions..
        if(this.currentState.getStateName() != States.BankSetupAndStock){
            this.currentState = newState;
            this.currentState.handleState(this);
        } else {
            this.currentState = newState;
        }

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
        currentState.handleState(this);
    }
}