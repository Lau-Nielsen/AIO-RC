package net.storm.plugins.examples.looped;

import net.storm.plugins.examples.looped.enums.States;

public interface StateMachineInterface {
    void handleState(StateMachine stateMachine, States state);
    States getStateName();
}
