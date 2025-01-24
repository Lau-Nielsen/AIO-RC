package net.storm.plugins.examples.looped;

import lombok.Getter;
import lombok.Setter;
import net.runelite.client.eventbus.EventBus;
import net.storm.plugins.examples.looped.enums.States;
import net.storm.plugins.examples.looped.states.Banking;

public class StateMachine {
    private StateMachineInterface currentState;
    @Getter
    private final SharedContext context;
    private final EventBus eventBus;
    @Setter
    private static boolean hasEventBusSubscription = false;

    public StateMachine(SharedContext context, EventBus eventBus) {
        this.context = context;
        this.eventBus = eventBus;
        this.currentState = new Banking(); // Default state
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